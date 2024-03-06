package cn.oneao.project.domain.vo.project;

import cn.oneao.common.core.domain.entity.SysUser;
import cn.oneao.project.domain.PjProject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PjIndexVO {
    private SysUser sysUser;
//    private
    private List<PjProject> projectList;
    private Integer toDoCount;  // 代办任务数
    private Integer allCount;   // 项目总数
    private List<ProjectIndexLogVO> logList;// 项目日志列表
}
