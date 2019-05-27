package cn.fengwoo.sealsteward.entity;

import java.util.List;

public class RecordData {

    private String cause;
    private String sealName;
    private String sealPeople;
    private Integer sealCount;
    private Integer restCount;
    private Integer uploadPhotoNum;
    private String failTime;
    private String sealTime;
    private String sealAddress;
    private String id;
    private Integer approveStatus;
    private String applyPdf;
    private String stampPdf;
    private String stampRecordPdf;
    private String headPortrait;
    private String orgStructureName;
    private Integer applyCount;


    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    public List<String> getStampRecordImgList() {
        return stampRecordImgList;
    }

    public void setStampRecordImgList(List<String> stampRecordImgList) {
        this.stampRecordImgList = stampRecordImgList;
    }

    private List<String> stampRecordImgList;

    public RecordData(Integer applyCount,String id, String cause, String sealName, String sealPeople,
                      Integer sealCount, Integer restCount, Integer uploadPhotoNum,
                      String failTime, String sealTime, String sealAddress, Integer approveStatus
    , String applyPdf , String stampPdf , String stampRecordPdf, String headPortrait, String orgStructureName ,List<String> stampRecordImgList) {
        this.applyCount = applyCount;
        this.id = id;
        this.cause = cause;
        this.sealName = sealName;
        this.sealPeople = sealPeople;
        this.sealCount = sealCount;
        this.restCount = restCount;
        this.uploadPhotoNum = uploadPhotoNum;
        this.failTime = failTime;
        this.sealTime = sealTime;
        this.sealAddress = sealAddress;
        this.approveStatus = approveStatus;
        this.applyPdf = applyPdf;
        this.stampPdf = stampPdf;
        this.stampRecordPdf = stampRecordPdf;
        this.headPortrait = headPortrait;
        this.orgStructureName = orgStructureName;
        this.stampRecordImgList =stampRecordImgList;

    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getApplyPdf() {
        return applyPdf;
    }

    public void setApplyPdf(String applyPdf) {
        this.applyPdf = applyPdf;
    }

    public String getStampPdf() {
        return stampPdf;
    }

    public void setStampPdf(String stampPdf) {
        this.stampPdf = stampPdf;
    }

    public String getStampRecordPdf() {
        return stampRecordPdf;
    }

    public void setStampRecordPdf(String stampRecordPdf) {
        this.stampRecordPdf = stampRecordPdf;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

}
