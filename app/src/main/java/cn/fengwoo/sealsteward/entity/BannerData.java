package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class BannerData implements Serializable{

    /**
     * 广告
     *
     * "data": [
     {
     "id": "string",
     "imageFile": "string",
     "jumpUrl": "string"
     }
     ],
     */

    private String id;
    private String imageFile;
    private String jumpUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }
    @Override
    public String toString() {
        return "BannerData{" +
                "id='" + id + '\'' +
                ", imageFile='" + imageFile + '\'' +
                ", jumpUrl='" + jumpUrl + '\'' +
                '}';
    }
}
