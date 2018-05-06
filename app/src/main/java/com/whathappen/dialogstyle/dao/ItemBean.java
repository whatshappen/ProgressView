package com.whathappen.dialogstyle.dao;

/**
 * @author created by Wangw ;
 * @version 1.0
 * @data created time at 2018/5/6 ;
 * @Description itemBean
 */
public class ItemBean {

    public int numIconId;//序号
    public int progressTypeId;//类型图标
    public String progressName;//进度名称

    public ItemBean(int numIconId, int progressTypeId, String progressName) {
        this.numIconId = numIconId;
        this.progressTypeId = progressTypeId;
        this.progressName = progressName;
    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "numIconId=" + numIconId +
                ", progressTypeId=" + progressTypeId +
                ", progressName='" + progressName + '\'' +
                '}';
    }
}
