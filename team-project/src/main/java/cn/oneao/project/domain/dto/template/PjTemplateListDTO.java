package cn.oneao.project.domain.dto.template;

import cn.oneao.common.core.page.MpPageDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PjTemplateListDTO extends MpPageDomain{
    private String name;
    private Integer isSystem;
}
