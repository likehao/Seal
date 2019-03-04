package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class UserInfoData implements Serializable {

    /**
     * 用户信息数据
     * {
     "code": 0,
     "data": {
     "address": "string",
     "admin": true,
     "authStatus": true,
     "autoGraph": "string",
     "companyId": "string",
     "companyName": "string",
     "headPortrait": "string",
     "id": "string",
     "job": "string",
     "mobilePhone": "string",
     "needSync": true,
     "orgStructureName": "string",
     "realName": "string",
     "token": "string",
     "userEmail": "string"
     },
     "message": "string"
     }
     */
    private String address;
    private Boolean admin;
    private Boolean authStatus;
    private String autoGraph;
    private String companyId;
    private String companyName;
    private String headPortrait;
    private String id;
    private String job;
    private String mobilePhone;
    private Boolean needSync;
    private String orgStructureName;
    private String realName;
    private String token;
    private String userEmail;
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Boolean authStatus) {
        this.authStatus = authStatus;
    }

    public String getAutoGraph() {
        return autoGraph;
    }

    public void setAutoGraph(String autoGraph) {
        this.autoGraph = autoGraph;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Boolean getNeedSync() {
        return needSync;
    }

    public void setNeedSync(Boolean needSync) {
        this.needSync = needSync;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "UserInfoData{" +
                "address='" + address + '\'' +
                ", admin=" + admin +
                ", authStatus=" + authStatus +
                ", autoGraph='" + autoGraph + '\'' +
                ", companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", headPortrait='" + headPortrait + '\'' +
                ", id='" + id + '\'' +
                ", job='" + job + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", needSync=" + needSync +
                ", orgStructureName='" + orgStructureName + '\'' +
                ", realName='" + realName + '\'' +
                ", token='" + token + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
