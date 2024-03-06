package cn.oneao.project.domain.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjReportListInTaskDTO {
    private Long projectId;
    private Long userId;
    private Date startTime;
    private Date endTime;
}
