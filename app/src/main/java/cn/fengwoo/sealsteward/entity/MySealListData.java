package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class MySealListData implements Serializable {

    private String sealName;

    public MySealListData(String sealName) {
        this.sealName = sealName;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

}
