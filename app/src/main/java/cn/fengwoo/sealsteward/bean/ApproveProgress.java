package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class ApproveProgress implements Serializable{

    /**
     * 审批进度
     *  "data": [
     {
     "approveLevel": 0,
     "approveOpinion": "string",
     "approveStatus": 0,
     "approveTime": "2019-04-22T01:08:39.231Z",   //批准时间
     "approveUser": "string",
     "approveUserName": "string",
     "id": "string",
     "orgStructureName": "string"
     }
     ],
     */

    private Integer approveLevel;
    private String approveOpinion;
    private Integer approveStatus;
    private String approveTime;
    private String approveUser;
    private String approveUserName;
    private String id;
    private String orgStructureName;
    private String createTime;
    private String mobilePhone;


    public ApproveProgress(Integer approveStatus, String approveUserName, String orgStructureName,String approveOpinion,String approveTime,String createTime,String mobilePhone) {
        this.approveStatus = approveStatus;
        this.approveUserName = approveUserName;
        this.orgStructureName = orgStructureName;
        this.approveOpinion = approveOpinion;
        this.approveTime = approveTime;
        this.createTime = createTime;
        this.mobilePhone = mobilePhone;
    }

    public Integer getApproveLevel() {
        return approveLevel;
    }

    public void setApproveLevel(Integer approveLevel) {
        this.approveLevel = approveLevel;
    }

    public String getApproveOpinion() {
        return approveOpinion;
    }

    public void setApproveOpinion(String approveOpinion) {
        this.approveOpinion = approveOpinion;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveUser() {
        return approveUser;
    }

    public void setApproveUser(String approveUser) {
        this.approveUser = approveUser;
    }

    public String getApproveUserName() {
        return approveUserName;
    }

    public void setApproveUserName(String approveUserName) {
        this.approveUserName = approveUserName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }


    @Override
    public String toString() {
        return "ApproveProgress{" +
                "approveLevel=" + approveLevel +
                ", approveOpinion='" + approveOpinion + '\'' +
                ", approveStatus=" + approveStatus +
                ", approveTime='" + approveTime + '\'' +
                ", approveUser='" + approveUser + '\'' +
                ", approveUserName='" + approveUserName + '\'' +
                ", id='" + id + '\'' +
                ", orgStructureName='" + orgStructureName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                '}';
    }

}
