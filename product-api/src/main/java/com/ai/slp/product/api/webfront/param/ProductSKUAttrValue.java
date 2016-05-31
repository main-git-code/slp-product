package com.ai.slp.product.api.webfront.param;

import java.io.Serializable;


public class ProductSKUAttrValue implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * 商品属性值标识
	 */
	private String attrvalueDefId;
	/**
	 * 属性值名称
	 */
	private String attrValueName;
	/**
	 * 是否自有属性
	 */
	private boolean isOwn;
	/**
     * 图片
     */
    private ProductImage image;
    /**
     * 图片url,有调用方组装
     */
    private String imageUrl;

	public String getAttrvalueDefId() {
		return attrvalueDefId;
	}
	public void setAttrvalueDefId(String attrvalueDefId) {
		this.attrvalueDefId = attrvalueDefId;
	}
	public boolean isOwn() {
		return isOwn;
	}
	public void setOwn(boolean own) {
		isOwn = own;
	}
	public String getAttrValueName() {
		return attrValueName;
	}
	public void setAttrValueName(String attrValueName) {
		this.attrValueName = attrValueName;
	}
	public ProductImage getImage() {
		return image;
	}
	public void setImage(ProductImage image) {
		this.image = image;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public boolean getIsOwn() {
		return isOwn;
	}
	public void setIsOwn(boolean isOwn) {
		this.isOwn = isOwn;
	}
}
