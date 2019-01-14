package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.UseSealApplyActivity;
import cn.fengwoo.sealsteward.adapter.WaitApplyAdapter;
import cn.fengwoo.sealsteward.entity.WaitApplyData;

public class SecondMyApplyFragmen extends Fragment implements AdapterView.OnItemClickListener{
    private View view;
    @BindView(R.id.applying_lv)
    ListView applying_lv;
    private WaitApplyAdapter waitApplyAdapter;
    private List<WaitApplyData> waitApplyDataList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.second_my_apply_fragmen,container,false);
        ButterKnife.bind(this,view);
        initData();
        return view;
    }

    private void initData() {
        waitApplyDataList = new ArrayList<>();
        waitApplyDataList.add(new WaitApplyData("请审批","公章","2019-09-09 19:19:19",10,"09/09 19:19:19"));
        waitApplyDataList.add(new WaitApplyData("请审批","公章","2019-09-09 19:19:19",10,"09/09 19:19:19"));
        waitApplyDataList.add(new WaitApplyData("请审批","公章","2019-09-09 19:19:19",10,"09/09 19:19:19"));
        waitApplyDataList.add(new WaitApplyData("请审批","公章","2019-09-09 19:19:19",10,"09/09 19:19:19"));
        waitApplyAdapter = new WaitApplyAdapter(getActivity(),waitApplyDataList,2);
        applying_lv.setAdapter(waitApplyAdapter);
        applying_lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), UseSealApplyActivity.class);
        startActivity(intent);
    }
}
