package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class OfflineRecordData implements Serializable {
    /**
     *  "data": [
     {
     "flowNumber": "string",
     "sealId": "string",
     "sealName": "string",
     "stampSeqNumber": 0,
     "stampTime": "2019-04-19T06:57:36.851Z",
     "stampType": 0,
     "userName": "string",
     "userNumber": 0
     }
     ],
     */

   private String flowNumber;
   private String sealId;
   private String sealName;
   private Integer stampSeqNumber;
   private String stampTime;
   private Integer stampType;
   private String userName;
   private Integer userNumber;

    public String getFlowNumber() {
        return flowNumber;
    }

    public void setFlowNumber(String flowNumber) {
        this.flowNumber = flowNumber;
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

    public Integer getStampSeqNumber() {
        return stampSeqNumber;
    }

    public void setStampSeqNumber(Integer stampSeqNumber) {
        this.stampSeqNumber = stampSeqNumber;
    }

    public String getStampTime() {
        return stampTime;
    }

    public void setStampTime(String stampTime) {
        this.stampTime = stampTime;
    }

    public Integer getStampType() {
        return stampType;
    }

    public void setStampType(Integer stampType) {
        this.stampType = stampType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }


    @Override
    public String toString() {
        return "OfflineRecordData{" +
                "flowNumber='" + flowNumber + '\'' +
                ", sealId='" + sealId + '\'' +
                ", sealName='" + sealName + '\'' +
                ", stampSeqNumber=" + stampSeqNumber +
                ", stampTime='" + stampTime + '\'' +
                ", stampType=" + stampType +
                ", userName='" + userName + '\'' +
                ", userNumber=" + userNumber +
                '}';
    }

}
