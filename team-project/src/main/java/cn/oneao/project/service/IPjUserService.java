package cn.oneao.project.service;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjUser;
import cn.oneao.project.domain.dto.user.PjUserListDTO;
import cn.oneao.project.domain.dto.user.PjUserAddDTO;
import cn.oneao.project.domain.dto.user.PjUserRemoveDTO;
import cn.oneao.project.domain.dto.user.PjUserUpdateDTO;
import cn.oneao.project.domain.vo.user.PjUserAllVO;
import cn.oneao.project.domain.vo.user.PjUserSeVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjUserService extends IService<PjUser> {
    // 根据项目id查询用户列表
    List<PjUserAllVO> getListByProjectId(Long projectId);
    // 根据项目id查询角色为项目经理的用户列表
    PjUserSeVo getRoleSeByProjectId(Long projectId);
    // 查询角色为项目经理的用户列表
    List<PjUserSeVo> getRoleSeAll();
    // 保存项目经理
    AjaxResult saveUserSe(PjUserAddDTO pjUserAddDTO);
    // 更新项目经理
    AjaxResult updateUserSe(PjUserUpdateDTO pjUserUpdateDTO);
    // 根据项目id查询用户列表
    MpPageVO getUserListByProjectId(PjUserListDTO pjUserListDTO);
    // 保存用户
    AjaxResult saveUser(PjUserAddDTO pjUserAddDTO);
    // 更新用户状态
    AjaxResult updateUser(PjUserUpdateDTO pjUserUpdateDTO);
    // 删除用户
    AjaxResult removeUsers(PjUserRemoveDTO pjUserRemoveDTO);
}
