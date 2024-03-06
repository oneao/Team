package cn.oneao.project.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjUserAddDTO {
    @NotNull(message = "项目id不能为空")
    @PositiveOrZero(message = "项目id有误")
    private Long projectId;
    @NotNull(message = "用户id不能为空")
    @PositiveOrZero(message = "用户id有误")
    private Long userId;
}
