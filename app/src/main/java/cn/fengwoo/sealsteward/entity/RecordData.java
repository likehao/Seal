package cn.fengwoo.sealsteward.entity;

public class RecordData {

    private String sealName;
    private String sealPeople;
    private String sealTime;
    private String sealAddress;

    public RecordData(String sealName,String sealPeople,String sealTime,String sealAddress){
        this.sealName = sealName;
        this.sealPeople = sealPeople;
        this.sealTime = sealTime;
        this.sealAddress = sealAddress;
    }
    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getSealPeople() {
        return sealPeople;
    }

    public void setSealPeople(String sealPeople) {
        this.sealPeople = sealPeople;
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
