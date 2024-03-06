package cn.oneao.project.domain.vo.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PjTaskLogAndCommentVO {
    private Long pjTaskCommentId; // 任务评论id
    private Integer type;// 操作类型 0：任务名称 1：任务状态 2：任务紧急程度 3：任务排序 4：任务描述 5：指定人 6：更新时间范围，7：子任务
    private Integer operationType;// 操作类型 0：添加 1：更新 2：删除 3：完成
    private String content;// 操作内容
    private String createBy;// 操作人
    private String avatar;// 头像
    private Date createTime;// 操作时间
    private Integer isLog;// 是否是日志 0：否 1：是
}
