package cn.fengwoo.sealsteward.entity;

import java.util.List;

/**
 * 树形列表第一级
 */
public class FirstModel {
    private boolean isCheck;
    private String title;
    private List<SecondModel> listSecondModel;

    public FirstModel() {
    }

    public boolean isCheck() {

        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public List<SecondModel> getListSecondModel() {
        return listSecondModel;
    }

    public void setListSecondModel(List<SecondModel> listSecondModel) {
        this.listSecondModel = listSecondModel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FirstModel(boolean isCheck, String title, List<SecondModel> listSecondModel) {

        this.isCheck = isCheck;
        this.title = title;
        this.listSecondModel = listSecondModel;
    }
}
