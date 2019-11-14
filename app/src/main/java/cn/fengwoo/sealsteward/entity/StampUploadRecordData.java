package cn.fengwoo.sealsteward.entity;

public class StampUploadRecordData {
    /**
     * address : string
     * applyId : string
     * id : string
     * latitude : 22.5555
     * longitude : 22.5555
     * sealId : string
     * stampSeqNumber : 0 // 盖章序号
     * stampTime : asdfsasfd
     * stampUser : string
     * startNo : asdf
     */

    private String address;
    private String applyId;
//    private String id;
    private double latitude;
    private double longitude;
    private String sealId;
    private int stampSeqNumber;
    private String stampTime;
    private String stampUser;
    private String startNo;
    private String startTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }

    public int getStampSeqNumber() {
        return stampSeqNumber;
    }

    public void setStampSeqNumber(int stampSeqNumber) {
        this.stampSeqNumber = stampSeqNumber;
    }

    public String getStampTime() {
        return stampTime;
    }

    public void setStampTime(String stampTime) {
        this.stampTime = stampTime;
    }

    public String getStampUser() {
        return stampUser;
    }

    public void setStampUser(String stampUser) {
        this.stampUser = stampUser;
    }

    public String getStartNo() {
        return startNo;
    }

    public void setStartNo(String startNo) {
        this.startNo = startNo;
    }
}
