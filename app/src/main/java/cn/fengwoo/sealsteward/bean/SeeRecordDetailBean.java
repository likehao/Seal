package cn.fengwoo.sealsteward.bean;

import java.io.Serializable;

public class SeeRecordDetailBean implements Serializable {

    /**
     * 记录详情
     * {
     "curPage": 0,      当前页
     "hasPage": true,   是否分页
     "pageSize": 0,     条数
     "param": 0
     }
     */
    private Integer curPage;
    private Boolean hasPage;
    private Integer pageSize;
    private String param;

    public Integer getCurPage() {
        return curPage;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
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

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "SeeRecordDetailBean{" +
                "curPage=" + curPage +
                ", hasPage=" + hasPage +
                ", pageSize=" + pageSize +
                ", param='" + param + '\'' +
                '}';
    }



}
