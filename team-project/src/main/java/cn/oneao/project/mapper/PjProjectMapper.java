package cn.oneao.project.mapper;

import cn.oneao.common.core.controller.BaseController;
import cn.oneao.common.core.domain.entity.SysUser;
import cn.oneao.project.domain.PjProject;
import cn.oneao.project.domain.dto.recycle.RecycleProjectDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface PjProjectMapper extends BaseMapper<PjProject> {
    int selectCountByName(String name);
    List<PjProject> selectPjProjectListByUserId(Long userId);
    // 根据项目id获取用户
    List<SysUser> selectSysUserListByProjectId(Long id);
    // 查询回收站的项目
    List<PjProject> getRecycleList(RecycleProjectDTO recycleProjectDTO);
    // 恢复项目
    void recovery(@Param("ids") Long[] ids);
    // 彻底删除项目
    void realDelete(@Param("ids") List<Long> ids);
    // 获取回收站的项目数量
    Long getRecycleListCount(RecycleProjectDTO recycleProjectDTO);
    // 查询回收站项目列表
    List<PjProject> selectDeleteProject(@Param("idList") List<Long> idList);
    // 根据任务id获取项目
    PjProject selectPjProjectByTaskId(Long taskId);
}
