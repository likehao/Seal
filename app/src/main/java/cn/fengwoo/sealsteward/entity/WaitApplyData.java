package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 待审批adapter
 */
public class WaitApplyData implements Serializable {

    private String cause;
    private String sealName;
    private String failTime;
    private Integer applyCount;
    private String applyTime;
    private String id;
    private Integer approveStatus;
    private String applyUserName;
    private String orgStructureName;
    private String headPortrait;
    private Integer sealCount;
    private Integer restCount;
    private Integer uploadPhotoNum;

    private String applyPdf;
    private String stampPdf;
    private String stampRecordPdf;
    private List<String> stampRecordImgList;

    private String autoGraph;

    public List<String> getStampRecordImgList() {
        return stampRecordImgList;
    }

    public void setStampRecordImgList(List<String> stampRecordImgList) {
        this.stampRecordImgList = stampRecordImgList;
    }


    public WaitApplyData(String cause, String sealName, String failTime, Integer applyCount, String applyTime, String id, Integer approveStatus, String applyUserName, String orgStructureName
   , String headPortrait, Integer sealCount , Integer restCount , Integer uploadPhotoNum, String applyPdf,String stampPdf,String stampRecordPdf,List<String> stampRecordImgList,String autoGraph) {
        this.cause = cause;
        this.sealName = sealName;
        this.failTime = failTime;
        this.applyCount = applyCount;
        this.applyTime = applyTime;
        this.id = id;
        this.approveStatus = approveStatus;
        this.applyUserName = applyUserName;
        this.orgStructureName = orgStructureName;
        this.headPortrait = headPortrait;
        this.sealCount = sealCount;
        this.restCount = restCount;
        this.uploadPhotoNum = uploadPhotoNum;
        this.applyPdf = applyPdf;
        this.stampPdf = stampPdf;
        this.stampRecordPdf = stampRecordPdf;
        this.stampRecordImgList = stampRecordImgList;
        this.autoGraph = autoGraph;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getFailTime() {
        return failTime;
    }

    public void setFailTime(String failTime) {
        this.failTime = failTime;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
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

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
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

    public String getAutoGraph() {
        return autoGraph;
    }

    public void setAutoGraph(String autoGraph) {
        this.autoGraph = autoGraph;
    }


}
