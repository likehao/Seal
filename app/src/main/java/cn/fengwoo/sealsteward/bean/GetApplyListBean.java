package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;
import java.util.List;

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
    private Integer applyCount;
    private String applyPdf;
    private String applyTime;
    private String applyUser;
    private String applyUserName;
    private Integer approveStatus;
    private String autoGraph;
    private Integer availableCount;
    private String expireTime;
    private String headPortrait;
    private String id;
    private String lastStampAddress;
    private String lastStampTime;
    private String orgStructureName;
    private Integer photoCount;
    private String sealId;
    private String sealName;
    private Integer stampCount;
    private String stampPdf;
    private List<String> stampRecordImgList;
    private String stampRecordPdf;
    public String getApplyCause() {
        return applyCause;
    }

    public void setApplyCause(String applyCause) {
        this.applyCause = applyCause;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    public String getApplyPdf() {
        return applyPdf;
    }

    public void setApplyPdf(String applyPdf) {
        this.applyPdf = applyPdf;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getAutoGraph() {
        return autoGraph;
    }

    public void setAutoGraph(String autoGraph) {
        this.autoGraph = autoGraph;
    }

    public Integer getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastStampAddress() {
        return lastStampAddress;
    }

    public void setLastStampAddress(String lastStampAddress) {
        this.lastStampAddress = lastStampAddress;
    }

    public String getLastStampTime() {
        return lastStampTime;
    }

    public void setLastStampTime(String lastStampTime) {
        this.lastStampTime = lastStampTime;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public Integer getStampCount() {
        return stampCount;
    }

    public void setStampCount(Integer stampCount) {
        this.stampCount = stampCount;
    }

    public String getStampPdf() {
        return stampPdf;
    }

    public void setStampPdf(String stampPdf) {
        this.stampPdf = stampPdf;
    }

    public List<String> getStampRecordImgList() {
        return stampRecordImgList;
    }

    public void setStampRecordImgList(List<String> stampRecordImgList) {
        this.stampRecordImgList = stampRecordImgList;
    }

    public String getStampRecordPdf() {
        return stampRecordPdf;
    }

    public void setStampRecordPdf(String stampRecordPdf) {
        this.stampRecordPdf = stampRecordPdf;
    }

    @Override
    public String toString() {
        return "GetApplyListBean{" +
                "applyCause='" + applyCause + '\'' +
                ", applyCount=" + applyCount +
                ", applyPdf='" + applyPdf + '\'' +
                ", applyTime='" + applyTime + '\'' +
                ", applyUser='" + applyUser + '\'' +
                ", applyUserName='" + applyUserName + '\'' +
                ", approveStatus=" + approveStatus +
                ", autoGraph='" + autoGraph + '\'' +
                ", availableCount=" + availableCount +
                ", expireTime='" + expireTime + '\'' +
                ", headPortrait='" + headPortrait + '\'' +
                ", id='" + id + '\'' +
                ", lastStampAddress='" + lastStampAddress + '\'' +
                ", lastStampTime='" + lastStampTime + '\'' +
                ", orgStructureName='" + orgStructureName + '\'' +
                ", photoCount=" + photoCount +
                ", sealId='" + sealId + '\'' +
                ", sealName='" + sealName + '\'' +
                ", stampCount=" + stampCount +
                ", stampPdf='" + stampPdf + '\'' +
                ", stampRecordImgList=" + stampRecordImgList +
                ", stampRecordPdf='" + stampRecordPdf + '\'' +
                '}';
    }



}
