package cn.oneao.project.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTaskTag implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id; // 任务标签id
    private Long pjTaskId; // 任务id
    private String name; // 任务标签名称
    private String type; // 任务标签颜色
}
