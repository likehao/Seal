package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class SeeRecordBean implements Serializable {

    private String SerialNumber;   //流水号
    private String sealPerson;   //盖章人
    private String sealName;     //印章名称
    private String sealTime;  //盖章时间
    private String sealAddress;  //盖章地址
    private Integer stampType;  //盖章方式

    public SeeRecordBean(String serialNumber, String sealTime, String sealAddress) {
        this.SerialNumber = serialNumber;
        this.sealTime = sealTime;
        this.sealAddress = sealAddress;
    }

    public SeeRecordBean(String serialNumber, String sealName, String sealPerson, String sealTime,Integer stampType) {
        this.SerialNumber = serialNumber;
        this.sealName = sealName;
        this.sealPerson = sealPerson;
        this.sealTime = sealTime;
        this.stampType = stampType;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }


    public String getSealPerson() {
        return sealPerson;
    }

    public void setSealPerson(String sealPerson) {
        this.sealPerson = sealPerson;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getSealTime() {
        return sealTime;
    }

    public void setSealTime(String sealTime) {
        this.sealTime = sealTime;
    }

    public String getSealAddress() {
        return sealAddress;
    }

    public void setSealAddress(String sealAddress) {
        this.sealAddress = sealAddress;
    }

    public Integer getStampType() {
        return stampType;
    }

    public void setStampType(Integer stampType) {
        this.stampType = stampType;
    }

}
