package cn.fengwoo.sealsteward.entity;

public class RecordData {

    private String couse;
    private String sealName;
    private String sealPeople;
    private Integer sealCount;
    private Integer restCount;
    private Integer uploadPhotoNum;
    private String failTime;
    private String sealTime;
    private String sealAddress;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecordData(String id,String couse, String sealName, String sealPeople, Integer sealCount, Integer restCount, Integer uploadPhotoNum, String failTime, String sealTime, String sealAddress) {
        this.id = id;
        this.couse = couse;
        this.sealName = sealName;
        this.sealPeople = sealPeople;
        this.sealCount = sealCount;
        this.restCount = restCount;
        this.uploadPhotoNum = uploadPhotoNum;
        this.failTime = failTime;
        this.sealTime = sealTime;
        this.sealAddress = sealAddress;
    }

    public String getCouse() {
        return couse;
    }

    public void setCouse(String couse) {
        this.couse = couse;
    }
    public Integer getSealCount() {
        return sealCount;
    }

    public void setSealCount(Integer sealCount) {
        this.sealCount = sealCount;
    }

    public Integer getRestCount() {
        return restCount;
    }

    public void setRestCount(Integer restCount) {
        this.restCount = restCount;
    }

    public Integer getUploadPhotoNum() {
        return uploadPhotoNum;
    }

    public void setUploadPhotoNum(Integer uploadPhotoNum) {
        this.uploadPhotoNum = uploadPhotoNum;
    }

    public String getFailTime() {
        return failTime;
    }

    public void setFailTime(String failTime) {
        this.failTime = failTime;
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
