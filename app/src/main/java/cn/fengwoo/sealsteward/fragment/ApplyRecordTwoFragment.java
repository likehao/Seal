package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.UseSealApplyActivity;
import cn.fengwoo.sealsteward.adapter.WaitApplyAdapter;
import cn.fengwoo.sealsteward.bean.ApplyListData;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.WaitApplyData;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 审批历史（已驳回）
 */
public class ApplyRecordTwoFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View view;
    @BindView(R.id.apply_record_two_lv)
    ListView apply_record_two_lv;
    private WaitApplyAdapter waitApplyAdapter;
    private List<WaitApplyData> waitApplyDataList;
    @BindView(R.id.apply_record_two_smt)
    SmartRefreshLayout apply_record_two_smt;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;
    private int i =1;
    ResponseInfo<List<GetApplyListBean>> responseInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.apply_record_two_fragment, container, false);
        ButterKnife.bind(this, view);
        initData();
        setSmartRefreshLayout();
        return view;
    }

    private void initData() {
        waitApplyDataList = new ArrayList<>();
        apply_record_two_lv.setOnItemClickListener(this);
        apply_record_two_smt.autoRefresh();   //自动刷新
        waitApplyAdapter = new WaitApplyAdapter(getActivity(), waitApplyDataList, 6);
        apply_record_two_lv.setAdapter(waitApplyAdapter);
    }

    /**
     * 刷新加载
     */
    public void setSmartRefreshLayout() {
        apply_record_two_smt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                waitApplyDataList.clear();
                i = 1;
                getRecordTwo(refreshLayout);
                refreshLayout.finishRefresh(); //刷新完成
            }
        });
        apply_record_two_smt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                i += 1;
                apply_record_two_smt.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动
                getRecordTwo(refreshLayout);
                //如果成功有数据就加载
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    refreshLayout.finishLoadMore(2000);
                } else {
                    refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
                }
            }
        });

    }

    private void getRecordTwo(RefreshLayout refreshLayout){
        ApplyListData applyListData = new ApplyListData();
        applyListData.setCurPage(i);
        applyListData.setHasExportPdf(false);
        applyListData.setHasPage(true);
        applyListData.setPageSize(10);
        applyListData.setParam(8);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.APPLYLIST, 2, null, applyListData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<GetApplyListBean>>>() {
                }
                        .getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    for (GetApplyListBean app : responseInfo.getData()) {
                        //时间戳转为时间
                        String expireTime = DateUtils.getDateString(Long.parseLong(app.getExpireTime())); //失效时间
                        String applyTime = DateUtils.getDateString(Long.parseLong(app.getApplyTime()));  //申请时间
                        waitApplyDataList.add(new WaitApplyData(app.getApplyCause(), app.getSealName()
                                , expireTime, app.getApplyCount(), applyTime, app.getId(), app.getApproveStatus()
                                , app.getApplyUserName(), app.getOrgStructureName()
                                , app.getHeadPortrait(), app.getStampCount(), app.getAvailableCount(), app.getPhotoCount()
                                ,app.getApplyPdf(),app.getStampPdf(),app.getStampRecordPdf()));
                    }
                    //请求数据
                    if (null != getActivity()) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                waitApplyAdapter.notifyDataSetChanged(); //刷新数据
                                no_record_ll.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", waitApplyDataList.get(position).getId());
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.APPLYDETAIL, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "查看详情错误错误!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<GetApplyListBean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<GetApplyListBean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    Intent intent = new Intent(getActivity(), UseSealApplyActivity.class);
                    intent.putExtra("sealName", waitApplyDataList.get(position).getSealName());
                    intent.putExtra("count", waitApplyDataList.get(position).getApplyCount());
                    intent.putExtra("failTime", waitApplyDataList.get(position).getFailTime());
                    intent.putExtra("cause", waitApplyDataList.get(position).getCause());
                    intent.putExtra("pdf", waitApplyDataList.get(position).getApplyPdf());
                    startActivity(intent);
                } else {
                    Looper.prepare();
                    Toast.makeText(getActivity(), responseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        });
    }
}
