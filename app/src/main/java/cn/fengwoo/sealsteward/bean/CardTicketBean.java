package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

/**
 * 卡券
 */
public class CardTicketBean implements Serializable{
    /**
     * {
     "code": 0,
     "data": [
     {
     "amountOfMoney": 0,
     "content": "string",
     "createTime": "2019-08-30T01:28:42.192Z",
     "id": "string",
     "receiveStatus": true,
     "useMonths": 0
     }
     ],
     "message": "string"
     }
     */

    private Double amountOfMoney;
    private String content;
    private String createTime;
    private String id;
    private Boolean receiveStatus;
    private Integer useMonths;
    private Integer rechargeType;

    public CardTicketBean(Double amountOfMoney, String content, Boolean receiveStatus,String id,Integer rechargeType) {
        this.amountOfMoney = amountOfMoney;
        this.content = content;
        this.receiveStatus = receiveStatus;
        this.id = id;
        this.rechargeType = rechargeType;
    }

    @Override
    public String toString() {
        return "CardTicketBean{" +
                "amountOfMoney='" + amountOfMoney + '\'' +
                ", content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                ", id='" + id + '\'' +
                ", receiveStatus=" + receiveStatus +
                ", useMonths=" + useMonths +
                ", rechargeType=" + rechargeType +
                '}';
    }

    public Double getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Double amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(Boolean receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public Integer getUseMonths() {
        return useMonths;
    }

    public void setUseMonths(Integer useMonths) {
        this.useMonths = useMonths;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
    }
}
