package cn.oneao.project.service.impl;

import cn.oneao.project.domain.PjTaskLog;
import cn.oneao.project.domain.vo.project.ProjectIndexLogVO;
import cn.oneao.project.mapper.PjTaskLogMapper;
import cn.oneao.project.service.IPjTaskLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IPjTaskLogServiceImpl extends ServiceImpl<PjTaskLogMapper, PjTaskLog> implements IPjTaskLogService {
    @Autowired
    private PjTaskLogMapper pjTaskLogMapper;
    @Override
    public List<ProjectIndexLogVO> selectIndexLog(List<Long> pjTaskIdList, List<Long> projectIdList) {
        return pjTaskLogMapper.selectIndexLog(pjTaskIdList,projectIdList);
    }
}
