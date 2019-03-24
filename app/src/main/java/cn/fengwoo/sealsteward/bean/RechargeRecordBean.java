package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class RechargeRecordBean implements Serializable {

    /**
     * 充值记录
     *  "data": [
     {
     "amountOfMoney": 0,  //资金总额
     "companyId": "string",
     "currentServiceExpireTime": "2019-03-23T02:13:22.874Z",
     "id": "string",
     "orderNumber": "string",
     "orgStructureName": "string",
     "packageContent": "string",
     "paymentResult": true,
     "paymentType": 0,
     "rechargeTime": "2019-03-23T02:13:22.874Z",
     "sealId": "string",
     "sealName": "string",
     "servicePackageId": "string",
     "userId": "string"
     }
     ],
     */
    private Double amountOfMoney;
    private String companyId;
    private String currentServiceExpireTime;
    private String id;
    private String orderNumber;
    private String orgStructureName;
    private String packageContent;
    private Boolean paymentResult;
    private Integer paymentType;
    private String rechargeTime;
    private String sealId;
    private String sealName;
    private String servicePackageId;
    private String userId;

    public Double getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Double amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCurrentServiceExpireTime() {
        return currentServiceExpireTime;
    }

    public void setCurrentServiceExpireTime(String currentServiceExpireTime) {
        this.currentServiceExpireTime = currentServiceExpireTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    public String getPackageContent() {
        return packageContent;
    }

    public void setPackageContent(String packageContent) {
        this.packageContent = packageContent;
    }

    public Boolean getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(Boolean paymentResult) {
        this.paymentResult = paymentResult;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(String rechargeTime) {
        this.rechargeTime = rechargeTime;
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

    public String getServicePackageId() {
        return servicePackageId;
    }

    public void setServicePackageId(String servicePackageId) {
        this.servicePackageId = servicePackageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "RechargeRecordBean{" +
                "amountOfMoney=" + amountOfMoney +
                ", companyId='" + companyId + '\'' +
                ", currentServiceExpireTime='" + currentServiceExpireTime + '\'' +
                ", id='" + id + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", orgStructureName='" + orgStructureName + '\'' +
                ", packageContent='" + packageContent + '\'' +
                ", paymentResult=" + paymentResult +
                ", paymentType=" + paymentType +
                ", rechargeTime='" + rechargeTime + '\'' +
                ", sealId='" + sealId + '\'' +
                ", sealName='" + sealName + '\'' +
                ", servicePackageId='" + servicePackageId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

}
