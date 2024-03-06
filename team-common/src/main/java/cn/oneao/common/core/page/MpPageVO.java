package cn.oneao.common.core.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MpPageVO {
    private Long total;
    private Object rows;
    
    public MpPageVO(Page page) {
        this.total = page.getTotal();
        this.rows = page.getRecords();
    }
}
