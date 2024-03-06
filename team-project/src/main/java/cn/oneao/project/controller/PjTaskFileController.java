package cn.oneao.project.controller;

import cn.oneao.common.config.TeamConfig;
import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.common.utils.file.FileUploadUtils;
import cn.oneao.common.utils.file.FileUtils;
import cn.oneao.framework.config.ServerConfig;
import cn.oneao.project.constants.OperationConstants;
import cn.oneao.project.constants.TaskTypeConstants;
import cn.oneao.project.domain.PjTaskFile;
import cn.oneao.project.domain.PjTaskLog;
import cn.oneao.project.domain.dto.task.PjTaskFileUpdateDTO;
import cn.oneao.project.mapper.PjTaskLogMapper;
import cn.oneao.project.service.IPjTaskFileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/project/taskFile")
@Slf4j
public class PjTaskFileController {
    @Autowired
    private IPjTaskFileService pjTaskFileService;
    @Autowired
    private PjTaskLogMapper pjTaskLogMapper;
    @Autowired
    private ServerConfig serverConfig;

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("pjTaskId") Long pjTaskId) throws Exception {
        try {
            PjTaskFile pjTaskFile = new PjTaskFile();
            pjTaskFile.setPjTaskId(pjTaskId);
            // 上传文件路径
            String filePath = TeamConfig.getUploadPath();
            String originalFilename = file.getOriginalFilename();
            pjTaskFile.setFileName(originalFilename);
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            // 添加到任务文件表
            int dateEndIndex = fileName.indexOf("/", fileName.indexOf("/", fileName.indexOf("/") + 1) + 1);
            String result = filePath + fileName.substring(dateEndIndex);
            pjTaskFile.setPhysicalPath(result);
            pjTaskFile.setFilePath(url);
            assert originalFilename != null;
            pjTaskFile.setFileSuffix(getFileExtension(originalFilename));
            pjTaskFile.setFileSize(file.getSize());
            pjTaskFileService.save(pjTaskFile);
            // 添加操作日志
            PjTaskLog pjTaskLog = new PjTaskLog();
            pjTaskLog.setOperationType(OperationConstants.ADD);
            pjTaskLog.setPjTaskId(pjTaskId);
            pjTaskLog.setType(TaskTypeConstants.FILE);
            pjTaskLog.setContent(SecurityUtils.getUsername() + "上传了文件：" + originalFilename);
            pjTaskLogMapper.insert(pjTaskLog);
            // 返回成功
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    // 获取文件后缀名的方法
    private String getFileExtension(String originalFilename) {
        // 获取文件名
        // 获取最后一个"."出现的位置
        int lastDotIndex = originalFilename.lastIndexOf(".");
        // 如果文件名中没有"."或者"."在文件名的开头或结尾，则返回空字符串
        if (lastDotIndex == -1 || lastDotIndex == 0 || lastDotIndex == originalFilename.length() - 1) {
            return "";
        }
        // 返回文件名中最后一个"."后面的部分作为后缀名
        return originalFilename.substring(lastDotIndex + 1);
    }

    @DeleteMapping("{fileId}")
    public AjaxResult removeFile(@PathVariable("fileId") Long id) {
        try {
            PjTaskFile pjTaskFile = pjTaskFileService.getById(id);
            if (pjTaskFile != null) {
                String filePath = pjTaskFile.getFilePath();
                FileUtils.deleteFile(filePath);
                pjTaskFileService.removeById(id);
                // 添加操作日志
                PjTaskLog pjTaskLog = new PjTaskLog();
                pjTaskLog.setOperationType(OperationConstants.DELETE);
                pjTaskLog.setPjTaskId(pjTaskFile.getPjTaskId());
                pjTaskLog.setType(TaskTypeConstants.FILE);
                pjTaskLog.setContent(SecurityUtils.getUsername() + "移除了文件：" + pjTaskFile.getFileName());
                pjTaskLogMapper.insert(pjTaskLog);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/{projectId}")
    public AjaxResult getFileRecycle(@PathVariable Long projectId) {
        return AjaxResult.success(pjTaskFileService.getListByProjectId(projectId));
    }

    @PutMapping("/renameFile")
    public AjaxResult renameFile(@RequestBody PjTaskFileUpdateDTO pjTaskFileUpdateDTO) {
        return AjaxResult.success(pjTaskFileService.renameFile(pjTaskFileUpdateDTO));
    }

    @PutMapping("/deleteFile")
    public AjaxResult deleteFile(@RequestBody PjTaskFileUpdateDTO pjTaskFileUpdateDTO) {
        return AjaxResult.success(pjTaskFileService.deleteFileByFileId(pjTaskFileUpdateDTO));
    }
    @DeleteMapping("/deleteFile/{ids}")
    public AjaxResult deleteFile(@PathVariable Long[] ids) {
        for (Long id : ids) {
            PjTaskFile pjTaskFile = pjTaskFileService.getById(id);
            if (pjTaskFile != null) {
                pjTaskFileService.removeById(id);
                // 添加操作日志
                PjTaskLog pjTaskLog = new PjTaskLog();
                pjTaskLog.setOperationType(OperationConstants.DELETE);
                pjTaskLog.setPjTaskId(pjTaskFile.getPjTaskId());
                pjTaskLog.setType(TaskTypeConstants.FILE);
                pjTaskLog.setContent(SecurityUtils.getUsername() + "移除了文件：" + pjTaskFile.getFileName());
                pjTaskLogMapper.insert(pjTaskLog);
            }
        }
        return AjaxResult.success();
    }
    @GetMapping("/getFileList")
    public AjaxResult getFileList(PjTaskFile pjTaskFile) {
        MpPageVO mpPageVO = pjTaskFileService.getFileList(pjTaskFile);
        return AjaxResult.success(mpPageVO);
    }

    @GetMapping("/getRecycleFile")
    public AjaxResult getRecycleFileList(PjTaskFile pjTaskFile) {
        MpPageVO mpPageVO = pjTaskFileService.getRecycleFile(pjTaskFile);
        return AjaxResult.success(mpPageVO);
    }
}
