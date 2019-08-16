package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;
import java.util.List;

public class UseSealDetailData implements Serializable {
    private String id;
    private String orgStructureName;
    private Integer stampCount;
    private Integer sealTotalCount;
    private Integer totalStampCount;
    private Integer userTotalCount;
    private List<orgStructureStatisticVoList> orgStructureStatisticVoList;

    public List<UseSealDetailData.orgStructureStatisticVoList> getOrgStructureStatisticVoList() {
        return orgStructureStatisticVoList;
    }

    public void setOrgStructureStatisticVoList(List<UseSealDetailData.orgStructureStatisticVoList> orgStructureStatisticVoList) {
        this.orgStructureStatisticVoList = orgStructureStatisticVoList;
    }

    @Override
    public String toString() {
        return "UseSealDetailData{" +
                "id='" + id + '\'' +
                ", orgStructureName='" + orgStructureName + '\'' +
                ", stampCount=" + stampCount +
                ", sealTotalCount=" + sealTotalCount +
                ", totalStampCount=" + totalStampCount +
                ", userTotalCount=" + userTotalCount +
                ", orgStructureStatisticVoList=" + orgStructureStatisticVoList +
                '}';
    }


    public Integer getSealTotalCount() {
        return sealTotalCount;
    }

    public void setSealTotalCount(Integer sealTotalCount) {
        this.sealTotalCount = sealTotalCount;
    }

    public Integer getTotalStampCount() {
        return totalStampCount;
    }

    public void setTotalStampCount(Integer totalStampCount) {
        this.totalStampCount = totalStampCount;
    }

    public Integer getUserTotalCount() {
        return userTotalCount;
    }

    public void setUserTotalCount(Integer userTotalCount) {
        this.userTotalCount = userTotalCount;
    }

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


    public static class orgStructureStatisticVoList{
        private String id;
        private String orgStructureName;
        private Integer stampCount;

        @Override
        public String toString() {
            return "orgStructureStatisticVoList{" +
                    "id='" + id + '\'' +
                    ", orgStructureName='" + orgStructureName + '\'' +
                    ", stampCount=" + stampCount +
                    '}';
        }

        public orgStructureStatisticVoList(String id, String orgStructureName, Integer stampCount) {
            this.id = id;
            this.orgStructureName = orgStructureName;
            this.stampCount = stampCount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrgStructureName() {
            return orgStructureName;
        }

        public void setOrgStructureName(String orgStructureName) {
            this.orgStructureName = orgStructureName;
        }

        public Integer getStampCount() {
            return stampCount;
        }

        public void setStampCount(Integer stampCount) {
            this.stampCount = stampCount;
        }


    }
}
