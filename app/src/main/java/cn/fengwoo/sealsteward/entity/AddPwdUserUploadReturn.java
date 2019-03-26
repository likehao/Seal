package cn.fengwoo.sealsteward.entity;

public class AddPwdUserUploadReturn {
    /**
     * id : 11d81bfaddea4acc9c3eb7d3e20d09a6
     * userId : 36d206e430ea49aabc79fea2c076f9b6
     * userName : null
     * sealId : 48e7c3e171ed4bb490f82a0d5ef349ec
     * sealName : null
     * orgStructureName : null
     * userNumber : null
     * password : 170258
     * userType : 1
     * stampCount : 1122
     * expireTime : 1553689320000
     */

    private String id;
    private String userId;
    private Object userName;
    private String sealId;
    private Object sealName;
    private Object orgStructureName;
    private Object userNumber;
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

    public Object getUserName() {
        return userName;
    }

    public void setUserName(Object userName) {
        this.userName = userName;
    }

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }

    public Object getSealName() {
        return sealName;
    }

    public void setSealName(Object sealName) {
        this.sealName = sealName;
    }

    public Object getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(Object orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    public Object getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Object userNumber) {
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
