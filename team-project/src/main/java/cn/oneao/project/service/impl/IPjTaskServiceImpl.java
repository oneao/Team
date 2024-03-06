package cn.oneao.project.service.impl;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.domain.entity.SysUser;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.ProjectTypeConstants;
import cn.oneao.project.constants.TaskTypeConstants;
import cn.oneao.project.domain.*;
import cn.oneao.project.domain.dto.task.PjTaskAddDTO;
import cn.oneao.project.domain.dto.task.PjTaskChildAddDTO;
import cn.oneao.project.domain.vo.task.PjTaskFileVO;
import cn.oneao.project.domain.vo.task.PjTaskListVO;
import cn.oneao.project.domain.vo.task.PjTaskLogAndCommentVO;
import cn.oneao.project.domain.vo.task.PjTaskRecycleVO;
import cn.oneao.project.mapper.*;
import cn.oneao.project.service.IPjTaskService;
import cn.oneao.project.service.IPjTaskTagService;
import cn.oneao.project.utils.SeUtils;
import cn.oneao.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPjTaskServiceImpl extends ServiceImpl<PjTaskMapper, PjTask> implements IPjTaskService {
    @Autowired
    private PjTaskMapper pjTaskMapper;
    @Autowired
    private PjTaskStagesMapper pjTaskStagesMapper;
    @Autowired
    private IPjTaskTagService pjTaskTagService;
    @Autowired
    private PjTaskLogMapper pjTaskLogMapper;
    @Autowired
    private PjTaskCommentMapper pjTaskCommentMapper;
    @Autowired
    private PjUserMapper pjUserMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PjTaskFileMapper pjTaskFileMapper;
    @Autowired
    private PjProjectLogMapper pjProjectLogMapper;
    @Autowired
    private PjTaskLikeMapper pjTaskLikeMapper;
    @Autowired
    private SeUtils seUtils;
    @Autowired
    private PjProjectMapper pjProjectMapper;

    @Override
    public List<PjTaskListVO> getTaskListByProjectId(Long projectId) {
        List<PjTaskListVO> result = new ArrayList<>();
        // 查询项目下的任务阶段
        LambdaQueryWrapper<PjTaskStages> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTaskStages::getPjProjectId, projectId);
        queryWrapper.orderByAsc(PjTaskStages::getSortNum);
        List<PjTaskStages> pjTaskStages = pjTaskStagesMapper.selectList(queryWrapper);

        // 查询项目下的任务
        pjTaskStages.forEach(item -> {
            PjTaskListVO pjTaskListVO = new PjTaskListVO();
            pjTaskListVO.setTaskStageId(item.getId());
            pjTaskListVO.setTaskStageName(item.getName());
            pjTaskListVO.setTaskStageSortNum(item.getSortNum());

            LambdaQueryWrapper<PjTask> pjTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
            pjTaskLambdaQueryWrapper.eq(PjTask::getPjTaskStagesId, item.getId());
            pjTaskLambdaQueryWrapper.isNull(PjTask::getParentId);
            pjTaskLambdaQueryWrapper.orderByAsc(PjTask::getSortNum);

            List<PjTask> pjTasks = pjTaskMapper.selectList(pjTaskLambdaQueryWrapper);

            pjTasks.forEach(task -> {
                LambdaQueryWrapper<PjTaskLog> taskLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
                taskLogLambdaQueryWrapper.eq(PjTaskLog::getPjTaskId, task.getId());
                Long logSize = pjTaskLogMapper.selectCount(taskLogLambdaQueryWrapper);
                LambdaQueryWrapper<PjTaskComment> taskCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                taskCommentLambdaQueryWrapper.eq(PjTaskComment::getPjTaskId, task.getId());
                Long commentSize = pjTaskCommentMapper.selectCount(taskCommentLambdaQueryWrapper);
                task.setLogAndCommentSize(logSize.intValue() + commentSize.intValue());
            });
            pjTaskListVO.setTaskList(pjTasks);
            pjTaskListVO.setShowAddTask(false); //默认不显示添加任务按钮

            result.add(pjTaskListVO);
        });
        // 返回结果
        return result;
    }

    // 删除项目任务列表时，先检查是否有子任务
    @Override
    public boolean hasTask(Long taskStageId) {
        LambdaQueryWrapper<PjTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTask::getPjTaskStagesId, taskStageId);
        return pjTaskMapper.selectCount(queryWrapper) > 0;
    }

    // 新增任务
    @Override
    public AjaxResult addTask(PjTaskAddDTO pjTaskAddDTO) {
        Long taskStageId = pjTaskAddDTO.getTaskStageId();
        String name = pjTaskAddDTO.getName();
        Integer sortNum = pjTaskAddDTO.getSortNum();
        //新增
        PjTask pjTask = new PjTask();
        pjTask.setPjTaskStagesId(taskStageId);
        pjTask.setName(name);
        pjTask.setSortNum(sortNum);
        pjTask.setStatus(0);
        pjTask.setUrgency(0);
        this.save(pjTask);
        // 记录日志
        PjProjectLog pjProjectLog = new PjProjectLog();
        Long pjTaskStagesId = pjTask.getPjTaskStagesId();
        PjTaskStages pjTaskStages = pjTaskStagesMapper.selectById(pjTaskStagesId);
        pjProjectLog.setPjProjectId(pjTaskStages.getPjProjectId());
        pjProjectLog.setOperationType(OperationConstants.ADD);
        pjProjectLog.setType(ProjectTypeConstants.TASK);
        pjProjectLog.setContent(SecurityUtils.getUsername() + "新增任务：" + name);
        pjProjectLogMapper.insert(pjProjectLog);
        return AjaxResult.success();
    }

    // 根据id获取任务
    @Override
    public PjTask getTaskById(Long id) {
        PjTask pjTask = this.getById(id);
        Long assignId = pjTask.getProjectUserId();
        if (assignId != null) {
            SysUser sysUser = sysUserMapper.selectUserById(assignId);
            pjTask.setAssignUserName(sysUser.getNickName());
        }
        // 获取父任务
        if (pjTask.getParentId() != null) {
            PjTask parentTask = pjTaskMapper.selectById(pjTask.getParentId());
            pjTask.setParentName(parentTask.getName());
        }
        // 子任务
        LambdaQueryWrapper<PjTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTask::getParentId, id);
        List<PjTask> pjTasks = pjTaskMapper.selectList(queryWrapper);
        pjTask.setChildTaskList(pjTasks);
        // 标签
        List<PjTaskTag> tagList = pjTaskTagService.getListByTaskId(id);
        pjTask.setTagList(tagList);
        // 日志和评论
        List<PjTaskLogAndCommentVO> logAndCommentList = new ArrayList<>();
        addTaskLog(id, logAndCommentList);
        addTaskComment(id, logAndCommentList);
        logAndCommentList = logAndCommentList.stream().sorted(Comparator.comparing(PjTaskLogAndCommentVO::getCreateTime).reversed()).collect(Collectors.toList());
        pjTask.setLogAndCommentList(logAndCommentList);
        // 文件
        LambdaQueryWrapper<PjTaskFile> taskFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        taskFileLambdaQueryWrapper.eq(PjTaskFile::getPjTaskId, id);
        List<PjTaskFile> pjTaskFiles = pjTaskFileMapper.selectList(taskFileLambdaQueryWrapper);
        pjTask.setFileList(pjTaskFiles);
        pjTask.setFileNum(pjTaskFiles.size());
        // 获取点赞信息
        // 是否点赞
        LambdaQueryWrapper<PjTaskLike> taskLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        taskLikeLambdaQueryWrapper.eq(PjTaskLike::getPjTaskId, id);
        taskLikeLambdaQueryWrapper.eq(PjTaskLike::getUserId, SecurityUtils.getUserId());
        PjTaskLike pjTaskLike = pjTaskLikeMapper.selectOne(taskLikeLambdaQueryWrapper);
        if (pjTaskLike != null) {
            pjTask.setIsLike(1);
        } else {
            pjTask.setIsLike(0);
        }
        // 点赞数量
        Long likeCount = pjTaskLikeMapper.selectLikeCountByTaskId(id);
        pjTask.setLikeNum(likeCount.intValue());
        // 评论数量
        List<PjTaskComment> pjTaskComments = pjTaskCommentMapper.selectList(new LambdaQueryWrapper<PjTaskComment>().eq(PjTaskComment::getPjTaskId, id));
        pjTask.setCommentNum(pjTaskComments.size());
        // 项目id
        PjProject pjProject = pjProjectMapper.selectPjProjectByTaskId(id);
        pjTask.setPjProjectId(pjProject.getId());
        return pjTask;
    }

    private void addTaskLog(Long id, List<PjTaskLogAndCommentVO> list) {
        List<PjTaskLog> pjTaskLogs = pjTaskLogMapper.selectList(new LambdaQueryWrapper<PjTaskLog>().eq(PjTaskLog::getPjTaskId, id).ne(PjTaskLog::getType, TaskTypeConstants.COMMENT));
        for (PjTaskLog pjTaskLog : pjTaskLogs) {
            PjTaskLogAndCommentVO pjTaskLogAndCommentVO = new PjTaskLogAndCommentVO();

            pjTaskLogAndCommentVO.setType(pjTaskLog.getType());
            pjTaskLogAndCommentVO.setOperationType(pjTaskLog.getOperationType());
            pjTaskLogAndCommentVO.setContent(pjTaskLog.getContent());
            pjTaskLogAndCommentVO.setCreateBy(pjTaskLog.getCreateBy());
            pjTaskLogAndCommentVO.setCreateTime(pjTaskLog.getCreateTime());
            pjTaskLogAndCommentVO.setIsLog(1);

            list.add(pjTaskLogAndCommentVO);
        }
    }

    private void addTaskComment(Long id, List<PjTaskLogAndCommentVO> list) {
        List<PjTaskComment> pjTaskComments = pjTaskCommentMapper.selectList(new LambdaQueryWrapper<PjTaskComment>().eq(PjTaskComment::getPjTaskId, id));
        for (PjTaskComment pjTaskComment : pjTaskComments) {
            PjTaskLogAndCommentVO pjTaskLogAndCommentVO = new PjTaskLogAndCommentVO();

            pjTaskLogAndCommentVO.setPjTaskCommentId(pjTaskComment.getId());
            pjTaskLogAndCommentVO.setContent(pjTaskComment.getContent());

            Long createId = pjTaskComment.getCreateId();
            SysUser sysUser = sysUserMapper.selectUserById(createId);

            pjTaskLogAndCommentVO.setCreateBy(sysUser.getNickName());
            pjTaskLogAndCommentVO.setAvatar(sysUser.getAvatar());
            pjTaskLogAndCommentVO.setCreateTime(pjTaskComment.getCreateTime());
            pjTaskLogAndCommentVO.setIsLog(0);
            list.add(pjTaskLogAndCommentVO);
        }
    }

    // 更新任务
    @Override
    public void updateTaskById(PjTask pjTask) {
        // 设置状态信息
        Integer status = pjTask.getStatus();
        if (status == 2) {
            pjTask.setRealEndTime(new Date());
        } else {
            pjTask.setRealEndTime(null);
        }
        // 更新任务
        pjTaskMapper.updateById(pjTask);
        // 记录日志
        switch (pjTask.getTaskType()) {
            case TaskTypeConstants.NAME:
                addTaskLogByUpdate(SecurityUtils.getUsername() + "更新任务名称为：" + status, pjTask);
                break;
            case TaskTypeConstants.STATUS:
                String statusStr = status == 0 ? "未开始" : status == 1 ? "正在进行" : "已完成";
                addTaskLogByUpdate(SecurityUtils.getUsername() + "更新任务状态为：" + statusStr, pjTask);
                // 记录项目日志
                addProjectLog(SecurityUtils.getUsername() + "更新<" + pjTask.getName() + ">任务状态为：" + statusStr, pjTask);
                break;
            case TaskTypeConstants.URGENCY:
                String urgencyStr = pjTask.getUrgency() == 0 ? "一般" : pjTask.getUrgency() == 1 ? "紧急" : "非常紧急";
                addTaskLogByUpdate(SecurityUtils.getUsername() + "更新任务紧急程度为：" + urgencyStr, pjTask);
                // 记录项目日志
                addProjectLog(SecurityUtils.getUsername() + "更新<" + pjTask.getName() + ">任务紧急程度为：" + urgencyStr, pjTask);
                break;
            case TaskTypeConstants.ORDER:
                addTaskLogByUpdate(SecurityUtils.getUsername() + "更新任务排序为：" + pjTask.getSortNum(), pjTask);
                break;
            case TaskTypeConstants.DESCRIPTION:
                addTaskLogByUpdate(SecurityUtils.getUsername() + "更新了任务描述", pjTask);
                break;
            case TaskTypeConstants.ASSIGN:
                Long projectUserId = pjTask.getProjectUserId();
                SysUser sysUser = sysUserMapper.selectUserById(projectUserId);
                addTaskLogByUpdate(SecurityUtils.getUsername() + "更新任务执行人为：" + sysUser.getNickName(), pjTask);
                // 记录项目日志
                addProjectLog(SecurityUtils.getUsername() + "更新<" + pjTask.getName() + ">任务执行人为：" + sysUser.getNickName(), pjTask);
                break;
            case TaskTypeConstants.TIME:
                addTaskLogByUpdate(SecurityUtils.getUsername() + "更新任务时间范围为：" + dateFormat(pjTask.getBeginTime()) + "至" + dateFormat(pjTask.getEndTime()), pjTask);
                break;
            case TaskTypeConstants.CHILD_TASK:
                String statusStrF = status == 0 ? "未开始" : status == 1 ? "正在进行" : "已完成";
                addTaskLogByUpdate(SecurityUtils.getUsername() + "更新任务状态为：" + statusStrF, pjTask);
                pjTask.setId(pjTask.getParentId());
                addTaskLogByUpdate(SecurityUtils.getUsername() + "更新子任务<" + pjTask.getName() + ">状态为：" + statusStrF, pjTask);
                break;
        }
    }

    // 新增子任务
    @Override
    public AjaxResult addChildTask(PjTaskChildAddDTO pjTaskChildAddDTO) {
        Long parentId = pjTaskChildAddDTO.getParentId();
        PjTask pjTaskByParentId = this.getById(parentId);
        String taskName = pjTaskChildAddDTO.getTaskName();

        PjTask pjTask = new PjTask();
        pjTask.setParentId(parentId);
        pjTask.setName(taskName);
        pjTask.setStatus(0);
        pjTask.setSortNum(0);
        pjTask.setUrgency(0);
        pjTask.setPjTaskStagesId(pjTaskByParentId.getPjTaskStagesId());

        int insert = pjTaskMapper.insert(pjTask);
        // 记录日志
        PjTaskLog pjTaskLog = new PjTaskLog();
        pjTaskLog.setPjTaskId(pjTask.getId());
        pjTaskLog.setType(TaskTypeConstants.CHILD_TASK);
        pjTaskLog.setOperationType(OperationConstants.ADD);
        pjTaskLog.setContent(SecurityUtils.getUsername() + "新增子任务：" + taskName + "\uD83C\uDF89");
        return AjaxResult.success(insert);
    }

    @Override
    public boolean hasChildTask(Long parentId) {
        LambdaQueryWrapper<PjTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTask::getParentId, parentId);
        queryWrapper.ne(PjTask::getStatus, 2);
        queryWrapper.last("LIMIT 1"); // 限制只查询一条记录，因为我们只需要知道是否存在子任务
        return !pjTaskMapper.selectList(queryWrapper).isEmpty();
    }

    @Override
    public List<PjTaskLogAndCommentVO> getTaskLog(Long taskId) {
        List<PjTaskLogAndCommentVO> logAndCommentList = new ArrayList<>();
        addTaskLog(taskId, logAndCommentList);
        return logAndCommentList;
    }

    @Override
    public List<PjTaskLogAndCommentVO> getTaskComment(Long taskId) {
        List<PjTaskLogAndCommentVO> logAndCommentList = new ArrayList<>();
        addTaskComment(taskId, logAndCommentList);
        return logAndCommentList;
    }

    @Override
    public List<PjTaskRecycleVO> getTaskRecycle(Long projectId) {
        return pjTaskMapper.getTaskRecycle(projectId);
    }

    @Override
    public int realRemoveById(Long[] ids) {
        for (Long id : ids) {
            realDeleteTask(id);
        }
        return 1;
    }

    private void realDeleteTask(Long id) {
        PjTask pjTask = pjTaskMapper.getTaskById(id);
        PjTaskLog pjTaskLog = new PjTaskLog();
        pjTaskLog.setPjTaskId(id);
        pjTaskLog.setOperationType(OperationConstants.REAL_DELETE);
        pjTaskLog.setType(ProjectTypeConstants.TASK);
        pjTaskLog.setContent(SecurityUtils.getUsername() + "彻底删除了任务<" + pjTask.getName() + ">");
        pjTaskLogMapper.insert(pjTaskLog);
        loopRemoveTask(id);
    }

    @Override
    public int revoverTask(Long[] ids) {
        for (Long id : ids) {
            recoverTask(id);
        }
        return 1;
    }

    private void recoverTask(Long id) {
        pjTaskMapper.recoverTask(id); // 恢复任务
        PjTask pjTask = pjTaskMapper.selectById(id);
        PjTaskLog pjTaskLog = new PjTaskLog();
        pjTaskLog.setPjTaskId(id);
        pjTaskLog.setOperationType(OperationConstants.ENABLE);
        pjTaskLog.setType(ProjectTypeConstants.TASK);
        pjTaskLog.setContent(SecurityUtils.getUsername() + "恢复任务<" + pjTask.getName() + ">");
        pjTaskLogMapper.insert(pjTaskLog);
    }

    @Override
    public List<PjTaskFileVO> getFileRecycle(Long projectId) {
        List<PjTaskFileVO> pjTaskFileVOS = pjTaskMapper.getFileRecycle(projectId);
        // 文件大小转换
        pjTaskFileVOS.forEach(item -> {
            int i = Integer.parseInt(item.getFileSize());
            item.setFileSize(changeFileSize((long) i));
        });
        return pjTaskFileVOS;
    }

    @Override
    public int recoverFile(Long[] ids) {
        for (Long id : ids) {
            PjTaskFile pjTaskFile = pjTaskFileMapper.getByTaskFileById(id);
            PjTask pjTask = pjTaskMapper.selectById(pjTaskFile.getPjTaskId());
            PjTaskLog pjTaskLog = new PjTaskLog();
            pjTaskLog.setPjTaskId(pjTask.getId());
            pjTaskLog.setOperationType(OperationConstants.ENABLE);
            pjTaskLog.setType(ProjectTypeConstants.FILE);
            pjTaskLog.setContent(SecurityUtils.getUsername() + "恢复了任务文件<" + pjTaskFile.getFileName() + ">");
            pjTaskLogMapper.insert(pjTaskLog);
            pjTaskFileMapper.recoverFile(id);
        }
        return 1;
    }

    @Override
    public int realDeleteFileById(Long[] ids) {

        for (Long id : ids) {
            PjTaskFile pjTaskFile = pjTaskFileMapper.getByTaskFileById(id);
            String physicalPath = pjTaskFile.getPhysicalPath();
            // 直接删除物理文件
            if (!ObjectUtils.isEmpty(physicalPath)) {
                File file = new File(physicalPath);
                if (file.exists()) {
                    boolean delete = file.delete();
                }
            }
            PjTask pjTask = pjTaskMapper.selectById(pjTaskFile.getPjTaskId());
            PjTaskLog pjTaskLog = new PjTaskLog();
            pjTaskLog.setPjTaskId(pjTask.getId());
            pjTaskLog.setOperationType(OperationConstants.REAL_DELETE);
            pjTaskLog.setType(ProjectTypeConstants.FILE);
            pjTaskLog.setContent(SecurityUtils.getUsername() + "彻底删除了任务文件<" + pjTaskFile.getFileName() + ">");
            pjTaskLogMapper.insert(pjTaskLog);
            pjTaskFileMapper.deleteFileByFileid(id);
        }
        return 1;
    }

    @Override
    public MpPageVO getTaskList(PjTask pjTask) {
        MpPageVO mpPageVO = new MpPageVO();

        pjTask.setStart(pjTask.getStart() - 1);
        List<Long> seProjectIds = seUtils.getSeProjectIds();
        if (seProjectIds.isEmpty()) {
            return mpPageVO;
        }
        pjTask.setSeProjectIds(seProjectIds);
        List<PjTask> list = pjTaskMapper.getTaskList(pjTask);// 获取任务列表
        list.forEach(item -> {
            Long assignId = item.getProjectUserId();
            if (assignId != null) {
                SysUser sysUser = sysUserMapper.selectUserById(assignId);
                item.setAssignUserName(sysUser.getNickName());
            }
        });
        Long total = pjTaskMapper.getTaskListCount(pjTask);// 获取任务总数
        mpPageVO.setTotal(total);
        setTask(list);
        mpPageVO.setRows(list);
        return mpPageVO;
    }

    // 获取回收站任务
    @Override
    public MpPageVO getRecycleTask(PjTask pjTask) {
        MpPageVO mpPageVO = new MpPageVO();
        List<Long> seProjectIds = seUtils.getSeProjectIds();
        if (seProjectIds.isEmpty()) {
            return mpPageVO;
        }
        pjTask.setStart(pjTask.getStart() - 1);
        pjTask.setSeProjectIds(seProjectIds);
        List<PjTask> list = pjTaskMapper.getRecycleTask(pjTask); // 获取回收站任务
        setTask(list);
        Long total = pjTaskMapper.getAllRecycleTaskCount(pjTask);// 获取回收站任务总数
        mpPageVO.setRows(list);
        mpPageVO.setTotal(total);
        return mpPageVO;
    }

    // 根据项目id获取任务
    @Override
    public List<PjTask> listByProjectId(Long projectId) {
        LambdaQueryWrapper<PjTaskStages> taskStagesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        taskStagesLambdaQueryWrapper.eq(PjTaskStages::getPjProjectId, projectId);
        List<PjTaskStages> pjTaskStages = pjTaskStagesMapper.selectList(taskStagesLambdaQueryWrapper);
        List<Long> taskStagesIds = pjTaskStages.stream().map(PjTaskStages::getId).collect(Collectors.toList());
        LambdaQueryWrapper<PjTask> queryWrapper = new LambdaQueryWrapper<>();
        if (taskStagesIds.isEmpty()) {
            return new ArrayList<>();
        }
        queryWrapper.in(PjTask::getPjTaskStagesId, taskStagesIds);
        return pjTaskMapper.selectList(queryWrapper);
    }

    @Override
    public PjTask selectTaskById(Long id) {
        return pjTaskMapper.selectTaskById(id);
    }

    //  根据任务id获取任务阶段
    @Override
    public String getTaskStageByTaskId(Long taskId) {
        PjTask pjTask = pjTaskMapper.selectById(taskId);
        if (pjTask != null) {
            PjTaskStages pjTaskStages = pjTaskStagesMapper.selectById(pjTask.getPjTaskStagesId());
            if (pjTaskStages != null) {
                String name = pjTaskStages.getName();
                return getParentName(pjTask, name);
            }
        }
        return "";
    }

    private String getParentName(PjTask pjTask, String name) {
        if (pjTask.getParentId() != null) {
            PjTask parentTask = pjTaskMapper.selectById(pjTask.getParentId());
            name = name + " > " + parentTask.getName();
            return getParentName(parentTask, name);
        } else {
            return name;
        }
    }

    private void setTask(List<PjTask> list) {
        list.forEach(item -> {
            LambdaQueryWrapper<PjTaskLike> taskLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            taskLikeLambdaQueryWrapper.eq(PjTaskLike::getPjTaskId, item.getId());
            Long taskLikeNum = pjTaskLikeMapper.selectCount(taskLikeLambdaQueryWrapper);
            item.setLikeNum(taskLikeNum.intValue()); // 点赞数
            LambdaQueryWrapper<PjTaskComment> taskCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            taskCommentLambdaQueryWrapper.eq(PjTaskComment::getPjTaskId, item.getId());
            Long taskCommentNum = pjTaskCommentMapper.selectCount(taskCommentLambdaQueryWrapper);
            item.setLogAndCommentSize(taskCommentNum.intValue()); // 评论数
            LambdaQueryWrapper<PjTaskFile> taskFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            taskFileLambdaQueryWrapper.eq(PjTaskFile::getPjTaskId, item.getId());
            Long taskFileNum = pjTaskFileMapper.selectCount(taskFileLambdaQueryWrapper);
            item.setFileNum(taskFileNum.intValue()); // 文件数
        });
    }

    private String changeFileSize(Long fileSize) {
        if (fileSize < 1024) {
            return fileSize + "B";
        } else if (fileSize < 1024 * 1024) {
            return fileSize / 1024 + "KB";
        } else if (fileSize < 1024 * 1024 * 1024) {
            return fileSize / 1024 / 1024 + "MB";
        } else {
            return fileSize / 1024 / 1024 / 1024 + "GB";
        }
    }

    private void loopRemoveTask(Long id) {
        LambdaQueryWrapper<PjTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTask::getParentId, id);
        List<PjTask> list = pjTaskMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            for (PjTask pjTask : list) {
                loopRemoveTask(pjTask.getId());
            }
        } else {
            realRemoveTaskAndOther(id);
        }
    }

    private void realRemoveTaskAndOther(Long id) {
        // 删除任务
        int row1 = pjTaskMapper.realDeleteTaskById(id);
        // 删除任务标签
        LambdaQueryWrapper<PjTaskTag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(PjTaskTag::getPjTaskId, id);
        boolean remove = pjTaskTagService.remove(tagLambdaQueryWrapper);
        // 任务日志（不删除）
        // 删除点赞表
        LambdaQueryWrapper<PjTaskLike> taskLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        taskLikeLambdaQueryWrapper.eq(PjTaskLike::getPjTaskId, id);
        pjTaskLikeMapper.delete(taskLikeLambdaQueryWrapper);
        // 删除任务评论
        LambdaQueryWrapper<PjTaskComment> taskCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        taskCommentLambdaQueryWrapper.eq(PjTaskComment::getPjTaskId, id);
        pjTaskCommentMapper.delete(taskCommentLambdaQueryWrapper);
        // 删除任务文件
        LambdaQueryWrapper<PjTaskFile> taskFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        taskFileLambdaQueryWrapper.eq(PjTaskFile::getPjTaskId, id);
        List<PjTaskFile> pjTaskFiles = pjTaskFileMapper.selectList(taskFileLambdaQueryWrapper);
        // 直接删除物理文件
        pjTaskFiles.forEach(item -> {
            String physicalPath = item.getPhysicalPath();
            if (!ObjectUtils.isEmpty(physicalPath)) {
                File file = new File(physicalPath);
                if (file.exists()) {
                    boolean delete = file.delete();
                }
            }
        });
        int i = pjTaskFileMapper.deleteFileByTaskId(id);
    }

    private void addTaskLogByUpdate(String content, PjTask pjTask) {
        PjTaskLog pjTaskLog = new PjTaskLog();
        pjTaskLog.setPjTaskId(pjTask.getId());
        pjTaskLog.setType(pjTask.getTaskType());
        pjTaskLog.setOperationType(OperationConstants.UPDATE);
        pjTaskLog.setContent(content);
        pjTaskLogMapper.insert(pjTaskLog);
    }

    private String dateFormat(Date date) {
        // 指定日期时间格式
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 格式化日期对象
        return formatter.format(date);
    }

    private void addProjectLog(String content, PjTask pjTask) {
        PjProjectLog pjProjectLog = new PjProjectLog();
        PjTaskStages pjTaskStages = pjTaskStagesMapper.selectById(pjTask.getPjTaskStagesId());
        pjProjectLog.setPjProjectId(pjTaskStages.getPjProjectId());
        pjProjectLog.setOperationType(OperationConstants.UPDATE);
        pjProjectLog.setType(ProjectTypeConstants.TASK);
        pjProjectLog.setContent(content);
        pjProjectLogMapper.insert(pjProjectLog);
    }
}
