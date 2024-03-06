package cn.oneao.project.domain;

import cn.oneao.common.core.page.MpPageDomain;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjBug extends MpPageDomain {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String name;
    private Long pjProjectId;
    private Long pjTaskId;
    private String description;
    private Integer severity; // bug的严重程度（0：低，1：中，2：高）
    private Integer priority;// bug的优先级（0：低，1：中，2：高）
    private Integer status;// bug的状态（0：未解决，1：已解决）
    private Long assignTo;
    private String suggestion;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
    private Date solveTime;//解决时间
    @TableField(exist = false)
    private String pjProjectName;
    @TableField(exist = false)
    private String pjTaskName;
    @TableField(exist = false)
    private String assignToName;
}
