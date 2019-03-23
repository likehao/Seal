package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class SeeRecordBean implements Serializable {

    private String SerialNumber;   //流水号
    private String sealTime;  //盖章时间
    private String sealAddress;  //盖章地址

    public SeeRecordBean(String serialNumber, String sealTime, String sealAddress) {
        SerialNumber = serialNumber;
        this.sealTime = sealTime;
        this.sealAddress = sealAddress;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
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

}
