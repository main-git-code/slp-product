package com.ai.slp.product.api.product.param;

import java.io.Serializable;

/**
 * 商品管理售中与仓库商品返回类
 * 
 * Date: 2016年4月25日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author lipeng16
 */
public class ProductStorageSale implements Serializable{

    private static final long serialVersionUID = 1L;

	/**
     * 商品名称
     */
    private String prodName;
    
    /**
     * 商品ID
     */
    private String prodId;

    /**
     * 商品类目ID
     */
    private String productCatId;
    
    /**
     * 商品类目名称
     */
    private String productCatName;

    /**
     * 商品类型
     */
    private String productType;
    
    /**
     * 商品图ID
     */
    private Long proPictureId;
    
    /**
     * 价格
     */
    private double salePrice;
    
    /**
     * 剩余库存量
     */
    private long storageNum;

    /**
     * 总销量
     */
    private long saleNum;

    /**
     * 状态
     * 0新增
     * 1未编辑2已编辑
     * 3审核中4审核未通过
     * 5在售
     * 6仓库中（审核通过放入） 61售罄下架62废弃下架63自动下架
     * 7停用8废弃
     */
    private String state;

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

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

    public Long getProPictureId() {
        return proPictureId;
    }

    public void setProPictureId(Long proPictureId) {
        this.proPictureId = proPictureId;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public long getStorageNum() {
        return storageNum;
    }

    public void setStorageNum(long storageNum) {
        this.storageNum = storageNum;
    }

    public long getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(long saleNum) {
        this.saleNum = saleNum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    
}
