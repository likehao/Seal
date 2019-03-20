package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class GetApplyListBean implements Serializable {
    /**
     * 审批列表返回数据
     * "data": [
     * {
     * "applyCause": "string",
     * "applyCount": 0,
     * "applyPdf": "string",
     * "applyTime": "2019-03-16T09:11:22.199Z",
     * "applyUser": "string",
     * "applyUserName": "string",
     * "approveStatus": 0,
     * "autoGraph": "string",
     * "availableCount": 0,
     * "expireTime": "2019-03-16T09:11:22.199Z",
     * "headPortrait": "string",
     * "id": "string",
     * "lastStampAddress": "string",
     * "lastStampTime": "2019-03-16T09:11:22.199Z",
     * "orgStructureName": "string",
     * "photoCount": 0,
     * "sealId": "string",
     * "sealName": "string",
     * "stampCount": 0,
     * "stampPdf": "string",
     * "stampRecordImgList": [
     * "string"
     * ],
     * "stampRecordPdf": "string"
     * }
     * ],
     */
    private String applyCause;
    private String sealName;
    private String expireTime;
    private Integer applyCount;
    private String applyTime;
    private String applyUserName;
    private String orgStructureName;

    public String getApplyCause() {
        return applyCause;
    }

    public void setApplyCause(String applyCause) {
        this.applyCause = applyCause;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    @Override
    public String toString() {
        return "GetApplyListBean{" +
                "applyCause='" + applyCause + '\'' +
                ", sealName='" + sealName + '\'' +
                ", expireTime='" + expireTime + '\'' +
                ", applyCount='" + applyCount + '\'' +
                ", applyTime='" + applyTime + '\'' +
                ", applyUserName='" + applyUserName + '\'' +
                ", orgStructureName='" + orgStructureName + '\'' +
                '}';
    }
}
