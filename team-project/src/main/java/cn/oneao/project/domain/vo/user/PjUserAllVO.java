package cn.oneao.project.domain.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjUserAllVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private Integer role;
    private String avatar;
    private String nickName;
    private String deptName;
    private Integer projectStatus;
    private String createBy;
}
