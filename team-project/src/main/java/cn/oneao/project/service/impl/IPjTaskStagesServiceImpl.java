package cn.oneao.project.service.impl;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.ProjectTypeConstants;
import cn.oneao.project.domain.PjProjectLog;
import cn.oneao.project.domain.PjTaskStages;
import cn.oneao.project.domain.dto.task.PjTaskStagesAddDTO;
import cn.oneao.project.domain.dto.task.PjTaskStagesUpdateDTO;
import cn.oneao.project.mapper.PjProjectLogMapper;
import cn.oneao.project.mapper.PjTaskLogMapper;
import cn.oneao.project.mapper.PjTaskStagesMapper;
import cn.oneao.project.service.IPjTaskStagesService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IPjTaskStagesServiceImpl extends ServiceImpl<PjTaskStagesMapper, PjTaskStages> implements IPjTaskStagesService {
    @Autowired
    private PjTaskStagesMapper pjTaskStagesMapper;
    @Autowired
    private PjProjectLogMapper pjProjectLogMapper;
    // 添加任务阶段
    @Override
    public AjaxResult addTaskStages(PjTaskStagesAddDTO pjTaskStagesAddDTO) {
        Long projectId = pjTaskStagesAddDTO.getProjectId();
        String taskStageName = pjTaskStagesAddDTO.getTaskStageName();
        Integer sortNum = pjTaskStagesAddDTO.getSortNum();
        //新增
        PjTaskStages pjTaskStages = new PjTaskStages();
        pjTaskStages.setPjProjectId(projectId);
        pjTaskStages.setName(taskStageName);
        pjTaskStages.setSortNum(sortNum);
        this.save(pjTaskStages);
        // 记录日志
        PjProjectLog pjProjectLog = new PjProjectLog();
        pjProjectLog.setPjProjectId(pjTaskStages.getPjProjectId());
        pjProjectLog.setType(ProjectTypeConstants.TASK_STAGE);
        pjProjectLog.setOperationType(OperationConstants.ADD);
        pjProjectLog.setContent(SecurityUtils.getUsername() + "添加任务阶段：" + pjTaskStages.getName());
        pjProjectLogMapper.insert(pjProjectLog);
        return AjaxResult.success();
    }
    // 更新任务阶段
    @Override
    public AjaxResult updateTaskStages(PjTaskStagesUpdateDTO pjTaskStagesUpdateDTO) {
        Long taskStageId = pjTaskStagesUpdateDTO.getTaskStageId();

        PjTaskStages pjTaskStages = new PjTaskStages();
        pjTaskStages.setId(taskStageId);
        pjTaskStages.setName(pjTaskStagesUpdateDTO.getTaskStageName());
        pjTaskStages.setSortNum(pjTaskStagesUpdateDTO.getSortNum());
        this.updateById(pjTaskStages);

        return AjaxResult.success();
    }

    @Override
    public boolean isHasSameName(String taskStageName, Long projectId) {
        return pjTaskStagesMapper.selectCountByName(taskStageName,projectId) > 0;
    }
}
