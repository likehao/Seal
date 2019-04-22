package cn.fengwoo.sealsteward.entity;

import java.util.List;

public class ChangeOrgEntity {
    /**
     * ids : ["good","good"]
     * orgStrId : string
     * type : 0
     */

    private String orgStrId;
    private int type;
    private List<String> ids;

    public String getOrgStrId() {
        return orgStrId;
    }

    public void setOrgStrId(String orgStrId) {
        this.orgStrId = orgStrId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
