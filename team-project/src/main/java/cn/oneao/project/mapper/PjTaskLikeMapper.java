package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjTaskLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PjTaskLikeMapper extends BaseMapper<PjTaskLike> {
    // 根据任务id删除任务下的点赞
    void realDeleteByTaskIds(@Param("taskIds") List<Long> taskIds);
    // 根据任务id查询点赞数
    Long selectLikeCountByTaskId(Long id);
}
