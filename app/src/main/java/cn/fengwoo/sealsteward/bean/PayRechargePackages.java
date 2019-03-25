package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class PayRechargePackages implements Serializable {


    /**
     * 费用套餐
     *   "data": [
     {
     "amountOfMoney": 0,
     "content": "string",
     "id": "string"
     }
     ],
     */
    private Double amountOfMoney;
    private String content;
    private String id;

    public PayRechargePackages(Double amountOfMoney, String content, String id) {
        this.amountOfMoney = amountOfMoney;
        this.content = content;
        this.id = id;
    }

    public PayRechargePackages(String content ) {
        this.content = content;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PayRechargePackages{" +
                "amountOfMoney=" + amountOfMoney +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
