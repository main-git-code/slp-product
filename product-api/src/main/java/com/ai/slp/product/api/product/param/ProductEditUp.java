package com.ai.slp.product.api.product.param;

import java.sql.Date;

import com.ai.opt.base.vo.BaseResponse;
/**
 * 商品编辑上架返回类
 * 
 * Date: 2016年4月22日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author lipeng
 */
public class ProductEditUp extends BaseResponse{
    
    /**
     * 商品ID
     */
    private String prodId;
    
    /**
     * 商品类目ID
     */
    private String productCatId;
    
    /**
     * 类目名称
     */
    private String productCatName;
    
    /**
     * 商品类型
     */
    private String productType;
    
    /**
     * 商品图ID
     */
    private long proPictureId;
    
    /**
     * 商品名称
     */
    private String prodName;
    
    /**
     * 价格
     */
    private long salePrice;
    
    /**
     * 总库存
     */
    private long totalNum;
    
    /**
     * 状态
     * 0
     * 1未编辑2已编辑
     * 3审核中4审核未通过
     * 5在售
     * 6仓库中（审核通过放入） 61售罄下架62废弃下架63自动下架
     * 7停用8废弃
     */
    private String state;
    
    /**
     * 申请优先级:1优先0普通
     */
    private int priorityNumber;
    
    /**
     * 优先理由
     */
    private String priorityReason;
    
    /**
     * 生成时间-排序按时间升序
     */
    private Date creatTime;
    
    /**
     * 拒绝原因-被拒绝参数
     */
    private String refuseReason;
    
    /**
     * 拒绝描述-被拒绝参数
     */
    private String refuseDes;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProductCatId() {
        return productCatId;
    }

    public void setProductCatId(String productCatId) {
        this.productCatId = productCatId;
    }

    public String getProductCatName() {
        return productCatName;
    }

    public void setProductCatName(String productCatName) {
        this.productCatName = productCatName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public long getProPictureId() {
        return proPictureId;
    }

    public void setProPictureId(long proPictureId) {
        this.proPictureId = proPictureId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public long getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(long salePrice) {
        this.salePrice = salePrice;
    }

    public long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(long totalNum) {
        this.totalNum = totalNum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPriorityNumber() {
        return priorityNumber;
    }

    public void setPriorityNumber(int priorityNumber) {
        this.priorityNumber = priorityNumber;
    }

    public String getPriorityReason() {
        return priorityReason;
    }

    public void setPriorityReason(String priorityReason) {
        this.priorityReason = priorityReason;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public String getRefuseDes() {
        return refuseDes;
    }

    public void setRefuseDes(String refuseDes) {
        this.refuseDes = refuseDes;
    }

    
}
