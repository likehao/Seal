package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class RechargeRecordData implements Serializable {

    private String rechargeTime;  //充值时间
    private Boolean rechargeResult;  //充值结果
    private String sealName;  //印章名称
    private String department; //部门
    private String setMeal;  //套餐
    private String endTime; //到期时间
    private Double money;  //实付金额
    private Integer rechargeWay;  //支付方式

    public RechargeRecordData(String rechargeTime, Boolean rechargeResult, String sealName, String department, String setMeal, Integer rechargeWay, String endTime, Double money) {
        this.rechargeTime = rechargeTime;
        this.rechargeResult = rechargeResult;
        this.sealName = sealName;
        this.department = department;
        this.setMeal = setMeal;
        this.rechargeWay = rechargeWay;
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

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getRechargeWay() {
        return rechargeWay;
    }

    public void setRechargeWay(Integer rechargeWay) {
        this.rechargeWay = rechargeWay;
    }

    public Boolean getRechargeResult() {
        return rechargeResult;
    }

    public void setRechargeResult(Boolean rechargeResult) {
        this.rechargeResult = rechargeResult;
    }

}
