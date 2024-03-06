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
public class PjTaskStagesAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "项目id不能为空")
    private Long projectId;
    @NotNull(message = "任务名称不能为空")
    private String taskStageName;
    @NotNull(message = "任务名称不能为空")
    @PositiveOrZero(message = "任务顺序有误")
    private Integer sortNum;
}