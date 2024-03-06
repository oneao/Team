package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PjReportMapper extends BaseMapper<PjReport> {
    int recoveryReport(List<Long> ids);


    List<PjReport> selectRecycleList(PjReport pjReport);

    Long selectRecycleCount(PjReport pjReport);
}
