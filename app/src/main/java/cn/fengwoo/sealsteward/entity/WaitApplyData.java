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

}
