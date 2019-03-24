package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longsh.optionframelibrary.OptionBottomDialog;
import com.scwang.smartrefresh.header.BezierCircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RecordAdapter;
import cn.fengwoo.sealsteward.adapter.SeeRecordAdapter;
import cn.fengwoo.sealsteward.bean.RecordListBean;
import cn.fengwoo.sealsteward.bean.SeeRecordBean;
import cn.fengwoo.sealsteward.bean.SeeRecordDetailBean;
import cn.fengwoo.sealsteward.bean.StampRecordData;
import cn.fengwoo.sealsteward.bean.StampRecordList;
import cn.fengwoo.sealsteward.entity.RecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.MessagePopuwindow;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 详情
 */
public class SeeRecordActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.message_more_iv)
    ImageView message_more_iv;
    MessagePopuwindow messagePopuwindow;
    @BindView(R.id.see_record_lv)
    ListView see_record_lv;
    @BindView(R.id.see_RecordDetail_smt)
    SmartRefreshLayout see_RecordDetail_smt;
    @BindView(R.id.detail_sealCount_tv)
    TextView detail_sealCount_tv;
    @BindView(R.id.detail_restCount_tv)
    TextView detail_restCount_tv;
    @BindView(R.id.detail_photoNum_tv)
    TextView detail_photoNum_tv;
    @BindView(R.id.detail_sealPerson_tv)
    TextView detail_sealPerson_tv;
    private SeeRecordAdapter seeRecordAdapter;
    private List<SeeRecordBean> list;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_record);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        title_tv.setText("详情");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        message_more_iv.setVisibility(View.VISIBLE);
        message_more_iv.setOnClickListener(this);

    }

    @SuppressLint("SetTextI18n")
    private void initData(){
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        int count = intent.getIntExtra("count",0);
        int restCount = intent.getIntExtra("restCount",0);
        int photoNum = intent.getIntExtra("photoNum",0);
        String sealName = intent.getStringExtra("sealName");
        String sealPerson = intent.getStringExtra("sealPerson");

        detail_sealPerson_tv.setText(sealPerson);
        detail_sealCount_tv.setText(count+"");
        detail_restCount_tv.setText(restCount+"");
        detail_photoNum_tv.setText(photoNum+"");
        title_tv.setText(sealName);
        list = new ArrayList<>();
        setSmartDetail();
    }
    /**
     * 刷新盖章详情
     */
    private void setSmartDetail(){
        see_RecordDetail_smt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list.clear();
                SeeRecordDetailBean seeRecordDetailBean = new SeeRecordDetailBean();
                seeRecordDetailBean.setCurPage(1);
                seeRecordDetailBean.setHasPage(true);
                seeRecordDetailBean.setPageSize(10);
                seeRecordDetailBean.setParam(id);

                HttpUtil.sendDataAsync(SeeRecordActivity.this, HttpUrl.SEALRECORDLIST, 2, null, seeRecordDetailBean, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<List<RecordListBean>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<RecordListBean>>>() {
                        }
                                .getType());
                        if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                            for (RecordListBean app : responseInfo.getData()) {
                                String sealTime = DateUtils.getDateString(Long.parseLong(app.getStampTime()));  //最近盖章时间戳转为时间
                                list.add(new SeeRecordBean(app.getFlowNumber(),sealTime,app.getAddress()));
                            }
                            //请求数据
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seeRecordAdapter = new SeeRecordAdapter(SeeRecordActivity.this,list);
                                    see_record_lv.setAdapter(seeRecordAdapter);
                                    seeRecordAdapter.notifyDataSetChanged(); //刷新数据
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
        see_RecordDetail_smt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();  //加载完成
                refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.message_more_iv:
                messagePopuwindow = new MessagePopuwindow(this,2);
                messagePopuwindow.showPopuwindow(v);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        see_RecordDetail_smt.autoRefresh();
    }

}
