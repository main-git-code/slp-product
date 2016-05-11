package com.ai.slp.product.api.productcat.param;

import com.ai.opt.base.vo.BaseInfo;
import com.ai.slp.product.api.productcat.interfaces.IProductCatSV;

import javax.validation.constraints.NotNull;

/**
 * 类目属性查询对象<br>
 *
 * Date: 2016年4月26日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * @author liutong5
 */
public class AttrQueryForCat extends BaseInfo {
    /**
     * 类目标识,必填
     */
    @NotNull(message = "类目标识不能为空",groups = {
            IProductCatSV.QueryAttrByCatAndType.class,
            IProductCatSV.QueryAttrAndValIdByCatAndType.class})
    private String productCatId;
    /**
     * 属性类型,必填<br>
     * 1关键属性;2销售属性;3非关键属性
     */
    @NotNull(message = "属性类型不能为空",groups = {
            IProductCatSV.QueryAttrByCatAndType.class,
            IProductCatSV.QueryAttrAndValIdByCatAndType.class})
    private String attrType;

    public String getProductCatId() {
        return productCatId;
    }

    public void setProductCatId(String productCatId) {
        this.productCatId = productCatId;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }
}
