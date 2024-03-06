package cn.oneao.project.service;

import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjProjectLog;
import cn.oneao.project.domain.dto.log.PjProjectLogByProjectIdDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjProjectLogService extends IService<PjProjectLog> {
    // 查询项目日志列表
    MpPageVO listByProjectId(PjProjectLogByProjectIdDTO pjProjectLogByProjectIdDTO);
}
