package com.ai.slp.product.service.atom.interfaces;

import com.ai.slp.product.dao.mapper.bo.ProdCatAttrValue;

import java.util.List;

/**
 * 商品类目属性值操作
 *
 * Date: 2016年05月01日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 *
 * @author liutong5
 */
public interface IProdCatAttrValAtomSV {

    /**
     * 删除类目属性关系对应属性值
     *
     * @param tenantId
     * @param catAttrId
     * @param operId
     * @return
     */
    public int deleteByCat(String tenantId,String catAttrId,Long operId);

    /**
     * 删除类目属性下的一个属性值
     *
     * @param tenantId
     * @param catAttrId
     * @param attrValId
     * @param operId
     * @return
     */
    public int deleteValByAttr(String tenantId,String catAttrId,String attrValId,Long operId);

    /**
     * 查询类目属性关系对应的属性值
     *
     * @param tenantId
     * @param catAttrId
     * @return
     */
    public List<ProdCatAttrValue> queryByCatAttrId(String tenantId,String catAttrId);

    /**
     * 查询指定关系和属性值的属性值信息
     *
     * @param tenantId
     * @param catAttrId
     * @param valId
     * @return
     */
    public ProdCatAttrValue queryByCatAndCatAttrId(String tenantId,String catAttrId,String valId);

    /**
     * 添加类目属性值关系
     * @param attrValue
     * @return
     */
    public int installCatAttrVal(ProdCatAttrValue attrValue);

    /**
     * 根据唯一标识查询类目与属性值的关联
     *
     * @param tenantId
     * @param catAttrValId
     * @return
     */
    public ProdCatAttrValue selectById(String tenantId,String catAttrValId);

    /**
     * 更新类目对应的属性值
     *
     * @param attrValue
     * @return
     */
    public int update(ProdCatAttrValue attrValue);
//    
//    /**
//     * 根据属性值标识查询关联类目个数
//     * 
//     * @param tenantId
//     * @param catAttrValId
//     * @return
//     * @author lipeng16
//     */
//    public int queryNumByAttrvalId(String tenantId,String catAttrValId);
}
