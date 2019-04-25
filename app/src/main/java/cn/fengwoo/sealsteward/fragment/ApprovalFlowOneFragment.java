package cn.fengwoo.sealsteward.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.ApprovalFlowOneAdapter;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 审批人
 */
public class ApprovalFlowOneFragment extends Fragment {
    @BindView(R.id.approval_flow_one_smt)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.approval_flow_one_lv)
    ListView listView;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;
    private View view;
    private ApprovalFlowOneAdapter oneAdapter;
    private CommonAdapter flowOneAdapter;
    private ArrayList<SealInfoData.SealApproveFlowListBean> list;
    private String sealApproveFlowListString;
    private List<SealInfoData.SealApproveFlowListBean> systemFuncListInfo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.approval_flow_one,container,false);
        ButterKnife.bind(this, view);
        initView();
        setApprovalFlowSmt();
        smartRefreshLayout.autoRefresh();
        return view;

    }

    private void initView() {
        list = new ArrayList<>();
        flowOneAdapter = new CommonAdapter<SealInfoData.SealApproveFlowListBean>(getActivity(),list,R.layout.approval_flow_one_item) {
            @Override
            public void convert(ViewHolder viewHolder, SealInfoData.SealApproveFlowListBean bean, int position) {
                viewHolder.setText(R.id.approvalFlow_name_tv,bean.getApproveUserName());
                viewHolder.setText(R.id.approvalFlow_department_tv,bean.getOrgStructureName());
                viewHolder.setText(R.id.approvalFlow_level_tv,bean.getApproveLevel()+ "级审批");

                viewHolder.setOnClickListener(R.id.oneDelete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.size() > 1) {
                            //((SwipeMenuLayout) viewHolder.getConvertView()).quickClose();  //点击侧滑菜单上的选项时关闭
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", list.get(position).getId());
                            HttpUtil.sendDataAsync(getActivity(), HttpUrl.DELETEAPPROVALFLOW, 4, hashMap, null, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("TAG", e + "删除审批流错误错误!!!!!!!!!!");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String result = response.body().string();
                                    Gson gson = new Gson();
                                    ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                                    }
                                            .getType());
                                    if (responseInfo.getCode() == 0) {
                                        if (responseInfo.getData()) {
                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        uploadSealInfo(list.get(position).getSealId());
                                                        list.remove(position);
                                                        flowOneAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(getActivity(),"请至少保留一位审批人",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };

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

    private void uploadSealInfo(String sealId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealIdOrMac", sealId);
        hashMap.put("type", "1");
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.SEAL_INFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "获取印章信息错误错误错误!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                Gson gson = new Gson();
                ResponseInfo<SealInfoData> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<SealInfoData>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    list.clear();
                    getApprovalOne();
                    smartRefreshLayout.autoRefresh();
                }
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
                if (bean.getApproveType() == 0) {
                    list.add(new SealInfoData.SealApproveFlowListBean(bean.getApproveUserName(), bean.getOrgStructureName(), bean.getApproveLevel(),bean.getId()));
                    no_record_ll.setVisibility(View.GONE);
                }
            }
            flowOneAdapter.notifyDataSetChanged();
        }
    }

}
