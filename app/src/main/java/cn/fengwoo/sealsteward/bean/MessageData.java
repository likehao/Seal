package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class MessageData implements Serializable {

    /**
     * 消息
     *   "data": [
     {
     "id": "string",
     "type": 0,
     "unreadCount": 0
     }
     ],
     */
    private String id;
    private Integer type;
    private Integer unreadCount;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public String toString() {
        return "MessageData{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", unreadCount=" + unreadCount +
                '}';
    }

}
