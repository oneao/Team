package cn.oneao.project.domain.dto.log;

import cn.oneao.common.core.page.MpPageDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjProjectLogByProjectIdDTO extends MpPageDomain {
    private Long projectId;
}
