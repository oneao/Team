package cn.oneao.project.aspect;

import cn.oneao.common.exception.project.ProjectUserNotInException;
import cn.oneao.common.exception.project.ProjectUserStatusException;
import cn.oneao.common.utils.ProjectUtils;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.domain.PjUser;
import cn.oneao.project.mapper.PjUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Slf4j
public class CheckIsProjectUserAspect {
    @Autowired
    private PjUserMapper pjUserMapper;
    @Transactional
    @Before("@annotation(cn.oneao.project.annotations.CheckIsProjectUser)")
    public void checkIsProjectUser() {
        Long userId = SecurityUtils.getUserId();
        Long projectId = ProjectUtils.getProjectId();
        LambdaQueryWrapper<PjUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjUser::getUserId, userId);
        queryWrapper.eq(PjUser::getPjProjectId, projectId);
        PjUser pjUser = pjUserMapper.selectOne(queryWrapper);
        if(SecurityUtils.isAdmin(userId)){
            return;
        }
        if (pjUser == null) {
            // 当前用户不在项目中
            throw new ProjectUserNotInException();
        }
        if(pjUser.getStatus() == 1){
            // 当前用户在项目中，但是被禁用
            throw new ProjectUserStatusException();
        }
    }
}
