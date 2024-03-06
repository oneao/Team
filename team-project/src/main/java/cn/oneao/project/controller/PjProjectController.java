package cn.oneao.project.controller;

import cn.oneao.common.annotation.Log;
import cn.oneao.common.core.controller.BaseController;
import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.enums.BusinessType;
import cn.oneao.project.domain.PjProject;
import cn.oneao.project.domain.dto.project.PjProjectArchiveListDTO;
import cn.oneao.project.domain.dto.project.PjProjectListDTO;
import cn.oneao.project.domain.dto.project.PjProjectAddDTO;
import cn.oneao.project.domain.dto.recycle.RecycleProjectDTO;
import cn.oneao.project.service.IPjProjectService;
import cn.oneao.project.service.IPjUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/project/project")
@Validated
public class PjProjectController extends BaseController {

    @Autowired
    private IPjProjectService pjProjectService;
    @Autowired
    private IPjUserService pjUserService;
    /**
     * 查询项目列表
     */
    @PreAuthorize("@ss.hasPermi('project:project:list')")
    @GetMapping("/list")
    public AjaxResult list(PjProjectListDTO pjProjectListDTO) {
        MpPageVO mpPageVO = pjProjectService.selectPjProjectList(pjProjectListDTO);
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 查询所有项目列表
     */
    @PreAuthorize("@ss.hasPermi('project:project:list')")
    @GetMapping("/listAll")
    public AjaxResult listAll() {
        List<PjProject> list = pjProjectService.selectPjProjectListAll();
        return AjaxResult.success(list);
    }

    /**
     * 获取项目详细信息
     */
    @PreAuthorize("@ss.hasPermi('project:project:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PositiveOrZero(message = "获取项目管理详细信息有误，原因：传入id有误") @PathVariable("id") Long id) {
        return success(pjProjectService.getById(id));
    }

    /**
     * 新增项目
     */
    @PreAuthorize("@ss.hasPermi('project:project:add')")
    @Log(title = "项目管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PjProjectAddDTO pjProjectAddDTO) {
        String name = pjProjectAddDTO.getName();
        if (pjProjectService.isHasSameName(name)) {
            return error("新增项目'" + name + "'失败，项目名称已存在");
        }
        return toAjax(pjProjectService.insertPjProject(pjProjectAddDTO));
    }

    /**
     * 修改项目
     */
    @PreAuthorize("@ss.hasPermi('project:project:edit')")
    @Log(title = "项目管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated PjProject pjProject) {
        String name = pjProject.getName();
        Long id = pjProject.getId();
        PjProject projectServiceById = pjProjectService.getById(id);

        if (projectServiceById != null && !name.equals(projectServiceById.getName()) && pjProjectService.isHasSameName(name)) {
            return error("修改项目'" + name + "'失败，项目名称已存在");
        }

        return toAjax(pjProjectService.updatePjProject(pjProject));
    }

    /**
     * 删除项目
     */
    @PreAuthorize("@ss.hasPermi('project:project:remove')")
    @Log(title = "项目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@Size(min = 1,message = "删除项目模版不能为空") @PathVariable Long[] ids) {
        return pjProjectService.deletePjProjectByIds(ids);
    }

    /**
     * 查询回收站列表
     * @param recycleProjectDTO 回收站查询条件
     * @return 回收站列表
     */
    @GetMapping("/getRecycle")
    public  AjaxResult getRecycleList(RecycleProjectDTO recycleProjectDTO){
        recycleProjectDTO.setStart(recycleProjectDTO.getStart() - 1);
        MpPageVO mpPageVO = pjProjectService.getRecycleList(recycleProjectDTO);
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 恢复项目
     * @param ids 项目ids
     * @return 返回结果
     */
    @PutMapping("/recovery/{ids}")
    public AjaxResult recovery(@Size(min = 1,message = "恢复项目模版不能为空") @PathVariable Long[] ids){
        pjProjectService.recovery(ids);
        return AjaxResult.success();
    }

    /**
     * 真实删除项目
     * @param ids 项目ids
     * @return 返回结果
     */
    @DeleteMapping("/realDelete/{ids}")
    public AjaxResult realDelete(@Size(min = 1,message = "删除项目模版不能为空") @PathVariable Long[] ids){
        pjProjectService.realDelete(ids);
        return AjaxResult.success();
    }

    /**
     * 更新收藏项目
     * @param projectIds 项目ids
     * @return 返回结果
     */
    @PutMapping("/updateCollection/{projectIds}")
    public AjaxResult updateCollection(@PathVariable("projectIds") Long[] projectIds) {
        return AjaxResult.success( pjProjectService.updateCollection(projectIds));
    }

    /**
     * 查询收藏的项目
     * @return 返回结果
     */
    @GetMapping("collection")
    public AjaxResult collection(PjProjectListDTO pjProjectListDTO) {
        MpPageVO mpPageVO = pjProjectService.selectCollectionList(pjProjectListDTO);
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 更新项目归档(恢复或取消归档)
     * @param projectIds 项目ids
     * @return 返回结果
     */
    @PutMapping("/updateArchive/{projectIds}")
    public AjaxResult updateArchive(@PathVariable("projectIds") Long[] projectIds) {
        return AjaxResult.success(pjProjectService.updateArchive(projectIds));
    }
    /**
     * 查询归档的项目
     * @return 返回结果
     */
    @GetMapping("archive")
    public AjaxResult archive(PjProjectArchiveListDTO pjProjectArchiveListDTO) {
        MpPageVO mpPageVO = pjProjectService.selectArchiveList(pjProjectArchiveListDTO);
        return AjaxResult.success(mpPageVO);
    }
}
