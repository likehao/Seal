package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RecordAdapter;
import cn.fengwoo.sealsteward.adapter.SeeRecordAdapter;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.bean.RecordListBean;
import cn.fengwoo.sealsteward.bean.SeeRecordBean;
import cn.fengwoo.sealsteward.bean.SeeRecordDetailBean;
import cn.fengwoo.sealsteward.bean.StampRecordData;
import cn.fengwoo.sealsteward.bean.StampRecordList;
import cn.fengwoo.sealsteward.entity.RecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.MessagePopuwindow;
import de.hdodenhof.circleimageview.CircleImageView;
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
    @BindView(R.id.record_detail_circle)
    CircleImageView record_detail_circle;
    @BindView(R.id.record_detail_department_tv)
    TextView department_tv;
    private SeeRecordAdapter seeRecordAdapter;
    private List<SeeRecordBean> list;
    String id;
    private Integer status,count;
    private String applyPdf,stampPdf,stampRecordPdf;
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
        count = intent.getIntExtra("count",0);
        int restCount = intent.getIntExtra("restCount",0);
        int photoNum = intent.getIntExtra("photoNum",0);
        String sealName = intent.getStringExtra("sealName");
        String sealPerson = intent.getStringExtra("sealPerson");
        String headPortrait = intent.getStringExtra("headPortrait");
        String orgStructureName = intent.getStringExtra("orgStructureName");
        //加载详情头像
        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(headPortrait);
        if(bitmap == null){
            HttpDownloader.downloadImage(SeeRecordActivity.this, 3, headPortrait, new DownloadImageCallback() {
                @Override
                public void onResult(final String fileName) {
                    if(fileName != null){
                        String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                        Picasso.with(SeeRecordActivity.this).load(sealPrintPath).into(record_detail_circle);
                    }
                }
            });
        }else{
            String sealPrintPath = "file://" + HttpDownloader.path + headPortrait;
            Picasso.with(SeeRecordActivity.this).load(sealPrintPath).into(record_detail_circle);
        }
        applyPdf = intent.getStringExtra("applyPdf");
        stampPdf = intent.getStringExtra("stampPdf");
        stampRecordPdf = intent.getStringExtra("stampRecordPdf");

        status = intent.getIntExtra("status",0);
        detail_sealPerson_tv.setText(sealPerson);
        detail_sealCount_tv.setText(count+"");
        detail_restCount_tv.setText(restCount+"");
        detail_photoNum_tv.setText(photoNum+"");
        title_tv.setText(sealName);
        department_tv.setText(orgStructureName);
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
                                    setListViewHeightBasedOnChildren(see_record_lv);
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
                //根据单据是否已关闭状态显示不同的popuwindow
                if (status == 5){
                    messagePopuwindow = new MessagePopuwindow(this,3);
                    messagePopuwindow.showPopuwindow(v);
                }else {
                    messagePopuwindow = new MessagePopuwindow(this,2);
                    messagePopuwindow.showPopuwindow(v);
                }
                break;
        }
    }

    /**
     * 处理注册事件
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        if (s.equals("上传照片")){
            Intent intent = new Intent(this,UploadFileActivity.class);
            intent.putExtra("code",1);
            intent.putExtra("id",id);
            intent.putExtra("count",count);
            startActivity(intent);
        } else if (s.equals("申请文件")){
            Intent intent = new Intent(this, FileActivity.class);
            intent.putExtra("fileName",applyPdf);
            startActivity(intent);
        }else if (s.equals("盖章文件")){
            Intent intent = new Intent(this, FileActivity.class);
            intent.putExtra("fileName",stampPdf);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, FileActivity.class);
            intent.putExtra("fileName",stampRecordPdf);
            startActivity(intent);
        }

    }

    /**
     * 解决ScrollView嵌套ListView只显示一个item的问题
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listadapter = listView.getAdapter();
        if (listadapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listadapter.getCount(); i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listadapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listadapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        see_RecordDetail_smt.autoRefresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);  //解除注册
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
