package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.ApprovalFlowOneAdapter;
import cn.fengwoo.sealsteward.adapter.ApprovalFlowTwoAdapter;
import cn.fengwoo.sealsteward.entity.SealInfoData;

/**
 * 抄送人
 */
public class ApprovalFlowTwoFragment extends Fragment {
    private View view;
    @BindView(R.id.approval_flow_two_smt)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.approval_flow_two_lv)
    ListView listView;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;
    private ApprovalFlowTwoAdapter flowTwoAdapter;
    private ArrayList<SealInfoData.SealApproveFlowListBean> list;
    private String sealApproveFlowListString;
    private List<SealInfoData.SealApproveFlowListBean> systemFuncListInfo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.approval_flow_two,container,false);
        ButterKnife.bind(this, view);
        initView();
        setApprovalFlowSmt();
        smartRefreshLayout.autoRefresh();
        return view;

    }


    private void initView() {
        list = new ArrayList<>();
        flowTwoAdapter = new ApprovalFlowTwoAdapter(list,getActivity());
        listView.setAdapter(flowTwoAdapter);
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

    /**
     * 获取解析审批流列表数据
     */
    private void getApprovalOne(){
        Intent intent = getActivity().getIntent();
        sealApproveFlowListString = intent.getStringExtra("sealApproveFlowList");
        systemFuncListInfo = new Gson().fromJson(sealApproveFlowListString, new TypeToken<List<SealInfoData.SealApproveFlowListBean>>() {
        }.getType());
        if (systemFuncListInfo != null) {
            for (SealInfoData.SealApproveFlowListBean bean : systemFuncListInfo) {
                if (bean.getApproveType() == 1) {
                    list.add(new SealInfoData.SealApproveFlowListBean(bean.getApproveUserName(), bean.getOrgStructureName(), bean.getId()));
                    no_record_ll.setVisibility(View.GONE);
                }
            }
            flowTwoAdapter.notifyDataSetChanged();
        }
    }

}
