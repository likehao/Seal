package cn.fengwoo.sealsteward.entity;

public class AddSealData {
    /**
     * dataProtocolVersion : string
     * name : string
     * mac : string
     * sealNo : string
     * scope : string
     * orgStructrueId : string
     */

    private String dataProtocolVersion;
    private String name;
    private String mac;
    private String sealNo;
    private String scope;
    private String orgStructrueId;
    private String sealPrint;

    private Boolean enableApprove;

    public Boolean getEnableApprove() {
        return enableApprove;
    }

    public void setEnableApprove(Boolean enableApprove) {
        this.enableApprove = enableApprove;
    }

    public String getSealPrint() {
        return sealPrint;
    }

    public void setSealPrint(String sealPrint) {
        this.sealPrint = sealPrint;
    }

    public String getDataProtocolVersion() {
        return dataProtocolVersion;
    }

    public void setDataProtocolVersion(String dataProtocolVersion) {
        this.dataProtocolVersion = dataProtocolVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSealNo() {
        return sealNo;
    }

    public void setSealNo(String sealNo) {
        this.sealNo = sealNo;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getOrgStructrueId() {
        return orgStructrueId;
    }

    public void setOrgStructrueId(String orgStructrueId) {
        this.orgStructrueId = orgStructrueId;
    }
}
