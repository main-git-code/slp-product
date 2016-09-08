package com.ai.slp.product.service.business.impl.comment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.opt.base.vo.BaseResponse;
import com.ai.opt.base.vo.PageInfoResponse;
import com.ai.opt.base.vo.ResponseHeader;
import com.ai.opt.sdk.constants.ExceptCodeConstants;
import com.ai.opt.sdk.util.BeanUtils;
import com.ai.opt.sdk.util.StringUtil;
import com.ai.slp.product.api.productcomment.param.PictureVO;
import com.ai.slp.product.api.productcomment.param.ProdCommentCreateRequest;
import com.ai.slp.product.api.productcomment.param.ProdCommentPageRequest;
import com.ai.slp.product.api.productcomment.param.ProdCommentPageResponse;
import com.ai.slp.product.api.productcomment.param.ProdCommentVO;
import com.ai.slp.product.constants.ProductCommentConstants;
import com.ai.slp.product.dao.mapper.bo.ProdComment;
import com.ai.slp.product.dao.mapper.bo.ProdCommentPicture;
import com.ai.slp.product.service.atom.interfaces.comment.IProdCommentAtomSV;
import com.ai.slp.product.service.atom.interfaces.comment.IProdCommentPictureAtomSV;
import com.ai.slp.product.service.business.interfaces.comment.IProdCommentBusiSV;

@Service
@Transactional
public class ProdCommentBusiSVImpl implements IProdCommentBusiSV {

	@Autowired
	IProdCommentAtomSV prodCommentAtomSV;
	@Autowired
	IProdCommentPictureAtomSV prodCommentPictureAtomSV;
	
	@Override
	public PageInfoResponse<ProdCommentPageResponse> queryPageBySku(ProdCommentPageRequest prodCommentPageRequest) {
		PageInfoResponse<ProdCommentPageResponse> result = new PageInfoResponse<ProdCommentPageResponse>();
		//查询评论信息
		ProdComment params = new ProdComment();
		BeanUtils.copyProperties(params, prodCommentPageRequest);
		Integer pageSize = prodCommentPageRequest.getPageSize();
		Integer pageNo = prodCommentPageRequest.getPageNo();
		List<ProdComment> queryPageList = prodCommentAtomSV.queryPageList(params, pageSize, pageNo);
		ResponseHeader responseHeader = new ResponseHeader(true,ExceptCodeConstants.Special.SUCCESS,"");
		result.setResponseHeader(responseHeader );
		result.setPageNo(pageNo);
		result.setPageSize(pageSize);
		if(queryPageList == null || queryPageList.size() == 0){
			result.setCount(0);
			result.setResult(null);
		}else{
			//查询条数
			Integer count = prodCommentAtomSV.queryCountByParams(params);
			result.setCount(count);
			List<ProdCommentPageResponse> prodCommentList = getProdCommentResponseList(queryPageList);
			result.setResult(prodCommentList);
		}
		return result;
	}

	/**
	 * 获得分页查询返回对象List
	 * @param queryPageList
	 * @return
	 * @author jiaxs
	 * @ApiDocMethod
	 * @ApiCode
	 * @RestRelativeURL
	 */
	private List<ProdCommentPageResponse> getProdCommentResponseList(List<ProdComment> queryPageList) {
		//获得有图片的评论
		Set<String> commentIdSet = new HashSet<String>();
		for(ProdComment prodComment : queryPageList){
			String isPicture = prodComment.getIsPicture();
			if(ProductCommentConstants.HasPicture.YSE.equals(isPicture)){
				commentIdSet.add(prodComment.getCommentId());
			}
		}
		//查询图片信息
		Map<String, List<PictureVO>> commentPictureMap = new HashMap<String, List<PictureVO>>();
		if(commentIdSet.size()>0){
			for(String commentId : commentIdSet){
				List<ProdCommentPicture> pictureList = prodCommentPictureAtomSV.queryPictureListByCommentId(commentId);
				List<PictureVO> pictureVoList = new LinkedList<PictureVO>();
				for(ProdCommentPicture pricture : pictureList){
					PictureVO pictureVO = new PictureVO();
					pictureVO.setPicDir(pricture.getPicAddr());
					pictureVO.setPicName(pricture.getPicName());
					pictureVoList.add(pictureVO);
				}
				commentPictureMap.put(commentId, pictureVoList);
			}
		}
		List<ProdCommentPageResponse> prodCommentList = new LinkedList<ProdCommentPageResponse>();
		for(ProdComment prodComment : queryPageList){
			//转换返回对象
			ProdCommentPageResponse prodCommentPageResponse = new ProdCommentPageResponse();
			BeanUtils.copyProperties(prodCommentPageResponse, prodComment);
			//设置图片list
			String commentId = prodComment.getCommentId();
			if(commentPictureMap.containsKey(commentId)){
				prodCommentPageResponse.setPictureList(commentPictureMap.get(commentId));
			}
			prodCommentList.add(prodCommentPageResponse);
		}
		return prodCommentList;
	}

	@Override
	public BaseResponse createProdComment(ProdCommentCreateRequest prodCommentCreateRequest) {
		BaseResponse baseResponse = new BaseResponse();
		List<ProdCommentVO> commentList = prodCommentCreateRequest.getCommentList();
		if(commentList != null && commentList.size() >0){
			for(ProdCommentVO prodCommentVO : commentList){
				//添加评论
				ProdComment params = new ProdComment();
				BeanUtils.copyProperties(params, prodCommentVO);
				params.setTenantId(prodCommentCreateRequest.getTenantId());
				params.setOrderId(prodCommentCreateRequest.getOrderId());
				List<PictureVO> pictureList = prodCommentVO.getPictureList();
				//判断是否有图片
				boolean isHasPicture = pictureList != null && pictureList.size() >0;
				if(isHasPicture){
					params.setIsPicture(ProductCommentConstants.HasPicture.YSE);
				}else{
					params.setIsPicture(ProductCommentConstants.HasPicture.NO);
				}
				String prodCommentId = prodCommentAtomSV.createProdComment(params);
				//添加商品图片
				if(!StringUtil.isBlank(prodCommentId) && isHasPicture){
					for(PictureVO pictureVO : pictureList){
						ProdCommentPicture prodCommentPicture = new ProdCommentPicture();
						BeanUtils.copyProperties(prodCommentPicture, pictureVO);
						prodCommentPicture.setCommentId(prodCommentId);
						prodCommentPictureAtomSV.createPicture(prodCommentPicture);
					}
				}
			}
			ResponseHeader responseHeader = new ResponseHeader(true,ExceptCodeConstants.Special.SUCCESS,"");
			baseResponse.setResponseHeader(responseHeader );
		}else{
			ResponseHeader responseHeader = new ResponseHeader(false,ExceptCodeConstants.Special.NO_DATA_OR_CACAE_ERROR,"无数据");
			baseResponse.setResponseHeader(responseHeader );
		}
		return baseResponse;
	}

}