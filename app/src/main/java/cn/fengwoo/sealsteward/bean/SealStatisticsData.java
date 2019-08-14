package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class SealStatisticsData implements Serializable {
    private String endTime;
    private Integer month;
    private String orgStructureId;
    private String sealId;
    private Integer searchType;
    private String startTime;
    private String userId;
    private Integer year;

    private String id;
    private String name;
    private String sealPrint;
    private String stampCount;

    public SealStatisticsData(String id ,String name, String sealPrint, String stampCount) {
        this.id = id;
        this.name = name;
        this.sealPrint = sealPrint;
        this.stampCount = stampCount;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getOrgStructureId() {
        return orgStructureId;
    }

    public void setOrgStructureId(String orgStructureId) {
        this.orgStructureId = orgStructureId;
    }

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSealPrint() {
        return sealPrint;
    }

    public void setSealPrint(String sealPrint) {
        this.sealPrint = sealPrint;
    }

    public String getStampCount() {
        return stampCount;
    }

    public void setStampCount(String stampCount) {
        this.stampCount = stampCount;
    }


    @Override
    public String toString() {
        return "SealStatisticsDatai{" +
                "endTime='" + endTime + '\'' +
                ", month=" + month +
                ", orgStructureId='" + orgStructureId + '\'' +
                ", sealId='" + sealId + '\'' +
                ", searchType=" + searchType +
                ", startTime='" + startTime + '\'' +
                ", userId='" + userId + '\'' +
                ", year=" + year +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sealPrint='" + sealPrint + '\'' +
                ", stampCount='" + stampCount + '\'' +
                '}';
    }
}
