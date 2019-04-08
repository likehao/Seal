package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class ApplyConfirm implements Serializable{

    /**
     * 申请确认
     * {
     "applyId": "string",
     "approveOpinion": "string",
     "approveStatus": 0
     }
     */

    private String applyId;
    private String approveOpinion;
    private Integer approveStatus;

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getApproveOpinion() {
        return approveOpinion;
    }

    public void setApproveOpinion(String approveOpinion) {
        this.approveOpinion = approveOpinion;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

}
