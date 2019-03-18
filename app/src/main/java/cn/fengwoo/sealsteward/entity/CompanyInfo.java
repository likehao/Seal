package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class CompanyInfo implements Serializable {

    /**
     * 用户公司列表
     *
     "data": [
     {
     "authStatus": true,
     "authTime": "2019-03-09T02:25:45.975Z",
     "authUser": "string",
     "belongUser": "string",
     "companyName": "string",
     "id": "string",
     "legalPersonName": "string",
     "socialCreditCode": "string"
     }
     ]
     */
    private Boolean authStatus;
    private String authTime;
    private String authUser;
    private String belongUser;
    private String companyName;
    private String id;
    private String legalPersonName;
    private String socialCreditCode;

    public CompanyInfo(){

    }
    public CompanyInfo(String companyName,String id) {
        this.companyName = companyName;
        this.id = id;
    }


    public Boolean getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Boolean authStatus) {
        this.authStatus = authStatus;
    }

    public String getAuthTime() {
        return authTime;
    }

    public void setAuthTime(String authTime) {
        this.authTime = authTime;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getBelongUser() {
        return belongUser;
    }

    public void setBelongUser(String belongUser) {
        this.belongUser = belongUser;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLegalPersonName() {
        return legalPersonName;
    }

    public void setLegalPersonName(String legalPersonName) {
        this.legalPersonName = legalPersonName;
    }

    public String getSocialCreditCode() {
        return socialCreditCode;
    }

    public void setSocialCreditCode(String socialCreditCode) {
        this.socialCreditCode = socialCreditCode;
    }

    @Override
    public String toString() {
        return "CompanyListInfo{" +
                "authStatus=" + authStatus +
                ", authTime='" + authTime + '\'' +
                ", authUser='" + authUser + '\'' +
                ", belongUser='" + belongUser + '\'' +
                ", companyName='" + companyName + '\'' +
                ", id='" + id + '\'' +
                ", legalPersonName='" + legalPersonName + '\'' +
                ", socialCreditCode='" + socialCreditCode + '\'' +
                '}';
    }

}
