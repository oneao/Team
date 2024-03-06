package cn.oneao.project.controller;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.TaskTypeConstants;
import cn.oneao.project.domain.PjTaskLog;
import cn.oneao.project.domain.PjTaskTag;
import cn.oneao.project.mapper.PjTaskLogMapper;
import cn.oneao.project.service.IPjTaskTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/taskTag")
@Slf4j
public class PjTaskTagController {
    @Autowired
    private IPjTaskTagService pjTaskTagService;
    @Autowired
    private PjTaskLogMapper pjTaskLogMapper;
    /**
     * 新增任务标签
     * @param pjTaskTag 任务标签信息
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('project:taskTag:add')")
    @PostMapping
    public AjaxResult addTaskTag(@RequestBody PjTaskTag pjTaskTag) {
        boolean flag = pjTaskTagService.save(pjTaskTag);
        // 记录日志
        PjTaskLog pjTaskLog = new PjTaskLog();
        pjTaskLog.setType(OperationConstants.ADD);
        pjTaskLog.setOperationType(TaskTypeConstants.TAG);
        pjTaskLog.setPjTaskId(pjTaskTag.getPjTaskId());
        // 新增任务标签
        pjTaskLog.setContent(SecurityUtils.getUsername() + " 🏷️ 新增任务标签：" + pjTaskTag.getName());
        pjTaskLogMapper.insert(pjTaskLog);
        return AjaxResult.success(flag);
    }
    /**
     * 删除任务标签
     * @param id 任务标签id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('project:taskTag:remove')")
    @DeleteMapping("/{id}")
    public AjaxResult addTaskTag(@PathVariable("id") Long id) {
        PjTaskTag pjTaskTag = pjTaskTagService.getById(id);
        // 记录日志
        PjTaskLog pjTaskLog = new PjTaskLog();
        pjTaskLog.setType(OperationConstants.ADD);
        pjTaskLog.setOperationType(TaskTypeConstants.TAG);
        pjTaskLog.setPjTaskId(pjTaskTag.getPjTaskId());
        // 删除任务标签
        pjTaskLog.setContent(SecurityUtils.getUsername() + " ❌ 删除任务标签：" + pjTaskTag.getName());
        pjTaskLogMapper.insert(pjTaskLog);
        return AjaxResult.success(pjTaskTagService.removeById(id));
    }

}
