package cn.oneao.project.domain.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjUserSeVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String nickName;
    private String deptName;
    private String email;
    private String phone;
    private String sex;
    private String avatar;
    private Integer accountStatus;
    private Integer projectStatus;
}
