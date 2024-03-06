package cn.oneao.project.service;

import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjReport;
import cn.oneao.project.domain.dto.report.PjReportListInTaskDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjReportService extends IService<PjReport> {
    MpPageVO getList(PjReport pjReport);
    // 获取自己的报告列表
    MpPageVO getSelfList(PjReport pjReport);
    //
    List<PjReport> getListByProjectId(PjReportListInTaskDTO pjReportListInTaskDTO);
}
