package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class PplAddEntity implements Serializable {
    private static final long serialVersionUID=1L;
    /**
     * id : 2c75ac621446491d96d3f9cee2152136
     * userId : 89ce71addc644ac98ba49d0f94788a20
     * headPortrait : f575d93a351441449188bfe6719b8749.jpg
     * userName : asssaasXchdydydtxfztztxyxyxgcgxgguxxfhzzfhfzhfyz
     * mobilePhone : 13342905836
     * companyId : 1d845a892723456689a8744864b55862
     * orgStructureId : null
     * job : null
     * status : 0
     * handleUser : null
     * content : 666
     */

    private String id;
    private String userId;
    private String headPortrait;
    private String userName;
    private String mobilePhone;
    private String companyId;
    private String orgStructureId;
    private String job;
    private int status;
    private Object handleUser;
    private String content;

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

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getOrgStructureId() {
        return orgStructureId;
    }

    public void setOrgStructureId(String orgStructureId) {
        this.orgStructureId = orgStructureId;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getHandleUser() {
        return handleUser;
    }

    public void setHandleUser(Object handleUser) {
        this.handleUser = handleUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
