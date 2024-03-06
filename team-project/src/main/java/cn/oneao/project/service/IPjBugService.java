package cn.oneao.project.service;

import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjBug;
import cn.oneao.project.domain.PjProject;
import cn.oneao.project.domain.PjTask;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IPjBugService extends IService<PjBug> {
    List<PjProject> getOption();

    boolean addBug(PjBug pjBug);

    MpPageVO getList(PjBug pjBug);

    List<PjProject> getAllOption();

    List<PjBug> getMyBug();
    // 申报
   MpPageVO getBugDeclare(PjBug pjBug);
    // 派发
    MpPageVO getBugDistribute(PjBug pjBug);
}
