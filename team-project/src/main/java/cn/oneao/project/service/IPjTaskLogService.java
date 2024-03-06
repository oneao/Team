package cn.oneao.project.service;

import cn.oneao.project.domain.PjTaskLog;
import cn.oneao.project.domain.vo.project.ProjectIndexLogVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjTaskLogService extends IService<PjTaskLog> {
    List<ProjectIndexLogVO> selectIndexLog(List<Long> pjTaskIdList, List<Long> projectIdList);
}
