package cn.oneao.project.service.impl;

import cn.oneao.common.core.domain.entity.SysDept;
import cn.oneao.common.core.domain.entity.SysUser;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.domain.PjReport;
import cn.oneao.project.domain.PjUser;
import cn.oneao.project.domain.dto.report.PjReportListInTaskDTO;
import cn.oneao.project.mapper.PjProjectMapper;
import cn.oneao.project.mapper.PjReportMapper;
import cn.oneao.project.mapper.PjUserMapper;
import cn.oneao.project.service.IPjReportService;
import cn.oneao.system.domain.SysPost;
import cn.oneao.system.mapper.SysDeptMapper;
import cn.oneao.system.mapper.SysPostMapper;
import cn.oneao.system.mapper.SysUserMapper;
import cn.oneao.system.service.ISysDeptService;
import cn.oneao.system.service.ISysPostService;
import cn.oneao.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.valueextraction.UnwrapByDefault;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPjReportServiceImpl extends ServiceImpl<PjReportMapper, PjReport> implements IPjReportService {
    @Autowired
    private PjReportMapper pjReportMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private SysPostMapper sysPostMapper;
    @Autowired
    private PjUserMapper pjUserMapper;

    @Override
    public MpPageVO getList(PjReport pjReport) {
        Page<PjReport> page = new Page<>(pjReport.getPageNum(), pjReport.getPageSize());

        LambdaQueryWrapper<PjReport> queryWrapper = new LambdaQueryWrapper<>();
        String name = pjReport.getName();
        Long userId = pjReport.getUserId();
        Long deptId = pjReport.getDeptId();// 根据部门id查询该部门下面的用户
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(PjReport::getName, name);
        }
        if (userId != null) {
            queryWrapper.eq(PjReport::getUserId, userId);
        }
        // 根据岗位id查询该部门下面的岗位
        if (deptId != null) {
            Long postId = pjReport.getPostId();
            List<SysUser> postList = sysUserMapper.selectUserByPostId(postId);
            List<Long> userIds = postList.stream().map(SysUser::getUserId).collect(Collectors.toList());
            queryWrapper.in(PjReport::getUserId, userIds);
        }
        SysUser sysUser = new SysUser();
        // 根据部门id查询该部门下面的用户
        if (deptId != null) {
            sysUser.setDeptId(deptId);
            List<SysUser> deptList = sysUserService.selectUserList(sysUser);
            List<Long> userIds = deptList.stream().map(SysUser::getUserId).collect(Collectors.toList());
            queryWrapper.in(PjReport::getUserId, userIds);
        }
        // 根据时间查询
        Date startTime = pjReport.getStartTime();
        Date endTime = pjReport.getEndTime();
        if (startTime != null && endTime != null) {
            queryWrapper.between(PjReport::getCreateTime, startTime, endTime);
        }
        // 根据类型查询
        Page<PjReport> pjReportPage = pjReportMapper.selectPage(page, queryWrapper);
        List<PjReport> records = pjReportPage.getRecords();
        records.forEach(item -> {
            SysUser user = sysUserService.selectUserById(item.getUserId());
            if (user.getDept().getParentName() != null) {
                item.setDeptName(user.getDept().getParentName() + "-" + user.getDept().getDeptName());
            } else {
                item.setDeptName(user.getDept().getDeptName());
            }
            List<SysPost> sysPosts = sysPostMapper.selectPostsByUserName(user.getUserName());
            StringBuilder postName = new StringBuilder();
            for (SysPost sysPost : sysPosts) {
                postName.append(sysPost.getPostName()).append(",");
            }
            if (postName.length() > 0) {
                item.setPostName(postName.substring(0, postName.length() - 1));
            }
            item.setSysUser(user);
        });

        return new MpPageVO(pjReportPage);
    }

    // 获取自己的报告列表
    @Override
    public MpPageVO getSelfList(PjReport pjReport) {
        Page<PjReport> page = new Page<>(pjReport.getPageNum(), pjReport.getPageSize());

        LambdaQueryWrapper<PjReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjReport::getUserId, SecurityUtils.getUserId());
        String name = pjReport.getName();
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(PjReport::getName, name);
        }
        // 根据时间查询
        Date startTime = pjReport.getStartTime();
        Date endTime = pjReport.getEndTime();
        if (startTime != null && endTime != null) {
            queryWrapper.between(PjReport::getCreateTime, startTime, endTime);
        }
        // 根据类型查询
        Page<PjReport> pjReportPage = pjReportMapper.selectPage(page, queryWrapper);
        return new MpPageVO(pjReportPage);
    }

    // 根据项目id查询报告
    @Override
    public List<PjReport> getListByProjectId(PjReportListInTaskDTO pjReportListInTaskDTO) {
        Long projectId = pjReportListInTaskDTO.getProjectId();

        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getPjProjectId, projectId);
        List<PjUser> pjUsers = pjUserMapper.selectList(queryWrapper);
        List<Long> userIds = pjUsers.stream().map(PjUser::getUserId).collect(Collectors.toList());
        LambdaQueryWrapper<PjReport> reportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportLambdaQueryWrapper.in(PjReport::getUserId, userIds);


        if (pjReportListInTaskDTO.getUserId() != null) {
            reportLambdaQueryWrapper.eq(PjReport::getUserId, pjReportListInTaskDTO.getUserId());
        }

        LocalDate yesterday = LocalDate.now().minusDays(1); // 获取昨天日期
        LocalDateTime startOfYesterday = LocalDateTime.of(yesterday, LocalTime.MIN); // 昨天的开始时间
        LocalDateTime now = LocalDateTime.now();

        if (pjReportListInTaskDTO.getStartTime() != null && pjReportListInTaskDTO.getEndTime() != null) {
            reportLambdaQueryWrapper.between(PjReport::getCreateTime, pjReportListInTaskDTO.getStartTime(), pjReportListInTaskDTO.getEndTime());
        } else {
            reportLambdaQueryWrapper.between(PjReport::getCreateTime, startOfYesterday, now);
        }
        List<PjReport> pjReports = pjReportMapper.selectList(reportLambdaQueryWrapper);
        pjReports.forEach(item -> {
            SysUser user = sysUserService.selectUserById(item.getUserId());
            item.setSysUser(user);
        });
        return pjReports;
    }
}
