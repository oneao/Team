package cn.oneao.project.service.impl;

import cn.oneao.project.domain.PjTaskComment;
import cn.oneao.project.mapper.PjTaskCommentMapper;
import cn.oneao.project.service.IPjTaskCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class IPjTaskCommentServiceImpl extends ServiceImpl<PjTaskCommentMapper, PjTaskComment> implements IPjTaskCommentService {
}
