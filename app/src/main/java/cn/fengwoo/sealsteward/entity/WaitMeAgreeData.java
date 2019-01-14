package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class WaitMeAgreeData implements Serializable {

    private String cause;
    private String applyPerson;
    private String department;
    private String applyTime;
    private String sealName;

    public WaitMeAgreeData(String cause, String applyPerson, String department, String applyTime, String sealName) {
        this.cause = cause;
        this.applyPerson = applyPerson;
        this.department = department;
        this.applyTime = applyTime;
        this.sealName = sealName;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getApplyPerson() {
        return applyPerson;
    }

    public void setApplyPerson(String applyPerson) {
        this.applyPerson = applyPerson;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

}
