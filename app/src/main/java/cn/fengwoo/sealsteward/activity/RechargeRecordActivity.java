package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RechargeRecordAdapter;
import cn.fengwoo.sealsteward.adapter.WaitApplyAdapter;
import cn.fengwoo.sealsteward.bean.ApplyListData;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.bean.RechargeRecordBean;
import cn.fengwoo.sealsteward.entity.RechargeRecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.WaitApplyData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 充值记录
 */
public class RechargeRecordActivity extends BaseActivity {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.recharge_record_lv)
    ListView recharge_record_lv;
    @BindView(R.id.recharge_smt)
    SmartRefreshLayout recharge_smt;
    RechargeRecordAdapter recordAdapter;
    List<RechargeRecordData> recordDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_record);

        ButterKnife.bind(this);
        initView();
        getRechagreRecord();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("充值记录");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recordDataList = new ArrayList<>();
    }

    /**
     * 获取充值记录信息
     */
    private void getRechagreRecord() {
        recharge_smt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                recordDataList.clear();
                ApplyListData applyListData = new ApplyListData();
                applyListData.setCurPage(1);
                applyListData.setHasPage(true);
                applyListData.setPageSize(10);
                HttpUtil.sendDataAsync(RechargeRecordActivity.this, HttpUrl.RECHARGERECORD, 2, null, applyListData, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<List<RechargeRecordBean>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<RechargeRecordBean>>>() {
                        }
                                .getType());
                        if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                            for (RechargeRecordBean app : responseInfo.getData()) {
                                try {
                                    Integer type = null;
                                    //时间戳转为时间
                                    String rechargeTime = DateUtils.getDateString(Long.parseLong(app.getRechargeTime()));// 支付时间
                                    String expireTime = DateUtils.getDateString(Long.parseLong(app.getCurrentServiceExpireTime()));// 服务费到期时间
                                 /*   if (app.getPaymentType() == 1) {
                                        type = Integer.parseInt("微信");
                                    } else {
                                        type = Integer.parseInt("支付宝");
                                    }*/
                                    recordDataList.add(new RechargeRecordData(rechargeTime, app.getPaymentResult(), app.getSealName()
                                            , app.getOrgStructureName(), app.getPackageContent(),app.getPaymentType() , expireTime, app.getAmountOfMoney()));
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                            }
                            //请求数据
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recordAdapter = new RechargeRecordAdapter(recordDataList, RechargeRecordActivity.this);
                                    recharge_record_lv.setAdapter(recordAdapter);
                                    recordAdapter.notifyDataSetChanged(); //刷新数据
                                    refreshLayout.finishRefresh(); //刷新完成
                                }
                            });

                        } else {
                            refreshLayout.finishRefresh(); //刷新完成
                            Looper.prepare();
                            showToast(responseInfo.getMessage());
                            Looper.loop();
                        }

                    }
                });

            }
        });
        recharge_smt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();  //加载完成
                refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recharge_smt.autoRefresh();  //自动刷新
    }
}
