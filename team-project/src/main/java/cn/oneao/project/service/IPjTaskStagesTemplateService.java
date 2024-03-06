package cn.oneao.project.service;

import cn.oneao.project.domain.PjTaskStagesTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjTaskStagesTemplateService extends IService<PjTaskStagesTemplate> {
    // 查询任务阶段列表
    List<PjTaskStagesTemplate> taskStagesList(Long id);
    // 获取任务阶段
    PjTaskStagesTemplate getTaskStages(Long id);
    // 新增任务阶段
    int addTaskStages(PjTaskStagesTemplate pjTaskStagesTemplate);
    // 修改任务阶段
    boolean isHasSameTaskStagesTemplateName(String name);
    // 修改任务阶段
    void removeByPjTemplateIds(Long[] ids);
}
