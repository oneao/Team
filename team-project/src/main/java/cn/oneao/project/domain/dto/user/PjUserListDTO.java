package cn.oneao.project.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PjUserListDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @PositiveOrZero(message = "项目id必须大于0")
    private Long projectId; // 项目id
    private Long deptId; // 部门id
    private Long postId;// 岗位id
    private String nickName;// 用户名
    @PositiveOrZero(message = "当前页必须大于0")
    private Integer pNum;// 当前页
    @PositiveOrZero(message = "每页显示记录数必须大于0")
    private Integer pSize;// 每页显示记录数
    private Integer inProject;// 是否在项目中 -1:全部 0:不在 1:在
}
