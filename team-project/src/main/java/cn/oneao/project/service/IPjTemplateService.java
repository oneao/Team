package cn.oneao.project.service;

import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.project.domain.PjTaskStagesTemplate;
import cn.oneao.project.domain.PjTemplate;
import cn.oneao.project.domain.dto.template.PjTemplateUpdateDTO;
import cn.oneao.project.domain.dto.template.PjTemplateListDTO;
import cn.oneao.project.domain.vo.template.PjTemplateSelectionVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPjTemplateService extends IService<PjTemplate>{
    // 查询项目模版列表
    MpPageVO selectPjTemplateList(PjTemplateListDTO pjTemplateListDTO);
    // 新增项目模版
    int insertPjTemplate(PjTemplateUpdateDTO pjTemplateAddDTO);
    // 判断项目模版名称是否存在
    boolean isHasPjTemplateByName(String name);
    //  获取项目模版详细信息
    PjTemplate selectPjTemplateById(Long id);
    // 修改项目模版
    int updatePjTemplate(PjTemplateUpdateDTO pjTemplate);
    // 删除项目模版
    int deleteLogicPjTemplateByIds(Long[] ids);
    // 查询所有项目模版列表
    List<PjTemplateSelectionVO> selectPjTemplateListAll();
}
