package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class StampRecordDatax implements Serializable {


    /**
     * curPage : 0
     * hasPage : true
     * pageSize : 0
     * param : {"sealId":"string","userId":"string","userNumber":0,"userType":0}
     */

    private int curPage;
    private boolean hasPage;
    private int pageSize;
    private ParamBean param;

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public boolean isHasPage() {
        return hasPage;
    }

    public void setHasPage(boolean hasPage) {
        this.hasPage = hasPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public ParamBean getParam() {
        return param;
    }

    public void setParam(ParamBean param) {
        this.param = param;
    }

    public static class ParamBean {
        /**
         * sealId : string
         * userId : string
         * userNumber : 0
         * userType : 0
         */

        private String sealId;
        private String userId;
        private int userNumber;
        private int userType;

        public String getSealId() {
            return sealId;
        }

        public void setSealId(String sealId) {
            this.sealId = sealId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getUserNumber() {
            return userNumber;
        }

        public void setUserNumber(int userNumber) {
            this.userNumber = userNumber;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }
    }
}
