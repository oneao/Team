package cn.oneao.project.utils;

import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.domain.PjProject;
import cn.oneao.project.domain.PjUser;
import cn.oneao.project.mapper.PjProjectMapper;
import cn.oneao.project.mapper.PjUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeUtils {
    @Autowired
    private PjUserMapper pjUserMapper;
    @Autowired
    private PjProjectMapper pjProjectMapper;
    public List<Long> getSeProjectIds() {
        Long userId = SecurityUtils.getUserId();
        if (SecurityUtils.isAdmin(userId)) {
            LambdaQueryWrapper<PjProject> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(PjProject::getId);
            List<PjProject> pjProjectList = pjProjectMapper.selectList(queryWrapper);
            return pjProjectList.stream().map(PjProject::getId).collect(java.util.stream.Collectors.toList());
        }
        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getUserId, userId);
        List<PjUser> pjUserList = pjUserMapper.selectList(queryWrapper);
        return pjUserList.stream().map(PjUser::getPjProjectId).collect(java.util.stream.Collectors.toList());
    }
}
