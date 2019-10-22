package cn.fengwoo.sealsteward.entity;

public class SealInfoUpdateData {
    /**
     * dataProtocolVersion : 2
     * id : 00:15:84:00:01:67
     * mac : dhhj
     * name : 1233
     * orgStructrueId : cghh
     * scope : asdf
     * sealNo : 123
     * sealPrint : 123
     * serviceTime : 2
     * crossDepartmentApply : true
     * enableEnclosure : true
     */

    private String dataProtocolVersion;
    private String id;
    private String mac;
    private String name;
    private String orgStructrueId;
    private String scope;
    private String sealNo;
    private String sealPrint;
    private long serviceTime;
    private boolean crossDepartmentApply;
    private boolean enableEnclosure;
    private boolean enableApprove;

    public boolean isEnableApprove() {
        return enableApprove;
    }

    public void setEnableApprove(boolean enableApprove) {
        this.enableApprove = enableApprove;
    }

    public String getDataProtocolVersion() {
        return dataProtocolVersion;
    }

    public void setDataProtocolVersion(String dataProtocolVersion) {
        this.dataProtocolVersion = dataProtocolVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgStructrueId() {
        return orgStructrueId;
    }

    public void setOrgStructrueId(String orgStructrueId) {
        this.orgStructrueId = orgStructrueId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSealNo() {
        return sealNo;
    }

    public void setSealNo(String sealNo) {
        this.sealNo = sealNo;
    }

    public String getSealPrint() {
        return sealPrint;
    }

    public void setSealPrint(String sealPrint) {
        this.sealPrint = sealPrint;
    }

    public long getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(long serviceTime) {
        this.serviceTime = serviceTime;
    }

    public boolean isCrossDepartmentApply() {
        return crossDepartmentApply;
    }

    public void setCrossDepartmentApply(boolean crossDepartmentApply) {
        this.crossDepartmentApply = crossDepartmentApply;
    }

    public boolean isEnableEnclosure() {
        return enableEnclosure;
    }

    public void setEnableEnclosure(boolean enableEnclosure) {
        this.enableEnclosure = enableEnclosure;
    }
}
