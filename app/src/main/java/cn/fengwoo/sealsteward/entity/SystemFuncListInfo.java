package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class SystemFuncListInfo implements Serializable {
    /**
     *  "code": 0,
     "id": "string",
     "name": "string"
     */

    private Integer code;
    private String id;
    private String name;

    @Override
    public String toString() {
        return "SystemFuncListInfo{" +
                "code=" + code +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
