package cn.oneao.project.domain;

import cn.oneao.project.domain.common.PageSqlDomain;
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
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTaskFile extends PageSqlDomain {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long pjTaskId;
    private String fileName;
    private Long fileSize;
    private String filePath;
    private String physicalPath;
    private String fileSuffix;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
    @TableField(exist = false)
    private String fileSizeStr;
    @TableField(exist = false)
    private String taskName;
    @TableField(exist = false)
    private Long pjProjectId;
    @TableField(exist = false)
    private List<Long> seProjectIds; // 项目id列表
}
