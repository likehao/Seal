package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import cn.fengwoo.sealsteward.utils.ListViewForScrollView;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.MessagePopuwindow;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ??????
 */
public class SeeRecordActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.message_more_iv)
    TextView message_more_iv;
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
    @BindView(R.id.photoNum_ll)
    Button photoNum_ll;
    @BindView(R.id.photo_tip)
    TextView photo_tip;
    @BindView(R.id.detail_cause_tv)
    TextView detail_cause_tv;
    private SeeRecordAdapter seeRecordAdapter;
    private List<SeeRecordBean> list;
    String id;
    private Integer status,count;
    private String applyPdf,stampPdf,stampRecordPdf;
    private final static int REQUESTCODE = 222;
    private  ArrayList<String> listImg;
    private int i = 1;
    ResponseInfo<List<RecordListBean>> responseInfo;
    private boolean isRead = false;
    private int type,scan;
    private String companyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_record);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        title_tv.setText("??????");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        message_more_iv.setVisibility(View.VISIBLE);
        message_more_iv.setOnClickListener(this);
        photoNum_ll.setOnClickListener(this);
        see_RecordDetail_smt.autoRefresh();
    }

    @SuppressLint("SetTextI18n")
    private void initData(){
        Intent intent = getIntent();
        listImg = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList = getIntent().getStringArrayListExtra("photoList");
    //    ArrayList<String> alist = (List<Object>)getIntent().getSerializableExtra("photoList");
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                //listImg.add(String.valueOf(alist));
                listImg.add(arrayList.get(i));
            }
        }

        id = intent.getStringExtra("id");
        count = intent.getIntExtra("count",0);
        int restCount = intent.getIntExtra("restCount",0);
        int photoNum = intent.getIntExtra("photoNum",0);
        String sealName = intent.getStringExtra("sealName");
        String sealPerson = intent.getStringExtra("sealPerson");
        String headPortrait = intent.getStringExtra("headPortrait");
        String orgStructureName = intent.getStringExtra("orgStructureName");
        String cause = intent.getStringExtra("cause");
        companyId = intent.getStringExtra("companyId");
//        int approveStatus = intent.getIntExtra("approveStatus",0);


        //??????????????????
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
        type = intent.getIntExtra("type",0);

        status = intent.getIntExtra("status",0);
        if (status == 5) {
            // ??????????????????
            isRead = true;
            photo_tip.setText("??????????????????");
        }else {
            isRead = false;
            photo_tip.setText("??????????????????");
        }
        detail_sealPerson_tv.setText(sealPerson);
        detail_sealCount_tv.setText(count+"");
        detail_restCount_tv.setText(restCount+"");
        detail_photoNum_tv.setText(photoNum+"");
        title_tv.setText(sealName);
        department_tv.setText(orgStructureName);
        if (cause != null) {
            detail_cause_tv.setText(cause);
        }
        list = new ArrayList<>();
        setSmartDetail();
        seeRecordAdapter = new SeeRecordAdapter(SeeRecordActivity.this,list);
        see_record_lv.setAdapter(seeRecordAdapter);

        if (count - restCount > 0) {
            seeRecordAdapter.setRedCount(count - restCount);
        }

        scan = intent.getIntExtra("scan",0);

    }
    /**
     * ??????????????????
     */
    private void setSmartDetail(){
        see_RecordDetail_smt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list.clear();
                i = 1;
                getDetail(refreshLayout);
            }
        });
        see_RecordDetail_smt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                i += 1;
                see_RecordDetail_smt.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//????????????????????????
                getDetail(refreshLayout);
            }
        });

    }

    /**
     * ??????????????????
     */
    private void getDetail(RefreshLayout refreshLayout){
        SeeRecordDetailBean seeRecordDetailBean = new SeeRecordDetailBean();
        seeRecordDetailBean.setCurPage(i);
        seeRecordDetailBean.setHasPage(true);
        seeRecordDetailBean.setPageSize(10);
        seeRecordDetailBean.setParam(id);

        HttpUtil.sendDataAsync(SeeRecordActivity.this, HttpUrl.SEALRECORDLIST, 2, null, seeRecordDetailBean, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????????????????????????????!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<RecordListBean>>>() {
                }
                        .getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    for (RecordListBean app : responseInfo.getData()) {
                        String sealTime = DateUtils.getDateString(Long.parseLong(app.getStampTime()));  //?????????????????????????????????
                        list.add(new SeeRecordBean(app.getFlowNumber(),sealTime,app.getAddress()));
                    }
                    //????????????
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.finishRefresh(); //????????????
                            refreshLayout.finishLoadMore();//????????????
                            setListViewHeightBasedOnChildren(see_record_lv);
                            seeRecordAdapter.notifyDataSetChanged(); //????????????
                        }
                    });

                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.finishRefresh(); //????????????
                            refreshLayout.finishLoadMore();//????????????
                            refreshLayout.finishLoadMoreWithNoMoreData();  //??????????????????,??????????????????????????????
                        }
                    });
                }

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
                //????????????????????????????????????????????????popuwindow
                if (status == 5){
                    messagePopuwindow = new MessagePopuwindow(this,3);
                    messagePopuwindow.showPopuwindow(v);
                }else {
                    messagePopuwindow = new MessagePopuwindow(this,2);
                    messagePopuwindow.showPopuwindow(v);
                }
                break;
            case R.id.photoNum_ll:
                Intent intent = new Intent(this,UploadFileActivity.class);
                intent.putExtra("category", 5);
                intent.putExtra("code",1);
                intent.putExtra("id",id);
                intent.putExtra("isRead", isRead);
                intent.putStringArrayListExtra("photoList",listImg);
                intent.putExtra("type",type);
                intent.putExtra("scan",scan);
                intent.putExtra("companyId",companyId);
                startActivityForResult(intent,REQUESTCODE);
                break;
        }
    }

    /**
     * ??????????????????
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        if (s.equals("????????????")){
            Intent intent = new Intent(this,UploadFileActivity.class);
            intent.putExtra("category", 5);
            intent.putExtra("code",1);
            intent.putExtra("id",id);
            intent.putExtra("count",count);
            intent.putStringArrayListExtra("photoList",listImg);
            startActivityForResult(intent,REQUESTCODE);
         //   startActivity(intent);
        } else if (s.equals("????????????")){
            Intent intent = new Intent(this, FileActivity.class);
            intent.putExtra("fileName",applyPdf);
            intent.putExtra("companyId",companyId);
            startActivity(intent);
        }else if (s.equals("????????????")){
            Intent intent = new Intent(this, FileActivity.class);
            intent.putExtra("fileName",stampPdf);
            intent.putExtra("companyId",companyId);
            startActivity(intent);
        }else if(s.equals("????????????")){
            Intent intent = new Intent(this, FileActivity.class);
            intent.putExtra("fileName",stampRecordPdf);
            intent.putExtra("companyId",companyId);
            startActivity(intent);
        }
    }

    /**
     * ??????ScrollView??????ListView???????????????item?????????
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // ??????ListView?????????Adapter
        ListAdapter listadapter = listView.getAdapter();
        if (listadapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listadapter.getCount(); i++) {
            // listAdapter.getCount()????????????????????????
            View listItem = listadapter.getView(i, null, listView);
            // ????????????View ?????????
            listItem.measure(0, 0);
            // ??????????????????????????????
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listadapter.getCount() - 1));
        // listView.getDividerHeight()???????????????????????????????????????
        // params.height??????????????????ListView???????????????????????????
        listView.setLayoutParams(params);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);  //????????????
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE && resultCode == 222){
            setResult(111);
            finish();
        }
    }
}
