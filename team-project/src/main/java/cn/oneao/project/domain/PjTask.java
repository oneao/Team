package cn.oneao.project.domain;

import cn.oneao.common.core.page.MpPageDomain;
import cn.oneao.project.domain.common.PageSqlDomain;
import cn.oneao.project.domain.vo.task.PjTaskLogAndCommentVO;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("pj_task")
public class PjTask extends PageSqlDomain {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id; // 任务阶段id
    private Long pjTaskStagesId; // 任务阶段id
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private Long parentId; // 父任务id
    private String name;// 任务名称
    private String description;// 任务描述
    private Integer sortNum; // 排序号
    private Integer status; // 任务状态（0：未开始，1：进行中，2：已结束）
    private Integer urgency;// 紧急程度（0：一般，1：紧急，2：非常紧急）
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
    /** 更新人 */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
    @TableField(updateStrategy=FieldStrategy.IGNORED) // 可以设置为空
    private Date beginTime; // 开始时间
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private Date endTime; // 结束时间
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private Date realEndTime; // 实际结束时间
    private Long projectUserId;// 项目成员id(指派人)
    @TableField(exist = false)
    private String assignUserName;// 指派人名称
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;
    @TableField(exist = false)
    private List<PjTask> childTaskList; // 子任务列表
    @TableField(exist = false)
    private Integer taskType; // 操作类型
    @TableField(exist = false)
    private List<PjTaskTag> tagList; // 任务标签列表
    @TableField(exist = false)
    private List<PjTaskLogAndCommentVO> logAndCommentList; // 任务日志和评论列表
    @TableField(exist = false)
    private Integer logAndCommentSize; // 任务日志和评论列表
    @TableField(exist = false)
    private Integer commentNum; // 评论数
    @TableField(exist = false)
    private List<PjTaskFile> fileList; // 任务文件列表
    @TableField(exist = false)
    private Integer fileNum; // 任务文件列表
    @TableField(exist = false)
    private Integer isLike; // 是否点赞（0：否，1：是）
    @TableField(exist = false)
    private Integer likeNum; // 点赞数
    @TableField(exist = false)
    private String parentName; // 父任务名称
    @TableField(exist = false)
    private Long pjProjectId;// 项目id
    @TableField(exist = false)
    private String pjProjectName; // 项目名称
    @TableField(exist = false)
    private List<Long> seProjectIds; // 项目id列表
}
