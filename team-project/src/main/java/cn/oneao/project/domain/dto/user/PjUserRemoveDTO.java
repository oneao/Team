package cn.oneao.project.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PjUserRemoveDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "项目id不能为空")
    @PositiveOrZero(message = "项目id有误")
    private Long projectId;
    @NotNull(message = "用户id不能为空")
    @Size(min = 1, message = "用户id不能为空")
    private Long[] ids;
}
