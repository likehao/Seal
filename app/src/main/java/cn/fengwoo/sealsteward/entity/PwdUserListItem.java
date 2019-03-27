package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class PwdUserListItem implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * id : 637210766cd54af5ae63f487fec48161
     * userId : 36d206e430ea49aabc79fea2c076f9b6
     * userName : 易额额
     * sealId : 48e7c3e171ed4bb490f82a0d5ef349ec
     * sealName : 1223
     * orgStructureName : 公章管理处
     * userNumber : 83
     * password : 826984
     * userType : 1
     * stampCount : 55
     * expireTime : 1553748132000
     */

    private String id;
    private String userId;
    private String userName;
    private String sealId;
    private String sealName;
    private String orgStructureName;
    private int userNumber;
    private String password;
    private int userType;
    private int stampCount;
    private long expireTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getStampCount() {
        return stampCount;
    }

    public void setStampCount(int stampCount) {
        this.stampCount = stampCount;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
