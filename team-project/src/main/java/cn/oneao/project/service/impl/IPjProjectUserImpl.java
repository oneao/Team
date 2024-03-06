package cn.oneao.project.service.impl;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.domain.entity.SysUser;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.ProjectTypeConstants;
import cn.oneao.project.domain.PjProjectLog;
import cn.oneao.project.domain.PjUser;
import cn.oneao.project.domain.dto.user.PjUserListDTO;
import cn.oneao.project.domain.dto.user.PjUserAddDTO;
import cn.oneao.project.domain.dto.user.PjUserRemoveDTO;
import cn.oneao.project.domain.dto.user.PjUserUpdateDTO;
import cn.oneao.project.domain.vo.user.PjUserAllVO;
import cn.oneao.project.domain.vo.user.PjUserSeVo;
import cn.oneao.project.domain.vo.user.PjUserVO;
import cn.oneao.project.mapper.PjProjectLogMapper;
import cn.oneao.project.mapper.PjUserMapper;
import cn.oneao.project.service.IPjUserService;
import cn.oneao.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IPjProjectUserImpl extends ServiceImpl<PjUserMapper, PjUser> implements IPjUserService {
    @Autowired
    private PjUserMapper pjUserMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private PjProjectLogMapper pjProjectLogMapper;

    // 根据项目id查询用户列表
    @Override
    public List<PjUserAllVO> getListByProjectId(Long projectId) {
        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getPjProjectId, projectId);
        queryWrapper.orderByDesc(PjUser::getRole);
        List<PjUser> pjUserList = this.list(queryWrapper);
        List<PjUserAllVO> pjUserAllVOList = new ArrayList<>();
        pjUserList.forEach(item -> {
            Long userId = item.getUserId();
            SysUser sysUser = sysUserService.selectUserById(userId);
            PjUserAllVO pjUserAllVO = new PjUserAllVO();
            pjUserAllVO.setUserId(userId);
            pjUserAllVO.setAvatar(sysUser.getAvatar());
            pjUserAllVO.setRole(item.getRole());
            pjUserAllVO.setNickName(sysUser.getNickName());
            pjUserAllVO.setProjectStatus(item.getStatus());
            pjUserAllVO.setCreateBy(item.getCreateBy());
            if (sysUser.getDept().getParentName() != null) {
                pjUserAllVO.setDeptName(sysUser.getDept().getParentName() + "-" + sysUser.getDept().getDeptName());
            } else {
                pjUserAllVO.setDeptName(sysUser.getDept().getDeptName());
            }
            pjUserAllVOList.add(pjUserAllVO);
        });
        return pjUserAllVOList;
    }

    // 根据项目id查询角色为项目经理的用户列表
    @Override
    public PjUserSeVo getRoleSeByProjectId(Long projectId) {
        PjUserSeVo pjUserSeVo = new PjUserSeVo();

        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getPjProjectId, projectId);
        queryWrapper.eq(PjUser::getRole, 1);
        PjUser pjUser = this.getOne(queryWrapper);

        if (!ObjectUtils.isEmpty(pjUser)) {
            Long userId = pjUser.getUserId();
            SysUser sysUser = sysUserService.selectUserById(userId);
            pjUserSeVo.setId(userId);
            copySysUserToPjUserVo(sysUser, pjUserSeVo);
            pjUserSeVo.setProjectStatus(pjUser.getStatus());
        }

        return pjUserSeVo;
    }

    // 查询角色为项目经理的用户列表
    @Override
    public List<PjUserSeVo> getRoleSeAll() {
        List<SysUser> sysUserList = new ArrayList<>();
        List<PjUserSeVo> pjUserSeVoList = new ArrayList<>();
        // 查询岗位为项目经理的用户列表
        sysUserList = sysUserService.selectUserByPostCode("Se");
        sysUserList.forEach(item -> {
            if (item.isAdmin()) {
                return;
            }
            PjUserSeVo pjUserSeVo = new PjUserSeVo();
            pjUserSeVo.setId(item.getUserId());
            copySysUserToPjUserVo(item, pjUserSeVo);
            pjUserSeVoList.add(pjUserSeVo);
        });
        return pjUserSeVoList;
    }

    // 保存项目经理
    @Override
    public AjaxResult saveUserSe(PjUserAddDTO pjUserAddDTO) {
        Long projectId = pjUserAddDTO.getProjectId();
        Long userId = pjUserAddDTO.getUserId();

        PjUser pjUser = getPjUserSeByProjectId(projectId);

        if (ObjectUtils.isEmpty(pjUser)) {
            pjUser = new PjUser();
            pjUser.setPjProjectId(projectId);
            pjUser.setUserId(userId);
            pjUser.setRole(1);
            pjUser.setStatus(1);
            this.save(pjUser);
        } else {
            return AjaxResult.error("项目经理已存在");
        }
        // 查询用户信息
        SysUser sysUser = sysUserService.selectUserById(userId);
        // 记录日志
        PjProjectLog pjProjectLog = new PjProjectLog();

        pjProjectLog.setPjProjectId(projectId);
        pjProjectLog.setType(ProjectTypeConstants.USER_SE);
        pjProjectLog.setOperationType(OperationConstants.UPDATE);
        pjProjectLog.setContent(SecurityUtils.getUsername() + "设置该项目的项目经理为：" + sysUser.getUserName());
        pjProjectLogMapper.insert(pjProjectLog);
        return AjaxResult.success();
    }

    // 更新项目经理
    @Override
    @Transactional
    public AjaxResult updateUserSe(PjUserUpdateDTO pjUserUpdateDTO) {
        Long projectId = pjUserUpdateDTO.getProjectId();
        Integer projectStatus = pjUserUpdateDTO.getProjectStatus();
        Long userId = pjUserUpdateDTO.getUserId();

        PjUser pjUser = getPjUserSeByProjectId(projectId);

        if (!ObjectUtils.isEmpty(pjUser)) {
            pjUser.setStatus(projectStatus);
            pjUser.setUserId(userId);
            this.updateById(pjUser);
        } else {
            return AjaxResult.error("项目经理不存在");
        }

        // 查询用户信息
        SysUser sysUser = sysUserService.selectUserById(userId);
        // 记录日志
        PjProjectLog pjProjectLog = new PjProjectLog();
        pjProjectLog.setPjProjectId(projectId);
        pjProjectLog.setType(ProjectTypeConstants.USER_SE);
        pjProjectLog.setOperationType(OperationConstants.UPDATE);
        pjProjectLog.setContent(SecurityUtils.getUsername() + "更新该项目的项目经理为：" + sysUser.getUserName());
        pjProjectLogMapper.insert(pjProjectLog);

        return AjaxResult.success();
    }

    // 根据项目id查询用户列表
    @Override
    public MpPageVO getUserListByProjectId(PjUserListDTO pjUserListDTO) {
        MpPageVO mpPageVO = new MpPageVO();

        pjUserListDTO.setPNum(pjUserListDTO.getPNum() - 1);
        List<PjUserVO> userListByProjectId = pjUserMapper.getUserListByProjectId(pjUserListDTO);
        Long total = pjUserMapper.getUserListByProjectIdTotal(pjUserListDTO);
        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getPjProjectId, pjUserListDTO.getProjectId());
        queryWrapper.eq(PjUser::getRole, 1);
        PjUser pjUser = this.getOne(queryWrapper);
        //过滤项目经理
        if (!ObjectUtils.isEmpty(pjUser)) {
            Long userId = pjUser.getUserId();
            userListByProjectId = userListByProjectId.stream()
                    .filter(user -> !user.getUserId().equals(userId))
                    .collect(Collectors.toList());
            total = total - 1;
        }
        mpPageVO.setRows(userListByProjectId);
        mpPageVO.setTotal(total);
        return mpPageVO;
    }

    // 保存用户
    @Override
    public AjaxResult saveUser(PjUserAddDTO pjUserAddDTO) {
        Long userId = pjUserAddDTO.getUserId();
        Long projectId = pjUserAddDTO.getProjectId();
        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getUserId, userId);
        queryWrapper.eq(PjUser::getPjProjectId, projectId);
        PjUser pjUser = this.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(pjUser)) {
            pjUser = new PjUser();
            pjUser.setUserId(userId);
            pjUser.setPjProjectId(projectId);
            pjUser.setRole(0);
            pjUser.setStatus(0);
            this.save(pjUser);
            // 查询用户信息
            SysUser sysUser = sysUserService.selectUserById(userId);
            // 记录日志
            PjProjectLog pjProjectLog = new PjProjectLog();
            pjProjectLog.setPjProjectId(projectId);
            pjProjectLog.setType(ProjectTypeConstants.USER);
            pjProjectLog.setOperationType(OperationConstants.ADD);
            pjProjectLog.setContent(SecurityUtils.getUsername() + "添加项目用户：" + sysUser.getUserName());
            pjProjectLogMapper.insert(pjProjectLog);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("用户已存在");
        }
    }

    // 更新用户状态
    @Override
    public AjaxResult updateUser(PjUserUpdateDTO pjUserUpdateDTO) {
        Long projectId = pjUserUpdateDTO.getProjectId();
        Long userId = pjUserUpdateDTO.getUserId();
        Integer projectStatus = pjUserUpdateDTO.getProjectStatus();
        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getUserId, userId);
        queryWrapper.eq(PjUser::getPjProjectId, projectId);
        PjUser pjUser = this.getOne(queryWrapper);
        if (!ObjectUtils.isEmpty(pjUser)) {
            pjUser.setStatus(projectStatus);
            this.updateById(pjUser);
            // 查询用户信息
            SysUser sysUser = sysUserService.selectUserById(userId);
            // 记录日志
            PjProjectLog pjProjectLog = new PjProjectLog();
            pjProjectLog.setPjProjectId(projectId);
            pjProjectLog.setType(ProjectTypeConstants.USER);
            if (projectStatus == 1) {
                pjProjectLog.setOperationType(OperationConstants.DISABLE);
                pjProjectLog.setContent(SecurityUtils.getUsername() + "更新项目用户：" + sysUser.getUserName() + "状态为禁用");
            } else {
                pjProjectLog.setOperationType(OperationConstants.ENABLE);
                pjProjectLog.setContent(SecurityUtils.getUsername() + "更新项目用户：" + sysUser.getUserName() + "状态为启用");
            }
            pjProjectLogMapper.insert(pjProjectLog);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("用户不存在");
        }
    }

    // 删除用户
    @Override
    @Transactional
    public AjaxResult removeUsers(PjUserRemoveDTO pjUserRemoveDTO) {
        Long projectId = pjUserRemoveDTO.getProjectId();
        Long[] ids = pjUserRemoveDTO.getIds();
        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(PjUser::getPjProjectId, projectId);
        queryWrapper.eq(PjUser::getRole, 0);
        queryWrapper.in(PjUser::getUserId, Arrays.asList(ids));

        this.remove(queryWrapper);
        // 查询用户信息
        for (Long id : ids) {
            SysUser sysUser = sysUserService.selectUserById(id);
            // 记录日志
            PjProjectLog pjProjectLog = new PjProjectLog();
            pjProjectLog.setPjProjectId(projectId);
            pjProjectLog.setType(ProjectTypeConstants.USER);
            pjProjectLog.setOperationType(OperationConstants.REAL_DELETE);
            pjProjectLog.setContent(SecurityUtils.getUsername() + "移除项目用户：" + sysUser.getUserName());
            pjProjectLogMapper.insert(pjProjectLog);
        }
        return AjaxResult.success();
    }

    // 将SysUser对象的属性拷贝到PjUserSeVo对象
    private void copySysUserToPjUserVo(SysUser item, PjUserSeVo pjUserSeVo) {
        pjUserSeVo.setNickName(item.getNickName());
        if (item.getDept() != null && item.getDept().getParentName() != null && item.getDept().getDeptName() != null) {
            pjUserSeVo.setDeptName(item.getDept().getParentName() + "-" + item.getDept().getDeptName());
        }
        pjUserSeVo.setEmail(item.getEmail());
        pjUserSeVo.setPhone(item.getPhonenumber());
        pjUserSeVo.setSex(item.getSex());
        pjUserSeVo.setAvatar(item.getAvatar());
        pjUserSeVo.setAccountStatus(Integer.parseInt(item.getStatus()));
    }

    // 根据项目id查询角色为项目经理的用户
    private PjUser getPjUserSeByProjectId(Long projectId) {
        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getPjProjectId, projectId);
        queryWrapper.eq(PjUser::getRole, 1);
        return this.getOne(queryWrapper);
    }
}
