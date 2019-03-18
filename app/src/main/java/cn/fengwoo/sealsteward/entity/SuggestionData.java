package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class SuggestionData implements Serializable{
    /**
     * 意见反馈
     * {
     "content": "string",
     "id": "string",
     "screenshot": "string",
     "type": 0
     }
     */
    private String content;
    private String id;
    private String screenshot;
    private Integer type;

    @Override
    public String toString() {
        return "SuggestionData{" +
                "content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", screenshot='" + screenshot + '\'' +
                ", type=" + type +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
