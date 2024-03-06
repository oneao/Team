package cn.oneao.project.service;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.project.domain.PjTaskStages;
import cn.oneao.project.domain.dto.task.PjTaskStagesAddDTO;
import cn.oneao.project.domain.dto.task.PjTaskStagesUpdateDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IPjTaskStagesService extends IService<PjTaskStages>{
    // 添加任务阶段
    AjaxResult addTaskStages(PjTaskStagesAddDTO pjTaskStagesAddDTO);
    // 更新任务阶段
    AjaxResult updateTaskStages(PjTaskStagesUpdateDTO pjTaskStagesUpdateDTO);
    // 判断是否有相同的任务阶段名
    boolean isHasSameName(String taskStageName, Long projectId);
}
