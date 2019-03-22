package cn.fengwoo.sealsteward.entity;

public class SealData {
    /**
     * id : 4c1f302ba6234a6bb86c2efcf2cd66d9
     * mac : 00:15:84:00:01:67
     * name : 一23
     * sealNo : 1233
     * scope : 455
     * sealPrint : df57e850cf52432d8f22555d1bceabe7.jpg
     * serviceTime : 1555579046000
     * serviceType : 1
     * dataProtocolVersion : 2
     * orgStructrueId : 3030397b3e084fee9767654e4f477ccb
     * crossDepartmentApply : true
     * enableEnclosure : true
     * sealEnclosure : {"id":"53d3365047264461845512af33e50f90","sealId":"4c1f302ba6234a6bb86c2efcf2cd66d9","longitude":114.11664466292447,"latitude":22.65715698531227,"scope":50,"address":"广东省深圳市龙岗区甘李六路","enableFlag":null}
     * sealApproveFlowList : null
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
    private SealEnclosureBean sealEnclosure;
    private Object sealApproveFlowList;

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

    public SealEnclosureBean getSealEnclosure() {
        return sealEnclosure;
    }

    public void setSealEnclosure(SealEnclosureBean sealEnclosure) {
        this.sealEnclosure = sealEnclosure;
    }

    public Object getSealApproveFlowList() {
        return sealApproveFlowList;
    }

    public void setSealApproveFlowList(Object sealApproveFlowList) {
        this.sealApproveFlowList = sealApproveFlowList;
    }

    public static class SealEnclosureBean {
        /**
         * id : 53d3365047264461845512af33e50f90
         * sealId : 4c1f302ba6234a6bb86c2efcf2cd66d9
         * longitude : 114.11664466292447
         * latitude : 22.65715698531227
         * scope : 50
         * address : 广东省深圳市龙岗区甘李六路
         * enableFlag : null
         */

        private String id;
        private String sealId;
        private double longitude;
        private double latitude;
        private int scope;
        private String address;
        private Object enableFlag;

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

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public int getScope() {
            return scope;
        }

        public void setScope(int scope) {
            this.scope = scope;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Object getEnableFlag() {
            return enableFlag;
        }

        public void setEnableFlag(Object enableFlag) {
            this.enableFlag = enableFlag;
        }
    }
}
