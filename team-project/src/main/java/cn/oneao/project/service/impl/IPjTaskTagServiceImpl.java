package cn.oneao.project.service.impl;

import cn.oneao.project.domain.PjTask;
import cn.oneao.project.domain.PjTaskTag;
import cn.oneao.project.mapper.PjTaskTagMapper;
import cn.oneao.project.service.IPjTaskTagService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IPjTaskTagServiceImpl extends ServiceImpl<PjTaskTagMapper, PjTaskTag> implements IPjTaskTagService {
    @Autowired
    private PjTaskTagMapper pjTaskTagMapper;
    @Override
    public List<PjTaskTag> getListByTaskId(Long id) {
        LambdaUpdateWrapper<PjTaskTag> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(PjTaskTag::getPjTaskId, id);
        return pjTaskTagMapper.selectList(queryWrapper);
    }
}
