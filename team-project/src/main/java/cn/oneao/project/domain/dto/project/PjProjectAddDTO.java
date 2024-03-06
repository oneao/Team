package cn.oneao.project.domain.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjProjectAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "项目模板id不能为空")
    @PositiveOrZero(message = "项目模板id有误")
    private Long pjTemplateId;
    @NotNull(message = "项目名称不能为空")
    private String name;
    private String description;
    @NotNull(message = "项目封面不能为空")
    private String cover;
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
