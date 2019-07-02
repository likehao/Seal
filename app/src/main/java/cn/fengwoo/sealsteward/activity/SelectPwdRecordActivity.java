package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.PwdRecordAdapter;
import cn.fengwoo.sealsteward.bean.OfflineRecordData;
import cn.fengwoo.sealsteward.bean.SeeRecordBean;
import cn.fengwoo.sealsteward.bean.StampRecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 查询到的密码盖章记录
 */
public class SelectPwdRecordActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.set_back_ll)
    LinearLayout setBackLl;
    @BindView(R.id.pwdRecord_lv)
    ListView pwdRecordLv;
    @BindView(R.id.pwdRecord_smt)
    SmartRefreshLayout pwdRecordSmt;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;
    PwdRecordAdapter adapter;
    private List<SeeRecordBean> list;
    private int i;
    ResponseInfo<List<OfflineRecordData>> responseInfo;
    String begin, end, applyUser, sealId;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pwd_record);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        titleTv.setText("密码盖章");
        setBackLl.setVisibility(View.VISIBLE);
        setBackLl.setOnClickListener(this);

        Intent intent = getIntent();
        end = intent.getStringExtra("end");
        begin = intent.getStringExtra("begin");
        applyUser = intent.getStringExtra("personId");
        sealId = intent.getStringExtra("sealId");

        list = new ArrayList<>();
        pwdRecordSmt.autoRefresh();  //自动刷新
        getPwdRecord();
        adapter = new PwdRecordAdapter(list, SelectPwdRecordActivity.this);
        pwdRecordLv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    private void getPwdRecord() {
        pwdRecordSmt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                i = 1;
                list.clear();
                getPwdRecordData(refreshLayout);

            }
        });

        pwdRecordSmt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                i += 1;
                pwdRecordSmt.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动
                getPwdRecordData(refreshLayout);
            }
        });
    }

    String endTime,startTime;
    /**
     * 获取密码盖章记录请求数据
     */
    private void getPwdRecordData(RefreshLayout refreshLayout) {
        StampRecordData stampRecordData = new StampRecordData();
        StampRecordData.Param param = new StampRecordData.Param();
        stampRecordData.setCurPage(i);
        stampRecordData.setHasExportPdf(false);
        stampRecordData.setHasPage(true);
        stampRecordData.setPageSize(10);
        try {
            //时间转为时间戳
            if (end != null && begin != null) {
                endTime = DateUtils.dateToStamp2(end);
                startTime = DateUtils.dateToStamp2(begin);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        param.Param(applyUser, endTime, sealId, 2,startTime);  // type 1.APP盖章   2.密码盖章
        stampRecordData.setParam(param);

        HttpUtil.sendDataAsync(this, HttpUrl.OFFLINERECORD, 2, null, stampRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<OfflineRecordData>>>() {
                }.getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    for (OfflineRecordData app : responseInfo.getData()) {
                        String stampTime = DateUtils.getDateString(Long.parseLong(app.getStampTime()));
                        list.add(new SeeRecordBean(app.getFlowNumber(),app.getSealName(),app.getUserName(),stampTime));
                    }
                    //请求数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.finishRefresh(); //刷新完成
                            refreshLayout.finishLoadMore();//结束加载
                            adapter.notifyDataSetChanged(); //刷新数据
                            no_record_ll.setVisibility(View.GONE);
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.finishRefresh(); //刷新完成
                            refreshLayout.finishLoadMore();//结束加载
                            refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
                        }
                    });
                }
            }
        });
    }
}
