package cn.oneao.project.controller;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.TaskTypeConstants;
import cn.oneao.project.domain.PjTask;
import cn.oneao.project.domain.PjTaskComment;
import cn.oneao.project.domain.PjTaskLog;
import cn.oneao.project.service.IPjTaskCommentService;
import cn.oneao.project.service.IPjTaskLogService;
import cn.oneao.project.service.IPjTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/taskComment")
@Validated
public class PjTaskCommentController {
    @Autowired
    private IPjTaskCommentService pjTaskCommentService;
    @Autowired
    private IPjTaskLogService pjTaskLogService;
    @PreAuthorize("@ss.hasPermi('project:taskComment:add')")
    @PostMapping
    public AjaxResult addTaskComment(@RequestBody PjTaskComment pjTaskComment) {
        Long userId = SecurityUtils.getUserId();
        pjTaskComment.setCreateId(userId);
        boolean save = pjTaskCommentService.save(pjTaskComment);

        PjTaskLog pjTaskLog = new PjTaskLog();
        pjTaskLog.setPjTaskId(pjTaskComment.getPjTaskId());
        pjTaskLog.setOperationType(OperationConstants.ADD);
        pjTaskLog.setType(TaskTypeConstants.COMMENT);
        pjTaskLog.setContent(SecurityUtils.getUsername() + "添加了一条评论");
        return AjaxResult.success(save);
    }
    @PreAuthorize("@ss.hasPermi('project:taskComment:remove')")
    @DeleteMapping("/{commentId}")
    public AjaxResult removeTaskComment(@PathVariable("commentId") Long commentId){
        return AjaxResult.success(pjTaskCommentService.removeById(commentId));
    }
}
