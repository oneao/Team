package cn.oneao.common.core.page;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MpPageDomain implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 当前记录起始索引 */
    @TableField(exist = false)
    private Integer pageNum;
    /** 每页显示记录数 */
    @TableField(exist = false)
    private Integer pageSize;
    /** 开始时间 */
    @TableField(exist = false)
    private Date startTime;
    /** 结束时间 */
    @TableField(exist = false)
    private Date endTime;
}
