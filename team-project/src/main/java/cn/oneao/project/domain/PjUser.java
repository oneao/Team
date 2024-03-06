package cn.oneao.project.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long pjProjectId;
    private Long userId;
    private Integer role;// 角色（0：普通成员，1：项目经理）
    private Integer status;// 状态（0：正常，1：禁用）
    private Integer isCollection;// 是否收藏（0：否，1：是）
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}
