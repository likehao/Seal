package cn.fengwoo.sealsteward.entity;

import java.util.List;

public class OrganizationalStructureData {
    /**
     * code : 0
     * message : 成功
     * data : [{"id":"d0abd9f57e484612bb36708b50fad180","parentId":null,"name":"13480635240的公司","type":1},{"id":"dd3b2727f74f44b3b19e505cd9d19ef4","parentId":"d0abd9f57e484612bb36708b50fad180","name":"公章管理处","type":2},{"id":"db2f28b2140b455f94992148966469e9","parentId":"d0abd9f57e484612bb36708b50fad180","name":"售销","type":2},{"id":"3030397b3e084fee9767654e4f477ccb","parentId":"d0abd9f57e484612bb36708b50fad180","name":"司法","type":2},{"id":"78c396f1aed245ec9dae37f5fd9648df","parentId":"dd3b2727f74f44b3b19e505cd9d19ef4","name":"13480635240","type":3}]
     * fileName : null
     */

    private int code;
    private String message;
    private Object fileName;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getFileName() {
        return fileName;
    }

    public void setFileName(Object fileName) {
        this.fileName = fileName;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : d0abd9f57e484612bb36708b50fad180
         * parentId : null
         * name : 13480635240的公司
         * type : 1
         */

        private String id;
        private Object parentId;
        private String name;
        private int type;
        private String portrait;


        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getParentId() {
            return parentId;
        }

        public void setParentId(Object parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
