package cn.fengwoo.sealsteward.bean;

/**
 * 定义消息事件类
 */
public class MessageEvent {
    public String msgType,msg;

    public MessageEvent(String msgType, String msg) {
        this.msgType = msgType;
        this.msg = msg;
    }


}
