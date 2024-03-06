package cn.oneao.project.service;

import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjTaskFile;
import cn.oneao.project.domain.dto.task.PjTaskFileUpdateDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjTaskFileService extends IService<PjTaskFile> {
    List<PjTaskFile> getListByProjectId(Long projectId);

    int renameFile(PjTaskFileUpdateDTO pjTaskFileUpdateDTO);

    int deleteFileByFileId(PjTaskFileUpdateDTO pjTaskFileUpdateDTO);

    MpPageVO getFileList(PjTaskFile pjTaskFile);

    MpPageVO getRecycleFile(PjTaskFile pjTaskFile);
}
