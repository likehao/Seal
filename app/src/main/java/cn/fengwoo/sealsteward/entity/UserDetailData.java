package cn.fengwoo.sealsteward.entity;

import java.util.List;

public class UserDetailData {

    /**
     * id : f2556b5f4d6e4723b277b3b6ac814a6b
     * realName : 15172668330
     * mobilePhone : 15172668330
     * userEmail : null
     * headPortrait : null
     * autoGraph : c09573b69b574725b5dba2e7a25b46a9.jpg
     * address : null
     * authStatus : false
     * companyId : 1339157b3df94caab0e496fe46d5d49d
     * companyName : 15767997730的公司
     * orgStructureId : 9300ffbe0cb04bfb9a821e797ebb0239
     * orgStructureName : 公章管理处
     * job :
     * needSync : null
     * token : 061fa786419b4be0892abef7d028cfa6
     * code : null
     * systemFuncList : [{"id":"266b076d34e511e998a22cfda1ba8f68","code":1001,"name":"添加印章"},{"id":"7680b087421611e9af5a2cfda1ba8f68","code":1015,"name":"编辑印章信息"},{"id":"267381ce34e511e998a22cfda1ba8f68","code":1003,"name":"删除印章"},{"id":"267692e734e511e998a22cfda1ba8f68","code":1004,"name":"设置印章长按时间"},{"id":"f25f49df421511e9af5a2cfda1ba8f68","code":1013,"name":"设置盖章延时时间"},{"id":"2679db9934e511e998a22cfda1ba8f68","code":1005,"name":"设置印章启动密码"},{"id":"267ceff634e511e998a22cfda1ba8f68","code":1006,"name":"设置印章按键密码"},{"id":"f26461e3421511e9af5a2cfda1ba8f68","code":1014,"name":"设置印章语音开关"},{"id":"2681798f34e511e998a22cfda1ba8f68","code":1007,"name":"重置印章"},{"id":"268490a434e511e998a22cfda1ba8f68","code":1008,"name":"添加脱机用户"},{"id":"39ae3e97421511e9af5a2cfda1ba8f68","code":1011,"name":"设置脱机用户权限"},{"id":"39b4d216421511e9af5a2cfda1ba8f68","code":1012,"name":"删除脱机用户"},{"id":"2687d67534e511e998a22cfda1ba8f68","code":1009,"name":"设置电子围栏"},{"id":"268b020f34e511e998a22cfda1ba8f68","code":1010,"name":"设置审批流"},{"id":"268e645734e511e998a22cfda1ba8f68","code":2001,"name":"添加用户"},{"id":"26916fff34e511e998a22cfda1ba8f68","code":2002,"name":"删除用户"},{"id":"78bdfb99421811e9af5a2cfda1ba8f68","code":1017,"name":"添加用户权限"},{"id":"2697435e34e511e998a22cfda1ba8f68","code":2003,"name":"设置用户权限"},{"id":"269a5ee034e511e998a22cfda1ba8f68","code":2004,"name":"查询公司人员盖章记录"},{"id":"f3371243418811e9af5a2cfda1ba8f68","code":3001,"name":"添加组织架构"},{"id":"b9f8b134421611e9af5a2cfda1ba8f68","code":1016,"name":"编辑组织架构"},{"id":"f33d24bc418811e9af5a2cfda1ba8f68","code":3002,"name":"删除组织架构"}]
     * funcIdList : ["269a5ee034e511e998a22cfda1ba8f68","78bdfb99421811e9af5a2cfda1ba8f68","26916fff34e511e998a22cfda1ba8f68","f3371243418811e9af5a2cfda1ba8f68","268e645734e511e998a22cfda1ba8f68","2687d67534e511e998a22cfda1ba8f68","2697435e34e511e998a22cfda1ba8f68","b9f8b134421611e9af5a2cfda1ba8f68"]
     * admin : false
     */

    private String id;
    private String realName;
    private String mobilePhone;
    private Object userEmail;
    private Object headPortrait;
    private String autoGraph;
    private Object address;
    private boolean authStatus;
    private String companyId;
    private String companyName;
    private String orgStructureId;
    private String orgStructureName;
    private String job;
    private Object needSync;
    private String token;
    private Object code;
    private boolean admin;
    private List<SystemFuncListBean> systemFuncList;
    private List<String> funcIdList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Object getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(Object userEmail) {
        this.userEmail = userEmail;
    }

    public Object getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(Object headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getAutoGraph() {
        return autoGraph;
    }

    public void setAutoGraph(String autoGraph) {
        this.autoGraph = autoGraph;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public boolean isAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(boolean authStatus) {
        this.authStatus = authStatus;
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

    public String getOrgStructureId() {
        return orgStructureId;
    }

    public void setOrgStructureId(String orgStructureId) {
        this.orgStructureId = orgStructureId;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Object getNeedSync() {
        return needSync;
    }

    public void setNeedSync(Object needSync) {
        this.needSync = needSync;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<SystemFuncListBean> getSystemFuncList() {
        return systemFuncList;
    }

    public void setSystemFuncList(List<SystemFuncListBean> systemFuncList) {
        this.systemFuncList = systemFuncList;
    }

    public List<String> getFuncIdList() {
        return funcIdList;
    }

    public void setFuncIdList(List<String> funcIdList) {
        this.funcIdList = funcIdList;
    }

    public static class SystemFuncListBean {
        /**
         * id : 266b076d34e511e998a22cfda1ba8f68
         * code : 1001
         * name : 添加印章
         */

        private String id;
        private int code;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}