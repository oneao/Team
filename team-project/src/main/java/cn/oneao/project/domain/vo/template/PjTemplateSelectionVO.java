package cn.oneao.project.domain.vo.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjTemplateSelectionVO {
    private String label;
    private Long value;
    private String cover;
    private Integer isSystem;
}
