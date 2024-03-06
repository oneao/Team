package cn.oneao.project.service;

import cn.oneao.project.domain.PjTaskTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjTaskTagService extends IService<PjTaskTag> {
    // 根据任务id获取任务标签列表
    List<PjTaskTag> getListByTaskId(Long id);
}
