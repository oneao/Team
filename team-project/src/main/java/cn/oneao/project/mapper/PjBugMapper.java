package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjBug;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PjBugMapper extends BaseMapper<PjBug> {
    void realDeleteByProjectIds(@Param("ids") List<Long> ids);
}
