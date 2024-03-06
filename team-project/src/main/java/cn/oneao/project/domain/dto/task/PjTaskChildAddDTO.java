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
public class PjTaskChildAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "父任务id不能为空")
    @PositiveOrZero(message = "父任务id有误")
    private Long parentId;
    @NotNull(message = "任务名称不能为空")
    private String taskName;
}
