package cn.fengwoo.sealsteward.entity;

import java.util.List;

public class SetPermissionData {
    /**
     * funcIdList : ["string","asdf"]
     * userId : string
     */

    private String userId;
    private List<String> funcIdList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getFuncIdList() {
        return funcIdList;
    }

    public void setFuncIdList(List<String> funcIdList) {
        this.funcIdList = funcIdList;
    }
}
