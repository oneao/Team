package cn.oneao.project.controller;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.domain.PjReport;
import cn.oneao.project.domain.dto.report.PjReportListInTaskDTO;
import cn.oneao.project.service.IPjReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/project/report")
public class PjReportController {
    @Autowired
    private IPjReportService pjReportService;

    // 获取报告列表
    @GetMapping
    public AjaxResult listReport(PjReport pjReport) {
        MpPageVO mpPageVO = pjReportService.getList(pjReport);
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 获取自己的报告列表
     *
     * @param pjReport 报告
     * @return 报告列表
     */
    @GetMapping("/my")
    public AjaxResult listSelfReport(PjReport pjReport) {
        MpPageVO mpPageVO = pjReportService.getSelfList(pjReport);
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 添加报告
     *
     * @param pjReport 报告
     * @return 添加结果
     */
    @PostMapping
    public AjaxResult addReport(@RequestBody PjReport pjReport) {
        LambdaQueryWrapper<PjReport> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = SecurityUtils.getUserId();
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN); // 今天的开始时间
        LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX); // 今天的结束时间

        queryWrapper.eq(PjReport::getUserId, userId);
        queryWrapper.between(PjReport::getCreateTime, startOfToday, endOfToday);

        int count = (int) pjReportService.count(queryWrapper);
        if (count > 0) {
            return AjaxResult.error("今天已经提交过报告");
        }
        pjReport.setUserId(userId);
        return AjaxResult.success(pjReportService.save(pjReport));
    }

    /**
     * 获取日报
     * @param id 日报id
     * @return 日报
     */
    @GetMapping("{id}")
    public AjaxResult getReport(@PathVariable("id") Long id) {
        return AjaxResult.success(pjReportService.getById(id));
    }
    /**
     * 修改报告
     *
     * @param pjReport 报告
     * @return 修改结果
     */
    @PutMapping
    public AjaxResult updateReport(@RequestBody PjReport pjReport) {
        PjReport pjReportById = pjReportService.getById(pjReport.getId());
        Date createTime = pjReportById.getCreateTime();
        LocalDateTime localCreateTime = createTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN); // 今天的开始时间
        LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX); // 今天的结束时间

        if (localCreateTime.isBefore(startOfToday) || localCreateTime.isAfter(endOfToday)) {
            return AjaxResult.error("只能修改今天的报告");
        }

        return AjaxResult.success(pjReportService.updateById(pjReport));
    }

    /**
     * 彻底删除报告
     *
     * @param ids 报告id
     * @return 删除结果
     */
    @DeleteMapping("{ids}")
    public AjaxResult deleteReport(@PathVariable("ids") Long[] ids) {
            for (Long id : ids) {
                PjReport pjReport = pjReportService.getById(id);
                Date createTime = pjReport.getCreateTime();
                LocalDateTime localCreateTime = createTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN); // 今天的开始时间
                LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX); // 今天的结束时间

                if (localCreateTime.isBefore(startOfToday) || localCreateTime.isAfter(endOfToday)) {
                    return AjaxResult.error("只能删除今天的报告");
                }
            }
        return AjaxResult.success(pjReportService.removeByIds(Arrays.asList(ids)));
    }

    // 获取报告列表
    @GetMapping("/getListInTask")
    public AjaxResult getListByProjectId(PjReportListInTaskDTO pjReportListInTaskDTO) {
        return AjaxResult.success(pjReportService.getListByProjectId(pjReportListInTaskDTO));
    }
}
