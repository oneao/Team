package cn.oneao.project.controller;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.domain.entity.SysUser;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.domain.*;
import cn.oneao.project.domain.vo.project.PjIndexVO;
import cn.oneao.project.domain.vo.project.ProjectIndexLogVO;
import cn.oneao.project.service.*;
import cn.oneao.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/project/index")
public class PjIndexController {
    @Autowired
    private IPjUserService pjUserService;
    @Autowired
    private IPjProjectService pjProjectService;
    @Autowired
    private IPjTaskService pjTaskService;
    @Autowired
    private IPjTaskStagesService pjTaskStagesService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IPjTaskLogService pjTaskLogService;
    @Autowired
    private IPjProjectLogService pjProjectLogService;
    @GetMapping
    public AjaxResult getIndex(){
        PjIndexVO result = new PjIndexVO();
        // 获取当前用户信息
        Long userId = SecurityUtils.getUserId();
        SysUser sysUser = sysUserService.selectUserById(userId);
        result.setSysUser(sysUser);
        // 查询该用户下面的项目
        LambdaQueryWrapper<PjUser> pjUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pjUserLambdaQueryWrapper.eq(PjUser::getUserId,userId);
        List<PjUser> pjUserList = pjUserService.list(pjUserLambdaQueryWrapper);
        List<Long> projectIdList = pjUserList.stream().map(PjUser::getPjProjectId).collect(Collectors.toList());
        // 查询项目信息
        LambdaQueryWrapper<PjProject> pjProjectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pjProjectLambdaQueryWrapper.in(PjProject::getId,projectIdList);
        List<PjProject> pjProjectList = pjProjectService.list(pjProjectLambdaQueryWrapper);
        result.setProjectList(pjProjectList);
        // 查询所有任务
        LambdaQueryWrapper<PjTaskStages> pjTaskStagesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pjTaskStagesLambdaQueryWrapper.in(PjTaskStages::getPjProjectId,projectIdList);
        List<PjTaskStages> pjTaskStagesList = pjTaskStagesService.list(pjTaskStagesLambdaQueryWrapper);
        List<Long> pjTaskStageIdList = pjTaskStagesList.stream().map(PjTaskStages::getId).collect(Collectors.toList());
        LambdaQueryWrapper<PjTask> pjTaskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pjTaskLambdaQueryWrapper.in(PjTask::getPjTaskStagesId,pjTaskStageIdList);
        List<PjTask> pjTaskList = pjTaskService.list(pjTaskLambdaQueryWrapper);   // 任务列表
        Integer allCount = pjTaskList.size();
        result.setAllCount(allCount);
        Integer toDoCount = (int) pjTaskList.stream().filter(pjTask -> pjTask.getStatus() != 2).count();
        result.setToDoCount(toDoCount);
        List<Long> pjTaskIdList = pjTaskList.stream().map(PjTask::getId).collect(Collectors.toList());
        // 查询相关日志
        List<ProjectIndexLogVO> projectIndexLogVOList = new ArrayList<>();
        projectIndexLogVOList = pjTaskLogService.selectIndexLog(pjTaskIdList,projectIdList);
        projectIndexLogVOList.sort(Comparator.comparing(ProjectIndexLogVO::getCreateTime).reversed());
        // 截取前20条记录
        if (projectIndexLogVOList.size() > 20) {
            projectIndexLogVOList = projectIndexLogVOList.subList(0, 20);
        }
        result.setLogList(projectIndexLogVOList);
        return AjaxResult.success(result);
    }
}
