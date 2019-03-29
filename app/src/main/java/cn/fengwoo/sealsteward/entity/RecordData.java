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
    private Integer approveStatus;
    private String applyPdf;
    private String stampPdf;
    private String stampRecordPdf;
    private String headPortrait;
    private String orgStructureName;

    public RecordData(String id, String couse, String sealName, String sealPeople,
                      Integer sealCount, Integer restCount, Integer uploadPhotoNum,
                      String failTime, String sealTime, String sealAddress, Integer approveStatus
    , String applyPdf , String stampPdf , String stampRecordPdf, String headPortrait, String orgStructureName) {
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
        this.approveStatus = approveStatus;
        this.applyPdf = applyPdf;
        this.stampPdf = stampPdf;
        this.stampRecordPdf = stampRecordPdf;
        this.headPortrait = headPortrait;
        this.orgStructureName = orgStructureName;

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
