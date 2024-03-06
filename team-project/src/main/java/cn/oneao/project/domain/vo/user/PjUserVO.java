package cn.oneao.project.domain.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjUserVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;// 用户id
    private String nickName;// 用户名
    private String email;// 邮箱
    private String phoneNumber;// 手机号
    private Integer sex;// 性别 -0:男 1:女
    private String postName;// 岗位名称
    private String createBy;// 创建人(邀请人)
    private Integer accountStatus;// 账号状态 -0:正常 1:禁用
    private Integer projectStatus;// 项目状态 -0:正常 1:禁用
    private Integer isInProject; // 是否在项目中 -0:不在 1:在
}
