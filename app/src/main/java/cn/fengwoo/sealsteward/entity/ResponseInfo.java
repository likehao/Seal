package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class ResponseInfo<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;
    public ResponseInfo(){

    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
