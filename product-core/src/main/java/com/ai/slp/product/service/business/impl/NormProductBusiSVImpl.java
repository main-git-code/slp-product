package com.ai.slp.product.service.business.impl;

import java.sql.Timestamp;
import java.util.*;

import com.ai.slp.product.api.normproduct.param.*;
import com.ai.slp.product.dao.mapper.attach.ProdCatAttrAttch;
import com.ai.slp.product.dao.mapper.bo.*;
import com.ai.slp.product.service.atom.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.opt.base.exception.BusinessException;
import com.ai.opt.base.vo.PageInfo;
import com.ai.opt.sdk.util.BeanUtils;
import com.ai.slp.product.api.common.param.PageInfoForRes;
import com.ai.slp.product.constants.CommonSatesConstants;
import com.ai.slp.product.constants.StandedProductConstants;
import com.ai.slp.product.service.business.interfaces.INormProductBusiSV;
import com.ai.slp.product.util.DateUtils;
import com.ai.slp.product.vo.StandedProdPageQueryVo;

/**
 * Created by jackieliu on 16/4/27.
 */
@Service
@Transactional
public class NormProductBusiSVImpl implements INormProductBusiSV {
    private static Logger logger = LoggerFactory.getLogger(NormProductBusiSVImpl.class);
    private static String ATTR_LINE_VAL = "||";
    @Autowired
    IStandedProductAtomSV standedProductAtomSV;
    @Autowired
    IStandedProductLogAtomSV standedProductLogAtomSV;
    @Autowired
    IStandedProdAttrAtomSV standedProdAttrAtomSV;
    @Autowired
    IStandedProdAttrLogAtomSV standedProdAttrLogAtomSV;
    @Autowired
    IStorageGroupAtomSV storageGroupAtomSV;
    @Autowired
    IProdCatAttrAttachAtomSV catAttrAttachAtomSV;
    @Autowired
    IProdAttrValDefAtomSV attrValDefAtomSV;
    @Autowired
    IProdCatDefAtomSV catDefAtomSV;

    /**
     * 添加标准品
     *
     * @param normProdct
     * @return 添加成功后的标准品的标识
     */
    @Override
    public String installNormProd(NormProdSaveRequest normProdct) {
        //添加标准品
        StandedProduct standedProduct = new StandedProduct();
        BeanUtils.copyProperties(standedProduct, normProdct);
        standedProduct.setStandedProductName(normProdct.getProductName());
        if (normProdct.getCreateTime() != null)
            standedProduct.setCreateTime(DateUtils.toTimeStamp(normProdct.getCreateTime()));
        //添加成功,添加标准品日志
        if (standedProductAtomSV.installObj(standedProduct) > 0) {
            StandedProductLog productLog = new StandedProductLog();
            BeanUtils.copyProperties(productLog, standedProduct);
            standedProductLogAtomSV.insert(productLog);
        }
        //添加标准品属性值
        List<NormProdAttrValRequest> attrValList = normProdct.getAttrValList();
        Timestamp nowTime = DateUtils.currTimeStamp();
        for (NormProdAttrValRequest attrValReq : attrValList) {
            StandedProdAttr prodAttr = new StandedProdAttr();
            BeanUtils.copyProperties(prodAttr, attrValReq);
            prodAttr.setTenantId(normProdct.getTenantId());
            prodAttr.setAttrvalueDefId(attrValReq.getAttrValId());
            prodAttr.setAttrValueName(attrValReq.getAttrVal());
            prodAttr.setAttrValueName2(attrValReq.getAttrVal2());
            prodAttr.setState(CommonSatesConstants.STATE_ACTIVE);//设置为有效
            prodAttr.setOperTime(DateUtils.toTimeStamp(attrValReq.getOperTime()));
            //添加成功,添加日志
            if (standedProdAttrAtomSV.installObj(prodAttr) > 0) {
                StandedProdAttrLog prodAttrLog = new StandedProdAttrLog();
                BeanUtils.copyProperties(prodAttrLog, prodAttr);
                standedProdAttrLogAtomSV.installObj(prodAttrLog);
            }
        }
        return standedProduct.getStandedProdId();
    }

    /**
     * 更新标准品
     *
     * @param normProdct
     */
    @Override
    public void updateNormProd(NormProdSaveRequest normProdct) {
        String tenantId = normProdct.getTenantId(), productId = normProdct.getProductId();
        //查询是否存在
        StandedProduct standedProduct = standedProductAtomSV.selectById(tenantId, productId);
        if (standedProduct == null)
            throw new BusinessException("", "不存在指定标准品,租户ID:" + tenantId + ",标准品标识:" + productId);
        /*
        总共有6种状态变化,
        1.不允许变更为废弃状态;2.已废弃标准品不允许变更状态;3.可用变为不可用,需要检查.
         */
        //状态变更
        if (normProdct.getState().equals(standedProduct.getState())) {
            //变更为废弃
            if (StandedProductConstants.STATUS_DISCARD.equals(normProdct.getState()))
                throw new BusinessException("", "不允许变更为[废弃]状态,请使用废弃接口");
            //废弃状态下不允许变更为其他状态
            else if (StandedProductConstants.STATUS_DISCARD.equals(standedProduct.getState()))
                throw new BusinessException("", "不允许将[废弃]状态变更为其他状态");
            //将可用变为不可用
            else if (StandedProductConstants.STATUS_ACTIVE.equals(standedProduct.getState())
                    && StandedProductConstants.STATUS_INACTIVE.equals(normProdct)) {
                //检查是否有启用状态库存组
                int groupNum = storageGroupAtomSV.queryCountActive(tenantId, productId);
                if (groupNum > 0)
                    throw new BusinessException("", "该标准品下存在[" + groupNum + "]个启用的库存组,不允许变更为不可用");
            }
        }
        //变更信息
        standedProduct.setStandedProductName(normProdct.getProductName());
        standedProduct.setProductType(normProdct.getProductType());
        standedProduct.setState(normProdct.getState());
        if (standedProductAtomSV.updateObj(standedProduct)>0){
            StandedProductLog productLog = new StandedProductLog();
            BeanUtils.copyProperties(productLog, standedProduct);
            standedProductLogAtomSV.insert(productLog);
        }
        //变更属性值. 1.将原来属性值设置为不可用;2,启用新的属性值.

    }


    /**
     * 查询指定标准品信息
     *
     * @param tenantId  租户id
     * @param productId 标准品标识
     * @return
     */
    @Override
    public NormProdInfoResponse queryById(String tenantId, String productId) {
        StandedProduct product = standedProductAtomSV.selectById(tenantId,productId);
        if (product==null) {
            logger.warn("租户[{0}]下不存在标识[{1}]的标准品",tenantId,productId);
            throw new BusinessException("", "未找到对应标准品信息,租户id="+tenantId+",标准品标识="+productId);
        }
        NormProdInfoResponse response = new NormProdInfoResponse();
        //标准品信息填充返回值
        BeanUtils.copyProperties(response,product);
        response.setProductId(product.getStandedProdId());
        response.setProductName(product.getStandedProductName());
//        response.setCreateName("");//TODO... 获取创建者名称
//        response.setOperName("");//TODO... 获取操作者名称
        Map<Long,Set<String>> attrAndValueIds = new HashMap<>();
        //查询属性信息
        List<StandedProdAttr> attrList = standedProdAttrAtomSV.queryByNormProduct(tenantId,productId);
        for (StandedProdAttr prodAttr:attrList){
            Set<String> attrVal = attrAndValueIds.get(prodAttr.getAttrId());
            if (attrVal == null) {
                attrVal = new HashSet<>();
            }
            attrVal.add(prodAttr.getAttrvalueDefId());
            if (!attrAndValueIds.containsKey(prodAttr.getAttrId()))
                attrAndValueIds.put(prodAttr.getAttrId(),attrVal);
        }
        response.setAttrAndValueIds(attrAndValueIds);
        return response;
    }

    @Override
    public PageInfoForRes<NormProdResponse> queryForPage(NormProdRequest productRequest) {
        StandedProdPageQueryVo pageQueryVo = new StandedProdPageQueryVo();
        BeanUtils.copyProperties(pageQueryVo,productRequest);
        //查询结果
        PageInfo<StandedProduct> productPageInfo = standedProductAtomSV.queryForPage(pageQueryVo);
        //接口输出接口
        PageInfoForRes<NormProdResponse> normProdPageInfo = new PageInfoForRes<NormProdResponse>();
        BeanUtils.copyProperties(normProdPageInfo,productPageInfo);
        //添加结果集
        List<StandedProduct> productList = productPageInfo.getResult();
        List<NormProdResponse> normProductList = new ArrayList<NormProdResponse>();
        normProdPageInfo.setResult(normProductList);
        for (StandedProduct standedProduct:productList){
            NormProdResponse normProduct = new NormProdResponse();
            BeanUtils.copyProperties(normProduct,standedProduct);
//            normProduct.setCreateName(""); //TODO... 创建者账户名
//            normProduct.setOperTime(""); //TODO... 操作者账号名
            ProductCat productCat = catDefAtomSV.selectAllStateById(
                    standedProduct.getTenantId(),standedProduct.getProductCatId());
            if (productCat!=null)
                normProduct.setCatName(productCat.getProductCatName());
            normProduct.setCatId(standedProduct.getProductCatId());
            normProduct.setProductId(standedProduct.getStandedProdId());
            normProduct.setProductName(standedProduct.getStandedProductName());
            normProductList.add(normProduct);
        }
        return normProdPageInfo;
    }

    @Override
    public void discardProduct(String tenantId, String productId, Long operId, Date operTime) {
        StandedProduct standedProduct = standedProductAtomSV.selectById(tenantId,productId);
        if (standedProduct==null)
            throw  new BusinessException("","不存在指定标准品,租户ID:"+tenantId+",标准品标识:"+productId);
        //查询没有废弃的库存组
        int noDiscardNum = storageGroupAtomSV.queryCountNoDiscard(tenantId,productId);
        if (noDiscardNum>0)
            throw new BusinessException("","该标准品下存在["+noDiscardNum+"]个未废弃库存组");
        standedProduct.setOperId(operId);
        standedProduct.setOperTime(operTime==null?null:DateUtils.toTimeStamp(operTime));
        standedProduct.setState(StandedProductConstants.STATUS_DISCARD);//设置废弃
        //添加日志
        if (standedProductAtomSV.updateObj(standedProduct)>0){
            StandedProductLog productLog = new StandedProductLog();
            BeanUtils.copyProperties(productLog,standedProduct);
            standedProductLogAtomSV.insert(productLog);
        }
    }

    /**
     * 查询标准品下指定类型的属性及属性值信息
     *
     * @param tenantId
     * @param productId
     * @param attrType
     * @return
     */
    @Override
    public Map<ProdCatAttrDef, List<ProductAttrValDef>> queryAttrOfProduct(String tenantId, String productId, String attrType) {
        //查询标准品信息
        StandedProduct standedProduct = standedProductAtomSV.selectById(tenantId,productId);
        if (standedProduct==null)
            throw new BusinessException("","未找到对应标准品信息,租户ID:"+tenantId+",标准品标识:"+productId);
        Map<ProdCatAttrDef, List<ProductAttrValDef>> attrDefMap = new HashMap<>();
        //查询对应类目属性
        List<ProdCatAttrAttch> catAttrAttches = catAttrAttachAtomSV.queryAttrOfByIdAndType(tenantId,standedProduct.getProductCatId(),attrType);
        //查询标准品对应属性的属性值
        for (ProdCatAttrAttch catAttrAttch:catAttrAttches){
            ProdCatAttrDef catAttrDef = new ProdCatAttrDef();
            BeanUtils.copyProperties(catAttrDef,catAttrAttch);
            List<ProductAttrValDef> attrValDefList = new ArrayList<>();
            attrDefMap.put(catAttrDef,attrValDefList);
            //查询属性值
            List<StandedProdAttr> prodAttrs = standedProdAttrAtomSV.queryAttrVal(
                    tenantId,productId,catAttrAttch.getAttrId());
            for (StandedProdAttr prodAttr:prodAttrs){
                ProductAttrValDef valDef = new ProductAttrValDef();
                BeanUtils.copyProperties(valDef,prodAttr);
                valDef.setProductId(prodAttr.getStandedProdId());
                valDef.setAttrValId(prodAttr.getAttrvalueDefId());
                valDef.setAttrVal(prodAttr.getAttrValueName());
                valDef.setAttrVal2(prodAttr.getAttrValueName2());
                if (prodAttr.getAttrvalueDefId()!=null){
                    ProdAttrvalueDef attrvalueDef = attrValDefAtomSV.selectById(
                            tenantId,prodAttr.getAttrvalueDefId());
                    if (attrvalueDef!=null)
                        valDef.setAttrVal(attrvalueDef.getAttrValueName());
                }
                attrValDefList.add(valDef);
            }
        }

        return attrDefMap;
    }

    /**
     * 更新标准品的属性值
     */
    private void updateStandedProdAttr(NormProdSaveRequest normProdct){
        //
        List<NormProdAttrValRequest> attrValList = normProdct.getAttrValList();
        //查询原来的属性值
        Map<String,StandedProdAttr> oldAttrValMap = queryOldProdAttr(normProdct.getTenantId(),normProdct.getProductId());

        for (NormProdAttrValRequest attrValReq : attrValList) {
            //查询属性值是否已存在
            String attrKey = attrValReq.getAttrId()+ATTR_LINE_VAL+attrValReq.getAttrValId();
            StandedProdAttr prodAttr = oldAttrValMap.get(attrKey);
            int upNum = 0;
            //如果已经存在,则进行更新
            if (prodAttr!=null){
                prodAttr.setAttrValueName(attrValReq.getAttrVal());
                prodAttr.setAttrValueName2(attrValReq.getAttrVal2());
                prodAttr.setSerialNumber(attrValReq.getSerialNumber());
                prodAttr.setOperId(attrValReq.getOperId());
                prodAttr.setOperTime(attrValReq.getOperTime());
                upNum = standedProdAttrAtomSV.updateObj(prodAttr);
            }else {
                prodAttr = new StandedProdAttr();
                BeanUtils.copyProperties(prodAttr, attrValReq);
                prodAttr.setAttrvalueDefId(attrValReq.getAttrValId());
                prodAttr.setAttrValueName(attrValReq.getAttrVal());
                prodAttr.setAttrValueName2(attrValReq.getAttrVal2());
                prodAttr.setState(CommonSatesConstants.STATE_ACTIVE);//设置为有效
                upNum = standedProdAttrAtomSV.installObj(prodAttr);
            }
            oldAttrValMap.remove(attrKey);
            //添加日志
            if (upNum > 0) {
                StandedProdAttrLog prodAttrLog = new StandedProdAttrLog();
                BeanUtils.copyProperties(prodAttrLog, prodAttr);
                standedProdAttrLogAtomSV.installObj(prodAttrLog);
            }
        }
        //将未更新属性值设置为无效.
        loseActiveAttr(oldAttrValMap.values(),normProdct.getOperId());
    }

    /**
     * 获取标准品已关联属性值
     *
     * @param tenantId
     * @param productId
     * @return
     */
    private Map<String,StandedProdAttr> queryOldProdAttr(String tenantId,String productId){
        List<StandedProdAttr> prodAttrList = standedProdAttrAtomSV.queryByNormProduct(
                tenantId,productId);
        Map<String,StandedProdAttr> oldAttrMap = new HashMap<>();
        for (StandedProdAttr prodAttr:prodAttrList){
            oldAttrMap.put(prodAttr.getAttrId()+ATTR_LINE_VAL+prodAttr.getAttrvalueDefId(),prodAttr);
        }
        return oldAttrMap;
    }

    /**
     * 将为涉及的属性值设置为失效
     *
     * @param oldAttrValList
     */
    private void loseActiveAttr(Collection<StandedProdAttr> oldAttrValList, Long operId){
        if (oldAttrValList==null || oldAttrValList.isEmpty())
            return;
        for (StandedProdAttr prodAttr:oldAttrValList){
            prodAttr.setOperId(operId);
            prodAttr.setOperTime(DateUtils.currTimeStamp());
            prodAttr.setState(CommonSatesConstants.STATE_INACTIVE);
            if (standedProdAttrAtomSV.updateObj(prodAttr)>0){
                StandedProdAttrLog prodAttrLog = new StandedProdAttrLog();
                BeanUtils.copyProperties(prodAttrLog, prodAttr);
                standedProdAttrLogAtomSV.installObj(prodAttrLog);
            }
        }
    }

}
