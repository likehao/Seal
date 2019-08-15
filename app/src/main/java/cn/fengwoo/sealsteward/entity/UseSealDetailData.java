package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class UseSealDetailData implements Serializable {
    private String id;
    private String orgStructureName;
    private Integer stampCount;

    public UseSealDetailData(){

    }
    public UseSealDetailData(String id ,String orgStructureName, Integer stampCount) {
        this.id = id;
        this.orgStructureName = orgStructureName;
        this.stampCount = stampCount;
    }

    public String getOrgStructureName() {
        return orgStructureName;
    }

    public void setOrgStructureName(String orgStructureName) {
        this.orgStructureName = orgStructureName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStampCount() {
        return stampCount;
    }

    public void setStampCount(Integer stampCount) {
        this.stampCount = stampCount;
    }

    @Override
    public String toString() {
        return "UseSealDetailData{" +
                "id='" + id + '\'' +
                ", orgStructureName='" + orgStructureName + '\'' +
                ", stampCount=" + stampCount +
                '}';
    }
}
