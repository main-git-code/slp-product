package com.ai.slp.product.api.productCat;

import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.slp.product.api.productcat.interfaces.IProductCatCacheSV;
import com.ai.slp.product.api.productcat.param.ProductCatInfo;
import com.ai.slp.product.api.productcat.param.ProductCatUniqueReq;
import org.junit.Test;

/**
 * Created by jackieliu on 16/7/25.
 */
public class IProductCatCacheConsumerSV {

    @Test
    public void queryByCatId(){
        IProductCatCacheSV catCacheSV = DubboConsumerFactory.getService(IProductCatCacheSV.class);
        ProductCatUniqueReq uniqueReq = new ProductCatUniqueReq();
        uniqueReq.setTenantId("SLP");
        uniqueReq.setProductCatId("10000010010000");
        ProductCatInfo catInfo = catCacheSV.queryByCatId(uniqueReq);
        System.out.println(catInfo.getProductCatName());
    }
}
