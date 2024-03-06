package cn.oneao.project.domain.dto.project;

import cn.oneao.common.core.page.MpPageDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PjProjectListDTO extends MpPageDomain {
    private String name;
    private Integer status;
    private Date beginTime;
    private Date endTime;
}
