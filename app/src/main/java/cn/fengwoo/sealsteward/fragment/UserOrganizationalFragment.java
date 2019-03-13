package cn.fengwoo.sealsteward.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.ExpandListViewAdapter;
import cn.fengwoo.sealsteward.adapter.NodeTreeAdapter;
import cn.fengwoo.sealsteward.entity.FirstModel;
import cn.fengwoo.sealsteward.entity.SecondModel;
import cn.fengwoo.sealsteward.entity.ThirdModel;
import cn.fengwoo.sealsteward.utils.Dept;
import cn.fengwoo.sealsteward.utils.Node;
import cn.fengwoo.sealsteward.utils.NodeHelper;

/**
 * 用户组织架构
 */
public class UserOrganizationalFragment extends Fragment{


    ///
    private ListView mListView;
    private NodeTreeAdapter mAdapter;
    private LinkedList<Node> mLinkedList = new LinkedList<>();

    private View view;
    List<FirstModel> mListData;
//    private ExpandListViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_organizational_fragment,container,false);
        ButterKnife.bind(this,view);
//        initDate();

        mListView = (ListView)view.findViewById(R.id.id_tree);
        mAdapter = new NodeTreeAdapter(getActivity(),mListView,mLinkedList);
        mListView.setAdapter(mAdapter);
        initData();
        return view;

    }

    private void initData(){
        List<Node> data = new ArrayList<>();
        addOne(data);
        mLinkedList.addAll(NodeHelper.sortNodes(data));
        mAdapter.notifyDataSetChanged();
    }

//    private void initDate() {
//        mListData = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
//            FirstModel firstModel = new FirstModel();
//            List<SecondModel> listSecondModel = new ArrayList<>();
//            firstModel.setTitle("第一级" + i);
//            firstModel.setListSecondModel(listSecondModel);
//            mListData.add(firstModel);
//            for (int j = 0; j < 5; j++) {
//                SecondModel secondModel = new SecondModel();
//                List<ThirdModel> thirdModelList = new ArrayList<>();
//                secondModel.setTitle("第二级" + j);
//                secondModel.setListThirdModel(thirdModelList);
//                listSecondModel.add(secondModel);
//                for (int k = 0; k < 3; k++) {
//                    ThirdModel thirdModel = new ThirdModel();
//                    thirdModel.setTitle("第三级" + k);
//                    thirdModelList.add(thirdModel);
//                }
//            }
//        }
//        mAdapter = new ExpandListViewAdapter(mListData, getActivity());
//        expandableLv.setAdapter(mAdapter);
//
//        //默认展开列表
//        int groupCount = expandableLv.getCount();
//        for (int in = 0; in < groupCount; in++) {
//            expandableLv.expandGroup(in);
//        }
//    }


    private void addOne(List<Node> data){
        data.add(new Dept(1, 0, "总公司"));//可以直接注释掉此项，即可构造一个森林
        data.add(new Dept(2, 1, "一级部一级部门一级部门一级部门门级部门一级部门级部门一级部门一级部门门级部一级"));
        data.add(new Dept(3, 1, "一级部门"));
        data.add(new Dept(4, 1, "一级部门"));

        data.add(new Dept(222, 5, "二级部门--测试1"));
        data.add(new Dept(223, 5, "二级部门--测试2"));

        data.add(new Dept(5, 1, "一级部门"));

        data.add(new Dept(224, 5, "二级部门--测试3"));
        data.add(new Dept(225, 5, "二级部门--测试4"));

        data.add(new Dept(6, 1, "一级部门"));
        data.add(new Dept(7, 1, "一级部门"));
        data.add(new Dept(8, 1, "一级部门"));
        data.add(new Dept(9, 1, "一级部门"));
        data.add(new Dept(10, 1, "一级部门"));

        for (int i = 2;i <= 10;i++){
            for (int j = 0;j < 10;j++){
                data.add(new Dept(1+(i - 1)*10+j,i, "二级部门"+j));
            }
        }

        for (int i = 0;i < 5;i++){
            data.add(new Dept(101+i,11, "三级部门"+i));
        }

        for (int i = 0;i < 5;i++){
            data.add(new Dept(106+i,22, "三级部门"+i));
        }
        for (int i = 0;i < 5;i++){
            data.add(new Dept(111+i,33, "三级部门"+i));
        }
        for (int i = 0;i < 5;i++){
            data.add(new Dept(115+i,44, "三级部门"+i));
        }

//        for (int i = 0;i < 5;i++){
//            data.add(new Dept(401+i,101, "四级部门"+i));
//        }
    }

}
