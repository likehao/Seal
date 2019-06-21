package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.WaitApplyAdapter;
import cn.fengwoo.sealsteward.adapter.WaitMeAgreeAdapter;
import cn.fengwoo.sealsteward.bean.ApplyListData;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.WaitApplyData;
import cn.fengwoo.sealsteward.entity.WaitMeAgreeData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 待我审批
 */
public class WaitMeAgreeActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.wait_me_agree_lv)
    ListView wait_me_agree_lv;
    private WaitMeAgreeAdapter waitMeAgreeAdapter;
    private List<WaitMeAgreeData> waitMeAgreeDataList;
    @BindView(R.id.wait_me_agree_apply_smartRL)
    SmartRefreshLayout wait_me_agree_apply_smartRL;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;
    private int i = 1;
    ResponseInfo<List<GetApplyListBean>> responseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_me_agree);

        ButterKnife.bind(this);
        setSmartRefreshLayout();
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("待我审批");
        Intent intent = getIntent();
        String param = intent.getStringExtra("msgId");
        if (param != null) {
            updateReadMsg(param);  //更新已读
        }

        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        waitMeAgreeDataList = new ArrayList<>();
        wait_me_agree_lv.setOnItemClickListener(this);
        wait_me_agree_apply_smartRL.autoRefresh();
        waitMeAgreeAdapter = new WaitMeAgreeAdapter(WaitMeAgreeActivity.this, waitMeAgreeDataList);
        wait_me_agree_lv.setAdapter(waitMeAgreeAdapter);
    }

    /**
     * 刷新加载
     */
    public void setSmartRefreshLayout() {
        wait_me_agree_apply_smartRL.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                waitMeAgreeDataList.clear(); //清除数据
                i = 1;
                getWaitMeAgreeData();
                refreshLayout.finishRefresh(); //刷新完成
            }
        });
        wait_me_agree_apply_smartRL.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                i += 1;
                wait_me_agree_apply_smartRL.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动
                getWaitMeAgreeData();
                //如果成功有数据就加载
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    refreshLayout.finishLoadMore(2000);
                } else {
                    refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
                }
            }
        });

    }

    /**
     * 获取待我审批记录请求
     */
    private void getWaitMeAgreeData() {
        ApplyListData applyListData = new ApplyListData();
        applyListData.setCurPage(i);
        applyListData.setHasExportPdf(false);
        applyListData.setHasPage(true);
        applyListData.setPageSize(10);
        applyListData.setParam(6);
        HttpUtil.sendDataAsync(WaitMeAgreeActivity.this, HttpUrl.APPLYLIST, 2, null, applyListData, new Callback() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                            for (GetApplyListBean app : responseInfo.getData()) {
                                //时间戳转为时间
                                String applyTime = DateUtils.getDateString(Long.parseLong(app.getApplyTime()));  //申请时间
                                waitMeAgreeDataList.add(new WaitMeAgreeData(app.getApplyCause(), app.getApplyUserName()
                                        , app.getOrgStructureName(), applyTime, app.getSealName(), app.getApplyCount(),
                                        app.getExpireTime(), app.getId(), app.getApplyPdf(),app.getAutoGraph()));
                            }
                            //请求数据
                            waitMeAgreeAdapter.notifyDataSetChanged(); //刷新数据
                            no_record_ll.setVisibility(View.GONE);

                        }else {
                            waitMeAgreeAdapter.notifyDataSetChanged(); //刷新数据
                            if (waitMeAgreeDataList.size() == 0){
                                no_record_ll.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, UseSealApplyActivity.class);
        intent.putExtra("waitAgree", 1);
        intent.putExtra("sealName", waitMeAgreeDataList.get(position).getSealName());
        intent.putExtra("count", waitMeAgreeDataList.get(position).getApplyCount());
        //时间戳转时间
        String failTime = DateUtils.getDateString(Long.parseLong(waitMeAgreeDataList.get(position).getFailTime()));
        intent.putExtra("failTime", failTime);
        intent.putExtra("cause", waitMeAgreeDataList.get(position).getCause());
        intent.putExtra("applyId", waitMeAgreeDataList.get(position).getApplyId());
        intent.putExtra("pdf", waitMeAgreeDataList.get(position).getPdf());
        intent.putExtra("autoGraph", waitMeAgreeDataList.get(position).getAutoGraph());
        startActivityForResult(intent, 88);
    }

    /**
     * 更新已读消息
     */
    private void updateReadMsg(String msgId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("messageId", msgId);
        Utils.log("9999999999msgId:" + msgId);

        HttpUtil.sendDataAsync(WaitMeAgreeActivity.this, HttpUrl.UPDATEREADMSG, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("9999999999result:" + result);
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        Log.e("TAG", "消息已被阅读成功!!!!!!!!!");
                    }
                } else {
                    Log.e("TAG", responseInfo.getMessage() + "错误错误!!!!!!!!!!!!!");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 88) {
            wait_me_agree_apply_smartRL.autoRefresh();
            waitMeAgreeAdapter.notifyDataSetChanged(); //刷新数据
        }
    }
}
