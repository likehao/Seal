package cn.fengwoo.sealsteward.entity;

public class UpdateEnclosureData {

    /**
     * address : string
     * enableFlag : true
     * latitude : 11.111111
     * longitude : 11.11111
     * scope : string
     * sealId : string
     */

    private String address;
    private boolean enableFlag;
    private double latitude;
    private double longitude;
    private String scope;
    private String sealId;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(boolean enableFlag) {
        this.enableFlag = enableFlag;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSealId() {
        return sealId;
    }

    public void setSealId(String sealId) {
        this.sealId = sealId;
    }
}
