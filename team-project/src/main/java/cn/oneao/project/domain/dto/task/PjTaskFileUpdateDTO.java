package cn.oneao.project.domain.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTaskFileUpdateDTO implements Serializable {
    private Long fileId;
    private Long projectId;
    private String fileName;
}
