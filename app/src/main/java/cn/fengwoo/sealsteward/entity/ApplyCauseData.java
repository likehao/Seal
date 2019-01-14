package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class ApplyCauseData implements Serializable {

    private String cause;
    private Integer count;

    public ApplyCauseData(String cause, Integer count) {
        this.cause = cause;
        this.count = count;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
