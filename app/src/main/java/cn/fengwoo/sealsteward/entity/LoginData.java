package cn.fengwoo.sealsteward.entity;

import android.content.Context;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class LoginData implements Serializable {

    /**
     * 登录返回数据
     *
     * {
     "code": 0,
     "data": {
     "address": "string",
     "admin": true,
     "authStatus": true,
     "autoGraph": "string",
     "code": "string",
     "companyId": "string",
     "companyName": "string",
     "funcIdList": [
     "string"
     ],
     "headPortrait": "string",
     "id": "string",
     "job": "string",
     "mobilePhone": "string",
     "needSync": true,
     "orgStructureName": "string",
     "realName": "string",
     "systemFuncList": [
     {
     "code": 0,
     "id": "string",
     "name": "string"
     }
     ],
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
    private String code;
    private String companyId;
    private String companyName;
    private List<SystemFuncListInfo> funcIdList;
    private String headPortrait;
    private String id;
    private String job;
    private String mobilePhone;
    private Boolean needSync;
    private String orgStructureName;
    private String realName;
    private List<SystemFuncListInfo> systemFuncList;
    private String token;
    private String userEmail;

    public LoginData(){

    }
    public List<SystemFuncListInfo> getFuncIdList() {
        return funcIdList;
    }

    public void setFuncIdList(List<SystemFuncListInfo> funcIdList) {
        this.funcIdList = funcIdList;
    }

    public List<SystemFuncListInfo> getSystemFuncList() {
        return systemFuncList;
    }

    public void setSystemFuncList(List<SystemFuncListInfo> systemFuncList) {
        this.systemFuncList = systemFuncList;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        return "LoginData{" +
                "address='" + address + '\'' +
                ", admin=" + admin +
                ", authStatus=" + authStatus +
                ", autoGraph='" + autoGraph + '\'' +
                ", code='" + code + '\'' +
                ", companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", funcIdList=" + funcIdList +
                ", headPortrait='" + headPortrait + '\'' +
                ", id='" + id + '\'' +
                ", job='" + job + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", needSync=" + needSync +
                ", orgStructureName='" + orgStructureName + '\'' +
                ", realName='" + realName + '\'' +
                ", systemFuncList=" + systemFuncList +
                ", token='" + token + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }

    class FuncId{

        private String funcIdList;

        public String getFuncIdList() {
            return funcIdList;
        }

        public void setFuncIdList(String funcIdList) {
            this.funcIdList = funcIdList;
        }
    }

    //登录
    public void login(Context context){
        context.getSharedPreferences("userdata",Context.MODE_PRIVATE).edit()
                .putString("login",new Gson().toJson(this)).apply();
    }

    public static boolean isLogin(Context context){
        return context.getSharedPreferences("userdata",Context.MODE_PRIVATE).contains("login");
    }

    //退出
    public static void logout(Context context){
        context.getSharedPreferences("userdata",Context.MODE_PRIVATE).edit()
                .remove("login").apply();
    }

}
