package cn.oneao.project.mapper;

import cn.oneao.project.domain.PjTaskFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PjTaskFileMapper extends BaseMapper<PjTaskFile> {
    int deleteFileByTaskId(Long id);

    int recoverFile(Long id);

    PjTaskFile getByTaskFileById(Long id);

    int deleteFileByFileid(Long id);

    List<PjTaskFile>  getListByProjectId(Long projectId);
    // 根据任务id删除任务下的文件
    void realDeleteByTaskIds(@Param("taskIds") List<Long> taskIds);

    List<PjTaskFile> getFileList(PjTaskFile pjTaskFile);
    Long getFileListCount(PjTaskFile pjTaskFile);

    List<PjTaskFile> getRecycleFile(PjTaskFile pjTaskFile);

    Long getRecycleFileCount(PjTaskFile pjTaskFile);
}
