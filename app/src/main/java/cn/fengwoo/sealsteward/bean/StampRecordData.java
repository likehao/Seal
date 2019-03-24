package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class StampRecordData implements Serializable {
    /**
     * 印章记录
     * {
     "curPage": 0,      当前页
     "hasPage": true,   是否分页
     "pageSize": 0,     条数
     "param": 0
     }
     */
    private Integer curPage;
    private Boolean hasExportPdf;
    private Boolean hasPage;
    private Integer pageSize;
    private Parem param;

    @Override
    public String toString() {
        return "StampRecordData{" +
                "curPage=" + curPage +
                "hasExportPdf=" + hasExportPdf +
                ", hasPage=" + hasPage +
                ", pageSize=" + pageSize +
                ", param=" + param +
                '}';
    }

    public Integer getCurPage() {
        return curPage;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public Boolean getHasExportPdf() {
        return hasExportPdf;
    }

    public void setHasExportPdf(Boolean hasExportPdf) {
        this.hasExportPdf = hasExportPdf;
    }
    public Boolean getHasPage() {
        return hasPage;
    }

    public void setHasPage(Boolean hasPage) {
        this.hasPage = hasPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Parem getParam() {
        return param;
    }

    public void setParam(Parem param) {
        this.param = param;
    }

    public static class Parem{

        /**
         * "param": {
         "applyUser": "string",
         "endTime": "2019-03-23T02:13:22.762Z",
         "sealId": "string",
         "startTime": "2019-03-23T02:13:22.762Z"
         }
         */
        private String applyUser;
        private String endTime;
        private String sealId;
        private String startTime;

        public void Parem(String applyUser, String endTime, String sealId, String startTime) {
            this.applyUser = applyUser;
            this.endTime = endTime;
            this.sealId = sealId;
            this.startTime = startTime;
        }

        public String getApplyUser() {
            return applyUser;
        }

        public void setApplyUser(String applyUser) {
            this.applyUser = applyUser;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getSealId() {
            return sealId;
        }

        public void setSealId(String sealId) {
            this.sealId = sealId;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        @Override
        public String toString() {
            return "Parem{" +
                    "applyUser='" + applyUser + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", sealId='" + sealId + '\'' +
                    ", startTime='" + startTime + '\'' +
                    '}';
        }

    }

}
