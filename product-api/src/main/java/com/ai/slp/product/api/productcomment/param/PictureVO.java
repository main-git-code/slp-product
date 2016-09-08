package com.ai.slp.product.api.productcomment.param;

import java.io.Serializable;

public class PictureVO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 图片目录
	 */
	private String picDir;
	
	/**
	 * 图片名称
	 */
	private String picName;
	
	private String vfsId;
	
	/**
	 * 序列号
	 */
	private Long serialNumber;
	
	public String getVfsId() {
		return vfsId;
	}

	public void setVfsId(String vfsId) {
		this.vfsId = vfsId;
	}

	public String getPicDir() {
		return picDir;
	}

	public void setPicDir(String picDir) {
		this.picDir = picDir;
	}

	public String getPicName() {
		return picName;
	}

	public void setPicName(String picName) {
		this.picName = picName;
	}

	public Long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(Long serialNumber) {
		this.serialNumber = serialNumber;
	}
}