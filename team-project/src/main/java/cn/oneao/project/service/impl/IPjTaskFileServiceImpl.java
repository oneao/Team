package cn.oneao.project.service.impl;

import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.ProjectTypeConstants;
import cn.oneao.project.domain.PjProjectLog;
import cn.oneao.project.domain.PjTaskFile;
import cn.oneao.project.domain.dto.task.PjTaskFileUpdateDTO;
import cn.oneao.project.mapper.PjProjectLogMapper;
import cn.oneao.project.mapper.PjTaskFileMapper;
import cn.oneao.project.service.IPjTaskFileService;
import cn.oneao.project.utils.SeUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IPjTaskFileServiceImpl extends ServiceImpl<PjTaskFileMapper, PjTaskFile> implements IPjTaskFileService {
    @Autowired
    private PjTaskFileMapper pjTaskFileMapper;
    @Autowired
    private PjProjectLogMapper pjProjectLogMapper;
    @Autowired
    private SeUtils seUtils;
    @Override
    public List<PjTaskFile> getListByProjectId(Long projectId) {
        List<PjTaskFile> listByProjectId = pjTaskFileMapper.getListByProjectId(projectId);
        listByProjectId.forEach(item ->{
            item.setFileSizeStr(changeFileSize(item.getFileSize()));
        });
        return listByProjectId;
    }

    @Override
    public int renameFile(PjTaskFileUpdateDTO pjTaskFileUpdateDTO) {
        String fileName = pjTaskFileUpdateDTO.getFileName();
        Long id = pjTaskFileUpdateDTO.getFileId();
        PjTaskFile pjTaskFile = this.getById(id);
        String oldFileName = pjTaskFile.getFileName();
        fileName = fileName + "." + pjTaskFile.getFileSuffix();
        pjTaskFile.setFileName(fileName);
        boolean b = this.updateById(pjTaskFile);
        PjProjectLog pjProjectLog = new PjProjectLog();
        pjProjectLog.setOperationType(OperationConstants.UPDATE);
        pjProjectLog.setType(ProjectTypeConstants.FILE);
        pjProjectLog.setPjProjectId(pjTaskFileUpdateDTO.getProjectId());
        pjProjectLog.setContent(SecurityUtils.getUsername() + "将文件名<" + oldFileName + ">修改为<" + fileName +">");
        pjProjectLogMapper.insert(pjProjectLog);
        return b ? 1 : 0;
    }

    @Override
    public int deleteFileByFileId(PjTaskFileUpdateDTO pjTaskFileUpdateDTO) {
        Long fileId = pjTaskFileUpdateDTO.getFileId();
        Long projectId = pjTaskFileUpdateDTO.getProjectId();
        PjTaskFile pjTaskFile = this.getById(fileId);
        String fileName = pjTaskFile.getFileName();
        boolean b = this.removeById(fileId);
        PjProjectLog pjProjectLog = new PjProjectLog();
        pjProjectLog.setOperationType(OperationConstants.DELETE);
        pjProjectLog.setType(ProjectTypeConstants.FILE);
        pjProjectLog.setPjProjectId(projectId);
        pjProjectLog.setContent(SecurityUtils.getUsername() + "删除了文件<" + fileName + ">");
        pjProjectLogMapper.insert(pjProjectLog);
        return b ? 1 : 0;
    }

    @Override
    public MpPageVO getFileList(PjTaskFile pjTaskFile) {
        MpPageVO mpPageVO = new MpPageVO();
        List<Long> seProjectIds = seUtils.getSeProjectIds();
        if (seProjectIds.isEmpty()) {
            return mpPageVO;
        }
        pjTaskFile.setSeProjectIds(seProjectIds);
        pjTaskFile.setStart(pjTaskFile.getStart() - 1);
        List<PjTaskFile> list = pjTaskFileMapper.getFileList(pjTaskFile);
        list.forEach(item ->{
            item.setFileSizeStr(changeFileSize(item.getFileSize()));
        });
        Long fileListCount = pjTaskFileMapper.getFileListCount(pjTaskFile);
        mpPageVO.setRows(list);
        mpPageVO.setTotal(fileListCount);
        return mpPageVO;
    }

    @Override
    public MpPageVO getRecycleFile(PjTaskFile pjTaskFile) {
        MpPageVO mpPageVO = new MpPageVO();
        List<Long> seProjectIds = seUtils.getSeProjectIds();
        if (seProjectIds.isEmpty()) {
            return mpPageVO;
        }
        pjTaskFile.setSeProjectIds(seProjectIds);
        pjTaskFile.setStart(pjTaskFile.getStart() - 1);
        List<PjTaskFile> list = pjTaskFileMapper.getRecycleFile(pjTaskFile);
        list.forEach(item ->{
            item.setFileSizeStr(changeFileSize(item.getFileSize()));
        });
        Long fileListCount = pjTaskFileMapper.getRecycleFileCount(pjTaskFile);
        mpPageVO.setRows(list);
        mpPageVO.setTotal(fileListCount);
        return mpPageVO;
    }

    private String changeFileSize(Long fileSize) {
        if (fileSize < 1024) {
            return fileSize + "B";
        } else if (fileSize < 1024 * 1024) {
            return fileSize / 1024 + "KB";
        } else if (fileSize < 1024 * 1024 * 1024) {
            return fileSize / 1024 / 1024 + "MB";
        }else {
            return fileSize / 1024 / 1024 / 1024 + "GB";
        }
    }
}
