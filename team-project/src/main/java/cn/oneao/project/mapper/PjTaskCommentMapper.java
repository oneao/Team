package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjTaskComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PjTaskCommentMapper extends BaseMapper<PjTaskComment> {
    void realDeleteByTaskIds(@Param("taskIds") List<Long> taskIds);
}
