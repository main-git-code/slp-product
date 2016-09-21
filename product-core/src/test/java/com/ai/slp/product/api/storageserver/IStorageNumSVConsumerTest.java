package com.ai.slp.product.api.storageserver;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ai.opt.base.vo.BaseResponse;
import com.ai.opt.base.vo.ResponseHeader;
import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.slp.product.api.storageserver.interfaces.IStorageNumSV;
import com.ai.slp.product.api.storageserver.param.StorageNumBackReq;
import com.ai.slp.product.api.storageserver.param.StorageNumRes;
import com.ai.slp.product.api.storageserver.param.StorageNumUserReq;
import com.ai.slp.product.constants.CommonTestConstants;

/**
 * Created by jackieliu on 16/7/12.
 */
public class IStorageNumSVConsumerTest {

    @Test
    public void useStorageNum(){
        IStorageNumSV storageNumSV = DubboConsumerFactory.getService(IStorageNumSV.class);
        StorageNumUserReq userReq = new StorageNumUserReq();
        userReq.setTenantId(CommonTestConstants.COMMON_TENANT_ID);
        userReq.setSkuId("0000000000000194");
        userReq.setSkuNum(4);
        StorageNumRes numRes = storageNumSV.useStorageNum(userReq);
        System.out.println(numRes.toString());
        Map<String,Integer> skuMap = numRes.getStorageNum();
        for (Map.Entry<String,Integer> skuNum:skuMap.entrySet()){
            System.out.println("Sku storage="+skuNum.getKey()+",num="+skuNum.getValue());
        }
    }

    @Test
    public void backStorageNum(){
        IStorageNumSV storageNumSV = DubboConsumerFactory.getService(IStorageNumSV.class);
        StorageNumBackReq backReq = new StorageNumBackReq();
        backReq.setTenantId("SLP");
        backReq.setSkuId("1000000000002409");
        Map<String,Integer> userMap = new HashMap<>();
        userMap.put("100000100009",1);
        backReq.setStorageNum(userMap);
        BaseResponse baseResponse = storageNumSV.backStorageNum(backReq);
        ResponseHeader header = baseResponse.getResponseHeader();
        System.out.println(header.getResultCode()+","+header.getResultMessage());
    }
}
