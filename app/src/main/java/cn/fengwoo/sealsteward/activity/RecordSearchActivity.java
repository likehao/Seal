package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RecordAdapter;
import cn.fengwoo.sealsteward.bean.StampRecordData;
import cn.fengwoo.sealsteward.bean.StampRecordDataxx;
import cn.fengwoo.sealsteward.bean.StampRecordList;
import cn.fengwoo.sealsteward.entity.RecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 关于
 */
public class RecordSearchActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.et_searchx)
    EditText et_searchx;

    @BindView(R.id.record_refreshLayout)
    SmartRefreshLayout record_refreshLayout;

    @BindView(R.id.ll_record)
    LinearLayout ll_record;

    @BindView(R.id.lv_record)
    ListView lv_record;

    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;

    @BindView(R.id.ll_choice)
    LinearLayout ll_choice;

    @BindView(R.id.rl_first)
    RelativeLayout rl_first;

    @BindView(R.id.rl_sec)
    RelativeLayout rl_sec;

    @BindView(R.id.iv_first)
    ImageView iv_first;

    @BindView(R.id.iv_sec)
    ImageView iv_sec;

    private List<RecordData> list;
    private int i = 1, j = 1;
    ResponseInfo<List<StampRecordList>> responseInfo;

    private Intent intent;
    String applyId;
    private final static int REQUESTCODE = 111;

    private RecordAdapter recordAdapter;

    private boolean isFirstChose = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_search);
        ButterKnife.bind(this);
        initData();
        initView();
//        record_refreshLayout.autoRefresh(); //自动刷新
//        setSmartRefreshLayout();

    }

    private void initData() {
        list = new ArrayList<>();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("搜索");
        set_back_ll.setOnClickListener(this);
        rl_first.setOnClickListener(this);
        rl_sec.setOnClickListener(this);
        lv_record.setOnItemClickListener(this);
        et_searchx.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utils.log("IME_ACTION_SEARCH");
                    String key = et_searchx.getText().toString().trim();
                    if (!TextUtils.isEmpty(key)) {
                        record_refreshLayout.autoRefresh(); //自动刷新一次
                        setSmartRefreshLayout();

//                        showToast("请输入您想要搜索的地址");
                        // lose focus
//                        et_searchx.setFocusable(false);
//                        et_searchx.setFocusableInTouchMode(false);
                        et_searchx.clearFocus();

                        // 强制隐藏Android输入法窗口
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_searchx.getWindowToken(), 0);

                        return true;
                    }
//                    //  下面就是大家的业务逻辑
//                    searchPoi(key);
//                    //  这里记得一定要将键盘隐藏了
//                    hideKeyBoard();
                    return true;
                }
                return false;
            }
        });

        et_searchx.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点
                    Utils.log("获得焦点");
                    ll_record.setVisibility(View.GONE);
                    ll_choice.setVisibility(View.VISIBLE);

                } else {
                    // 失去焦点
                    Utils.log("失去焦点");
                    ll_record.setVisibility(View.VISIBLE);
                    ll_choice.setVisibility(View.GONE);
                }
            }
        });


        recordAdapter = new RecordAdapter(list, this);
        lv_record.setAdapter(recordAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.rl_first:
                isFirstChose = true;
                iv_first.setVisibility(View.VISIBLE);
                iv_sec.setVisibility(View.GONE);
                break;
            case R.id.rl_sec:
                isFirstChose = false;
                iv_sec.setVisibility(View.VISIBLE);
                iv_first.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        //     intent.putStringArrayListExtra("photoList", (ArrayList<String>) list.get(position).getStampRecordImgList());
        startActivityForResult(intent, REQUESTCODE);
    }


    /**
     * 刷新加载记录列表
     */
    public void setSmartRefreshLayout() {
        record_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list.clear();
                i = 1;
                getData();
                refreshLayout.finishRefresh(); //刷新完成

            }
        });
        record_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                i += 1;
                record_refreshLayout.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动
                getData();
                //如果成功有数据就加载
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    refreshLayout.finishLoadMore(2000);
                } else {
                    refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
                }

            }
        });
    }


    private void getData() {
        StampRecordDataxx stampRecordData = new StampRecordDataxx();
        stampRecordData.setCurPage(i);
        stampRecordData.setHasPage(true);
        stampRecordData.setPageSize(10);
        StampRecordDataxx.Param param = new StampRecordDataxx.Param();
        param.setSearchType(isFirstChose?"1":"2");
        param.setSearchContent(et_searchx.getText().toString().trim());
        stampRecordData.setParam(param);

        HttpUtil.sendDataAsync(this, HttpUrl.STAMPRECORDAPPLYLIST, 2, null, stampRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("getData:" + result);
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<StampRecordList>>>() {
                }
                        .getType());
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
                        list.add(new RecordData(app.getApplyCount(),app.getId(), app.getApplyCause(), app.getSealName(), app.getApplyUserName()
                                , app.getStampCount(), app.getAvailableCount(), photoCount
                                , failTime, sealTime, app.getLastStampAddress(), app.getApproveStatus(),
                                app.getApplyPdf(), app.getStampPdf(), app.getStampRecordPdf(), app.getHeadPortrait(), app.getOrgStructureName()
                                , app.getStampRecordImgList()));

                    }
                    //请求数据
                    if (null != this) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recordAdapter.notifyDataSetChanged(); //刷新数据
                                no_record_ll.setVisibility(View.GONE);
                            }
                        });
                    }

                }else{
                    Utils.log("暂无盖章记录x");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(list!=null&&list.size()>0){
                                return;
                            }
                            no_record_ll.setVisibility(View.VISIBLE);
                            ll_record.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

}
