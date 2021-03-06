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
import android.widget.EditText;
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
import cn.fengwoo.sealsteward.activity.MainActivity;
import cn.fengwoo.sealsteward.activity.PwdRecordSearchActivity;
import cn.fengwoo.sealsteward.activity.RecordSearchActivity;
import cn.fengwoo.sealsteward.activity.SeeRecordActivity;
import cn.fengwoo.sealsteward.activity.SelectSealRecodeActivity;
import cn.fengwoo.sealsteward.adapter.PwdRecordAdapter;
import cn.fengwoo.sealsteward.adapter.RecordAdapter;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.bean.OfflineRecordData;
import cn.fengwoo.sealsteward.bean.SeeRecordBean;
import cn.fengwoo.sealsteward.bean.StampRecordData;
import cn.fengwoo.sealsteward.bean.StampRecordList;
import cn.fengwoo.sealsteward.entity.RecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ??????
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
    String begin, end, personId, sealId, cause;
    int type;
    @BindView(R.id.select_record_smt)  //???????????????????????????????????????
            SmartRefreshLayout select_record_smt;
    @BindView(R.id.select_record_lv)
    ListView select_record_lv;
    @BindView(R.id.select_record_ll)
    LinearLayout select_record_ll;
    @BindView(R.id.record_ll)
    LinearLayout record_ll;

    @BindView(R.id.et_search)
    EditText et_search;

    @BindView(R.id.ll_search)
    LinearLayout ll_search;

    @BindView(R.id.ll_pwdRecord)
    LinearLayout ll_pwdRecord;

    @BindView(R.id.pwdRecord_lv)
    ListView pwdRecordLv;

    private final static int REQUESTCODE = 111;

    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;
    @BindView(R.id.no_record_ll2)
    LinearLayout no_record_ll2;

    @BindView(R.id.pwdRecord_smt)
    SmartRefreshLayout pwdRecordSmt;

    String applyId;
    ResponseInfo<List<StampRecordList>> responseInfo;
    private int i = 1, j = 1;
    PwdRecordAdapter adapter;
    private List<SeeRecordBean> pList;

    private int iP = 1;
    ResponseInfo<List<OfflineRecordData>> responseInfoP;

    private boolean isLeftClick = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.record_fragment, container, false);
        EventBus.getDefault().register(this);   //??????Eventbus
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setLeftOrRightListener(new MainActivity.LeftOrRightListener() {
            @Override
            public void whichSide(String side) {
                Utils.log("side:" + side);
                switch (side) {
                    case "left":
                        isLeftClick = true;
                        et_search.setHint(R.string.search_content);
                        record_refreshLayout.autoRefresh(); //????????????
                        ll_pwdRecord.setVisibility(View.GONE);
                        record_ll.setVisibility(View.VISIBLE);
                        select_record_ll.setVisibility(View.GONE);
                        break;
                    case "right":
                        isLeftClick = false;
                        et_search.setHint(R.string.search_content2);
                        pwdRecordSmt.autoRefresh();
                        ll_pwdRecord.setVisibility(View.VISIBLE);
                        record_ll.setVisibility(View.GONE);
                        select_record_ll.setVisibility(View.GONE);
                        break;
                }
            }
        });
        ButterKnife.bind(this, view);
        initView();
        setListener();
        record_refreshLayout.autoRefresh(); //????????????
        setSmartRefreshLayout();

        // ??????????????????
        getPwdRecord();
//        pwdRecordSmt.autoRefresh();

        initData();
        return view;
    }


    private void getPwdRecord() {
        pwdRecordSmt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                iP = 1;
                pList.clear();
                getPwdRecordData(refreshLayout);

            }
        });

        pwdRecordSmt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                iP += 1;
                pwdRecordSmt.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//????????????????????????
                getPwdRecordData(refreshLayout);
            }
        });
    }

    String endTime, startTime;

    /**
     * ????????????????????????????????????
     */
    private void getPwdRecordData(RefreshLayout refreshLayout) {
        StampRecordData stampRecordData = new StampRecordData();
        StampRecordData.Param param = new StampRecordData.Param();
        stampRecordData.setCurPage(iP);
        stampRecordData.setHasExportPdf(false);
        stampRecordData.setHasPage(true);
        stampRecordData.setPageSize(10);
        try {
            //?????????????????????
            if (end != null && begin != null) {
                endTime = DateUtils.dateToStamp2(end);
                startTime = DateUtils.dateToStamp2(begin);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        param.Param(applyUser, endTime, sealId, 2,startTime);  // type 1.APP??????   2.????????????  3.????????????
        param.Param(null, null, null, 2, null);  // type 1.APP??????   2.????????????
        stampRecordData.setParam(param);

        HttpUtil.sendDataAsync(getActivity(), HttpUrl.OFFLINERECORD, 2, null, stampRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????????????????????????????!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfoP = gson.fromJson(result, new TypeToken<ResponseInfo<List<OfflineRecordData>>>() {
                }.getType());
                if (responseInfoP.getData() != null && responseInfoP.getCode() == 0) {
                    for (OfflineRecordData app : responseInfoP.getData()) {
                        String stampTime = DateUtils.getDateString(Long.parseLong(app.getStampTime()));
                        pList.add(new SeeRecordBean(app.getFlowNumber(), app.getSealName(), app.getUserName(), stampTime,app.getStampType()));
                    }
                    //????????????
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.finishRefresh(); //????????????
                            refreshLayout.finishLoadMore();//????????????
                            adapter.notifyDataSetChanged(); //????????????
                            no_record_ll2.setVisibility(View.GONE);
                            no_record_ll.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iP == 1){
                                no_record_ll.setVisibility(View.VISIBLE);
                                no_record_ll2.setVisibility(View.VISIBLE);
                            }
                            refreshLayout.finishRefresh(); //????????????
                            refreshLayout.finishLoadMore();//????????????
                            adapter.notifyDataSetChanged(); //????????????
                            refreshLayout.finishLoadMoreWithNoMoreData();  //??????????????????,??????????????????????????????
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        list = new ArrayList<>();
        pList = new ArrayList<>();

    }

    private void initData() {
        recordAdapter = new RecordAdapter(list, getActivity());
        record_lv.setAdapter(recordAdapter);
        select_record_lv.setAdapter(recordAdapter);
        adapter = new PwdRecordAdapter(pList, getActivity());
        pwdRecordLv.setAdapter(adapter);
    }

    private void setListener() {
        record_lv.setOnItemClickListener(this);
        select_record_lv.setOnItemClickListener(this);
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("onclick");
                if (isLeftClick) {
                    startActivity(new Intent(getActivity(), RecordSearchActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), PwdRecordSearchActivity.class));
                }
            }
        });
    }

    /**
     * ????????????????????????
     */
    public void setSmartRefreshLayout() {
        record_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list.clear();
                recordAdapter.notifyDataSetChanged(); //????????????
                i = 1;
                getData(refreshLayout);
//                refreshLayout.finishRefresh(); //????????????

            }
        });
        record_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                i += 1;
                record_refreshLayout.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//????????????????????????
                getData(refreshLayout);
                //??????????????????????????????
/*                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    refreshLayout.finishLoadMore(2000);
                } else {
                    refreshLayout.finishLoadMoreWithNoMoreData();  //??????????????????,??????????????????????????????
                }*/

            }
        });
    }

    /**
     * ??????????????????
     */
    private void getData(RefreshLayout refreshLayout) {
        StampRecordData stampRecordData = new StampRecordData();
        stampRecordData.setCurPage(i);
        stampRecordData.setHasPage(true);
        stampRecordData.setPageSize(10);

        HttpUtil.sendDataAsync(getActivity(), HttpUrl.STAMPRECORDAPPLYLIST, 2, null, stampRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????????????????????????????!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<StampRecordList>>>() {
                }
                        .getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    for (StampRecordList app : responseInfo.getData()) {
                        String failTime = DateUtils.getDateString(Long.parseLong(app.getExpireTime()));  //???????????????????????????
                        String sealTime = DateUtils.getDateString(Long.parseLong(app.getLastStampTime()));  //?????????????????????????????????
                        int photoCount; //?????????????????????????????????null,????????????0?????????null????????????
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

                    }
                    //????????????
                    if (null != getActivity()) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.finishRefresh(); //????????????
                                refreshLayout.finishLoadMore(); //????????????
                                recordAdapter.notifyDataSetChanged(); //????????????
                                no_record_ll.setVisibility(View.GONE);
                                no_record_ll2.setVisibility(View.GONE);
                            }
                        });
                    }

                } else {
                    if (null != getActivity()) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 1){
                                    no_record_ll.setVisibility(View.VISIBLE);
                                    no_record_ll2.setVisibility(View.VISIBLE);
                                }
                                recordAdapter.notifyDataSetChanged(); //????????????
                                refreshLayout.finishRefresh(); //????????????
                                refreshLayout.finishLoadMore();//????????????
                                refreshLayout.finishLoadMoreWithNoMoreData();  //??????????????????,??????????????????????????????
                            }
                        });
                    }
                }

            }

        });
    }

    /**
     * ??????????????????
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        switch (s) {
            case "1":
                select_record_ll.setVisibility(View.GONE);
                record_ll.setVisibility(View.VISIBLE);
                record_refreshLayout.autoRefresh();  //????????????
                break;
            case "??????":
                Intent intent = new Intent(getActivity(), SelectSealRecodeActivity.class);
                startActivityForResult(intent, 100);
                break;
            case "????????????":
                record_refreshLayout.autoRefresh();
                break;
            case "????????????":
                record_refreshLayout.autoRefresh();
                pwdRecordSmt.autoRefresh();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);   //??????Eventbus
    }

    @Override
    public void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);  //????????????
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * ????????????????????????
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
            }
        });
        select_record_smt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                j += 1;
                select_record_smt.setEnableLoadMore(true);
                refreshLayout.setEnableOverScrollDrag(true);//????????????????????????
                getSelectData(refreshLayout);
            }
        });
    }

    /**
     * ??????????????????????????????
     */
//    String endTime, startTime;
    private void getSelectData(RefreshLayout refreshLayout) {
        StampRecordData stampRecordData = new StampRecordData();
        StampRecordData.Param param = new StampRecordData.Param();
        stampRecordData.setCurPage(j);
        stampRecordData.setHasExportPdf(false);
        stampRecordData.setHasPage(true);
        stampRecordData.setPageSize(10);
        try {
            //?????????????????????
            if (end != null && begin != null) {
                endTime = DateUtils.dateToStamp2(end);
                startTime = DateUtils.dateToStamp2(begin);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        param.Param(personId, endTime, sealId, 1, startTime, "1", cause);     // 1???APP?????????2???????????????
        stampRecordData.setParam(param);

        HttpUtil.sendDataAsync(getActivity(), HttpUrl.STAMPRECORDAPPLYLIST, 2, null, stampRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????????????????????????????!!!!!!!!!!!!!!!");
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
                        String failTime = DateUtils.getDateString(Long.parseLong(app.getExpireTime()));  //???????????????????????????
                        String sealTime = DateUtils.getDateString(Long.parseLong(app.getLastStampTime()));  //?????????????????????????????????
                        int photoCount; //?????????????????????????????????null,????????????0?????????null????????????
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
                    //????????????
                    if (null != getActivity()) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.finishRefresh(); //????????????
                                refreshLayout.finishLoadMore();//????????????
                                recordAdapter.notifyDataSetChanged(); //????????????
                                no_record_ll.setVisibility(View.GONE);
                                no_record_ll2.setVisibility(View.GONE);
                                EventBus.getDefault().post(new MessageEvent("title_ui","title_ui"));
                            }
                        });
                    }
                } else {
                    if (null != getActivity()) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (j == 1){
                                    no_record_ll.setVisibility(View.VISIBLE);
                                    no_record_ll2.setVisibility(View.VISIBLE);
                                }
                                refreshLayout.finishRefresh(); //????????????
                                refreshLayout.finishLoadMore();//????????????
                                recordAdapter.notifyDataSetChanged(); //????????????
                                refreshLayout.finishLoadMoreWithNoMoreData();  //??????????????????,??????????????????????????????
                                EventBus.getDefault().post(new MessageEvent("title_ui","title_ui"));
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        intent = new Intent(getActivity(), SeeRecordActivity.class);
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
        //     intent.putStringArrayListExtra("photoList", (ArrayList<String>) list.get(position).getStampRecordImgList());
        startActivityForResult(intent, REQUESTCODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {  //?????????????????????????????????
            end = data.getStringExtra("end");
            begin = data.getStringExtra("begin");
            personId = data.getStringExtra("personId");
            sealId = data.getStringExtra("sealId");
            cause = data.getStringExtra("cause");
            String sealType = data.getStringExtra("type");
           /* if (sealType != null && sealType.equals("????????????")){
                type = 1;
            }else {
                type = 0;
            }*/
            //??????????????????????????????????????????????????????????????????
            ll_pwdRecord.setVisibility(View.GONE);
            record_ll.setVisibility(View.GONE);
            getRecordList();   //????????????????????????
        }
        if (resultCode == REQUESTCODE) {
            //???????????????????????????????????????
            record_refreshLayout.autoRefresh();
            CommonDialog commonDialog = new CommonDialog(getActivity(), "??????", "?????????????????????,????????????????????????????????????,??????????????????????????????PDF", "??????");
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
     * ????????????
     */
    private void close() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", applyId);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.APPLICLOSE, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????????????????????????????!!!!!!!!!!!!!!!");
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
                    Log.e("TAG", responseInfo.getMessage() + "????????????????????????????????????!!!!!!!!!!!!!!!");
                }
            }
        });
    }
}
