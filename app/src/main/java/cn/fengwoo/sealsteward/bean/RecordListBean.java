package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class RecordListBean implements Serializable {

    /**
     * 盖章记录详情
     * "data": [
     * {
     * "address": "string",
     * "flowNumber": "string",
     * "id": "string",
     * "latitude": 0,
     * "longitude": 0,
     * "stampTime": "2019-03-22T12:30:35.754Z"
     * }
     * ],
     */
    private String address;
    private String flowNumber;
    private String id;
    private Double latitude;
    private Double longitude;
    private String stampTime;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFlowNumber() {
        return flowNumber;
    }

    public void setFlowNumber(String flowNumber) {
        this.flowNumber = flowNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStampTime() {
        return stampTime;
    }

    public void setStampTime(String stampTime) {
        this.stampTime = stampTime;
    }

    @Override
    public String toString() {
        return "RecordListBean{" +
                "address='" + address + '\'' +
                ", flowNumber='" + flowNumber + '\'' +
                ", id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", stampTime='" + stampTime + '\'' +
                '}';
    }
}
