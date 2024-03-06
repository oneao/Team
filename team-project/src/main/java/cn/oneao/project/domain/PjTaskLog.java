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

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PjTaskLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long pjTaskId;
    private Integer type;// 操作类型 0：任务名称 1：任务状态 2：任务紧急程度 3：任务排序 4：任务描述 5：指定人 6：更新时间范围，7：子任务
    private Integer operationType;// 操作类型 0：添加 1：更新 2：删除 3：完成
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
