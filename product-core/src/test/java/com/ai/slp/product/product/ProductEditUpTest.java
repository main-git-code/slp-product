package com.ai.slp.product.product;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ai.slp.product.api.product.param.ProductEditQueryReq;
import com.ai.slp.product.service.business.interfaces.IProductManagerBsuiSV;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:context/core-context.xml")
public class ProductEditUpTest {
	    @Autowired
	    IProductManagerBsuiSV productManagerBsuiSV;
	    @Test
	    public void queryProdEdit() {
	    	ProductEditQueryReq queryReq = new ProductEditQueryReq();
	    	queryReq.setTenantId("SLP");
	    	queryReq.setProductCatId("1");
			// 设置商品状态为新增和未编辑
			List<String> stateList = new ArrayList<>();
			// 设置状态，新增：0；未编辑1.
			stateList.add("0");
			stateList.add("1");
			queryReq.setStateList(stateList);
	    	productManagerBsuiSV.queryPageForEdit(queryReq);
	    }
}