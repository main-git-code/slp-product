package com.ai.slp.product.service.atom.impl.product;

import com.ai.slp.product.constants.CommonSatesConstants;
import com.ai.slp.product.constants.ProductConstants;
import com.ai.slp.product.dao.mapper.bo.product.ProdAudiences;
import com.ai.slp.product.dao.mapper.bo.product.ProdAudiencesCriteria;
import com.ai.slp.product.dao.mapper.interfaces.product.ProdAudiencesMapper;
import com.ai.slp.product.service.atom.interfaces.product.IProdAudiencesAtomSV;
import com.ai.slp.product.util.DateUtils;
import com.ai.slp.product.util.SequenceUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jackieliu on 16/6/2.
 */
@Component
public class ProdAudiencesAtomSVImpl implements IProdAudiencesAtomSV {
    @Autowired
    ProdAudiencesMapper audiencesMapper;

    /**
     * 查询符合用户类型和用户ID的受众新,用户类型和用户ID不能均为空
     *
     * @param tenantId
     * @param userType
     * @param userId
     * @param hasDiscard
     * @return
     */
    @Override
    public List<ProdAudiences> queryByUserType(String tenantId,String prodId, String userType, String userId, boolean hasDiscard) {
        if (StringUtils.isBlank(userType) && StringUtils.isBlank(userId))
            return Collections.emptyList();
        ProdAudiencesCriteria example = new ProdAudiencesCriteria();
        ProdAudiencesCriteria.Criteria criteria = example.createCriteria();
        criteria.andTenantIdEqualTo(tenantId)
                .andProdIdEqualTo(prodId);
        if (StringUtils.isNotBlank(userType))
            criteria.andUserTypeEqualTo(userType);
        if (StringUtils.isNotBlank(userId)) {
            List<String> userIdList = new ArrayList<>();
            userIdList.add(userId);
            userIdList.add(ProductConstants.ProdAudiences.userId.USER_TYPE);
            criteria.andUserIdIn(userIdList);
        }else {
            criteria.andUserIdEqualTo(ProductConstants.ProdAudiences.userId.USER_TYPE);
        }
        if (!hasDiscard){
            criteria.andStateEqualTo(CommonSatesConstants.STATE_ACTIVE);
        }
        return audiencesMapper.selectByExample(example);
    }

    @Override
    public int installAudiences(ProdAudiences prodAudiences) {
        prodAudiences.setProdAudiencesId(SequenceUtil.genProdAudiencesId());
        prodAudiences.setOperTime(DateUtils.currTimeStamp());
        return audiencesMapper.insert(prodAudiences);
    }

    @Override
    public int updateNoUser(String tenantId, String prodId, String userType,Long operId) {
        ProdAudiencesCriteria example = new ProdAudiencesCriteria();
        example.createCriteria().andTenantIdEqualTo(tenantId)
                .andProdIdEqualTo(prodId)
                .andUserTypeEqualTo(userType)
                .andStateEqualTo(CommonSatesConstants.STATE_ACTIVE);
        ProdAudiences prodAudiences = new ProdAudiences();
        prodAudiences.setState(CommonSatesConstants.STATE_INACTIVE);
        prodAudiences.setOperId(operId);
        prodAudiences.setOperTime(DateUtils.currTimeStamp());
        return audiencesMapper.updateByExampleSelective(prodAudiences,example);
    }
}
