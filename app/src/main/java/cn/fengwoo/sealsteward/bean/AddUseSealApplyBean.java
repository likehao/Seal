package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;
import java.util.List;

public class AddUseSealApplyBean implements Serializable {

    /**
     * 添加用印申请
     * {
     "applyCause": "string",
     "applyCount": 0,
     "applyUser": "string",
     "expireTime": "2019-03-16T09:11:22.133Z",
     "id": "string",
     "imgList": [
     "string"
     ],
     "sealId": "string"
     }
     */
    private String applyCause;
    private Integer applyCount;
    private String applyUser;
    private String expireTime;
    private String sealId;
    private List<String> imgList;

    //上传盖章详情图片
    private String applyId;

    private String fileType;
    private Integer fileNumber;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(Integer fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public String getApplyCause() {
        return applyCause;
    }

    public void setApplyCause(String applyCause) {
        this.applyCause = applyCause;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }

    @Override
    public String toString() {
        return "AddUseSealApplyBean{" +
                "applyCause='" + applyCause + '\'' +
                ", applyCount=" + applyCount +
                ", applyUser='" + applyUser + '\'' +
                ", expireTime='" + expireTime + '\'' +
                ", sealId='" + sealId + '\'' +
                ", imgList=" + imgList +
                ", applyId=" + applyId +
                ", fileType=" + fileType +
                ", fileNumber=" + fileNumber +
                '}';
    }

}
