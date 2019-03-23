package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class ApplyListData implements Serializable {

    /**
     * 申请审批列表
     * {
     "curPage": 0,      当前页
     "hasExportPdf": true,
     "hasPage": true,   是否分页
     "pageSize": 0,     条数
     "param": 0       status状态（0-待审批,1-已审批,2-审批中,3-已驳回,4已撤销,5已关闭,6待我审批）
     }
     */
    private Integer curPage;
    private Boolean hasExportPdf;
    private Boolean hasPage;
    private Integer pageSize;
    private Integer param;
    public Integer getCurPage() {
        return curPage;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public Boolean getHasExportPdf() {
        return hasExportPdf;
    }

    public void setHasExportPdf(Boolean hasExportPdf) {
        this.hasExportPdf = hasExportPdf;
    }

    public Boolean getHasPage() {
        return hasPage;
    }

    public void setHasPage(Boolean hasPage) {
        this.hasPage = hasPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getParam() {
        return param;
    }

    public void setParam(Integer param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "ApplyListData{" +
                "curPage=" + curPage +
                ", hasExportPdf=" + hasExportPdf +
                ", hasPage=" + hasPage +
                ", pageSize=" + pageSize +
                ", param=" + param +
                '}';
    }

}
