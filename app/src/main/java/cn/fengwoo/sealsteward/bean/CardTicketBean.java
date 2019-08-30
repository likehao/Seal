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

    private Integer amountOfMoney;
    private String content;
    private String createTime;
    private String id;
    private Boolean receiveStatus;
    private Integer useMonths;

    public CardTicketBean(Integer amountOfMoney, String content, Boolean receiveStatus,String id) {
        this.amountOfMoney = amountOfMoney;
        this.content = content;
        this.receiveStatus = receiveStatus;
        this.id = id;
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
                '}';
    }

    public Integer getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Integer amountOfMoney) {
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
}
