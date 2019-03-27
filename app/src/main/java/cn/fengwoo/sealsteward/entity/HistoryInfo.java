package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

/***
 * 登录时存储数据
 */
public class HistoryInfo implements Serializable{
    String phone;
    String name;
    String headPortrait;
    long time;

    public HistoryInfo(){

    }

    public HistoryInfo(String phone, String name,String headPortrait, long time) {
        this.phone = phone;
        this.name = name;
        this.headPortrait = headPortrait;
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHeadPortrait() {
        return headPortrait;
    }
    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
}
