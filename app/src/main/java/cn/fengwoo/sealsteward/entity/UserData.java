package cn.fengwoo.sealsteward.entity;

public class UserData {

    private int photo;
    private String name;
    private String phone;

    public UserData(int photo, String name, String phone) {
        this.photo = photo;
        this.name = name;
        this.phone = phone;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
