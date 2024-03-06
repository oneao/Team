package cn.oneao.project.domain.dto.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTemplateUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    @NotNull(message = "项目名称不能为空")
    private String name;
    private String description;
    @Range(min = 0, max = 1, message = "是否系统模板有误")
    private Integer isSystem;
    @NotNull(message = "项目封面不能为空")
    private String cover;
}
