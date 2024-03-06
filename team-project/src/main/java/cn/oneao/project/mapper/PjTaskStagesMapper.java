package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjTaskStages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
public interface PjTaskStagesMapper extends BaseMapper<PjTaskStages> {
    // 批量插入
    void insertBatch(List<PjTaskStages> pjTaskStagesList);

    int selectCountByName(@Param("taskStageName") String taskStageName, @Param("pjProjectId") Long pjProjectId);
    // 根据项目id删除项目下的任务阶段
    void realDeleteByProjectIds(@Param("ids") List<Long> ids);
}

