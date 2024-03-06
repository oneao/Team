package cn.oneao.project.controller;

import cn.oneao.common.annotation.Log;
import cn.oneao.common.core.controller.BaseController;
import cn.oneao.common.core.domain.AjaxResult;
import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.enums.BusinessType;
import cn.oneao.project.domain.PjTaskStagesTemplate;
import cn.oneao.project.domain.PjTemplate;
import cn.oneao.project.domain.dto.template.PjTemplateUpdateDTO;
import cn.oneao.project.domain.dto.template.PjTemplateListDTO;
import cn.oneao.project.domain.vo.template.PjTemplateSelectionVO;
import cn.oneao.project.service.IPjTaskStagesTemplateService;
import cn.oneao.project.service.IPjTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/project/template")
@Slf4j
public class PjTemplateController extends BaseController {
    @Autowired
    private IPjTemplateService pjTemplateService;
    @Autowired
    private IPjTaskStagesTemplateService pjTaskStagesTemplateService;
    /**
     * 查询项目模版列表
     *
     * @param pjTemplateListDTO 查询条件
     * @return 项目模版列表
     */
    @PreAuthorize("@ss.hasPermi('project:template:list')")
    @GetMapping("/list")
    public AjaxResult list(PjTemplateListDTO pjTemplateListDTO) {
        MpPageVO mpPageVO = pjTemplateService.selectPjTemplateList(pjTemplateListDTO);
        return AjaxResult.success(mpPageVO);
    }

    /**
     * 查询所有项目模版列表
     *
     * @return 项目模版列表
     */
    @PreAuthorize("@ss.hasPermi('project:template:list')")
    @GetMapping("/listAll")
    public AjaxResult listAll() {
        List<PjTemplateSelectionVO> list  = pjTemplateService.selectPjTemplateListAll();
        return AjaxResult.success(list);
    }

    /**
     * 新增项目模版
     */
    @PreAuthorize("@ss.hasPermi('project:template:add')")
    @Log(title = "项目模版", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PjTemplateUpdateDTO pjTemplateAddDTO) {
        String name = pjTemplateAddDTO.getName();
        if (pjTemplateService.isHasPjTemplateByName(name)) {
            return AjaxResult.error("新增项目模版'" + name + "'失败，项目模版名称已存在");
        }
        return toAjax(pjTemplateService.insertPjTemplate(pjTemplateAddDTO));
    }


    /**
     * 获取项目模版详细信息
     */
    @PreAuthorize("@ss.hasPermi('project:template:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PositiveOrZero(message = "获取项目模版详细信息有误，原因：传入id有误") @PathVariable("id") Long id) {
        return success(pjTemplateService.selectPjTemplateById(id));
    }

    /**
     * 修改项目模版
     */
    @PreAuthorize("@ss.hasPermi('project:template:edit')")
    @Log(title = "项目模版", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PjTemplateUpdateDTO pjTemplate) {
        String name = pjTemplate.getName();
        Long id = pjTemplate.getId();
        PjTemplate pjTemplateServiceById = pjTemplateService.getById(id);
        if (!name.equals(pjTemplateServiceById.getName())) {
            if (pjTemplateService.isHasPjTemplateByName(name)) {
                return AjaxResult.error("修改项目模版'" + name + "'失败，项目模版名称已存在");
            }
        }
        return toAjax(pjTemplateService.updatePjTemplate(pjTemplate));
    }

    /**
     * 删除项目模版
     */
    @PreAuthorize("@ss.hasPermi('project:template:remove')")
    @Log(title = "项目模版", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@Size(min = 1,message = "删除项目模版至少为1个") @PathVariable Long[] ids) {
//        pjTaskStagesTemplateService.removeByPjTemplateIds(ids);
        return toAjax(pjTemplateService.deleteLogicPjTemplateByIds(ids));
    }

    /**
     * 获取项目模版任务阶段列表
     * @param id 项目模版id
     * @return 项目模版任务阶段列表
     */
    @PreAuthorize("@ss.hasPermi('project:template:taskStages:list')")
    @GetMapping("/taskStagesList/{id}")
    public AjaxResult taskStagesList(@PositiveOrZero(message = "获取项目模版任务阶段列表有误，原因：传入id有误") @PathVariable(name = "id") Long id) {
        List<PjTaskStagesTemplate> list = pjTaskStagesTemplateService.taskStagesList(id);
        return success(list);
    }

    /**
     * 获取项目模版任务阶段
     * @param id 项目模版id
     * @return 项目模版任务阶段
     */
    @PreAuthorize("@ss.hasPermi('project:template:taskStages:query')")
    @GetMapping("/taskStages/{id}")
    public AjaxResult taskStages(@PositiveOrZero(message = "获取项目模版任务阶段有误，原因：传入id有误") @PathVariable(name = "id") Long id) {
        PjTaskStagesTemplate pjTaskStagesTemplate = pjTaskStagesTemplateService.getTaskStages(id);
        return success(pjTaskStagesTemplate);
    }

    /**
     * 新增项目模版任务阶段
     * @param pjTaskStagesTemplate 项目模版任务阶段
     * @return 新增结果
     */
    @PreAuthorize("@ss.hasPermi('project:template:taskStages:add')")
    @Log(title = "项目模版任务阶段", businessType = BusinessType.INSERT)
    @PostMapping("/taskStages")
    public AjaxResult addTaskStages(@Validated @RequestBody PjTaskStagesTemplate pjTaskStagesTemplate) {
        boolean hasSameTaskStagesTemplateName = pjTaskStagesTemplateService.isHasSameTaskStagesTemplateName(pjTaskStagesTemplate.getName());
        if(hasSameTaskStagesTemplateName) {
            return AjaxResult.error("新增项目模版任务阶段'" + pjTaskStagesTemplate.getName() + "'失败，项目模版任务阶段名称已存在");
        }
        return toAjax(pjTaskStagesTemplateService.addTaskStages(pjTaskStagesTemplate));
    }

    /**
     * 修改项目模版任务阶段
     * @param pjTaskStagesTemplate 项目模版任务阶段
     * @return 修改结果
     */
    @PreAuthorize("@ss.hasPermi('project:template:taskStages:edit')")
    @Log(title = "项目模版任务阶段", businessType = BusinessType.UPDATE)
    @PutMapping("/taskStages")
    public AjaxResult editTaskStages(@Validated @RequestBody PjTaskStagesTemplate pjTaskStagesTemplate) {
        String name = pjTaskStagesTemplate.getName();
        Long id = pjTaskStagesTemplate.getId();
        PjTaskStagesTemplate pjTaskStagesTemplateServiceById = pjTaskStagesTemplateService.getById(id);
        if (!name.equals(pjTaskStagesTemplateServiceById.getName())) {
            if (pjTaskStagesTemplateService.isHasSameTaskStagesTemplateName(name)) {
                return AjaxResult.error("修改项目模版任务阶段'" + name + "'失败，项目模版任务阶段名称已存在");
            }
        }
        return toAjax(pjTaskStagesTemplateService.updateById(pjTaskStagesTemplate));
    }

    /**
     * 删除项目模版任务阶段
     * @param id 项目模版任务阶段id
     * @return 删除结果
     */
    @PreAuthorize("@ss.hasPermi('project:template:taskStages:remove')")
    @Log(title = "项目模版任务阶段", businessType = BusinessType.DELETE)
    @DeleteMapping("/taskStages/{id}")
    @Transactional
    public AjaxResult removeTaskStages(@PositiveOrZero(message = "获取项目模版任务阶段有误，原因：传入id有误") @PathVariable Long id) {
        return toAjax(pjTaskStagesTemplateService.removeById(id));
    }

}

