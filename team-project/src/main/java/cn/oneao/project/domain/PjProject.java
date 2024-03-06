package cn.oneao.project.domain;

import cn.oneao.common.core.domain.entity.SysUser;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("pj_project")
public class PjProject implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 项目id */
    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "项目id不能为空")
    @PositiveOrZero(message = "项目id传入有误")
    private Long id;
    /** 项目名称 */
    @NotNull(message = "项目名称不能为空")
    private String name;
    /** 项目描述 */
    private String description;
    /** 项目封面 */
    @NotNull(message = "项目封面不能为空")
    private String cover;
    /** 开始时间 */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    /** 结束时间 */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /** 实际结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realEndTime;
    /** 项目状态（0：未开始，1：进行中，2：已结束） */
    @NotNull(message = "项目状态不能为空")
    @Range(min = 0, max = 2, message = "项目状态只能为0或1或2")
    private Integer status;
    /** 项目进度百分比 */
    private Double schedule;
    /** 是否归档（00：未归档，1：归档） */
    private Integer isArchive;
    /** 归档时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date archiveTime;
    /** 是否删除（0：存在，1：逻辑删除） */
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
    /** 更新人 */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(exist = false)
    private List<PjTask> pjTaskList;
    @TableField(exist = false)
    private List<SysUser> userList;
}
