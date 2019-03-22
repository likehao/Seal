package cn.fengwoo.sealsteward.bean;

public class MessageEvent {
    public String msgType,msg;

    public MessageEvent(String msgType, String msg) {
        this.msgType = msgType;
        this.msg = msg;
    }


}
