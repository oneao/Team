package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjUser;
import cn.oneao.project.domain.dto.user.PjUserListDTO;
import cn.oneao.project.domain.vo.user.PjUserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface PjUserMapper extends BaseMapper<PjUser>{
    // 根据项目id查询用户列表
    List<PjUserVO> getUserListByProjectId(PjUserListDTO pjUserListDTO);
    // 获取用户列表总数
    Long getUserListByProjectIdTotal(PjUserListDTO pjUserListDTO);
    // 根据项目id删除项目下的用户
    void realDeleteByProjectIds(@Param("ids") List<Long> ids);

}
