package cn.oneao.project.service;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjTask;
import cn.oneao.project.domain.dto.task.PjTaskAddDTO;
import cn.oneao.project.domain.dto.task.PjTaskChildAddDTO;
import cn.oneao.project.domain.vo.task.PjTaskFileVO;
import cn.oneao.project.domain.vo.task.PjTaskListVO;
import cn.oneao.project.domain.vo.task.PjTaskLogAndCommentVO;
import cn.oneao.project.domain.vo.task.PjTaskRecycleVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjTaskService extends IService<PjTask> {
    // 根据项目id查询任务列表
    List<PjTaskListVO> getTaskListByProjectId(Long projectId);
    // 删除项目任务列表时，先检查是否有子任务
    boolean hasTask(Long taskStageId);
    // 新增任务
    AjaxResult addTask(PjTaskAddDTO pjTaskAddDTO);
    // 根据id获取任务
    PjTask getTaskById(Long id);
    // 更新任务
    void updateTaskById(PjTask pjTask);
    // 新增子任务
    AjaxResult addChildTask(PjTaskChildAddDTO pjTaskChildAddDTO);
    // 是否还有子任务未完成
    boolean hasChildTask(Long taskId);
    // 获取任务日志
    List<PjTaskLogAndCommentVO> getTaskLog(Long taskId);
    // 获取任务评论
    List<PjTaskLogAndCommentVO>  getTaskComment(Long taskId);
    // 获取回收站任务
    List<PjTaskRecycleVO> getTaskRecycle(Long projectId);
    // 删除任务
    int realRemoveById(Long[] id);
    // 恢复任务
    int revoverTask(Long[] id);
    // 获取任务文件
    List<PjTaskFileVO> getFileRecycle(Long projectId);
    // 恢复文件
    int recoverFile(Long[] id);
    // 删除文件
    int realDeleteFileById(Long[] id);
    // 查询所有任务
    MpPageVO getTaskList(PjTask pjTask);
    // 查询回收站任务
    MpPageVO getRecycleTask(PjTask pjTask);

    List<PjTask> listByProjectId(Long projectId);

    PjTask selectTaskById(Long id);
    // 根据任务id获取任务阶段
    String getTaskStageByTaskId(Long taskId);
}
