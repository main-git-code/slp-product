package com.ai.slp.product.api.storage.param;

import javax.validation.constraints.NotNull;

import com.ai.opt.base.vo.BaseInfo;
import com.ai.slp.product.api.storage.interfaces.IStorageSV;

import java.util.Map;

/**
 * 库存售价信息<br>
 * 用于更新库存销售价
 *
 * Date: 2016年4月24日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * @author liutong5
 */
public class StorageSalePrice extends BaseInfo {
    private static final long serialVersionUID = 1L;

    /**
     * 操作人ID，必填
     */
    @NotNull(message = "操作人不能为空",
            groups = { IStorageSV.UpdateMultiStorageSalePrice.class })
    private Long operId;
    /**
     * 库存与待更新价格<br>
     * K:库存标识,不能为空;V:库存对应销售价,不能为空,不能小于0
     */
    private Map<String,Long> storageSalePrice;

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public Map<String, Long> getStorageSalePrice() {
        return storageSalePrice;
    }

    public void setStorageSalePrice(Map<String, Long> storageSalePrice) {
        this.storageSalePrice = storageSalePrice;
    }
}
