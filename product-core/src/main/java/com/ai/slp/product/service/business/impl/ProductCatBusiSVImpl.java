package com.ai.slp.product.service.business.impl;

import com.ai.opt.base.exception.BusinessException;
import com.ai.opt.base.vo.PageInfo;
import com.ai.opt.sdk.util.BeanUtils;
import com.ai.slp.product.api.common.param.PageInfoForRes;
import com.ai.slp.product.api.normproduct.param.ProdCatAttrDef;
import com.ai.slp.product.api.productcat.param.*;
import com.ai.slp.product.constants.CommonSatesConstants;
import com.ai.slp.product.constants.ProductCatConstants;
import com.ai.slp.product.dao.mapper.attach.ProdCatAttrAttch;
import com.ai.slp.product.dao.mapper.bo.ProdAttrvalueDef;
import com.ai.slp.product.dao.mapper.bo.ProdCatAttr;
import com.ai.slp.product.dao.mapper.bo.ProdCatAttrValue;
import com.ai.slp.product.dao.mapper.bo.ProductCat;
import com.ai.slp.product.service.atom.interfaces.*;
import com.ai.slp.product.service.business.interfaces.IProductCatBusiSV;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by jackieliu on 16/4/29.
 */
@Service
@Transactional
public class ProductCatBusiSVImpl implements IProductCatBusiSV {
    private static Logger logger = LoggerFactory.getLogger(ProductCatBusiSVImpl.class);
    @Autowired
    IProdCatDefAtomSV prodCatDefAtomSV;
    @Autowired
    IProdCatAttrAtomSV prodCatAttrAtomSV;
    @Autowired
    IStandedProductAtomSV productAtomSV;
    @Autowired
    IProdCatAttrValAtomSV prodCatAttrValAtomSV;
    @Autowired
    IProdCatAttrAttachAtomSV catAttrAttachAtomSV;
    @Autowired
    IStandedProdAttrAtomSV prodAttrAtomSV;

    @Override
    public PageInfoForRes<ProductCatInfo> queryProductCat(ProductCatPageQuery pageQuery) {
        PageInfo<ProductCat> catInfoPageInfo = prodCatDefAtomSV.queryForPage(
                pageQuery.getPageNo(),pageQuery.getPageSize(),pageQuery.getParentProductCatId(),
                pageQuery.getTenantId(),pageQuery.getProductCatId(),
                pageQuery.getProductCatName(),pageQuery.getIsChild()
        );
        PageInfoForRes<ProductCatInfo> pageInfoWrapper = new PageInfoForRes<>();
        pageInfoWrapper.setCount(catInfoPageInfo.getCount());
        pageInfoWrapper.setPageSize(catInfoPageInfo.getPageSize());
        pageInfoWrapper.setPageCount(catInfoPageInfo.getPageCount());
        pageInfoWrapper.setPageNo(catInfoPageInfo.getPageNo());
        List<ProductCat> productCatList = catInfoPageInfo.getResult();
        List<ProductCatInfo> catInfoList = new ArrayList<>();
        pageInfoWrapper.setResult(catInfoList);
        for (ProductCat productCat:productCatList){
            ProductCatInfo productCatInfo = new ProductCatInfo();
            BeanUtils.copyProperties(productCatInfo,productCat);
            productCatInfo.setSerialNumber(productCat.getSerialNumber());
            catInfoList.add(productCatInfo);
        }
        return pageInfoWrapper;
    }

    @Override
    public void addCatList(List<ProductCatParam> pcpList) {
        if (pcpList==null || pcpList.isEmpty())
            return;
        for(ProductCatParam catParam:pcpList){
            ProductCat productCat = new ProductCat();
            BeanUtils.copyProperties(productCat,catParam);
            prodCatDefAtomSV.insertProductCat(productCat);
        }
    }

    @Override
    public ProductCatInfo queryByCatId(String tenantId, String productCatId) {
        ProductCatInfo productCatInfo = null;
        ProductCat productCat = prodCatDefAtomSV.selectById(tenantId,productCatId);
        if (productCat!=null){
            productCatInfo = new ProductCatInfo();
            BeanUtils.copyProperties(productCatInfo,productCat);
        }
        return productCatInfo;
    }

    @Override
    public void updateByCatId(ProductCatParam catParam) {
        if (catParam==null)
            throw new BusinessException("","请求信息为空,无法更新");
        //查询分类
        ProductCat productCat = prodCatDefAtomSV.selectById(catParam.getTenantId(),catParam.getProductCatId());
        if (productCat==null)
            throw new BusinessException("","没有找到对应的类目信息,租户id:"+catParam.getTenantId()
                    +",类目标识:"+catParam.getProductCatId());
        //判断是否变更了"是否有子分类"选项
        if (!productCat.getIsChild().equals(catParam.getIsChild())){
            //若把有改成无
            if (ProductCatConstants.HAS_CHILD.equals(productCat.getIsChild())
                    && ProductCatConstants.NO_CHILD.equals(catParam.getIsChild())){
                //判断是否有子类目
                if (prodCatDefAtomSV.queryOfParent(productCat.getParentProductCatId())>0)
                    throw new BusinessException("","此类目下存在子类目,无法变更为[无子分类]");
            }else{
                //判断是否有关联属性
                List<ProdCatAttr> catAttrList =
                        prodCatAttrAtomSV.queryAttrsByCatId(productCat.getTenantId(),productCat.getProductCatId());
                if (catAttrList!=null && catAttrList.size()>0){
                    throw new BusinessException("","此类目已关联属性,无法变更为[有子分类]");
                }
            }
        }
        BeanUtils.copyProperties(productCat,catParam);
        prodCatDefAtomSV.updateProductCat(productCat);
    }

    @Override
    public void deleteByCatId(String tenantId, String productCatId,Long operId) {
        //查询是否存在类目
        ProductCat productCat = prodCatDefAtomSV.selectById(tenantId,productCatId);
        if (productCat==null)
            throw new BusinessException("","未找到要删除类目,租户id:"+tenantId+",类目标识:"+productCatId);
        //判断是否关联了标准品
        if (productAtomSV.queryByCatId(productCatId)>0)
            throw new BusinessException("","已关联了标准品，不可删除");
        //判断是否有子类目
        if (prodCatDefAtomSV.queryOfParent(Long.parseLong(productCatId))>0)
            throw new BusinessException("","此类目下存在子类目,不可删除");
        List<ProdCatAttr> catAttrList =
                prodCatAttrAtomSV.queryAttrsByCatId(tenantId,productCatId);
        //删除属性对应关系
        if (catAttrList!=null && catAttrList.size()>0){
            for (ProdCatAttr catAttr:catAttrList){
                //删除类目下属性与属性值的对应关系
                prodCatAttrValAtomSV.deleteByCat(tenantId,catAttr.getCatAttrId(),operId);
                //删除类目与属性对应关系
                prodCatAttrAtomSV.deleteByCatId(tenantId,catAttr.getCatAttrId(),operId);
            }
        }
        //删除类目
        prodCatDefAtomSV.deleteProductCat(tenantId,productCatId);
    }

    /**
     * 查询类目的类目链
     *
     * @param tenantId
     * @param productCatId
     * @return
     */
    @Override
    public List<ProductCatInfo> queryLinkOfCatById(String tenantId, String productCatId) {
        List<ProductCatInfo> catInfoList = new ArrayList<ProductCatInfo>();
        queryCatFoLinkById(catInfoList,tenantId,productCatId);
        return catInfoList;
    }

    /**
     * @param tenantId
     * @param productCatId
     * @param attrType
     * @return
     */
    @Override
    public Map<ProdCatAttrDef, List<AttrValInfo>> querAttrOfCatByIdAndType(
            String tenantId, String productCatId, String attrType) {
        Map<ProdCatAttrDef, List<AttrValInfo>> catAttrDefListMap = new HashMap<>();
        //查询类目属性集合
        List<ProdCatAttrAttch> attrAttchList = catAttrAttachAtomSV.queryAttrOfByIdAndType(
                tenantId,productCatId,attrType);
        //查询属性对应的属性值
        for (ProdCatAttrAttch attrAttch:attrAttchList){
            ProdCatAttrDef catAttrDef = new ProdCatAttrDef();
            BeanUtils.copyProperties(catAttrDef,attrAttch);
            //查询此属性是否关联标准品
            int prodNum = prodAttrAtomSV.queryProdNumOfAttr(tenantId,attrAttch.getAttrId());
            catAttrDef.setHasProduct(prodNum>0?true:false);
            //查询属性对应的属性值
            List<ProdAttrvalueDef> catAttrValList =
                    catAttrAttachAtomSV.queryValListByCatAttr(tenantId,attrAttch.getCatAttrId());
            List<AttrValInfo> valInfoList = new ArrayList<>();
            catAttrDefListMap.put(catAttrDef,valInfoList);
            for (ProdAttrvalueDef attrvalueDef:catAttrValList){
                AttrValInfo attrValInfo = new AttrValInfo();
                BeanUtils.copyProperties(attrValInfo,attrvalueDef);
                valInfoList.add(attrValInfo);
            }
        }
        return catAttrDefListMap;
    }

    /**
     * 查询类目下某个类型的属性标识和属性值标识集合
     *
     * @param tenantId
     * @param productCatId
     * @param attrType
     * @return
     */
    @Override
    public Map<Long, Set<String>> queryAttrAndValIdByCatIdAndType(String tenantId, String productCatId, String attrType) {
        Map<Long,Set<String>> idMap = new HashMap<>();
        //查询类目和属性的关联关系
        List<ProdCatAttr> catAttrList = prodCatAttrAtomSV.queryAttrOfCatByIdAndType(tenantId,productCatId,attrType);
        for (ProdCatAttr catAttr:catAttrList){
            Set<String> attrValIds = idMap.get(catAttr.getAttrId());
            if (attrValIds==null){
                attrValIds = new HashSet<>();
                idMap.put(catAttr.getAttrId(),attrValIds);
            }
            //查询关联关系对应属性值集合
            List<ProdCatAttrValue> attrValueList = prodCatAttrValAtomSV.queryByCatAttrId(tenantId,catAttr.getCatAttrId());
            for (ProdCatAttrValue attrValue:attrValueList){
                attrValIds.add(attrValue.getAttrvalueDefId());
            }
        }
        return idMap;
    }

    /**
     * 删除类目的属性或属性值关联
     *
     * @param catAttrVal
     */
    @Override
    public void deleteAttrOrVa(ProdCatAttrVal catAttrVal) {
        //若删除未关键属性或销售属性,需要检查是否关联标准品
        if (ProductCatConstants.ATTR_TYPE_KEY.equals(catAttrVal.getAttrType())
                || ProductCatConstants.ATTR_TYPE_SALE.equals(catAttrVal.getAttrType())) {
            int prodNum = prodAttrAtomSV.queryProdNumOfAttr(catAttrVal.getTenantId(), catAttrVal.getAttrId());
            if (prodNum > 0)
                throw new BusinessException("", "此属性已关联标准品,不允许删除");
        }
        //属性值不为空,表示只删除单个属性值
        if (StringUtils.isNotBlank(catAttrVal.getAttrvalueDefId())){
            //删除属性值
            prodCatAttrValAtomSV.deleteValByAttr(catAttrVal.getTenantId(),catAttrVal.getCatAttrId(),
                    catAttrVal.getAttrvalueDefId(),catAttrVal.getOperId());
        }else{//删除整个属性值
            //删除关联属性值
            prodCatAttrValAtomSV.deleteByCat(catAttrVal.getTenantId(),catAttrVal.getCatAttrId(),
                    catAttrVal.getOperId());
            //删除关联属性
            prodCatAttrAtomSV.deleteByCatAttrId(catAttrVal.getTenantId(),catAttrVal.getProductCatId(),
                    catAttrVal.getAttrId(),catAttrVal.getOperId());
        }
    }

    /**
     * 根据名称或首字母查询类目信息
     *
     * @param query
     * @return
     */
    @Override
    public List<ProductCatInfo> queryByNameOrFirst(ProductCatQuery query) {
        //判断是名称还是首字母
        String queryVal = query.getQueryVal();
        boolean isName = true;
        //查询内容长度为1,且为字母,则按字母进行查询
        if (StringUtils.isNotBlank(queryVal)
                && queryVal.length()==1
                && Character.isLetter(queryVal.charAt(0))){
            queryVal = queryVal.substring(0,1);
            isName = false;
        }
        List<ProductCatInfo> catInfoList = new ArrayList<>();
        List<ProductCat> catList = prodCatDefAtomSV.queryByNameOrFirst(
                query.getTenantId(),query.getParentProductCatId(),queryVal,isName);
        for (ProductCat cat:catList){
            ProductCatInfo catInfo = new ProductCatInfo();
            BeanUtils.copyProperties(catInfo,cat);
            catInfoList.add(catInfo);
        }
        return catInfoList;
    }

    /**
     * 类目添加指定属性类型的属性和属性值
     *
     * @param addCatAttrParam
     */
    @Override
    public void addAttrAndValOfAttrType(ProdCatAttrAddParam addCatAttrParam) {
        //查询类目是否存在
        String tenantId = addCatAttrParam.getTenantId();
        String catId = addCatAttrParam.getProductCatId();
        if (prodCatDefAtomSV.selectById(tenantId,catId)==null){
            throw new BusinessException("","未找到指定类目信息,租户ID:"+tenantId+",类目标识:"+catId);
        }
        String attrType = addCatAttrParam.getAttrType();
        Timestamp operTime = addCatAttrParam.getOperTime();
        Map<Long,Set<String>> attrAndVal = addCatAttrParam.getAttrAndVal();
        for (Long attId:attrAndVal.keySet()){
            //检查是否已经关联
            ProdCatAttr catAttr = prodCatAttrAtomSV.queryByCatIdAndTypeAndAttrId(tenantId,catId,attId,attrType);
            if (catAttr==null){
                catAttr = new ProdCatAttr();
                catAttr.setTenantId(tenantId);
                catAttr.setProductCatId(catId);
                catAttr.setAttrId(attId);
                catAttr.setAttrType(attrType);
                catAttr.setSerialNumber((short)0);
                catAttr.setState(CommonSatesConstants.STATE_ACTIVE);
                catAttr.setOperId(addCatAttrParam.getOperId());
                catAttr.setOperTime(operTime);
                //设置是否必填,关键属性和销售属性为必填,其他为非必填
                if (ProductCatConstants.ATTR_TYPE_KEY.equals(attrType)
                        || ProductCatConstants.ATTR_TYPE_SALE.equals(attrType))
                    catAttr.setIsNecessary("Y");
                else
                    catAttr.setIsNecessary("N");
                prodCatAttrAtomSV.insertProdCatAttr(catAttr);
            }
            //添加属性值
            Set<String> attrValSet = attrAndVal.get(attId);
            for (String valId:attrValSet){
                //检查关联关系是否已经存在
                ProdCatAttrValue catAttrValue = null;
                if (catAttr!=null){
                    catAttrValue = prodCatAttrValAtomSV.queryByCatAndCatAttrId(
                            tenantId,catAttr.getCatAttrId(),valId);
                    if (catAttrValue!=null)
                        continue;
                }
                //添加属性值
                catAttrValue = new ProdCatAttrValue();
                catAttrValue.setTenantId(tenantId);
                catAttrValue.setAttrvalueDefId(valId);
                catAttrValue.setCatAttrId(catAttr.getCatAttrId());
                catAttrValue.setSerialNumber((short)0);
                catAttrValue.setState(CommonSatesConstants.STATE_ACTIVE);
                catAttrValue.setOperId(addCatAttrParam.getOperId());
                catAttrValue.setOperTime(operTime);
                prodCatAttrValAtomSV.installCatAttrVal(catAttrValue);
            }
        }
    }

    /**
     * 更新类目属性和属性值
     *
     * @param updateParams
     * @return 更新成功条目数
     */
    @Override
    public int updateCatAttrAndVal(List<ProdCatAttrUpdateParam> updateParams) {
        //成功数量
        int successNum = 0;

        for (ProdCatAttrUpdateParam updateParam:updateParams){
            String tenantId = updateParam.getTenantId();
            if (StringUtils.isBlank(tenantId)){
                logger.warn("租户id不能为空\r\n"+updateParam.toString());
                continue;
            }

            switch (updateParam.getObjType()){
                //更新属性
                case "1":
                    //查询属性
                    ProdCatAttr prodCatAttr = prodCatAttrAtomSV.selectById(tenantId,updateParam.getUpdateObjId());
                    if (prodCatAttr==null) {
                        logger.warn("未找到指定的类目[属性]关联\r\n" + updateParam.toString());
                        continue;
                    }
                    prodCatAttr.setSerialNumber(updateParam.getSerialNumber());
                    prodCatAttr.setIsPicture(updateParam.getIsPicture());
                    prodCatAttr.setOperId(updateParam.getOperId());
                    prodCatAttr.setOperTime(updateParam.getOperTime());
                    prodCatAttrAtomSV.update(prodCatAttr);
                    successNum++;
                    break;
                //更新属性值
                case "2":
                    //查询属性值
                    ProdCatAttrValue attrVal = prodCatAttrValAtomSV.selectById(tenantId,updateParam.getUpdateObjId());
                    if (attrVal==null) {
                        logger.warn("未找到指定的类目[属性值]关系\r\n" + updateParam.toString());
                        continue;
                    }
                    attrVal.setSerialNumber(updateParam.getSerialNumber());
                    attrVal.setOperId(updateParam.getOperId());
                    attrVal.setOperTime(updateParam.getOperTime());
                    prodCatAttrValAtomSV.update(attrVal);
                    successNum++;
                    break;
                //未知类型
                default:
                    logger.warn("未知更新类型:\r\n"+updateParam.toString());
            }

        }
        return successNum;
    }


    private void queryCatFoLinkById(List<ProductCatInfo> catInfoList,String tenantId, String productCatId){
        ProductCatInfo catInfo = queryByCatId(tenantId,productCatId);
        if (catInfo==null)
            return;
        //已经达到根目录
        if (catInfo.getParentProductCatId()==null){
            catInfoList.add(catInfo);
            return;
        //若不是跟类目,则继续查询
        }else
            queryCatFoLinkById(catInfoList,tenantId,productCatId);
            catInfoList.add(catInfo);
    }
}
