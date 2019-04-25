package cn.fengwoo.sealsteward.entity;

public class DfuEntity {

    /**
     * content : string
     * fileName : string
     * id : string
     * version : string
     */

    private String content;
    private String fileName;
    private String id;
    private String version;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
