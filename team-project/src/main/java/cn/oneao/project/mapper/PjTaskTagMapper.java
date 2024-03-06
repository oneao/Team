package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjTaskTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PjTaskTagMapper extends BaseMapper<PjTaskTag> {
    // 根据任务id删除任务下的标签
    void realDeleteByTaskIds(@Param("taskIds") List<Long> taskIds);
}
