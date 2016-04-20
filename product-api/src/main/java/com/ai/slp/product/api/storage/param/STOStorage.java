package com.ai.slp.product.api.storage.param;

import com.ai.slp.product.api.storage.interfaces.INormProductStorageSV;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 虚拟库存信息<br>
 *
 * Date: 2016年4月20日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * @author liutong5
 */
public class STOStorage extends StorageStatus{
    /**
     * 库存名称
     */
    @NotNull(message = "库存名称不能为空",
            groups = { INormProductStorageSV.SaveStorage.class })
    @Length(max = 300,message = "库存名称长度不允许超过300",
            groups = {INormProductStorageSV.SaveStorage.class})
    private String storageName;

    /**
     * 库存组id
     */
    @NotNull(message = "库存组标识不能为空",
            groups = { INormProductStorageSV.SaveStorage.class })
    private Long groupId;

    /**
     * 虚拟库存量
     */
    @Min(value = 0,message = "虚拟库存量不能小于0",
            groups = {INormProductStorageSV.SaveStorage.class})
    private long totalNum;
    /**
     * 预警库存量
     */
    @Min(value = 0,message = "预警库存量不能小于0",
            groups = {INormProductStorageSV.SaveStorage.class})
    private long warnNum;

    /**
     * 生效时间
     */
    private Date activeTime;
    /**
     * 失效时间
     */
    private Date inactiveTime;

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(long totalNum) {
        this.totalNum = totalNum;
    }

    public long getWarnNum() {
        return warnNum;
    }

    public void setWarnNum(long warnNum) {
        this.warnNum = warnNum;
    }

    public Date getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Date activeTime) {
        this.activeTime = activeTime;
    }

    public Date getInactiveTime() {
        return inactiveTime;
    }

    public void setInactiveTime(Date inactiveTime) {
        this.inactiveTime = inactiveTime;
    }
}
