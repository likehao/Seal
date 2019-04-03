package cn.fengwoo.sealsteward.bean;

import java.util.List;

public class UploadHistoryRecord {
    /**
     * recordList : [{"stampSeqNumber":0,"stampTime":"2019-04-02T08:43:24.264Z","stampType":0,"userNumber":0}]
     * sealId : string
     */

    private String sealId;
    private List<RecordListBean> recordList;

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }

    public List<RecordListBean> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<RecordListBean> recordList) {
        this.recordList = recordList;
    }

    public static class RecordListBean {
        /**
         * stampSeqNumber : 0
         * stampTime : 2019-04-02T08:43:24.264Z
         * stampType : 0
         * userNumber : 0
         */

        private int stampSeqNumber;
        private String stampTime;
        private int stampType;
        private int userNumber;

        public int getStampSeqNumber() {
            return stampSeqNumber;
        }

        public void setStampSeqNumber(int stampSeqNumber) {
            this.stampSeqNumber = stampSeqNumber;
        }

        public String getStampTime() {
            return stampTime;
        }

        public void setStampTime(String stampTime) {
            this.stampTime = stampTime;
        }

        public int getStampType() {
            return stampType;
        }

        public void setStampType(int stampType) {
            this.stampType = stampType;
        }

        public int getUserNumber() {
            return userNumber;
        }

        public void setUserNumber(int userNumber) {
            this.userNumber = userNumber;
        }
    }
}
