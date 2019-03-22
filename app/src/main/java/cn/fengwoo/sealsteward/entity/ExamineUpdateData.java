package cn.fengwoo.sealsteward.entity;

import java.util.List;

public class ExamineUpdateData {
    /**
     * sealId : 4c1f302ba6234a6bb86c2efcf2cd66d9
     * list : [{"approveLevel":"43cdeef2b274455ea0e03c78259a575b","approveType":"4c1f302ba6234a6bb86c2efcf2cd66d9","approveUser":"78c396f1aed245ec9dae37f5fd9648df"}]
     */

    private String sealId;
    private List<ListBean> list;

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * approveLevel : 43cdeef2b274455ea0e03c78259a575b
         * approveType : 4c1f302ba6234a6bb86c2efcf2cd66d9
         * approveUser : 78c396f1aed245ec9dae37f5fd9648df
         */

        private String approveLevel;
        private String approveType;
        private String approveUser;

        public String getApproveLevel() {
            return approveLevel;
        }

        public void setApproveLevel(String approveLevel) {
            this.approveLevel = approveLevel;
        }

        public String getApproveType() {
            return approveType;
        }

        public void setApproveType(String approveType) {
            this.approveType = approveType;
        }

        public String getApproveUser() {
            return approveUser;
        }

        public void setApproveUser(String approveUser) {
            this.approveUser = approveUser;
        }
    }
}
