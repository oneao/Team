package cn.oneao.project.domain;

import cn.oneao.common.core.domain.entity.SysUser;
import cn.oneao.common.core.page.MpPageDomain;
import cn.oneao.project.domain.common.PageSqlDomain;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjReport extends MpPageDomain{
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Integer type;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
    @TableField(exist = false)
    private Long postId;// 岗位id
    @TableField(exist = false)
    private Long deptId;// 部门id
    @TableField(exist = false)
    private SysUser sysUser;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private String postName;
}
