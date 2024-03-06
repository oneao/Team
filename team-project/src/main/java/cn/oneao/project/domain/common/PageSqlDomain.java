package cn.oneao.project.domain.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PageSqlDomain implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableField(exist = false)
    private Integer start;
    @TableField(exist = false)
    private Integer end;
    @TableField(exist = false)
    private Date startTime;
    @TableField(exist = false)
    private Date endTime;
}
