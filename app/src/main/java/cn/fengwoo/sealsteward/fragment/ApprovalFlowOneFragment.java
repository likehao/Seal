package cn.fengwoo.sealsteward.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import butterknife.BindView;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.ApprovalFlowOneAdapter;
import cn.fengwoo.sealsteward.bean.ApproveProgress;

/**
 * 审批人
 */
public class ApprovalFlowOneFragment extends Fragment {
    @BindView(R.id.approval_flow_one_smt)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.approval_flow_one_lv)
    ListView listView;
    private View view;
    private ApprovalFlowOneAdapter flowOneAdapter;
    private ArrayList<ApproveProgress> list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.approval_flow_one,container,false);
        initView();
        setApprovalFlowSmt();
        return view;

    }

    private void initView() {
        list = new ArrayList<>();
        flowOneAdapter = new ApprovalFlowOneAdapter(list,getActivity());
        listView.setAdapter(flowOneAdapter);
    }

    private void setApprovalFlowSmt(){
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                 list.clear();
                getApprovalOne();
                refreshLayout.finishRefresh(); //刷新完成
            }
        });
    }

    private void getApprovalOne(){

    }
}
