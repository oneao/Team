package cn.oneao.project.service.impl;

import cn.oneao.project.domain.PjTaskStagesTemplate;
import cn.oneao.project.mapper.PjTaskStagesTemplateMapper;
import cn.oneao.project.service.IPjTaskStagesTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class IPjTaskStagesTemplateImpl extends ServiceImpl<PjTaskStagesTemplateMapper, PjTaskStagesTemplate> implements IPjTaskStagesTemplateService {
    @Autowired
    private PjTaskStagesTemplateMapper pjTaskStagesTemplateMapper;
    // 查询任务阶段列表
    @Override
    public List<PjTaskStagesTemplate> taskStagesList(Long id) {
        LambdaQueryWrapper<PjTaskStagesTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTaskStagesTemplate::getPjTemplateId, id);
        queryWrapper.orderByAsc(PjTaskStagesTemplate::getSortNum);
        return pjTaskStagesTemplateMapper.selectList(queryWrapper);
    }
    // 获取任务阶段
    @Override
    public PjTaskStagesTemplate getTaskStages(Long id) {
        return pjTaskStagesTemplateMapper.selectById(id);
    }
    // 新增任务阶段
    @Override
    public int addTaskStages(PjTaskStagesTemplate pjTaskStagesTemplate) {
        return pjTaskStagesTemplateMapper.insert(pjTaskStagesTemplate);
    }
    // 是否有相同的任务阶段模版名称
    @Override
    public boolean isHasSameTaskStagesTemplateName(String name) {
        LambdaQueryWrapper<PjTaskStagesTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjTaskStagesTemplate::getName, name);
        return pjTaskStagesTemplateMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public void removeByPjTemplateIds(Long[] ids) {
        pjTaskStagesTemplateMapper.deleteBatchByPjTemplateIds(Arrays.asList(ids));
    }
}
