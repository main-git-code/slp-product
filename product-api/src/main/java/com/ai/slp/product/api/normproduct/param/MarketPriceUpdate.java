package com.ai.slp.product.api.normproduct.param;


import com.ai.opt.base.vo.BaseInfo;
import com.ai.slp.product.api.normproduct.interfaces.INormProductSV;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * 标准品市场价更新请求<br>
 *
 * Date: 2016年4月22日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * @author liutong5
 */
public class MarketPriceUpdate extends BaseInfo {
    /**
     * 标准品ID
     */
    @NotBlank(message = "标准品ID不能为空",
            groups = { INormProductSV.UpdateMarketPrice.class })
    private String productId;
    /**
     * 市场价,单位:厘
     */
    @Min(value = 0,message = "市场价格不能小于0",
            groups = { INormProductSV.UpdateMarketPrice.class })
    private long marketPrice;

    /**
     * 操作人ID,必填<br>
     */
    @NotNull(message = "创建人ID不能为空",
            groups = { INormProductSV.UpdateMarketPrice.class})
    private Long operId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(long marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

}