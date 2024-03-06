package cn.oneao.project.controller;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.domain.R;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.annotations.CheckIsProjectUser;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.ProjectTypeConstants;
import cn.oneao.project.constants.TaskTypeConstants;
import cn.oneao.project.domain.*;
import cn.oneao.project.domain.dto.task.PjTaskAddDTO;
import cn.oneao.project.domain.dto.task.PjTaskChildAddDTO;
import cn.oneao.project.domain.vo.task.PjTaskListVO;
import cn.oneao.project.service.IPjProjectService;
import cn.oneao.project.service.IPjTaskLikeService;
import cn.oneao.project.service.IPjTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/project/task")
@Slf4j
@Validated
public class PjTaskController {
    @Autowired
    private IPjTaskService pjTaskService;
    @Autowired
    private IPjTaskLikeService pjTaskLikeService;
    @Autowired
    private IPjProjectService pjProjectService;
    /**
     * 根据项目id查询任务列表
     *
     * @param projectId 项目id
     * @return 任务列表
     */
//    @PreAuthorize("@ss.hasPermi('project:task:list')")
    @GetMapping("/list")
    @CheckIsProjectUser
    public AjaxResult listUser(@NotNull(message = "项目id不能为空") @PositiveOrZero(message = "项目id有误") @RequestParam("projectId") Long projectId) {
        List<PjTaskListVO> list = pjTaskService.getTaskListByProjectId(projectId);
        return AjaxResult.success(list);
    }
    @GetMapping("/listByProjectId/{projectId}")
    public AjaxResult getListByProjectId(@NotNull(message = "项目id不能为空") @PositiveOrZero(message = "项目id有误") @PathVariable("projectId") Long projectId) {
        return AjaxResult.success(pjTaskService.listByProjectId(projectId));
    }
    /**
     * 新增任务
     *
     * @param pjTaskAddDTO 任务信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('project:task:add')")
    @PostMapping
    public AjaxResult addTask(@RequestBody PjTaskAddDTO pjTaskAddDTO) {
        Long taskStageId = pjTaskAddDTO.getTaskStageId();
        String name = pjTaskAddDTO.getName();
        LambdaQueryWrapper<PjTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTask::getPjTaskStagesId, taskStageId);
        queryWrapper.eq(PjTask::getName, name);
        PjTask pjTask = pjTaskService.getOne(queryWrapper);
        if (pjTask != null) {
            return AjaxResult.error("任务名称已存在");
        }
        return AjaxResult.success(pjTaskService.addTask(pjTaskAddDTO));
    }

    /**
     * 获取任务详细信息
     *
     * @param id 任务id
     * @return 任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('project:task:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult queryById(@PositiveOrZero(message = "获取任务详细信息有误，原因：传入id有误") @PathVariable("id") Long id) {
        PjTask pjTask = pjTaskService.getTaskById(id);
        return AjaxResult.success(pjTask);
    }

    /**
     * 更新任务详细信息
     *
     * @param pjTask 任务信息
     * @return 任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('project:task:update')")
    @PutMapping
    public AjaxResult update(@RequestBody PjTask pjTask) {
        String name = pjTask.getName();
        Long id = pjTask.getId();
        PjTask taskServiceById = pjTaskService.getById(id);
        if (taskServiceById != null && !name.equals(taskServiceById.getName())) {
            LambdaQueryWrapper<PjTask> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PjTask::getPjTaskStagesId, taskServiceById.getPjTaskStagesId());
            queryWrapper.eq(PjTask::getName, name);
            PjTask task = pjTaskService.getOne(queryWrapper);
            if (task != null) {
                return AjaxResult.error("任务名称已存在");
            }
        }
        if (pjTask.getTaskType().equals(TaskTypeConstants.STATUS) && pjTask.getStatus() == 2 && pjTaskService.hasChildTask(id)) {
            return AjaxResult.error("该任务下还有子任务未完成，不能修改为已完成状态");
        }
        pjTaskService.updateTaskById(pjTask);
        return AjaxResult.success();
    }

    /**
     * 新增子任务
     *
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('project:task:addChildTask')")
    @PostMapping("/addChildTask")
    public AjaxResult addChildTask(@RequestBody PjTaskChildAddDTO pjTaskChildAddDTO) {
        String taskName = pjTaskChildAddDTO.getTaskName();
        Long parentId = pjTaskChildAddDTO.getParentId();
        PjTask pjTask = pjTaskService.getById(parentId);
        if (pjTask.getName().equals(taskName)) {
            return AjaxResult.error("子任务不能与夫任务名称相同");
        }
        LambdaQueryWrapper<PjTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTask::getParentId, parentId);
        List<PjTask> list = pjTaskService.list(queryWrapper);
        for (PjTask task : list) {
            if (task.getName().equals(taskName)) {
                return AjaxResult.error("子任务列表中已存在该任务名称");
            }
        }
        if (pjTask.getStatus() == 2) {
            return AjaxResult.error("父任务已完成，不能新增子任务");
        }

        return AjaxResult.success(pjTaskService.addChildTask(pjTaskChildAddDTO));
    }

    /**
     * 获取任务日志
     *
     * @param taskId 任务id
     * @return 任务日志
     */
    @GetMapping("/getTaskLog")
    public AjaxResult getTaskLog(@NotNull(message = "任务id不能为空") @PositiveOrZero(message = "任务id有误") @RequestParam("taskId") Long taskId) {
        return AjaxResult.success(pjTaskService.getTaskLog(taskId));
    }

    /**
     * 获取任务评论
     *
     * @param taskId 任务id
     * @return 任务评论
     */
    @GetMapping("/getTaskComment")
    public AjaxResult getTaskComment(@NotNull(message = "任务id不能为空") @PositiveOrZero(message = "任务id有误") @RequestParam("taskId") Long taskId) {
        return AjaxResult.success(pjTaskService.getTaskComment(taskId));
    }

    /**
     * 删除任务
     *
     * @param id 任务id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('project:task:remove')")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PositiveOrZero(message = "删除任务有误，原因：传入id有误") @PathVariable("id") Long id) {
        if (pjTaskService.hasChildTask(id)) {
            return AjaxResult.error("该任务下还有子任务未完成，不能删除");
        }
        pjTaskService.removeById(id);
        PjProject pjProject = pjProjectService.selectPjProjectByTaskId(id);
        PjProjectLog pjProjectLog = new PjProjectLog();
        pjProjectLog.setPjProjectId(pjProject.getId());
        pjProjectLog.setType(ProjectTypeConstants.TASK);
        pjProjectLog.setOperationType(OperationConstants.DELETE);
        PjTask pjTask = pjTaskService.selectTaskById(id);
        pjProjectLog.setContent(SecurityUtils.getUsername() + "删除了任务<" + pjTask.getName()+">");
        return AjaxResult.success();
    }
    // 删除任务列表
    @DeleteMapping("removeTaskList/{ids}")
    public AjaxResult removeTask(@PathVariable Long[] ids) {
        for (Long id : ids) {
            if (pjTaskService.hasChildTask(id)) {
                return AjaxResult.error("该任务下还有子任务未完成，不能删除");
            }
        }
        boolean b = pjTaskService.removeByIds(Arrays.asList(ids));
        for (Long id : ids) {
            PjProject pjProject = pjProjectService.selectPjProjectByTaskId(id);
            PjProjectLog pjProjectLog = new PjProjectLog();
            pjProjectLog.setPjProjectId(pjProject.getId());
            pjProjectLog.setType(ProjectTypeConstants.TASK);
            pjProjectLog.setOperationType(OperationConstants.DELETE);
            PjTask pjTask = pjTaskService.selectTaskById(id);
            pjProjectLog.setContent(SecurityUtils.getUsername() + "删除了任务<" + pjTask.getName()+">");
        }
        return AjaxResult.success(b);
    }
    /**
     * 更新任务点赞数
     *
     * @param taskId 任务id
     * @return 结果
     */
    @PutMapping("/updateTaskLike/{taskId}")
    public AjaxResult updateTaskLike(@PathVariable Long taskId) {
        LambdaQueryWrapper<PjTaskLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTaskLike::getPjTaskId, taskId);
        queryWrapper.eq(PjTaskLike::getUserId, SecurityUtils.getUserId());
        PjTaskLike pjTaskLike = pjTaskLikeService.getOne(queryWrapper);
        if (pjTaskLike != null) {
            pjTaskLikeService.removeById(pjTaskLike.getId());
        } else {
            pjTaskLike = new PjTaskLike();
            pjTaskLike.setPjTaskId(taskId);
            pjTaskLike.setUserId(SecurityUtils.getUserId());
            pjTaskLikeService.save(pjTaskLike);
        }
        return AjaxResult.success();
    }

    /**
     * 获取任务回收站列表
     * @param projectId 项目id
     * @return 任务回收站列表
     */
    @GetMapping("/getRecycleTask/{projectId}")
    public AjaxResult getTaskRecycle(@PathVariable Long projectId) {
        return AjaxResult.success(pjTaskService.getTaskRecycle(projectId));
    }

    /**
     * 恢复任务
     * @param id 任务id
     * @return 结果
     */
    @PutMapping("/recoverTask/{id}")
    public AjaxResult recoverTask(@PathVariable Long[] id) {
        return AjaxResult.success(pjTaskService.revoverTask(id));
    }

    /**
     * 彻底删除任务
     * @param id 任务id
     * @return 结果
     */
    @DeleteMapping("/realDeleteTask/{id}")
    public AjaxResult deleteTask(@PathVariable Long[] id) {
        return AjaxResult.success(pjTaskService.realRemoveById(id));
    }

    /**
     * 获取回收站任务文件
     * @param projectId 项目id
     * @return 任务文件
     */
    @GetMapping("/getRecycleFile/{projectId}")
    public AjaxResult getFileRecycle(@PathVariable Long projectId) {
        return AjaxResult.success(pjTaskService.getFileRecycle(projectId));
    }

    /**
     * 恢复文件
     * @param id 文件id
     * @return 结果
     */
    @PutMapping("/recoverFile/{id}")
    public AjaxResult recoverFile(@PathVariable Long[] id){
        return AjaxResult.success(pjTaskService.recoverFile(id));
    }

    /**
     * 彻底删除文件
     * @param id 文件id
     * @return 结果
     */
    @DeleteMapping("/realDeleteFile/{id}")
    public AjaxResult deleteFile(@PathVariable Long[] id){
        return AjaxResult.success(pjTaskService.realDeleteFileById(id));
    }

    /**
     * 获取所有任务
     * @return 任务文件
     */
    @GetMapping("getTaskList")
    public AjaxResult getTaskList(PjTask pjTask){
        MpPageVO mpPageVO = pjTaskService.getTaskList(pjTask); // 已进行Se判断
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 获取回收站任务
     * @param pjTask 任务信息
     * @return 任务文件
     */
    @GetMapping("getRecycleTaskAll")
    public AjaxResult getRecycleTask(PjTask pjTask){
        MpPageVO mpPageVO = pjTaskService.getRecycleTask(pjTask);// 已进行Se判断
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 获取回收站文件
     * @param taskId 任务id
     * @return 任务文件
     */
    @GetMapping("getTaskStageByTaskId/{taskId}")
    public AjaxResult getTaskStageByTaskId(@PathVariable Long taskId) {
        return AjaxResult.success(pjTaskService.getTaskStageByTaskId(taskId));
    }
}
