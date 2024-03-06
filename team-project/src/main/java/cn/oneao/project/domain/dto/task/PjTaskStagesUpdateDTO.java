package cn.oneao.project.domain.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTaskStagesUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "任务阶段id不能为空")
    @PositiveOrZero(message = "任务阶段id有误")
    private Long taskStageId;
    @NotNull(message = "任务名称不能为空")
    private String taskStageName;
    @NotNull(message = "排序不能为空")
    private Integer sortNum;
}
