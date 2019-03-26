package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class ApplyCauseData implements Serializable {

    private String cause;
    private long count;

    public ApplyCauseData(String cause, long count) {
        this.cause = cause;
        this.count = count;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}
