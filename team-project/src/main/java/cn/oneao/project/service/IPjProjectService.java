package cn.oneao.project.service;

import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjProject;
import cn.oneao.project.domain.dto.project.PjProjectArchiveListDTO;
import cn.oneao.project.domain.dto.project.PjProjectListDTO;
import cn.oneao.project.domain.dto.project.PjProjectAddDTO;
import cn.oneao.project.domain.dto.recycle.RecycleProjectDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjProjectService extends IService<PjProject> {
    // 查询项目列表
    MpPageVO selectPjProjectList(PjProjectListDTO pjProjectListDTO);
    // 新增项目
    int insertPjProject(PjProjectAddDTO pjProjectAddDTO);
    // 判断是否有相同的项目名
    boolean isHasSameName(String name);
    // 修改项目
    int updatePjProject(PjProject pjProject);
    // 删除项目
    AjaxResult deletePjProjectByIds(Long[] ids);
    // 查询所有项目列表
    List<PjProject> selectPjProjectListAll();
    // 获取回收站的项目详细信息
    MpPageVO getRecycleList(RecycleProjectDTO recycleProjectDTO);
    //  恢复项目
    void recovery(Long[] ids);
    // 彻底删除项目
    void realDelete(Long[] ids);
    // 根据任务id获取项目
    PjProject selectPjProjectByTaskId(Long taskId);
    // 查询收藏项目列表
    MpPageVO selectCollectionList(PjProjectListDTO pjProjectListDTO);
    // 更新收藏项目
    int updateCollection(Long[] projectIds);
    // 更新项目归档
    int updateArchive(Long[] projectIds);
    // 查询归档项目列表
    MpPageVO selectArchiveList(PjProjectArchiveListDTO pjProjectArchiveListDTO);
}
