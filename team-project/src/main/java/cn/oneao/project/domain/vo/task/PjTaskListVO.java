package cn.oneao.project.domain.vo.task;

import cn.oneao.project.domain.PjTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTaskListVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long taskStageId; // 任务阶段id
    private String taskStageName; // 任务阶段名称
    private Integer taskStageSortNum; // 任务阶段排序号
    private List<PjTask> taskList; // 任务列表
    private boolean showAddTask;// 是否显示添加任务按钮
    private Integer logAndCommentCount; // 日志数量
}
