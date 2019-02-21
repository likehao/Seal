package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class RechargeRecordData implements Serializable {

    private String rechargeTime;  //充值时间
    private String sealName;  //印章名称
    private String department; //部门
    private String setMeal;  //套餐
    private String endTime ; //到期时间
    private String money;  //实付金额

    public RechargeRecordData(String rechargeTime, String sealName, String department, String setMeal, String endTime, String money) {
        this.rechargeTime = rechargeTime;
        this.sealName = sealName;
        this.department = department;
        this.setMeal = setMeal;
        this.endTime = endTime;
        this.money = money;
    }

    public String getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(String rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSetMeal() {
        return setMeal;
    }

    public void setSetMeal(String setMeal) {
        this.setMeal = setMeal;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }


}
