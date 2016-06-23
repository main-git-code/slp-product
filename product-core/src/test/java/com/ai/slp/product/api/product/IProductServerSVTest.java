package com.ai.slp.product.api.product;

import com.ai.slp.product.api.product.interfaces.IProductServerSV;
import com.ai.slp.product.api.product.param.SkuInfoQuery;
import com.ai.slp.product.constants.CommonConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jackieliu on 16/6/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:context/core-context.xml")
public class IProductServerSVTest {
    @Autowired
    IProductServerSV productServerSV;

    @Test
    public void queryProductSkuById(){
        SkuInfoQuery skuInfoQuery = new SkuInfoQuery();
        skuInfoQuery.setTenantId(CommonConstants.COMMON_TENANT_ID);
        skuInfoQuery.setSkuId("1000000000002405");
        productServerSV.queryProductSkuById(skuInfoQuery);
    }
}