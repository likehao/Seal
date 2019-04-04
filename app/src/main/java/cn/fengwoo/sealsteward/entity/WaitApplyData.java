package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

/**
 * 待审批adapter
 */
public class WaitApplyData implements Serializable {

    private String cause;
    private String sealName;
    private String failTime;
    private Integer applyCount;
    private String applyTime;
    private String id;
    private Integer approveStatus;
    private String applyUserName;
    private String orgStructureName;

    private String headPortrait;
    private String sealPeople;
    private Integer sealCount;
    private Integer restCount;
    private Integer uploadPhotoNum;

    public WaitApplyData(String cause, String sealName, String failTime, Integer applyCount, String applyTime, String id, Integer approveStatus, String applyUserName, String orgStructureName
    ,String sealPeople,String headPortrait, Integer sealCount ,Integer restCount ,Integer uploadPhotoNum) {
        this.cause = cause;
        this.sealName = sealName;
        this.failTime = failTime;
        this.applyCount = applyCount;
        this.applyTime = applyTime;
        this.id = id;
        this.approveStatus = approveStatus;

        this.applyUserName = applyUserName;
        this.orgStructureName = orgStructureName;

        this.sealPeople = sealPeople;
        this.headPortrait = headPortrait;
        this.sealCount = sealCount;
        this.restCount = restCount;
        this.uploadPhotoNum = uploadPhotoNum;
    }
    public WaitApplyData(String cause, String sealName, String failTime, Integer applyCount, String applyTime, String id, Integer approveStatus) {
        this.cause = cause;
        this.sealName = sealName;
        this.failTime = failTime;
        this.applyCount = applyCount;
        this.applyTime = applyTime;
        this.id = id;
        this.approveStatus = approveStatus;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getFailTime() {
        return failTime;
    }

    public void setFailTime(String failTime) {
        this.failTime = failTime;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
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

    public String getSealPeople() {
        return sealPeople;
    }

    public void setSealPeople(String sealPeople) {
        this.sealPeople = sealPeople;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public Integer getSealCount() {
        return sealCount;
    }

    public void setSealCount(Integer sealCount) {
        this.sealCount = sealCount;
    }

    public Integer getRestCount() {
        return restCount;
    }

    public void setRestCount(Integer restCount) {
        this.restCount = restCount;
    }

    public Integer getUploadPhotoNum() {
        return uploadPhotoNum;
    }

    public void setUploadPhotoNum(Integer uploadPhotoNum) {
        this.uploadPhotoNum = uploadPhotoNum;
    }


}
