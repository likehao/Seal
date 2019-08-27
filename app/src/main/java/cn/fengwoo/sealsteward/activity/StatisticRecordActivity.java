package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RecordAdapter;
import cn.fengwoo.sealsteward.bean.StampRecordData;
import cn.fengwoo.sealsteward.bean.StampRecordList;
import cn.fengwoo.sealsteward.entity.RecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 统计的盖章记录
 */
public class StatisticRecordActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    @BindView(R.id.statistics_record_smt)
    SmartRefreshLayout statistics_record_smt;
    @BindView(R.id.statistics_record_lv)
    ListView statistics_record_lv;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;
    private RecordAdapter recordAdapter;
    private List<RecordData> list;
    private int i = 1;
    private String userId, sealId, applyId;
    private Intent intent;
    private final static int REQUESTCODE = 111;
    private String endTime, startTime,beginMonth,nowTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_record);

        ButterKnife.bind(this);
        initView();
        getRecordList();
        initData();
    }

    private void initView() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        statistics_record_lv.setOnItemClickListener(this);
        title.setText("盖章记录");
        list = new ArrayList<>();
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        sealId = intent.getStringExtra("sealId");
        try {
            String staTime = intent.getStringExtra("startTime");
            String eTime = intent.getStringExtra("endTime");
            if (staTime != null && eTime != null){
                startTime = DateUtils.dateToStamp2(staTime);
                endTime = DateUtils.dateToStamp2(eTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        recordAdapter = new RecordAdapter(list, this);
        statistics_record_lv.setAdapter(recordAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.ll_search:
                startActivity(new Intent(this, RecordSearchActivity.class));
                break;

        }
    }

    private void getRecordList() {
        statistics_record_smt.autoRefresh();
        statistics_record_smt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list.clear();
                i = 1;
                getData(refreshLayout);
            }
        });
        statistics_record_smt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                i += 1;
                statistics_record_smt.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动
                getData(refreshLayout);
            }
        });
    }

    /**
     * 获取数据
     * @param refreshLayout
     */
    private void getData(RefreshLayout refreshLayout) {
        StampRecordData stampRecordData = new StampRecordData();
        StampRecordData.Param param = new StampRecordData.Param();
        stampRecordData.setCurPage(i);
        stampRecordData.setHasExportPdf(false);
        stampRecordData.setHasPage(true);
        stampRecordData.setPageSize(10);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-");
            //获取当前时间
            Date date = new Date(System.currentTimeMillis());
            String end = simpleDateFormat.format(date);
            //时间转为时间戳
            beginMonth = DateUtils.dateToStamp2(year + "-" + month + "-" + 01);
            nowTime = DateUtils.dateToStamp2(end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startTime != null && endTime != null){
            param.Param(userId, endTime, sealId, 1, startTime, "1", null);     // 1为APP盖章，2为密码盖章
        }else {
            param.Param(userId, nowTime, sealId, 1, beginMonth, "1", null);     // 1为APP盖章，2为密码盖章
        }
        stampRecordData.setParam(param);

        HttpUtil.sendDataAsync(this, HttpUrl.STAMPRECORDAPPLYLIST, 2, null, stampRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                ResponseInfo<List<StampRecordList>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<StampRecordList>>>() {
                }.getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {

                    for (StampRecordList app : responseInfo.getData()) {
                        String failTime = DateUtils.getDateString(Long.parseLong(app.getExpireTime()));  //过期时间戳转为时间
                        String sealTime = DateUtils.getDateString(Long.parseLong(app.getLastStampTime()));  //最近盖章时间戳转为时间
                        int photoCount; //如果没有照片数获取的是null,所以显示0不显示null在界面上
                        if (app.getPhotoCount() == null) {
                            photoCount = 0;
                        } else {
                            photoCount = app.getPhotoCount();
                        }
                        list.add(new RecordData(app.getApplyCount(), app.getId(), app.getApplyCause(), app.getSealName(), app.getApplyUserName()
                                , app.getStampCount(), app.getAvailableCount(), photoCount
                                , failTime, sealTime, app.getLastStampAddress(), app.getApproveStatus(),
                                app.getApplyPdf(), app.getStampPdf(), app.getStampRecordPdf(), app.getHeadPortrait(), app.getOrgStructureName()
                                , app.getStampRecordImgList()));
                        app.setStampRecordImgList(app.getStampRecordImgList());

                    }
                    //请求数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.finishRefresh(); //刷新完成
                            refreshLayout.finishLoadMore();//结束加载
                            recordAdapter.notifyDataSetChanged(); //刷新数据
                            no_record_ll.setVisibility(View.GONE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (i == 1) {
                                    no_record_ll.setVisibility(View.VISIBLE);
                            }
                            refreshLayout.finishRefresh(); //刷新完成
                            refreshLayout.finishLoadMore();//结束加载
                            recordAdapter.notifyDataSetChanged(); //刷新数据
                            refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        intent = new Intent(this, SeeRecordActivity.class);
        StampRecordList stampRecordList = new StampRecordList();
        stampRecordList.getId();
        intent.putExtra("id", list.get(position).getId());
        applyId = list.get(position).getId();
        intent.putExtra("count", list.get(position).getSealCount());
        intent.putExtra("restCount", list.get(position).getApplyCount());
        intent.putExtra("photoNum", list.get(position).getUploadPhotoNum());
        intent.putExtra("sealPerson", list.get(position).getSealPeople());
        intent.putExtra("sealName", list.get(position).getSealName());
        intent.putExtra("status", list.get(position).getApproveStatus());
        intent.putExtra("applyPdf", list.get(position).getApplyPdf());
        intent.putExtra("stampPdf", list.get(position).getStampPdf());
        intent.putExtra("stampRecordPdf", list.get(position).getStampRecordPdf());
        intent.putExtra("headPortrait", list.get(position).getHeadPortrait());
        intent.putExtra("orgStructureName", list.get(position).getOrgStructureName());
        intent.putExtra("photoList", (Serializable) list.get(position).getStampRecordImgList());
        intent.putExtra("cause", list.get(position).getCause());
        startActivityForResult(intent, REQUESTCODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUESTCODE) {
            //上传完记录照片返回时候刷新
            statistics_record_smt.autoRefresh();
            CommonDialog commonDialog = new CommonDialog(this, "提示", "是否关闭该单据,关闭后记录照片将不能修改,并在后台生成盖章记录PDF", "关闭");
            commonDialog.showDialog();
            commonDialog.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commonDialog.dialog.dismiss();
                    close();
                }
            });
        }
    }

    /**
     * 关闭单据
     */
    private void close() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", applyId);
        HttpUtil.sendDataAsync(this, HttpUrl.APPLICLOSE, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statistics_record_smt.autoRefresh();
                            }
                        });
                    }
                } else {
                    Log.e("TAG", responseInfo.getMessage() + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
                }
            }
        });
    }
}
