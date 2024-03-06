package cn.oneao.project.domain.dto.project;

import cn.oneao.common.core.page.MpPageDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjProjectArchiveListDTO extends MpPageDomain {
    private String name;
    private Date archiveTime;
}
