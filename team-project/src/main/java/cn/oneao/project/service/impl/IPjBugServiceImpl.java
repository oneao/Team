package cn.oneao.project.service.impl;

import cn.oneao.common.core.domain.entity.SysUser;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.domain.PjBug;
import cn.oneao.project.domain.PjProject;
import cn.oneao.project.domain.PjTask;
import cn.oneao.project.mapper.PjBugMapper;
import cn.oneao.project.mapper.PjProjectMapper;
import cn.oneao.project.mapper.PjTaskMapper;
import cn.oneao.project.service.IPjBugService;
import cn.oneao.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.Oneway;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IPjBugServiceImpl extends ServiceImpl<PjBugMapper, PjBug> implements IPjBugService {
    @Autowired
    private PjBugMapper pjBugMapper;
    @Autowired
    private PjProjectMapper pjProjectMapper;
    @Autowired
    private PjTaskMapper pjTaskMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<PjProject> getOption() {
        List<PjProject> result = new ArrayList<>();
        Long userId = SecurityUtils.getUserId();
        List<PjProject> list = pjProjectMapper.selectPjProjectListByUserId(userId);
        for (PjProject pjProject : list) {
            List<PjTask> pjTasks = pjTaskMapper.selectPjTaskListByProjectId(pjProject.getId());
            pjProject.setPjTaskList(pjTasks);
            result.add(pjProject);
        }
        return result;
    }

    @Override
    public List<PjProject> getAllOption() {
        List<PjProject> result = new ArrayList<>();
        LambdaQueryWrapper<PjProject> queryWrapper = new LambdaQueryWrapper<>();
        List<PjProject> list = pjProjectMapper.selectList(queryWrapper);
        for (PjProject pjProject : list) {
            List<PjTask> pjTasks = pjTaskMapper.selectPjTaskListByProjectId(pjProject.getId());
            pjProject.setPjTaskList(pjTasks);
            List<SysUser> userList = pjProjectMapper.selectSysUserListByProjectId(pjProject.getId());
            pjProject.setUserList(userList);
            result.add(pjProject);
        }

        return result;
    }

    @Override
    public List<PjBug> getMyBug() {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<PjBug> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjBug::getAssignTo, userId);

        return pjBugMapper.selectList(queryWrapper);
    }

    // 申报
    @Override
    public MpPageVO getBugDeclare(PjBug pjBug) {
        MpPageVO mpPageVO = new MpPageVO();
        Page<PjBug> page = new Page<>(pjBug.getPageNum(), pjBug.getPageSize());
        LambdaQueryWrapper<PjBug> queryWrapper = new LambdaQueryWrapper<>();
        getPjBugListByPjBug(pjBug, queryWrapper);
        String username = SecurityUtils.getUsername();
        queryWrapper.eq(PjBug::getCreateBy, username);
        Page<PjBug> pageVo = pjBugMapper.selectPage(page, queryWrapper);
        List<PjBug> records = pageVo.getRecords();
        getProjectAndTaskAndUserName(records);
        mpPageVO.setRows(records);
        mpPageVO.setTotal(pageVo.getTotal());
        return mpPageVO;
    }

    @Override
    public MpPageVO getBugDistribute(PjBug pjBug) {
        MpPageVO mpPageVO = new MpPageVO();
        Page<PjBug> page = new Page<>(pjBug.getPageNum(), pjBug.getPageSize());
        LambdaQueryWrapper<PjBug> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = SecurityUtils.getUserId();
        queryWrapper.eq(PjBug::getAssignTo, userId);
        getPjBugListByPjBug(pjBug, queryWrapper);
        Page<PjBug> pageVo = pjBugMapper.selectPage(page, queryWrapper);
        List<PjBug> records = pageVo.getRecords();
        getProjectAndTaskAndUserName(records);
        mpPageVO.setRows(records);
        mpPageVO.setTotal(pageVo.getTotal());
        return mpPageVO;
    }

    private void getPjBugListByPjBug(PjBug pjBug, LambdaQueryWrapper<PjBug> queryWrapper) {
        if (pjBug.getName() != null) {
            queryWrapper.like(PjBug::getName, pjBug.getName());
        }
        if (pjBug.getStatus() != null && pjBug.getStatus() != -1) {
            queryWrapper.eq(PjBug::getStatus, pjBug.getStatus());
        }
        if (pjBug.getPriority() != null && pjBug.getPriority() != -1) {
            queryWrapper.eq(PjBug::getPriority, pjBug.getPriority());
        }
        if (pjBug.getSeverity() != null && pjBug.getSeverity() != -1) {
            queryWrapper.eq(PjBug::getSeverity, pjBug.getSeverity());
        }
    }

    private void getProjectAndTaskAndUserName(List<PjBug> list) {
        for (PjBug item : list) {
            PjProject pjProject = pjProjectMapper.selectById(item.getPjProjectId());
            item.setPjProjectName(pjProject.getName());
            if (item.getPjTaskId() != null) {
                PjTask pjTask = pjTaskMapper.selectById(item.getPjTaskId());
                item.setPjTaskName(pjTask.getName());
            }
            if (item.getAssignTo() != null) {
                SysUser sysUser = sysUserMapper.selectUserById(item.getAssignTo());
                item.setAssignToName(sysUser.getNickName());
            }
        }
    }

    @Override
    public boolean addBug(PjBug pjBug) {
        pjBug.setStatus(0);
        return pjBugMapper.insert(pjBug) > 0;
    }

    @Override
    public MpPageVO getList(PjBug pjBug) {
        Page<PjBug> page = new Page<>(pjBug.getPageNum(), pjBug.getPageSize());
        LambdaQueryWrapper<PjBug> queryWrapper = new LambdaQueryWrapper<>();
        if (pjBug.getName() != null) {
            queryWrapper.like(PjBug::getName, pjBug.getName());
        }
        if (pjBug.getPjProjectId() != null) {
            queryWrapper.eq(PjBug::getPjProjectId, pjBug.getPjProjectId());
        }
        if (pjBug.getStatus() != null && pjBug.getStatus() != -1) {
            queryWrapper.eq(PjBug::getStatus, pjBug.getStatus());
        }
        if (pjBug.getPriority() != null && pjBug.getPriority() != -1) {
            queryWrapper.eq(PjBug::getPriority, pjBug.getPriority());
        }
        if (pjBug.getSeverity() != null && pjBug.getSeverity() != -1) {
            queryWrapper.eq(PjBug::getSeverity, pjBug.getSeverity());
        }
        Page<PjBug> pageVo = pjBugMapper.selectPage(page, queryWrapper);
        MpPageVO mpPageVO = new MpPageVO();
        mpPageVO.setTotal(pageVo.getTotal());
        List<PjBug> list = pageVo.getRecords();
        getProjectAndTaskAndUserName(list);
        mpPageVO.setRows(list);
        return mpPageVO;
    }
}
