package cn.oneao.project.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PjTaskComment implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @NotNull(message = "任务id不能为空")
    @PositiveOrZero(message = "任务id有误")
    private Long pjTaskId;
    private String content;
    private Long createId;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
