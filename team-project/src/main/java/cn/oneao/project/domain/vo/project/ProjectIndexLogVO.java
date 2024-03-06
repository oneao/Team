package cn.oneao.project.domain.vo.project;

import com.alibaba.druid.filter.AutoLoad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectIndexLogVO {
    private Integer isTask; // 是否是任务(0:否,1:是)
    private Integer operationType; // 操作类型
    private Integer type; // 类型
    private String content; // 内容
    private String createBy; // 创建人
    private Date createTime; // 创建时间
    private String avatar; // 头像
    private String name;// 任务或项目名称
}
