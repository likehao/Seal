package cn.fengwoo.sealsteward.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.ExpandListViewAdapter;
import cn.fengwoo.sealsteward.entity.FirstModel;
import cn.fengwoo.sealsteward.entity.SecondModel;
import cn.fengwoo.sealsteward.entity.ThirdModel;

/**
 * 用户组织架构
 */
public class UserOrganizationalFragment extends Fragment{

    private View view;
    List<FirstModel> mListData;
    private ExpandListViewAdapter mAdapter;
    @BindView(R.id.expandableLv)
    ExpandableListView expandableLv;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_organizational_fragment,container,false);
        ButterKnife.bind(this,view);
        initDate();
        return view;

    }

    private void initDate() {
        mListData = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            FirstModel firstModel = new FirstModel();
            List<SecondModel> listSecondModel = new ArrayList<>();
            firstModel.setTitle("第一级" + i);
            firstModel.setListSecondModel(listSecondModel);
            mListData.add(firstModel);
            for (int j = 0; j < 5; j++) {
                SecondModel secondModel = new SecondModel();
                List<ThirdModel> thirdModelList = new ArrayList<>();
                secondModel.setTitle("第二级" + j);
                secondModel.setListThirdModel(thirdModelList);
                listSecondModel.add(secondModel);
                for (int k = 0; k < 3; k++) {
                    ThirdModel thirdModel = new ThirdModel();
                    thirdModel.setTitle("第三级" + k);
                    thirdModelList.add(thirdModel);
                }
            }
        }
        mAdapter = new ExpandListViewAdapter(mListData, getActivity());
        expandableLv.setAdapter(mAdapter);

        //默认展开列表
        int groupCount = expandableLv.getCount();
        for (int in = 0; in < groupCount; in++) {
            expandableLv.expandGroup(in);
        }
    }
}
