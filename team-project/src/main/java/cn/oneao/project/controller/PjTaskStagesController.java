package cn.oneao.project.controller;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.ProjectTypeConstants;
import cn.oneao.project.domain.PjProjectLog;
import cn.oneao.project.domain.PjTaskStages;
import cn.oneao.project.domain.dto.task.PjTaskStagesAddDTO;
import cn.oneao.project.domain.dto.task.PjTaskStagesUpdateDTO;
import cn.oneao.project.service.IPjProjectLogService;
import cn.oneao.project.service.IPjTaskService;
import cn.oneao.project.service.IPjTaskStagesService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/project/taskStages")
@Slf4j
public class PjTaskStagesController {
    @Autowired
    private IPjTaskStagesService pjTaskStagesService;
    @Autowired
    private IPjTaskService pjTaskService;
    @Autowired
    private IPjProjectLogService pjProjectLogService;
    /**
     * 新增任务阶段
     * @param pjTaskStagesAddDTO 任务阶段信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('project:taskStages:add')")
    @PostMapping
    public AjaxResult addTaskStage(@RequestBody PjTaskStagesAddDTO pjTaskStagesAddDTO) {
        Long projectId = pjTaskStagesAddDTO.getProjectId();
        String taskStageName = pjTaskStagesAddDTO.getTaskStageName();

        boolean flag = pjTaskStagesService.isHasSameName(taskStageName, projectId);
        if (flag) {
            return AjaxResult.error("任务阶段名称已存在");
        }
        return AjaxResult.success(pjTaskStagesService.addTaskStages(pjTaskStagesAddDTO));
    }

    /**
     * 更新任务阶段
     * @param pjTaskStagesUpdateDTO 任务阶段信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('project:taskStages:update')")
    @PutMapping
    public AjaxResult updateTaskStage(@RequestBody PjTaskStagesUpdateDTO pjTaskStagesUpdateDTO) {
        Long taskStageId = pjTaskStagesUpdateDTO.getTaskStageId();
        LambdaQueryWrapper<PjTaskStages> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTaskStages::getId, taskStageId);
        PjTaskStages pjTaskStages = pjTaskStagesService.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(pjTaskStages)) {
            return AjaxResult.error("任务阶段不存在,请刷新页面重试");
        }

        String taskStageName = pjTaskStagesUpdateDTO.getTaskStageName();
        if(taskStageName.equals(pjTaskStages.getName())){
            return AjaxResult.success(pjTaskStagesService.updateTaskStages(pjTaskStagesUpdateDTO));
        }
        if(pjTaskStagesService.isHasSameName(taskStageName, pjTaskStages.getPjProjectId())){
            return AjaxResult.error("任务阶段名称已存在");
        }
        // 记录日志
        PjProjectLog pjProjectLog = new PjProjectLog();
        pjProjectLog.setPjProjectId(pjTaskStages.getPjProjectId());
        pjProjectLog.setOperationType(OperationConstants.UPDATE);
        pjProjectLog.setType(ProjectTypeConstants.TASK_STAGE);
        pjProjectLog.setContent("更新任务阶段名称：" + pjTaskStages.getName() + " -> " + taskStageName);
        pjProjectLogService.save(pjProjectLog);
        return AjaxResult.success(pjTaskStagesService.updateTaskStages(pjTaskStagesUpdateDTO));
    }

    @PreAuthorize("@ss.hasPermi('project:taskStages:remove')")
    @DeleteMapping("/{taskStageId}")
    public AjaxResult removeTaskStage(@PathVariable @NotNull(message = "任务列表id不能为空") Long taskStageId) {
        boolean flag = pjTaskService.hasTask(taskStageId);
        if(flag) {
            return AjaxResult.error("该任务阶段下有任务,不能删除，请先删除任务后再删除任务阶段。");
        }
        // 记录日志
        PjTaskStages pjTaskStages = pjTaskStagesService.getById(taskStageId);
        PjProjectLog pjProjectLog = new PjProjectLog();
        pjProjectLog.setPjProjectId(pjTaskStages.getPjProjectId());
        pjProjectLog.setOperationType(OperationConstants.DELETE);
        pjProjectLog.setType(ProjectTypeConstants.TASK_STAGE);
        pjProjectLog.setContent("删除任务阶段：" + pjTaskStages.getName());
        pjProjectLogService.save(pjProjectLog);
        // 删除任务阶段
        return AjaxResult.success(pjTaskStagesService.removeById(taskStageId));
    }
}
