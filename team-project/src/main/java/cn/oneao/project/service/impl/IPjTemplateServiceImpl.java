package cn.oneao.project.service.impl;

import cn.oneao.common.core.page.MpPageVO;
import cn.oneao.common.utils.SecurityUtils;
import cn.oneao.project.domain.PjTemplate;
import cn.oneao.project.domain.dto.template.PjTemplateUpdateDTO;
import cn.oneao.project.domain.dto.template.PjTemplateListDTO;
import cn.oneao.project.domain.vo.template.PjTemplateSelectionVO;
import cn.oneao.project.mapper.PjTemplateMapper;
import cn.oneao.project.service.IPjTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class IPjTemplateServiceImpl extends ServiceImpl<PjTemplateMapper, PjTemplate> implements IPjTemplateService {
    @Autowired
    private PjTemplateMapper pjTemplateMapper;

    @Override
    public MpPageVO selectPjTemplateList(PjTemplateListDTO pjTemplateListDTO) {
        Page<PjTemplate> pjTemplatePage = new Page<>(pjTemplateListDTO.getPageNum() - 1, pjTemplateListDTO.getPageSize());

        String name = pjTemplateListDTO.getName();
        Integer isSystem = pjTemplateListDTO.getIsSystem();

        LambdaQueryWrapper<PjTemplate> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(name)) {
            queryWrapper.like(PjTemplate::getName, name);
        }
        if (!ObjectUtils.isEmpty(isSystem) && isSystem != -1) {
            queryWrapper.eq(PjTemplate::getIsSystem, isSystem);
        }
        Page<PjTemplate> pjTemplatePageVO = pjTemplateMapper.selectPage(pjTemplatePage, queryWrapper);

        return new MpPageVO(pjTemplatePageVO);
    }

    @Override
    public int insertPjTemplate(PjTemplateUpdateDTO pjTemplateAddDTO) {
        Integer isSystem = pjTemplateAddDTO.getIsSystem();
        //如果设置默认
        isSystemPjTemplate(isSystem);

        PjTemplate pjTemplate = new PjTemplate();
        pjTemplate.setName(pjTemplateAddDTO.getName());
        pjTemplate.setDescription(pjTemplateAddDTO.getDescription());
        pjTemplate.setIsSystem(pjTemplateAddDTO.getIsSystem());
        pjTemplate.setCover(pjTemplateAddDTO.getCover());

        return pjTemplateMapper.insert(pjTemplate);
    }

    @Override
    public boolean isHasPjTemplateByName(String name) {
        return pjTemplateMapper.selectCountByName(name) > 0;
    }

    @Override
    public PjTemplate selectPjTemplateById(Long id) {
        return this.getById(id);
    }

    @Override
    public int updatePjTemplate(PjTemplateUpdateDTO pjTemplateUpdateDTO) {
        PjTemplate pjTemplate = new PjTemplate();

        pjTemplate.setId(pjTemplateUpdateDTO.getId());
        pjTemplate.setName(pjTemplateUpdateDTO.getName());
        pjTemplate.setDescription(pjTemplateUpdateDTO.getDescription());
        Integer isSystem = pjTemplateUpdateDTO.getIsSystem();
        pjTemplate.setIsSystem(isSystem);
        pjTemplate.setCover(pjTemplateUpdateDTO.getCover());
        String username = SecurityUtils.getUsername();
        pjTemplate.setUpdateBy(username);
        pjTemplate.setUpdateTime(new Date());

        //如果设置默认
        isSystemPjTemplate(isSystem);

        return pjTemplateMapper.updateById(pjTemplate);
    }

    private void isSystemPjTemplate(Integer isSystem) {
        if (isSystem == 1) {
            //那么已经默认的模版需要取消默认
            LambdaQueryWrapper<PjTemplate> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PjTemplate::getIsSystem, 1);
            PjTemplate pjTemplate = pjTemplateMapper.selectOne(queryWrapper);
            if (!ObjectUtils.isEmpty(pjTemplate)) {
                pjTemplate.setIsSystem(0);
                pjTemplateMapper.updateById(pjTemplate);
            }
        }
    }
    @Override
    @Transactional
    public int deleteLogicPjTemplateByIds(Long[] ids) {
        LambdaUpdateWrapper<PjTemplate> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PjTemplate::getIsDelete, 1);
        updateWrapper.in(PjTemplate::getId, (Object[]) ids);
        return pjTemplateMapper.update(null, updateWrapper);
    }
    // 查询所有项目模版列表
    @Override
    public List<PjTemplateSelectionVO> selectPjTemplateListAll() {
        LambdaQueryWrapper<PjTemplate> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(PjTemplate::getIsDelete, 0);
        queryWrapper.orderByDesc(PjTemplate::getIsSystem);

        List<PjTemplate> pjTemplateList = pjTemplateMapper.selectList(queryWrapper);

        List<PjTemplateSelectionVO> result = new ArrayList<>();

        pjTemplateList.forEach(item -> {
            PjTemplateSelectionVO pjTemplateSelectionVO = new PjTemplateSelectionVO();
            pjTemplateSelectionVO.setValue(item.getId());
            pjTemplateSelectionVO.setLabel(item.getName());
            pjTemplateSelectionVO.setIsSystem(item.getIsSystem());
            pjTemplateSelectionVO.setCover(item.getCover());
            result.add(pjTemplateSelectionVO);
        });

        return result;
    }
}
