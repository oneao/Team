package cn.oneao.project.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PjUserUpdateDTO {
    @PositiveOrZero(message = "项目id有误")
    private Long projectId;
    @PositiveOrZero(message = "用户id有误")
    private Long userId;
    @PositiveOrZero(message = "项目状态有误")
    private Integer projectStatus;
}
