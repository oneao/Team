package cn.oneao.project.controller;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.domain.entity.SysUser;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjBug;
import cn.oneao.project.service.IPjBugService;
import cn.oneao.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/project/bug")
public class PjBugController {
    @Autowired
    private IPjBugService pjBugService;
    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/getOption")
    public AjaxResult getOption() {
        return AjaxResult.success(pjBugService.getOption());
    }

    @GetMapping("/getAllOption")
    public AjaxResult getAllOption() {
        return AjaxResult.success(pjBugService.getAllOption());
    }

    @PostMapping
    public AjaxResult addBug(@RequestBody PjBug pjBug) {

        return AjaxResult.success(pjBugService.addBug(pjBug));
    }

    @GetMapping
    public AjaxResult list(PjBug pjBug) {
        MpPageVO mpPageVO = pjBugService.getList(pjBug);
        return AjaxResult.success(mpPageVO);
    }

    @GetMapping("/{id}")
    public AjaxResult getBug(@PathVariable Long id) {
        return AjaxResult.success(pjBugService.getById(id));
    }

    @PutMapping
    public AjaxResult updateBug(@RequestBody PjBug pjBug) {
        Long assignTo = pjBug.getAssignTo();
        Integer status = pjBug.getStatus();
        if (status == 1 && assignTo == null) {
            return AjaxResult.error("请选择BUG解决人");
        } else {
            pjBug.setSolveTime(new Date());
        }
        return AjaxResult.success(pjBugService.updateById(pjBug));
    }

    @DeleteMapping("/{bugIds}")
    public AjaxResult remove(@PathVariable Long[] bugIds) {
        return AjaxResult.success(pjBugService.removeByIds(Arrays.asList(bugIds)));
    }

    /**
     * 申报
     *
     * @return AjaxResult
     */
    @GetMapping("/declare")
    public AjaxResult getBugDeclare(PjBug pjBug) {
        MpPageVO mpPageVO = pjBugService.getBugDeclare(pjBug);
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 派发
     *
     * @return AjaxResult
     */
    @GetMapping("/distribute")
    public AjaxResult getBugDistribute(PjBug pjBug) {
        MpPageVO mpPageVO = pjBugService.getBugDistribute(pjBug);
        return AjaxResult.success(mpPageVO);
    }
}
