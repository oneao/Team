package cn.oneao.project.controller;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjProjectLog;
import cn.oneao.project.domain.dto.log.PjProjectLogByProjectIdDTO;
import cn.oneao.project.service.IPjProjectLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/project/projectLog")
@Validated
public class PjProjectLogController {
    @Autowired
    private IPjProjectLogService pjProjectLogService;

    @GetMapping("/listByProjectId")
    public AjaxResult listByProjectId(PjProjectLogByProjectIdDTO pjProjectLogByProjectIdDTO) {
        MpPageVO mpPageVO = pjProjectLogService.listByProjectId(pjProjectLogByProjectIdDTO);
        return AjaxResult.success(mpPageVO);
    }
}
