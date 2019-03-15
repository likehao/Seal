package cn.fengwoo.sealsteward.utils;

import android.text.TextUtils;

/**
 * Created by HQOCSHheqing on 2016/8/2.
 *
 * @description 部门类（继承Node），此处的泛型Integer是因为ID和parentID都为int
 * ，如果为String传入泛型String即可
 */
public class Dept extends Node<Integer>{

    private String id;//部门ID
    private String parentId;//父亲节点ID
    private String name;//部门名称
    private int typeInt;

    public int getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(int typeInt) {
        this.typeInt = typeInt;
    }


    public Dept() {
    }

    public Dept(String id, String parentId, String name,int typeInt) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.typeInt = typeInt;
    }

    /**
     * 此处返回节点ID
     * @return
     */
    @Override
    public String get_id() {
        return id;
    }

    /**
     * 此处返回父亲节点ID
     * @return
     */
    @Override
    public String get_parentId() {
        return parentId;
    }

    @Override
    public String get_label() {
        return name;
    }

    @Override
    public int get_type() {
        return typeInt;
    }

    @Override
    public boolean parent(Node dest) {
//        if (id == ((Integer)dest.get_parentId()).intValue()){
        if (id.equals(dest.get_parentId())){
            return true;
        }
        return false;
    }

    @Override
    public boolean child(Node dest) {
        if(TextUtils.isEmpty(parentId)){
            parentId = " ";
        }
        if (parentId.equals(dest.get_id())){
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
