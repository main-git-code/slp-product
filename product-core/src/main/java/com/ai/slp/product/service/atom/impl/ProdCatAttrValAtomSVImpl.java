package com.ai.slp.product.service.atom.impl;

import com.ai.slp.product.constants.CommonSatesConstants;
import com.ai.slp.product.dao.mapper.bo.ProdCatAttrValue;
import com.ai.slp.product.dao.mapper.bo.ProdCatAttrValueCriteria;
import com.ai.slp.product.dao.mapper.interfaces.ProdCatAttrValueMapper;
import com.ai.slp.product.service.atom.interfaces.IProdCatAttrValAtomSV;
import com.ai.slp.product.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by jackieliu on 16/5/1.
 */
@Component
public class ProdCatAttrValAtomSVImpl implements IProdCatAttrValAtomSV {
    @Autowired
    ProdCatAttrValueMapper attrValueMapper;
    /**
     * 删除类目属性关系对应属性值
     *
     * @param tenantId
     * @param catAttrId
     * @return
     */
    @Override
    public int deleteByCat(String tenantId, String catAttrId, Long operId, Timestamp operTime) {
        ProdCatAttrValueCriteria example = new ProdCatAttrValueCriteria();
        example.createCriteria().andTenantIdEqualTo(tenantId).andCatAttrIdEqualTo(catAttrId);
        ProdCatAttrValue attrValue = new ProdCatAttrValue();
        attrValue.setState(CommonSatesConstants.STATE_INACTIVE);
        attrValue.setOperId(operId);
        attrValue.setOperTime(operTime!=null?operTime: DateUtils.currTimeStamp());
        return attrValueMapper.updateByExampleSelective(attrValue,example);
    }

    /**
     * 删除类目属性下的一个属性值
     *
     * @param tenantId
     * @param catAttrId
     * @param attrValId
     * @param operId
     * @param operTime
     * @return
     */
    @Override
    public int deleteValByAttr(String tenantId, String catAttrId, String attrValId, Long operId, Timestamp operTime) {
        ProdCatAttrValueCriteria example = new ProdCatAttrValueCriteria();
        example.createCriteria().andTenantIdEqualTo(tenantId)
                .andCatAttrIdEqualTo(catAttrId).andAttrvalueDefIdGreaterThan(attrValId);
        ProdCatAttrValue attrValue = new ProdCatAttrValue();
        attrValue.setState(CommonSatesConstants.STATE_INACTIVE);
        attrValue.setOperId(operId);
        attrValue.setOperTime(operTime!=null?operTime: DateUtils.currTimeStamp());
        return attrValueMapper.updateByExampleSelective(attrValue,example);
    }

    /**
     * 查询类目属性关系对应的属性值
     *
     * @param tenantId
     * @param catAttrId
     * @return
     */
    @Override
    public List<ProdCatAttrValue> queryByCatAttrId(String tenantId, String catAttrId) {
        ProdCatAttrValueCriteria example = new ProdCatAttrValueCriteria();
        example.createCriteria().andTenantIdEqualTo(tenantId).andCatAttrIdEqualTo(catAttrId)
        .andStateEqualTo(CommonSatesConstants.STATE_ACTIVE);
        return attrValueMapper.selectByExample(example);
    }
}