package cn.oneao.project.service.impl;

import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.constants.ProjectTypeConstants;
import cn.oneao.project.domain.PjProjectLog;
import cn.oneao.project.domain.dto.log.PjProjectLogByProjectIdDTO;
import cn.oneao.project.mapper.PjProjectLogMapper;
import cn.oneao.project.service.IPjProjectLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IPjProjectLogServiceImpl extends ServiceImpl<PjProjectLogMapper, PjProjectLog> implements IPjProjectLogService {
    @Autowired
    private PjProjectLogMapper pjProjectLogMapper;
    @Override
    public MpPageVO listByProjectId(PjProjectLogByProjectIdDTO pjProjectLogByProjectIdDTO) {
        MpPageVO mpPageVO = new MpPageVO();
        Integer pageNum = pjProjectLogByProjectIdDTO.getPageNum();
        Integer pageSize = pjProjectLogByProjectIdDTO.getPageSize();

        Page<PjProjectLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PjProjectLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PjProjectLog::getPjProjectId, pjProjectLogByProjectIdDTO.getProjectId());
        queryWrapper.ne(PjProjectLog::getType, ProjectTypeConstants.USER);
        queryWrapper.ne(PjProjectLog::getType, ProjectTypeConstants.PROJECT);
        queryWrapper.ne(PjProjectLog::getType, ProjectTypeConstants.COLLECTION);
        queryWrapper.orderByDesc(PjProjectLog::getCreateTime);  // 按创建时间倒序
        List<PjProjectLog> pjProjectLogList = pjProjectLogMapper.selectPage(page, queryWrapper).getRecords();
        mpPageVO.setRows(pjProjectLogList);
        mpPageVO.setTotal(page.getTotal());
        return mpPageVO;
    }
}
