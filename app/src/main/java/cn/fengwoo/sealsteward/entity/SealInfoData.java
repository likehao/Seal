package cn.fengwoo.sealsteward.entity;

import java.util.List;

public class SealInfoData {
    /**
     * id : 4c1f302ba6234a6bb86c2efcf2cd66d9
     * mac : 00:15:84:00:01:67
     * name : dhhj
     * sealNo : 1233
     * scope : cghh
     * sealPrint : null
     * serviceTime : 1555579046000
     * serviceType : 1
     * dataProtocolVersion : 2
     * orgStructrueId : 3030397b3e084fee9767654e4f477ccb
     * crossDepartmentApply : true
     * enableEnclosure : false
     * sealEnclosure : null
     * sealApproveFlowList : [{"id":"d38dfc3623b14dc7b0206cd619261bb7","sealId":"4c1f302ba6234a6bb86c2efcf2cd66d9","approveUser":"78c396f1aed245ec9dae37f5fd9648df","approveUserName":"13480635240","approveUserPhone":"13480635240","orgStructureName":"公章管理处","approveType":0,"approveLevel":3}]
     */

    private String id;
    private String mac;
    private String name;
    private String sealNo;
    private String scope;
    private String sealPrint;
    private long serviceTime;
    private int serviceType;
    private String dataProtocolVersion;
    private String orgStructrueId;
    private boolean crossDepartmentApply;
    private boolean enableEnclosure;
    private Object sealEnclosure;
    private List<SealApproveFlowListBean> sealApproveFlowList;

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

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getDataProtocolVersion() {
        return dataProtocolVersion;
    }

    public void setDataProtocolVersion(String dataProtocolVersion) {
        this.dataProtocolVersion = dataProtocolVersion;
    }

    public String getOrgStructrueId() {
        return orgStructrueId;
    }

    public void setOrgStructrueId(String orgStructrueId) {
        this.orgStructrueId = orgStructrueId;
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

    public Object getSealEnclosure() {
        return sealEnclosure;
    }

    public void setSealEnclosure(Object sealEnclosure) {
        this.sealEnclosure = sealEnclosure;
    }

    public List<SealApproveFlowListBean> getSealApproveFlowList() {
        return sealApproveFlowList;
    }

    public void setSealApproveFlowList(List<SealApproveFlowListBean> sealApproveFlowList) {
        this.sealApproveFlowList = sealApproveFlowList;
    }

    public static class SealApproveFlowListBean {
        /**
         * id : d38dfc3623b14dc7b0206cd619261bb7
         * sealId : 4c1f302ba6234a6bb86c2efcf2cd66d9
         * approveUser : 78c396f1aed245ec9dae37f5fd9648df
         * approveUserName : 13480635240
         * approveUserPhone : 13480635240
         * orgStructureName : 公章管理处
         * approveType : 0
         * approveLevel : 3
         */

        private String id;
        private String sealId;
        private String approveUser;
        private String approveUserName;
        private String approveUserPhone;
        private String orgStructureName;
        private int approveType;
        private int approveLevel;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSealId() {
            return sealId;
        }

        public void setSealId(String sealId) {
            this.sealId = sealId;
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

        public String getApproveUserPhone() {
            return approveUserPhone;
        }

        public void setApproveUserPhone(String approveUserPhone) {
            this.approveUserPhone = approveUserPhone;
        }

        public String getOrgStructureName() {
            return orgStructureName;
        }

        public void setOrgStructureName(String orgStructureName) {
            this.orgStructureName = orgStructureName;
        }

        public int getApproveType() {
            return approveType;
        }

        public void setApproveType(int approveType) {
            this.approveType = approveType;
        }

        public int getApproveLevel() {
            return approveLevel;
        }

        public void setApproveLevel(int approveLevel) {
            this.approveLevel = approveLevel;
        }
    }
}
