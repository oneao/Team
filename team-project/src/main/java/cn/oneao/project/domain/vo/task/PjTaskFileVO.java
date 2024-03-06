package cn.oneao.project.domain.vo.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTaskFileVO {
    private Long id;
    private String taskName;
    private String fileName;
    private String fileSize;
    private Date  deleteTime;
}
