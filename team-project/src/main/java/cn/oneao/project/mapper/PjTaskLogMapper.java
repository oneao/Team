package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjTaskLog;
import cn.oneao.project.domain.vo.project.ProjectIndexLogVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PjTaskLogMapper extends BaseMapper<PjTaskLog> {
    List<ProjectIndexLogVO> selectIndexLog(@Param("taskIdList") List<Long> taskIdList, @Param("projectIdList") List<Long> projectIdList);
}
