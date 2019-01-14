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

    public WaitApplyData(String cause, String sealName, String failTime, Integer applyCount, String applyTime) {
        this.cause = cause;
        this.sealName = sealName;
        this.failTime = failTime;
        this.applyCount = applyCount;
        this.applyTime = applyTime;
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

}
