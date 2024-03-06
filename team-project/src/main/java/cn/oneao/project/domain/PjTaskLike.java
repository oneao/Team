package cn.oneao.project.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTaskLike implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long pjTaskId;
    private Long userId;
}
