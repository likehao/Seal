package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.SeeRecordActivity;
import cn.fengwoo.sealsteward.activity.SelectSealRecodeActivity;
import cn.fengwoo.sealsteward.adapter.RecordAdapter;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.bean.StampRecordData;
import cn.fengwoo.sealsteward.bean.StampRecordList;
import cn.fengwoo.sealsteward.entity.RecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.CommonDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 记录
 */
public class RecordFragment extends Fragment implements AdapterView.OnItemClickListener {
    private View view;
    private RecordAdapter recordAdapter;
    private List<RecordData> list;
    private Intent intent;
    @BindView(R.id.record_refreshLayout)
    SmartRefreshLayout record_refreshLayout;
    @BindView(R.id.record_lv)
    ListView record_lv;
    String begin, end, personId, sealId;
    @BindView(R.id.select_record_smt)  //单独用来放置查询出来的记录
            SmartRefreshLayout select_record_smt;
    @BindView(R.id.select_record_lv)
    ListView select_record_lv;
    @BindView(R.id.select_record_ll)
    LinearLayout select_record_ll;
    @BindView(R.id.record_ll)
    LinearLayout record_ll;
    private final static int REQUESTCODE = 111;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;
    String applyId;
    ResponseInfo<List<StampRecordList>> responseInfo;
    private int i = 1, j = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.record_fragment, container, false);

        ButterKnife.bind(this, view);
        initView();
        setListener();
        record_refreshLayout.autoRefresh(); //自动刷新
        setSmartRefreshLayout();
        initData();
        return view;
    }

    private void initView() {
        list = new ArrayList<>();
    }

    private void initData() {
        recordAdapter = new RecordAdapter(list, getActivity());
        record_lv.setAdapter(recordAdapter);
        select_record_lv.setAdapter(recordAdapter);
    }

    private void setListener() {
        record_lv.setOnItemClickListener(this);
        select_record_lv.setOnItemClickListener(this);
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
                getData(refreshLayout);
                refreshLayout.finishRefresh(); //刷新完成

            }
        });
        record_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                i += 1;
                record_refreshLayout.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动
                getData(refreshLayout);
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
     * 获取记录请求
     *
     * @param refreshLayout
     */
    private void getData(RefreshLayout refreshLayout) {
        StampRecordData stampRecordData = new StampRecordData();
        stampRecordData.setCurPage(i);
        stampRecordData.setHasPage(true);
        stampRecordData.setPageSize(10);

        HttpUtil.sendDataAsync(getActivity(), HttpUrl.STAMPRECORDAPPLYLIST, 2, null, stampRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
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
                        list.add(new RecordData(app.getId(), app.getApplyCause(), app.getSealName(), app.getApplyUserName()
                                , app.getStampCount(), app.getAvailableCount(), photoCount
                                , failTime, sealTime, app.getLastStampAddress(), app.getApproveStatus(),
                                app.getApplyPdf(), app.getStampPdf(), app.getStampRecordPdf(), app.getHeadPortrait(), app.getOrgStructureName()
                                , app.getStampRecordImgList()));

                    }
                    //请求数据
                    if (null != getActivity()) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recordAdapter.notifyDataSetChanged(); //刷新数据
                                no_record_ll.setVisibility(View.GONE);

                            }
                        });
                    }

                }

            }

        });
    }

    /**
     * 处理注册事件
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        if (s.equals("1")) {
            select_record_ll.setVisibility(View.GONE);
            record_ll.setVisibility(View.VISIBLE);
            record_refreshLayout.autoRefresh();  //自动刷新
        }
        if (s.equals("筛选")) {
            Intent intent = new Intent(getActivity(), SelectSealRecodeActivity.class);
            startActivityForResult(intent, 100);
        }
        if (s.equals("关闭刷新")) {
            record_refreshLayout.autoRefresh();
        }

    }

    /**
     * 获取查询记录列表
     */
    private void getRecordList() {
        select_record_ll.setVisibility(View.VISIBLE);
        record_ll.setVisibility(View.GONE);
        select_record_smt.autoRefresh();
        select_record_smt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list.clear();
                j = 1;
                getSelectData(refreshLayout);
                refreshLayout.finishRefresh(); //刷新完成
            }
        });
        select_record_smt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                j += 1;
                select_record_smt.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动
                getSelectData(refreshLayout);
                //如果成功有数据就加载
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    refreshLayout.finishLoadMore(2000);//延迟2000毫秒后结束加载
                } else {
                    refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
                }
            }
        });
    }

    /**
     * 发送获取查询记录请求
     *
     * @param refreshLayout
     */
    private void getSelectData(RefreshLayout refreshLayout) {
        StampRecordData stampRecordData = new StampRecordData();
        StampRecordData.Parem parem = new StampRecordData.Parem();
        stampRecordData.setCurPage(j);
        stampRecordData.setHasExportPdf(false);
        stampRecordData.setHasPage(true);
        stampRecordData.setPageSize(10);
        try {
            //时间转为时间戳
            if (end != null && begin != null) {
                String endTime = DateUtils.dateToStamp2(end);
                String startTime = DateUtils.dateToStamp2(begin);
                parem.Parem(personId, endTime, sealId, startTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        stampRecordData.setParam(parem);

        HttpUtil.sendDataAsync(getActivity(), HttpUrl.STAMPRECORDAPPLYLIST, 2, null, stampRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
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
                        list.add(new RecordData(app.getId(), app.getApplyCause(), app.getSealName(), app.getApplyUserName()
                                , app.getApplyCount(), app.getAvailableCount(), photoCount
                                , failTime, sealTime, app.getLastStampAddress(), app.getApproveStatus(),
                                app.getApplyPdf(), app.getStampPdf(), app.getStampRecordPdf(), app.getHeadPortrait(), app.getOrgStructureName()
                                , app.getStampRecordImgList()));
                        app.setStampRecordImgList(app.getStampRecordImgList());

                    }
                    //请求数据
                    if (null != getActivity()) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recordAdapter.notifyDataSetChanged(); //刷新数据
                                no_record_ll.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);   //注册Eventbus
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        intent = new Intent(getActivity(), SeeRecordActivity.class);
        StampRecordList stampRecordList = new StampRecordList();
        stampRecordList.getId();
        intent.putExtra("id", list.get(position).getId());
        applyId = list.get(position).getId();
        intent.putExtra("count", list.get(position).getSealCount());
        intent.putExtra("restCount", list.get(position).getRestCount());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            end = data.getStringExtra("end");
            begin = data.getStringExtra("begin");
            personId = data.getStringExtra("personId");
            sealId = data.getStringExtra("sealId");
            getRecordList();   //获取查询记录列表
        }
        if (resultCode == REQUESTCODE) {
            //上传完记录照片返回时候刷新
            CommonDialog commonDialog = new CommonDialog(getActivity(), "提示", "是否关闭该单据,关闭后记录照片将不能修改,并在后台生成盖章记录PDF", "关闭");
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
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.APPLICLOSE, 1, hashMap, null, new Callback() {
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
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    record_refreshLayout.autoRefresh();
                                }
                            });
                        }
                    }
                } else {
                    Log.e("TAG", responseInfo.getMessage() + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
                }
            }
        });
    }
}
