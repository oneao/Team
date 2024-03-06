package cn.oneao.project.mapper;

import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjTask;
import cn.oneao.project.domain.vo.task.PjTaskFileVO;
import cn.oneao.project.domain.vo.task.PjTaskRecycleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PjTaskMapper extends BaseMapper<PjTask> {
    // 获取回收站任务
    List<PjTaskRecycleVO> getTaskRecycle(Long projectId);
    // 删除任务
    int realDeleteTaskById(Long id);
    // 恢复任务
    int recoverTask(Long id);
    // 根据id获取任务
    PjTask getTaskById(Long id);
    // 获取任务文件
    List<PjTaskFileVO> getFileRecycle(Long projectId);
    // 根据项目id获取任务
    List<PjTask> selectPjTaskListByProjectId(Long id);
    // 根据项目列表id删除项目下的任务
    void realDeleteByTaskStageIds(@Param("pjTaskStageIds") List<Long> pjTaskStageIds);
    // 查询所有任务
    List<PjTask> getTaskList(PjTask pjTask);

    Long getTaskListCount(PjTask pjTask);
    // 查询回收站任务
    List<PjTask> getRecycleTask(PjTask pjTask);

    Long getAllRecycleTaskCount(PjTask pjTask);
    // 根据项目id查询任务列表
    PjTask selectTaskById(Long id);
}
