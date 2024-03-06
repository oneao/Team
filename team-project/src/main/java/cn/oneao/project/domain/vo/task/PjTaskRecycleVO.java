package cn.oneao.project.domain.vo.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTaskRecycleVO {
    private Long id;
    private String name;
    private String description;
    private Date deleteTime;
}
