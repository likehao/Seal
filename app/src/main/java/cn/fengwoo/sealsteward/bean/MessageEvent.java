package cn.fengwoo.sealsteward.bean;

/**
 * 定义消息事件类
 */
public class MessageEvent {
    public String msgType,msg;
    public byte[] bytes;
    public MessageEvent(String msgType, String msg) {
        this.msgType = msgType;
        this.msg = msg;
    }
    public MessageEvent(String msgType,byte[] bytes) {
        this.msgType = msgType;
        this.bytes = bytes;
    }

}
