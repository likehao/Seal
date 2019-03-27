package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class MessageDeatileBean implements Serializable{

    /**
     * 消息详情列表
     *  "data": [
     {
     "content": "string",
     "createTime": "2019-03-26T02:14:19.334Z",
     "id": "string",
     "title": "string"
     }
     ],
     */
    private String createTime;
    private String content;
    private String id;
    private String title;

    public MessageDeatileBean(String createTime,String title, String content) {
        this.createTime = createTime;
        this.title = title;
        this.content = content;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageDeatileBean{" +
                "createTime='" + createTime + '\'' +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

}
