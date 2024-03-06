package cn.oneao.project.service.impl;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.ProjectTypeConstants;
import cn.oneao.project.domain.*;
import cn.oneao.project.domain.dto.project.PjProjectArchiveListDTO;
import cn.oneao.project.domain.dto.project.PjProjectListDTO;
import cn.oneao.project.domain.dto.project.PjProjectAddDTO;
import cn.oneao.project.domain.dto.recycle.RecycleProjectDTO;
import cn.oneao.project.mapper.*;
import cn.oneao.project.service.IPjProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPjProjectServiceImpl extends ServiceImpl<PjProjectMapper, PjProject> implements IPjProjectService {
    @Autowired
    private PjProjectMapper pjProjectMapper; // 项目mapper
    @Autowired
    private PjTaskStagesMapper pjTaskStagesMapper; // 任务阶段mapper
    @Autowired
    private PjTaskStagesTemplateMapper pjTaskStagesTemplateMapper; // 任务阶段模板mapper
    @Autowired
    private PjProjectLogMapper pjProjectLogMapper; // 项目日志mapper
    @Autowired
    private PjUserMapper pjUserMapper; // 项目成员mapper
    @Autowired
    private PjTaskMapper pjTaskMapper; // 任务mapper
    @Autowired
    private PjTaskTagMapper pjTaskTagMapper; // 任务标签mapper
    @Autowired
    private PjTaskLikeMapper pjTaskLikeMapper; // 任务点赞mapper
    @Autowired
    private PjTaskFileMapper pjTaskFileMapper;// 任务文件mapper
    @Autowired
    private PjTaskCommentMapper pjTaskCommentMapper; // 任务评论mapper
    @Autowired
    private PjBugMapper pjBugMapper;// 删除项目下的bug

    // 查询项目列表
    @Override
    public MpPageVO selectPjProjectList(PjProjectListDTO pjProjectListDTO) {
        Page<PjProject> page = new Page<>(pjProjectListDTO.getPageNum(), pjProjectListDTO.getPageSize());

        String name = pjProjectListDTO.getName();
        Integer status = pjProjectListDTO.getStatus();
        Date beginTime = pjProjectListDTO.getBeginTime();
        Date endTime = pjProjectListDTO.getEndTime();

        LambdaQueryWrapper<PjProject> queryWrapper = new LambdaQueryWrapper<>();

        return judgeProjectListDTO(page, name, status, beginTime, endTime, queryWrapper);
    }

    // 新增项目
    @Override
    @Transactional
    public int insertPjProject(PjProjectAddDTO pjProjectAddDTO) {
        PjProject pjProject = new PjProject();

        BeanUtils.copyProperties(pjProjectAddDTO, pjProject);

        pjProject.setIsArchive(0);
        pjProject.setSchedule(0.0);
        pjProject.setStatus(0);// 项目状态：0-未开始，1-进行中，2-已完成（默认为0）
        // 插入项目
        pjProjectMapper.insert(pjProject);
        // 插入日志
        PjProjectLog pjProjectLog = new PjProjectLog();
        pjProjectLog.setOperationType(OperationConstants.ADD);
        pjProjectLog.setType(ProjectTypeConstants.PROJECT_NAME);
        pjProjectLog.setContent(SecurityUtils.getUsername() + "新增项目：" + pjProject.getName());
        pjProjectLog.setPjProjectId(pjProject.getId());
        pjProjectLogMapper.insert(pjProjectLog);

        // 将admin添加到项目成员中，为普通成员
        PjUser pjUser = new PjUser();
        pjUser.setPjProjectId(pjProject.getId());
        pjUser.setUserId(1L);
        pjUser.setRole(0);
        pjUser.setStatus(0);
        pjUser.setIsCollection(0);
        pjUserMapper.insert(pjUser);
        // 根据选择的模版，插入项目阶段
        Long pjTemplateId = pjProjectAddDTO.getPjTemplateId();
        // 根据模板id查询项目阶段模板
        LambdaQueryWrapper<PjTaskStagesTemplate> pjTaskStagesTemplateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pjTaskStagesTemplateLambdaQueryWrapper.eq(PjTaskStagesTemplate::getPjTemplateId, pjTemplateId);
        List<PjTaskStagesTemplate> pjTaskStagesTemplateList = pjTaskStagesTemplateMapper.selectList(pjTaskStagesTemplateLambdaQueryWrapper);

        // 项目阶段
        List<PjTaskStages> pjTaskStagesList = new ArrayList<>();
        for (PjTaskStagesTemplate item : pjTaskStagesTemplateList) {
            PjTaskStages pjTaskStages = new PjTaskStages();

            pjTaskStages.setName(item.getName());
            pjTaskStages.setSortNum(item.getSortNum());

            pjTaskStages.setPjProjectId(pjProject.getId());
            pjTaskStages.setCreateBy(SecurityUtils.getUsername());
            pjTaskStages.setCreateTime(new Date());
            pjTaskStagesList.add(pjTaskStages);
        }

        // 批量插入项目阶段
        try {
            pjTaskStagesMapper.insertBatch(pjTaskStagesList);
            return 1; // 插入成功，返回1
        } catch (Exception e) {
            // 插入失败，可以记录日志并根据实际需求进行处理
            return 0;
        }
    }

    // 判断是否有相同的项目名
    public boolean isHasSameName(String name) {
        LambdaQueryWrapper<PjProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjProject::getName, name);
        PjProject pjProject = this.getOne(queryWrapper);
        if (!ObjectUtils.isEmpty(pjProject)) {
            return false;
        }
        return pjProjectMapper.selectCountByName(name) > 0;
    }

    // 修改项目
    @Override
    public int updatePjProject(PjProject pjProject) {
        Integer status = pjProject.getStatus();
        LambdaUpdateWrapper<PjProject> updateWrapper = new LambdaUpdateWrapper<>();
        if (status == 2) {
            updateWrapper.set(PjProject::getRealEndTime, new Date()); // 项目状态为已完成时，设置项目结束时间为当前时间
            updateWrapper.set(PjProject::getSchedule, 100.0);
        } else {
            updateWrapper.set(PjProject::getRealEndTime, null);
        }
        updateWrapper.eq(PjProject::getId, pjProject.getId());
        // 添加日志
        PjProject oldPjProject = pjProjectMapper.selectById(pjProject.getId());
        Integer oldPjProjectStatus = oldPjProject.getStatus();
        Date oldBeginTime = oldPjProject.getBeginTime();
        Date oldEndTime = oldPjProject.getEndTime();
        Integer oldIsArchive = oldPjProject.getIsArchive();
        String oldDescription = oldPjProject.getDescription();
        String oldName = oldPjProject.getName();
        String oldCover = oldPjProject.getCover();
        if (!oldPjProjectStatus.equals(status)) {
            String statusStr = "";
            statusStr = status == 0 ? "未开始" : status == 1 ? "进行中" : "已结束";
            addProjectLog(SecurityUtils.getUsername() + "修改项目状态为：" + statusStr, pjProject);
        }
        if (!oldBeginTime.equals(pjProject.getBeginTime())) {
            addProjectLog(SecurityUtils.getUsername() + "修改项目开始时间为：" + dateFormat(pjProject.getBeginTime()), pjProject);
        }
        if (!oldEndTime.equals(pjProject.getEndTime())) {
            addProjectLog(SecurityUtils.getUsername() + "修改项目结束时间为：" + dateFormat(pjProject.getEndTime()), pjProject);
        }
        if (!oldIsArchive.equals(pjProject.getIsArchive())) {
            String isArchiveStr = "";
            isArchiveStr = pjProject.getIsArchive() == 0 ? "未归档" : "已归档";
            addProjectLog(SecurityUtils.getUsername() + "修改项目归档状态为：" + isArchiveStr + ",归档时间为" + dateFormat(pjProject.getArchiveTime()), pjProject);
        }
        if (!oldDescription.equals(pjProject.getDescription())) {
            addProjectLog(SecurityUtils.getUsername() + "修改了项目描述。", pjProject);
        }
        if (!oldName.equals(pjProject.getName())) {
            PjProjectLog pjProjectLog = new PjProjectLog();
            pjProjectLog.setOperationType(OperationConstants.UPDATE);
            pjProjectLog.setType(ProjectTypeConstants.PROJECT_NAME);
            pjProjectLog.setContent(SecurityUtils.getUsername() + "修改项目名称为：" + pjProject.getName());
            pjProjectLog.setPjProjectId(pjProject.getId());
            pjProjectLogMapper.insert(pjProjectLog);
        }
        if (!oldCover.equals(pjProject.getCover())) {
            addProjectLog(SecurityUtils.getUsername() + "修改了项目封面。", pjProject);
        }
        int update = pjProjectMapper.update(pjProject, updateWrapper);

        return update;
    }

    @Override
    @Transactional
    public AjaxResult deletePjProjectByIds(Long[] ids) {
        //删除项目（逻辑删除）
        this.removeBatchByIds(Arrays.asList(ids));
        return AjaxResult.success();
    }

    @Override
    public List<PjProject> selectPjProjectListAll() {
        LambdaQueryWrapper<PjProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjProject::getIsArchive, 0);
        return pjProjectMapper.selectList(queryWrapper);
    }

    @Override
    public MpPageVO getRecycleList(RecycleProjectDTO recycleProjectDTO) {
        MpPageVO mpPageVO = new MpPageVO();
        List<PjProject> recycleList = pjProjectMapper.getRecycleList(recycleProjectDTO);
        Long total = pjProjectMapper.getRecycleListCount(recycleProjectDTO);
        mpPageVO.setRows(recycleList);
        mpPageVO.setTotal(total);
        return mpPageVO;
    }

    @Override
    public void recovery(Long[] ids) {
        pjProjectMapper.recovery(ids);
    }

    @Override
    @Transactional
    public void realDelete(Long[] ids) {
        List<Long> idList = (List<Long>) Arrays.asList(ids);
        List<PjProject> queryPjProjects = pjProjectMapper.selectDeleteProject(idList);
        queryPjProjects.forEach(item -> {
            PjProjectLog pjProjectLog = new PjProjectLog();
            pjProjectLog.setOperationType(OperationConstants.REAL_DELETE);
            pjProjectLog.setType(ProjectTypeConstants.PROJECT);
            pjProjectLog.setContent(SecurityUtils.getUsername() + "彻底删除了项目：" + item.getName());
            pjProjectLog.setPjProjectId(item.getId());
            pjProjectLogMapper.insert(pjProjectLog);
        });
        // 删除项目
        pjProjectMapper.realDelete(idList);
        // 删除项目用户
        pjUserMapper.realDeleteByProjectIds(idList);
        // 删除任务列表
        LambdaQueryWrapper<PjTaskStages> taskStagesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        taskStagesLambdaQueryWrapper.in(PjTaskStages::getPjProjectId, idList);
        List<PjTaskStages> pjTaskStages = pjTaskStagesMapper.selectList(taskStagesLambdaQueryWrapper);
        List<Long> pjTaskStageIds = pjTaskStages.stream().map(PjTaskStages::getId).collect(Collectors.toList());
        pjTaskStagesMapper.realDeleteByProjectIds(idList);
        // 删除任务
        LambdaQueryWrapper<PjTask> taskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        taskLambdaQueryWrapper.in(PjTask::getPjTaskStagesId, pjTaskStageIds);
        List<PjTask> pjTasks = pjTaskMapper.selectList(taskLambdaQueryWrapper);
        List<Long> taskIds = pjTasks.stream().map(PjTask::getId).collect(Collectors.toList());
        pjTaskMapper.realDeleteByTaskStageIds(pjTaskStageIds);
        // 删除标签
        if (!taskIds.isEmpty()) {
            pjTaskTagMapper.realDeleteByTaskIds(taskIds);
            // 删除任务点赞
            pjTaskLikeMapper.realDeleteByTaskIds(taskIds);
            // 删除任务文件
            LambdaQueryWrapper<PjTaskFile> taskFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            taskFileLambdaQueryWrapper.in(PjTaskFile::getPjTaskId, taskIds);
            List<PjTaskFile> pjTaskFiles = pjTaskFileMapper.selectList(taskFileLambdaQueryWrapper);
            pjTaskFiles.forEach(item -> {
                if (item.getPhysicalPath() != null && !item.getPhysicalPath().isEmpty()) {
                    // 删除文件
                    File file = new File(item.getPhysicalPath());
                    if (file.exists()) {
                        file.delete();
                    }
                }
            });
            pjTaskFileMapper.realDeleteByTaskIds(taskIds);
            // 删除任务评论
            pjTaskCommentMapper.realDeleteByTaskIds(taskIds);
        }

    }

    // 根据任务id获取项目
    @Override
    public PjProject selectPjProjectByTaskId(Long taskId) {
        return pjProjectMapper.selectPjProjectByTaskId(taskId);
    }

    // 查询收藏项目列表
    @Override
    public MpPageVO selectCollectionList(PjProjectListDTO pjProjectListDTO) {
        Page<PjProject> page = new Page<>(pjProjectListDTO.getPageNum(), pjProjectListDTO.getPageSize());

        String name = pjProjectListDTO.getName();
        Integer status = pjProjectListDTO.getStatus();
        Date beginTime = pjProjectListDTO.getBeginTime();
        Date endTime = pjProjectListDTO.getEndTime();
        LambdaQueryWrapper<PjUser> pjUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pjUserLambdaQueryWrapper.eq(PjUser::getUserId, SecurityUtils.getUserId());
        pjUserLambdaQueryWrapper.eq(PjUser::getIsCollection, 1);
        List<PjUser> pjUsers = pjUserMapper.selectList(pjUserLambdaQueryWrapper);
        List<Long> pjProjectIds = pjUsers.stream().map(PjUser::getPjProjectId).collect(Collectors.toList());
        if (pjProjectIds.isEmpty()) {
            return new MpPageVO();
        }
        LambdaQueryWrapper<PjProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PjProject::getId, pjProjectIds);

        return judgeProjectListDTO(page, name, status, beginTime, endTime, queryWrapper);
    }

    @Override
    public int updateCollection(Long[] projectIds){
        Long userId = SecurityUtils.getUserId();

        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getUserId, userId);
        queryWrapper.in(PjUser::getPjProjectId, Arrays.asList(projectIds));
        List<PjUser> pjUserList = pjUserMapper.selectList(queryWrapper);
        for (PjUser pjUser : pjUserList) {
            Integer isCollection = pjUser.getIsCollection();
            if (isCollection == 1) {
                pjUser.setIsCollection(0);
            } else {
                pjUser.setIsCollection(1);
            }
            pjUserMapper.updateById(pjUser);

            PjProjectLog pjProjectLog = new PjProjectLog();
            pjProjectLog.setPjProjectId(pjUser.getPjProjectId());
            pjProjectLog.setType(ProjectTypeConstants.COLLECTION);
            if (pjUser.getIsCollection() == 1) {
                pjProjectLog.setOperationType(OperationConstants.UPDATE);
                pjProjectLog.setContent(SecurityUtils.getUsername() + "收藏项目");
            } else {
                pjProjectLog.setOperationType(OperationConstants.UPDATE);
                pjProjectLog.setContent(SecurityUtils.getUsername() + "取消收藏项目");
            }
            pjProjectLogMapper.insert(pjProjectLog);
        }
        return 1;
    }

    @Override
    public int updateArchive(Long[] projectIds) {
        LambdaQueryWrapper<PjProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PjProject::getId, Arrays.asList(projectIds));
        List<PjProject> pjProjects = pjProjectMapper.selectList(queryWrapper);
        for (PjProject pjProject : pjProjects) {
            Integer isArchive = pjProject.getIsArchive();
            if (isArchive == 1) {
                pjProject.setIsArchive(0);
                pjProject.setArchiveTime(null);
            } else {
                pjProject.setIsArchive(1);
                pjProject.setArchiveTime(new Date());
            }
            pjProjectMapper.updateById(pjProject);

            PjProjectLog pjProjectLog = new PjProjectLog();
            pjProjectLog.setPjProjectId(pjProject.getId());
            pjProjectLog.setType(ProjectTypeConstants.PROJECT);
            if (pjProject.getIsArchive() == 1) {
                pjProjectLog.setOperationType(OperationConstants.UPDATE);
                pjProjectLog.setContent(SecurityUtils.getUsername() + "归档项目");
            } else {
                pjProjectLog.setOperationType(OperationConstants.UPDATE);
                pjProjectLog.setContent(SecurityUtils.getUsername() + "取消归档项目");
            }
            pjProjectLogMapper.insert(pjProjectLog);
        }
        return 1;
    }

    @Override
    public MpPageVO selectArchiveList(PjProjectArchiveListDTO pjProjectArchiveListDTO) {
        Date archiveTime = pjProjectArchiveListDTO.getArchiveTime();
        String name = pjProjectArchiveListDTO.getName();
        Page<PjProject> page = new Page<>(pjProjectArchiveListDTO.getPageNum(), pjProjectArchiveListDTO.getPageSize());
        LambdaQueryWrapper<PjProject> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(name)) {
            queryWrapper.like(PjProject::getName, name);
        }
        if (!ObjectUtils.isEmpty(archiveTime)) {
            queryWrapper.ge(PjProject::getArchiveTime, archiveTime);
        }
        queryWrapper.eq(PjProject::getIsArchive, 1);
        Page<PjProject> pjProjectPage = pjProjectMapper.selectPage(page, queryWrapper);
        return new MpPageVO(pjProjectPage);
    }

    private MpPageVO judgeProjectListDTO(Page<PjProject> page, String name, Integer status, Date beginTime, Date endTime, LambdaQueryWrapper<PjProject> queryWrapper) {
        if (!ObjectUtils.isEmpty(name)) {
            queryWrapper.like(PjProject::getName, name);
        }

        if ((!ObjectUtils.isEmpty(status)) && status != -1) {
            queryWrapper.eq(PjProject::getStatus, status);
        }

        if (!ObjectUtils.isEmpty(beginTime)) {
            queryWrapper.ge(PjProject::getBeginTime, beginTime);
        }

        if (!ObjectUtils.isEmpty(endTime)) {
            queryWrapper.le(PjProject::getEndTime, endTime);
        }

        queryWrapper.eq(PjProject::getIsArchive, 0);

        Page<PjProject> pjProjectPage = pjProjectMapper.selectPage(page, queryWrapper);

        return new MpPageVO(pjProjectPage);
    }

    private void addProjectLog(String content, PjProject pjProject) {
        PjProjectLog pjProjectLog = new PjProjectLog();
        pjProjectLog.setOperationType(OperationConstants.UPDATE);
        pjProjectLog.setType(ProjectTypeConstants.PROJECT);
        pjProjectLog.setContent(content);
        pjProjectLog.setPjProjectId(pjProject.getId());
        pjProjectLogMapper.insert(pjProjectLog);
    }

    private String dateFormat(Date date) {
        // 指定日期时间格式
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 格式化日期对象
        return formatter.format(date);
    }
}
