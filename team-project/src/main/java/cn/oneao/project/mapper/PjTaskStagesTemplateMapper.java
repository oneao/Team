package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjTaskStagesTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface PjTaskStagesTemplateMapper extends BaseMapper<PjTaskStagesTemplate> {
    void deleteBatchByPjTemplateIds(List<Long> list);
}
