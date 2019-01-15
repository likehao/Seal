package cn.fengwoo.sealsteward.entity;

/**
 * 树形列表第三级
 */
public class ThirdModel {
    private boolean isCheck;
    private String title;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ThirdModel() {

    }

    public ThirdModel(boolean isCheck, String title) {

        this.isCheck = isCheck;
        this.title = title;
    }
}
