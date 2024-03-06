package cn.oneao.project.controller;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.domain.PjUser;
import cn.oneao.project.domain.dto.user.PjUserListDTO;
import cn.oneao.project.domain.dto.user.PjUserAddDTO;
import cn.oneao.project.domain.dto.user.PjUserRemoveDTO;
import cn.oneao.project.domain.dto.user.PjUserUpdateDTO;
import cn.oneao.project.domain.vo.user.PjUserAllVO;
import cn.oneao.project.domain.vo.user.PjUserSeVo;
import cn.oneao.project.service.IPjUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/project/user")
@Slf4j
public class PjUserController{
    @Autowired
    private IPjUserService pjUserService;

    /**
     * 根据项目id查询用户列表
     * @param projectId 项目id
     * @return 用户列表
     */
    @PreAuthorize("@ss.hasPermi('project:user:list')")
    @GetMapping("/list")
    public AjaxResult listUser(@PositiveOrZero(message = "项目id有误") @RequestParam("projectId") Long projectId) {
        List<PjUserAllVO> list = pjUserService.getListByProjectId(projectId);
        return AjaxResult.success(list);
    }

    /**
     * 根据项目id查询角色为项目经理的用户
     * Se:项目经理岗位编号
     * @param projectId 项目id
     * @return 用户列表
     */
    @PreAuthorize("@ss.hasPermi('project:user:list')")
    @GetMapping("/queryRoleSe/{projectId}")
    public AjaxResult queryRoleSe(@PositiveOrZero(message = "项目id有误") @PathVariable(name = "projectId") Long projectId) {
        PjUserSeVo pjUserSeVo = pjUserService.getRoleSeByProjectId(projectId);
        return AjaxResult.success(pjUserSeVo);
    }
    /**
     * 查询角色为项目经理的用户列表
     * Se:项目经理岗位编号
     * @return 用户列表
     */
    @PreAuthorize("@ss.hasPermi('project:user:list')")
     @GetMapping("/queryRoleSeAll")
    public AjaxResult queryRoleSeAll() {
        List<PjUserSeVo> list = pjUserService.getRoleSeAll();

        return AjaxResult.success(list);
    }

    /**
     * 新增项目经理
     * @param pjUserAddDTO 项目id，用户id
     * @return 新增结果
     */
    @PreAuthorize("@ss.hasPermi('project:user:addProjectRoleSe')")
    @PostMapping("/addProjectRoleSe")
    public AjaxResult addProjectRoleSe(@RequestBody PjUserAddDTO pjUserAddDTO) {
        return AjaxResult.success(pjUserService.saveUserSe(pjUserAddDTO));
    }

    /**
     * 更新项目经理
     * @param pjUserUpdateDTO 项目id，用户id，项目状态
     * @return 更新结果
     */
    @PreAuthorize("@ss.hasPermi('project:user:updateProjectRoleSe')")
    @PutMapping("/updateProjectRoleSe")
    public AjaxResult updateProjectRoleSe(@RequestBody PjUserUpdateDTO pjUserUpdateDTO) {
        return AjaxResult.success(pjUserService.updateUserSe(pjUserUpdateDTO));
    }

    /**
     * 查询项目成员列表
     * @param pjUserListDTO 项目id，部门id，岗位id，用户名，当前页，每页显示记录数，是否在项目中
     * @return 用户列表
     */
    @PreAuthorize("@ss.hasPermi('project:user:list')")
    @GetMapping("/queryUserList")
    public AjaxResult queryUserList(PjUserListDTO pjUserListDTO) {
        MpPageVO mpPageVO = pjUserService.getUserListByProjectId(pjUserListDTO);
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 新增项目成员
     * @param pjUserAddDTO 项目id，用户id
     * @return  新增结果
     */
    @PreAuthorize("@ss.hasPermi('project:user:addProjectUser')")
    @PostMapping("/addProjectUser")
    public AjaxResult addProjectUser(@RequestBody PjUserAddDTO pjUserAddDTO) {
        return AjaxResult.success(pjUserService.saveUser(pjUserAddDTO));
    }

    /**
     * 更新项目成员
     * @param pjUserUpdateDTO 项目id，用户id，项目状态
     * @return  新增结果
     */
    @PreAuthorize("@ss.hasPermi('project:user:updateProjectUser')")
    @PutMapping("/updateProjectUser")
    public AjaxResult updateProjectUser(@RequestBody PjUserUpdateDTO pjUserUpdateDTO) {
        return AjaxResult.success(pjUserService.updateUser(pjUserUpdateDTO));
    }

    /**
     * 移除项目成员
     * @param pjUserRemoveDTO 项目id，用户id列表
     * @return  新增结果
     */
    @PreAuthorize("@ss.hasPermi('project:user:removeProjectUser')")
    @DeleteMapping("/removeProjectUser")
    public AjaxResult removeProjectUser(@RequestBody PjUserRemoveDTO pjUserRemoveDTO) {
        return AjaxResult.success(pjUserService.removeUsers(pjUserRemoveDTO));
    }

    /**
     * 根据项目id查询当前用户
     * @param projectId 项目id
     * @return 用户列表
     */
    @GetMapping("{projectId}")
    public AjaxResult collection(@PathVariable("projectId") Long projectId) {
        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getPjProjectId,projectId);
        queryWrapper.eq(PjUser::getUserId, SecurityUtils.getUserId());
        PjUser pjUser = pjUserService.getOne(queryWrapper);
        return AjaxResult.success(pjUser);
//        return AjaxResult.success(pjUserService.collection(projectId));
    }
}
