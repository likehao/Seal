package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

/**
 *  选择最近时间（单条选择器）
 */
public class NearTime implements Serializable{
    private int id;
    private String name;

    public NearTime(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        //重写该方法，作为选择器显示的名称
        return name;
    }

}
