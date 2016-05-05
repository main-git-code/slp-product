package com.ai.slp.product.service.atom.interfaces.storage;

import com.ai.slp.product.dao.mapper.bo.storage.StorageGroup;

/**
 * 库存组原子操作
 *
 * Created by jackieliu on 16/4/29.
 */
public interface IStorageGroupAtomSV {
    /**
     * 查询标准品下已经不是废弃状态的库存组
     *
     * @param tenantId
     * @param standedProdId
     * @return
     */
    public int queryCountNoDiscard(String tenantId,String standedProdId);

    /**
     * 查询标准品下启用状态的库存组
     *
     * @param tenantId
     * @param standedProdId
     * @return
     */
    public int queryCountActive(String tenantId,String standedProdId);

    /**
     * 添加库存组信息
     *
     * @param group
     * @return
     */
    public int installGroup(StorageGroup group);

    /**
     * 查询指定标识的库存组
     *
     * @param tenantId
     * @param groupId
     * @return
     */
    public StorageGroup queryByGroupId(String tenantId,Long groupId);

    /**
     * 更新指定库存组标识的库存组信息
     *
     * @param group
     * @return
     */
    public int updateById(StorageGroup group);
}
