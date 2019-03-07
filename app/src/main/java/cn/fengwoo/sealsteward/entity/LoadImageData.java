package cn.fengwoo.sealsteward.entity;

import java.io.Serializable;

public class LoadImageData implements Serializable {

    /**
     * 上传图片
     *
     * {
     "code": 0,
     "data": {
     "errorMessage": "string",
     "fileName": "string",
     "success": true
     },
     "message": "string"
     }
     */
    private String errorMessage;
    private String fileName;
    private Boolean success;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "LoadImageData{" +
                "errorMessage='" + errorMessage + '\'' +
                ", fileName='" + fileName + '\'' +
                ", success=" + success +
                '}';
    }

}
