package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class UserStatisticsData implements Serializable {

    private String endTime;
    private Integer month;
    private String orgStructureId;
    private String sealId;
    private Integer searchType;
    private String startTime;
    private String userId;
    private Integer year;

    private String headPortrait;
    private String id;
    private String realName;
    private String stampCount;

    @Override
    public String toString() {
        return "UserStatisticsData{" +
                "endTime='" + endTime + '\'' +
                ", orgStructureId='" + orgStructureId + '\'' +
                ", sealId='" + sealId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", userId='" + userId + '\'' +
                ", month=" + month +
                ", searchType=" + searchType +
                ", year=" + year +
                ", id='" + id + '\'' +
                ", stampCount='" + stampCount + '\'' +
                ", headPortrait='" + headPortrait + '\'' +
                ", realName='" + realName + '\'' +
                '}';
    }

    public UserStatisticsData(){

    }
    public UserStatisticsData(String id,String realName, String headPortrait, String stampCount) {
        this.id = id;
        this.realName = realName;
        this.headPortrait = headPortrait;
        this.stampCount = stampCount;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
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

    public String getStampCount() {
        return stampCount;
    }

    public void setStampCount(String stampCount) {
        this.stampCount = stampCount;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

}
