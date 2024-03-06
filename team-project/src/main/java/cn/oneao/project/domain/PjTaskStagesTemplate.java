package cn.oneao.project.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("pj_task_stages_template")
public class PjTaskStagesTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @NotNull(message = "项目模板id不能为空")
    @PositiveOrZero(message = "项目模板id有误")
    private Long pjTemplateId;
    @NotNull(message = "阶段名称不能为空")
    private String name;
    @Min(value = 0, message = "阶段排序号有误")
    private Integer sortNum;
}
