package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PjTemplateMapper extends BaseMapper<PjTemplate> {
    int selectCountByName(String name);
}
