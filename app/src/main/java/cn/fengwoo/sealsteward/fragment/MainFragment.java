package cn.fengwoo.sealsteward.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.toast.ToastUtils;
import com.imuxuan.floatingview.FloatingMagnetView;
import com.imuxuan.floatingview.FloatingView;
import com.imuxuan.floatingview.MagnetViewListener;
import com.polidea.rxandroidble2.RxBleConnection;
import com.squareup.picasso.Picasso;
import com.sw.style.ViewStyleSetter;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tianma.netdetector.lib.NetStateChangeObserver;
import com.tianma.netdetector.lib.NetStateChangeReceiver;
import com.tianma.netdetector.lib.NetworkType;
import com.white.easysp.EasySP;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.ApplyCauseActivity;
import cn.fengwoo.sealsteward.activity.ApplyUseSealActivity;
import cn.fengwoo.sealsteward.activity.ApprovalRecordActivity;
import cn.fengwoo.sealsteward.activity.CameraAutoActivity;
import cn.fengwoo.sealsteward.activity.DfuActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.activity.PplAddActivity;
import cn.fengwoo.sealsteward.activity.SeeRecordActivity;
import cn.fengwoo.sealsteward.activity.WaitMeAgreeActivity;
import cn.fengwoo.sealsteward.bean.AddUseSealApplyBean;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.bean.MessageData;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.bean.UploadHistoryRecord;
import cn.fengwoo.sealsteward.entity.BannerData;
import cn.fengwoo.sealsteward.entity.DfuEntity;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.StampUploadRecordData;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
import cn.fengwoo.sealsteward.utils.DataTrans;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.GlideImageLoader;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import cn.fengwoo.sealsteward.utils.RxTimerUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.CustomDialog;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.mob.tools.utils.DeviceHelper.getApplication;


public class MainFragment extends Fragment implements View.OnClickListener, NetStateChangeObserver {

    private View view;
    //??????????????????
    private List<String> imageViews = new ArrayList<String>();
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.sealDevice_rl)
    RelativeLayout sealDevice_rl;
    @BindView(R.id.needSeal_rl)
    RelativeLayout needSeal_rl;
    @BindView(R.id.useSealApply_rl)
    RelativeLayout useSealApply_rl;
    @BindView(R.id.approval_record_rl)
    RelativeLayout approval_record_rl;
    @BindView(R.id.add_ppl)
    RelativeLayout add_ppl;
    private Intent intent;
    /*   @BindView(R.id.home_companyName_tv)
       TextView home_companyName_tv;*/
    @BindView(R.id.tv_battery)
    TextView tv_battery;
    @BindView(R.id.tv_times_done)
    TextView tv_times_done;
    @BindView(R.id.tv_times_left)
    TextView tv_times_left;
    @BindView(R.id.electric_ll)
    LinearLayout electric_ll;

    @BindView(R.id.tv_stamp_reason)
    TextView tv_stamp_reason;
    @BindView(R.id.tv_expired_time)
    TextView tv_expired_time;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_ble_name)
    TextView tv_ble_name;
    @BindView(R.id.wait_me_apply_rl)
    RelativeLayout wait_me_apply_rl;
    @BindView(R.id.msg_num_tv)
    TextView msg_num_tv;
    @BindView(R.id.msg_num_tv_ppl_add)
    TextView msg_num_tv_ppl_add;
    @BindView(R.id.tv_check_record)
    TextView tv_check_record;
    @BindView(R.id.sealImg_iv)
    ImageView sealImg_iv;  //??????
    @BindView(R.id.company)
    TextView company_name;
    LoadingView loadingView;
    private RxBleConnection rxBleConnection;
    private String availableCount = "0"; // ????????????
    private String stampReason = ""; // ????????????
    private int startNumber; // ????????????
    private int currentStampTimes = 0; // ??????????????????
    /**
     * ??????????????????
     */
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private BDLocation currentLocation;
    private String currentAddress = "";
    private OnGetGeoCoderResultListener listener;
    private int unuploadedQuantity; // ??????????????????????????????
    private UploadHistoryRecord mUploadHistoryRecord = new UploadHistoryRecord();
    private List<UploadHistoryRecord.RecordListBean> recordListBeanList = new ArrayList<>();
    private String stampTime; // ????????????
    private int stampSeqNumber; // ????????????
    private int stampType; // ????????????
    private int userNumber; // ????????????(???????????????
    private String systemTimeStampString;
    private String waitId;
    private boolean stampTag = false;
    private boolean isConnect = false; // ??????????????????
    private RxTimerUtil rxTimerUtil;
    private int stampNumber;
    private List<String> picsList;
    private int uploadPicIndex = 0;
    private List<String> allPic;
    private String causeId = ""; // ??????id
    private float firmwareVersion;
    private Long lastTime;
    private Vibrator vibrator;
    private boolean startSeal = false;
    private String bleName;

//    private boolean hasDfu = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 66:
//                    tv_times_done.setText(currentStampTimes + "");
//                    Utils.log(String.valueOf("******************************************"));
//                    tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");
//                    // ???????????????????????????0??????????????????
//                    if (tv_times_left.getText().toString().trim().equals("0")) {
////                            ((MyApp) getActivity().getApplication()).getConnectDisposable().dispose();
//                        ((MyApp) getActivity().getApplication()).removeAllDisposable();
//                        ((MyApp) getApplication()).setConnectionObservable(null);
//                        currentStampTimes = 0;
//                    }
                    break;
            }
            return false;
        }
    });

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Utils.log("00000 00000 onCreateView");

        //?????????
        SDKInitializer.initialize(getActivity().getApplicationContext());
        view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this, view);
        initView();
        initBanner();
        setListener();
//        permissions();
        getMessage();
        vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);

        return view;
    }

    @SuppressLint("ObsoleteSdkInt")
    private void initView() {
        loadingView = new LoadingView(getActivity());
        //  title_tv.setText(CommonUtil.getUserData(getActivity()).getCompanyName());
        company_name.setVisibility(View.VISIBLE);
        if (getActivity() != null) {
            company_name.setText(CommonUtil.getUserData(getActivity()).getCompanyName());
        }

        //??????banner???????????????
        ViewStyleSetter viewStyleSetter = new ViewStyleSetter(banner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewStyleSetter.setRound(20);//????????????
        }
    }


    //?????????????????????
    private MagnetViewListener magnetViewListener = new MagnetViewListener() {
        @Override
        public void onRemove(FloatingMagnetView magnetView) {

        }

        @Override
        public void onClick(FloatingMagnetView magnetView) {
            if (startSeal){
                showToast("???????????????,??????????????????");
            }else {
                Intent intent = new Intent(getActivity(),ApplyCauseActivity.class);
                startActivityForResult(intent, Constants.TO_WANT_SEAL);
            }
        }
    };


    private void setListener() {
        sealDevice_rl.setOnClickListener(this);
        needSeal_rl.setOnClickListener(this);
        useSealApply_rl.setOnClickListener(this);
        wait_me_apply_rl.setOnClickListener(this);
        add_ppl.setOnClickListener(this);
        approval_record_rl.setOnClickListener(this);
        tv_check_record.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.log("00000 00000 onCreate");

    }

    /**
     * ???????????????
     */
    private void initBanner() {
        loadingView.show();
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.BANNER, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                List<Integer> imgList = new ArrayList<Integer>();
                imgList.add(R.drawable.default_banner);
                loadBanner(imgList);
                Log.e("ATG", "?????????????????????!!!!!!!!!!!!!!");
            }

            @SuppressLint("SdCardPath")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Gson gson = new Gson();
                LogUtil.d("333" + result);
                ResponseInfo<List<BannerData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<BannerData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    Log.e("ATG", "?????????????????????!!!!!!!!!!!!!!");
                    imageViews.clear();
                    int count = responseInfo.getData().size();   //banner????????????
                    //??????????????????
                    for (int i = 0; i < count; i++) {
                        String imgName = responseInfo.getData().get(i).getImageFile();
                        //???????????????????????????
                        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(imgName);
                        if (bitmap != null) {
                            imageViews.add("file://" + HttpDownloader.path + imgName);
                        } else {
                            //???????????????
                            HttpDownloader.downloadImage(getActivity(), 8, imgName, "", new DownloadImageCallback() {
                                @Override
                                public void onResult(String fileName) {
                                    if (fileName != null) {
                                        imageViews.add("file://" + HttpDownloader.path + fileName);
                                        loadBanner(imageViews);
                                    }
                                }
                            });
                        }
                    }
                    loadBanner(imageViews);
                } else {
                    List<Integer> imgList = new ArrayList<Integer>();
                    imgList.add(R.drawable.default_banner);
                    loadBanner(imgList);
                    Log.e("ATG", "?????????????????????!!!!!!!!!!!!!!");
                }

            }
        });
    }

    private void loadBanner(List<?> list) {
        banner.setImageLoader(new GlideImageLoader());
        if (null != getActivity()) {
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    banner.setImages(list);
                    //banner?????????????????????????????????????????????
                    banner.start();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sealDevice_rl:
                if (!Utils.isBluetoothOpen()) {
                    ToastUtils.show("????????????????????????????????????");
                    return;
                }

                // ????????????
                ((MyApp) getApplication()).setConnectionObservable(null);
                ((MyApp) getActivity().getApplication()).removeAllDisposable();
                intent = new Intent(getActivity(), NearbyDeviceActivity.class);
                intent.putExtra("isAddNewSeal", true);
                permissions();
//                startActivityForResult(intent, Constants.TO_NEARBY_DEVICE);

                break;
            case R.id.needSeal_rl:
                if (Utils.isHaveCompanyId(getActivity())) {
                    if (!Utils.isLocationEnabled(getActivity())) {
                        showToast("??????????????????????????????");
                        return;
                    }

                    // ????????????
                    ((MyApp) getApplication()).setConnectionObservable(null);
                    ((MyApp) getActivity().getApplication()).removeAllDisposable();

                    if (!Utils.isConnect(getActivity())) {
                        intent = new Intent(getActivity(), NearbyDeviceActivity.class);
                        intent.putExtra("isAddNewSeal", false);
                        permissions();
//                    startActivityForResult(intent, Constants.TO_NEARBY_DEVICE);

                        return;
                    }
//                if(hasDfu){
//                    goToDfuPage();
//                    return;
//                }
                    lockSeal();
                    intent = new Intent(getActivity(), ApplyCauseActivity.class);
                    startActivityForResult(intent, Constants.TO_WANT_SEAL);
                } else {
                    showToast("??????????????????????????????????????????????????????????????????");
                }
                break;
            case R.id.useSealApply_rl:
                if (Utils.isHaveCompanyId(getActivity())) {
                    EasySP.init(getActivity()).putString("currentSealName", "");
                    intent = new Intent(getActivity(), ApplyUseSealActivity.class);
                    startActivity(intent);
                } else {
                    showToast("??????????????????????????????????????????????????????????????????");
                }
                break;
            case R.id.approval_record_rl:
                intent = new Intent(getActivity(), ApprovalRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.wait_me_apply_rl:
                if (Utils.isHaveCompanyId(getActivity())) {
                    Intent intent = new Intent(getActivity(), WaitMeAgreeActivity.class);
                    intent.putExtra("msgId", waitId);
                    startActivity(intent);
                } else {
                    showToast("??????????????????????????????????????????????????????????????????");
                }
                break;
            case R.id.tv_check_record:
                if (Utils.isHaveCompanyId(getActivity())) {
                    getApplyDetail();
                }
                break;
            case R.id.add_ppl:
                intent = new Intent(getActivity(), PplAddActivity.class);
//                intent.putExtra("msgId", waitId);
                startActivity(intent);
                break;
        }
    }

    /**
     * ????????????
     */
    private void getApplyDetail() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", EasySP.init(getActivity()).getString("currentApplyId"));
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.APPLYDETAIL, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????????????????!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<GetApplyListBean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<GetApplyListBean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    intent = new Intent(getActivity(), SeeRecordActivity.class);
//                    intent.putExtra("status", status);    //??????????????????????????????popuwindow
                    intent.putExtra("id", EasySP.init(getActivity()).getString("currentApplyId"));
                    intent.putExtra("count", responseInfo.getData().getStampCount());
                    intent.putExtra("restCount", responseInfo.getData().getApplyCount());
                    intent.putExtra("photoNum", responseInfo.getData().getPhotoCount());
                    intent.putExtra("headPortrait", responseInfo.getData().getHeadPortrait());
                    intent.putExtra("sealName", responseInfo.getData().getSealName());
                    intent.putExtra("orgStructureName", responseInfo.getData().getOrgStructureName());
                    intent.putExtra("sealPerson", responseInfo.getData().getSealName());
                    intent.putExtra("applyPdf", responseInfo.getData().getApplyPdf());
                    intent.putExtra("stampPdf", responseInfo.getData().getStampPdf());
                    intent.putExtra("stampRecordPdf", responseInfo.getData().getStampRecordPdf());
                    intent.putExtra("status", responseInfo.getData().getApproveStatus());
                    intent.putExtra("photoList", (Serializable) responseInfo.getData().getStampRecordImgList());
                    intent.putExtra("cause", (Serializable) responseInfo.getData().getApplyCause());
                    Utils.log("status:" + responseInfo.getData().getApproveStatus());
                    startActivity(intent);
                } else {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });

    }

    private void getMessage() {
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.MESSAGE, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????????????????????????????!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<MessageData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<MessageData>>>() {
                }
                        .getType());
                assert responseInfo != null;
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    for (MessageData messageData : responseInfo.getData()) {
                        if (null != getActivity()) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //???????????????
                                    int type = messageData.getType();
                                    int msgNum = messageData.getUnreadCount();
                                    String id = messageData.getId();
                                    if (type == 4) {
                                        waitId = id;
                                        if (msgNum != 0) {
                                            msg_num_tv.setVisibility(View.VISIBLE);    //????????????
                                            msg_num_tv.setText(msgNum + "");
                                        } else {
                                            msg_num_tv.setVisibility(View.GONE);
                                        }
                                    } else if (type == 6) {
                                        if (msgNum != 0) {
                                            msg_num_tv_ppl_add.setVisibility(View.VISIBLE);// ppl add
                                            msg_num_tv_ppl_add.setText(msgNum + "");
                                        } else {
                                            msg_num_tv_ppl_add.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    Log.e("TAG", "??????????????????!!!!!!!!!!!!!!!!");
                } else {
                    Log.e("TAG", responseInfo.getMessage());
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.log("00000 00000 onResume");
        if (getActivity() != null) {
            company_name.setText(CommonUtil.getUserData(getActivity()).getCompanyName());
        }
        getMessage();
    }

    /**
     * ????????????
     */
    @Override
    public void onStart() {
        super.onStart();
        Utils.log("00000 00000 onStart");

//        EventBus.getDefault().register(this);
        //????????????
        banner.startAutoPlay();
//        NetUtil.registerNetConnChangedReceiver(getActivity());
//        NetUtil.addNetConnChangedListener(netConnChangedListener);
        NetStateChangeReceiver.registerObserver(this);
        FloatingView.get().attach(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.log("00000 00000 onStop");

//        EventBus.getDefault().unregister(this);
        //????????????
        banner.stopAutoPlay();
//        NetUtil.unregisterNetConnChangedReceiver(getActivity());
//        NetUtil.removeNetConnChangedListener(netConnChangedListener);
        FloatingView.get().detach(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.log("00000 00000 onDestroy");

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        //???????????????
        FloatingView.get().remove();
    }

    /**
     * ??????????????????
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        if (s.equals("??????????????????")) {
            getMessage();
        }
        if (s.equals("connect_seal")) {
            intent = new Intent(getActivity(), NearbyDeviceActivity.class);
            intent.putExtra("isAddNewSeal", false);
            intent.putExtra("?????????????????????finish", "?????????????????????finish");
            permissions();
        }
        if (s.equals("????????????")) {
            company_name.setText(CommonUtil.getUserData(getActivity()).getCompanyName());
        }
    }

    private void getSystemTime() {
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.SYSTEM_TIME, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<String> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<String>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    systemTimeStampString = responseInfo.getData();
                    Utils.log("timeStampString" + systemTimeStampString);
                    setNotification();
                }
            }
        });
    }

    private void checkDfu(float version) {
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.DFU_UPGRADE_CHECK, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("result:" + result);
                Gson gson = new Gson();
                ResponseInfo<DfuEntity> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<DfuEntity>>() {
                }.getType());
                DfuEntity dfuEntity = responseInfo.getData();
                Utils.log("dfuEntity" + dfuEntity.getVersion());
                float latestVersion = Float.parseFloat(dfuEntity.getVersion());
                /// test
//                latestVersion = 5;
                EasySP.init(getActivity()).putString("dfu_current_version", version + "");
                if (latestVersion > version) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("??????????????????????????????");
                            Utils.log("UUUUUUUUUU:goToDfuPage");
//                            hasDfu = true;
//                            goToDfuPage();
                            EasySP.init(getActivity()).putString("hasNewDfuVersion", "1");
                        }
                    });
                    EasySP.init(getActivity()).putString("dfu_version", dfuEntity.getVersion());
                    EasySP.init(getActivity()).putString("dfu_file_name", dfuEntity.getFileName());
                    EasySP.init(getActivity()).putString("dfu_content", dfuEntity.getContent());
//                    EventBus.getDefault().post(new MessageEvent("dfu", "true"));
                } else {
//                    hasDfu = false;
//                    EventBus.getDefault().post(new MessageEvent("dfu", "false"));
                    EasySP.init(getActivity()).putString("hasNewDfuVersion", "0");
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 678:
                // ????????????
                ((MyApp) getApplication()).removeAllDisposable();
                ((MyApp) getApplication()).setConnectionObservable(null);

                allPic = new ArrayList<>();

                if (data == null) {
                    return;
                }

                allPic = data.getStringArrayListExtra("photoList");
                Utils.log("strList.size()" + allPic.size());

                // ????????????
                // ???array?????????????????????????????????
                // init index
                uploadPicIndex = 0;

                // ???????????????
                File file = new File(allPic.get(0));
                compressAndUpload(file);

//                ///???
//                if (picsList != null) {
//                    Utils.log("picsList.size()" + picsList.size());
//                    picsList.addAll(allPic);
//                    Utils.log("picsList.size()" + picsList.size());
//                }
                break;

            case Constants.TO_NEARBY_DEVICE:
                if (resultCode != Constants.TO_NEARBY_DEVICE) {
                    return;
                }

                // ??????ble????????????
                bleName = data.getStringExtra("bleName");
                //???????????????????????????????????????????????????
                String applicationConnect = data.getStringExtra("applicationConnect");
                //????????????
                String sealPrint = data.getStringExtra("sealPrint");

                //???????????????
//                openFloatingView(1);

                Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(sealPrint);
                if (bitmap == null) {
                    HttpDownloader.downloadImage(getActivity(), 3, sealPrint, new DownloadImageCallback() {
                        @Override
                        public void onResult(final String fileName) {
                            if (fileName != null) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                            Picasso.with(getContext()).load(sealPrintPath).into(sealImg_iv);
                                            sealImg_iv.setBackgroundResource(R.color.white);
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else {
                    String sealPrintPath = "file://" + HttpDownloader.path + sealPrint;
                    Picasso.with(getActivity()).load(sealPrintPath).into(sealImg_iv);
                    sealImg_iv.setBackgroundResource(R.color.white);
                }

                tv_ble_name.setText(bleName);
//                // ????????????
//                permissions();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // ??????????????????
                        // ???????????????setNotification();
                        // setNotification();?????????????????????
                        getSystemTime();
                    }
                }, 1000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

//                        if (hasDfu) {
//                            return;
//                        }
                        Utils.log("UUUUUUUUUU:jump");
                        // ????????? ???????????? ??????????????????    ???????????????????????????
                        if (applicationConnect != null && applicationConnect.equals("applicationConnect")) {

                        } else {
                            intent = new Intent(getActivity(), ApplyCauseActivity.class);
                            startActivityForResult(intent, Constants.TO_WANT_SEAL);
                        }
                    }
                }, 50);

                break;

            case Constants.TO_WANT_SEAL:
                if (resultCode != Constants.TO_WANT_SEAL) {
                    return;
                }

                if (!Utils.isLocationEnabled(getActivity())) {
                    showToast("??????????????????????????????????????????????????????");
                    // ????????????
                    ((MyApp) getActivity().getApplication()).removeAllDisposable();
                    ((MyApp) getApplication()).setConnectionObservable(null);
                    return;
                }

                if (tv_address.getText().toString().trim().equals("??????????????????")) {
                    showToast("??????????????????????????????????????????????????????");
                    // ????????????
                    ((MyApp) getActivity().getApplication()).removeAllDisposable();
                    ((MyApp) getApplication()).setConnectionObservable(null);
                    return;
                }

                stampTag = true;

                // ??????????????????
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        permissions();
                        if (stampTag && EasySP.init(getActivity()).getBoolean("enableEnclosure", false)) {
                            mLocationClient.start();
                            Utils.log("mLocationClient.start();");
                            handler.postDelayed(this, 1000);
                        }
                    }
                }, 1000);

                String expireTime = data.getStringExtra("expireTime");
                availableCount = data.getStringExtra("availableCount");
                stampReason = data.getStringExtra("stampReason");
                causeId = data.getStringExtra("id");

                // get pics list
                picsList = new ArrayList<>();
                picsList = data.getStringArrayListExtra("pics");
                if (picsList != null) {
                    Utils.log("picsList.size:" + picsList.size());
                }

                // ?????? ?????? ??????
                int mode = EasySP.init(getActivity()).getInt("take_pic_mode");
                Utils.log("int mode:" + mode);

                if (mode == 1) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            intent.setClass(getActivity(), CameraAutoActivity.class);
                            intent.putExtra("cancel", "cancel");
                            startActivityForResult(intent, 678);
                        }
                    }, 1000);
                } else {
                }

                tv_stamp_reason.setText(stampReason);
                tv_expired_time.setText(DateUtils.getDateString(Long.parseLong(expireTime)));

                // ?????????????????????????????????????????????
                currentStampTimes = 0;
                tv_times_done.setText(currentStampTimes + "");
                tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");

                Utils.log(tv_times_done.getText().toString());

                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {

                    // ??????
                    // ????????????
                    Utils.log("case Constants.TO_WANT_SEAL:");
                    byte[] startAllByte = CommonUtil.startData(Integer.valueOf(availableCount), expireTime);
                    ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.START, startAllByte).getBytes()))
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    characteristicValue -> {
                                        // Characteristic value confirmed.
                                        Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                    },
                                    throwable -> {
                                        // Handle an error here.
                                    }
                            ));
                }
                break;
        }
    }

    /**
     * ?????????
     */
    public void openFloatingView(int code){
        //???????????????
//        FloatingView.get().add();     //???????????????????????????
        //??????????????????
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        //??????MainActivity???MainFragment?????????
        if (code == 1){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(width-170,900,0,0);
            //        layoutParams.gravity = 200;
            //???????????????
            FloatingView.get()
                    .layoutParams(layoutParams)
                    .icon(R.drawable.seal_hover_button)
                    .listener(magnetViewListener);
        }else if (code == 2){
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(width-170,900,0,0);
            //        layoutParams.gravity = 200;
            //???????????????
            FloatingView.get()
                    .layoutParams(layoutParams)
                    .icon(R.drawable.seal_hover_button)
                    .listener(magnetViewListener);
        }

    }

    /**
     * ??????
     */
    private void startPost(String startTime){
        StampUploadRecordData stampUploadRecordData = new StampUploadRecordData();
        stampUploadRecordData.setSealId(EasySP.init(getActivity()).getString("currentSealId"));  //??????ID
        stampUploadRecordData.setApplyId(EasySP.init(getActivity()).getString("currentApplyId")); //????????????ID
        stampUploadRecordData.setStampUser(CommonUtil.getUserData(getActivity()).getId());   //?????????ID
        stampUploadRecordData.setStartTime(startTime);   //????????????
        stampUploadRecordData.setStartNo(String.valueOf(startNumber)); //????????????
        stampUploadRecordData.setLatitude(currentLocation.getLatitude());
        stampUploadRecordData.setLongitude(currentLocation.getLongitude());
        stampUploadRecordData.setAddress(currentAddress);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.START, 2, null, stampUploadRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                //{"code":0,"message":"??????","data":true,"count":0}
            }
        });
    }
    private void uploadStampRecord(int stampNumber, String timeStamp) {
        StampUploadRecordData stampUploadRecordData = new StampUploadRecordData();
        stampUploadRecordData.setAddress(currentAddress); //
        stampUploadRecordData.setApplyId(EasySP.init(getActivity()).getString("currentApplyId"));
        stampUploadRecordData.setLatitude(currentLocation.getLatitude()); //
        stampUploadRecordData.setLongitude(currentLocation.getLongitude()); //
        stampUploadRecordData.setSealId(EasySP.init(getActivity()).getString("currentSealId"));
        stampUploadRecordData.setStampSeqNumber(stampNumber);
        stampUploadRecordData.setStampTime(timeStamp); //
        stampUploadRecordData.setStampUser(CommonUtil.getUserData(getActivity()).getId());
        stampUploadRecordData.setStartNo(String.valueOf(startNumber)); //

        HttpUtil.sendDataAsync(getActivity(), HttpUrl.STAMP_UPLOAD_RECORD, 2, null, stampUploadRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    //   {"code":0,"message":"??????","data":{"result":true,"applyClosed":false}}
                    JSONObject jsonObject = new JSONObject(result);
                    String dataString = jsonObject.getString("data");
                    JSONObject jsonObject1 = new JSONObject(dataString);
                    Boolean applyClosed = jsonObject1.getBoolean("applyClosed");
                    //???????????????????????????????????????????????????
                    if (applyClosed) {
                        // ????????????
                        ((MyApp) getActivity().getApplication()).removeAllDisposable();
                        ((MyApp) getApplication()).setConnectionObservable(null);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonDialog commonDialog = new CommonDialog(getActivity(), "????????????????????????????????????", "", "??????");
                                commonDialog.cancel.setVisibility(View.GONE);
                                commonDialog.showDialog();
                                commonDialog.setClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        commonDialog.dialog.dismiss();
                                    }
                                });

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Utils.log(result);
//                currentStampTimes++;
//                Utils.log(String.valueOf(currentStampTimes));
//                handler.sendEmptyMessage(66);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        tv_times_done.setText(currentStampTimes + "");
//                        Utils.log(String.valueOf("******************************************" + tv_times_done.getText()));
////                        showToast("asdf");
//                        tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");
//                        // ???????????????????????????0??????????????????
//                        if (tv_times_left.getText().toString().trim().equals("0")) {
////                            ((MyApp) getActivity().getApplication()).getConnectDisposable().dispose();
//                            ((MyApp) getActivity().getApplication()).removeAllDisposable();
//                            ((MyApp) getApplication()).setConnectionObservable(null);
//                            stampTag = false;
//                            currentStampTimes = 0;
//                        }
                    }
                });
            }
        });
    }

    /**
     *
     */
    private void uploadHistoryStampRecord() {
        mUploadHistoryRecord.setSealId(EasySP.init(getActivity()).getString("currentSealId"));
        mUploadHistoryRecord.setRecordList(recordListBeanList);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.UPLOAD_HISTORY_RECORD, 2, null, mUploadHistoryRecord, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
            }
        });
    }

    public void showToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * ??????????????????
     */
    private void syncSealName(String bleName) {
        if(bleName != null && !bleName.equals("") && !bleName.contains("BHQKL") && !bleName.contains("baihe")){
            // ???????????????????????????????????????24?????????
            String name = bleName;
            if(name.length() > 24){
                name = name.substring(0,24);
            }
            try{
                byte[] nameData = name.getBytes("GB2312");
                ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                        .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.SET_SEAL_NAME, nameData).getBytes()))
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                characteristicValue -> {
                                    // Characteristic value confirmed.
//                                    String result = Utils.bytesToHexString(characteristicValue);
                                    Utils.log("??????????????????->" + Utils.bytesToHexString(characteristicValue));
                                },
                                throwable -> {
                                    // Handle an error here.
                                }
                        ));
            } catch (Exception ex){

            }
        }
    }

    private void readRecord() {
        byte[] targetNumber = new byte[]{1}; // ??????
        ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.NOTIFYHISTORYUPLOAD, targetNumber).getBytes()))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        characteristicValue -> {
                            // Characteristic value confirmed.
                            Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                ));
    }

    // ????????????????????????
    private void deleteRecord() {
        byte[] targetNumber = new byte[]{1}; // ??????
        ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.UPLOAD, targetNumber).getBytes()))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        characteristicValue -> {
                            // Characteristic value confirmed.
                            Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                ));
    }

    private void refreshTimes() {
        currentStampTimes++;
        Utils.log(String.valueOf(currentStampTimes));

        tv_times_done.setText(currentStampTimes + "");
        Utils.log(String.valueOf("******************************************" + tv_times_done.getText()));
//                        showToast("asdf");
        tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");

        if ((Integer.parseInt(availableCount) - currentStampTimes) < 0) {
            tv_times_left.setText("0");
        }

        // ???????????????????????????0??????????????????
        if (tv_times_left.getText().toString().trim().equals("0")) {
//                            ((MyApp) getActivity().getApplication()).getConnectDisposable().dispose();
            if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                ((MyApp) getActivity().getApplication()).removeAllDisposable();
                ((MyApp) getApplication()).setConnectionObservable(null);
            }
            stampTag = false;
//            currentStampTimes = 0;
        }
        if (tv_times_left.getText().toString().trim().equals("1")) {
            setAdmin0();
        }
    }

    @SuppressLint("CheckResult")
    private void setNotification() {
        isConnect = true;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // ????????????
//                permissions();
                startLocate();
            }
        });

        ((MyApp) getActivity().getApplication()).getRxBleDevice().observeConnectionStateChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        connectionState -> {
                            // Process your way.
                            isConnect = false;

                            Utils.log("connectionState:" + connectionState);
                            showToast("???????????????");

                            ((MyApp) getActivity().getApplication()).removeAllDisposable();
                            ((MyApp) getApplication()).setConnectionObservable(null);
                            stampTag = false;

                            initWhenDisconnectBle();
                            // ??????timer
                            rxTimerUtil.cancel();
                            // ??????currentSealId
                            EasySP.init(getActivity()).putString("currentSealId", "");
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );


        if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {

            // ??????
            // ??????

//            Utils.log("EasySP.init(getActivity()).getString(\"dataProtocolVersion\").equals(\"3\")");
//            RxBleClient rxBleClient = RxBleClient.create(getActivity());
//            rxBleClient.setLogLevel(RxBleLog.VERBOSE);
//            RxBleDevice device = rxBleClient.getBleDevice(EasySP.init(getActivity()).getString("mac"));
//            device.establishConnection(false);

            byte[] byteTime = CommonUtil.getDateTime(Long.parseLong(systemTimeStampString));
            byte[] eleByte = new byte[]{0};

            if (((MyApp) getActivity().getApplication()).getConnectionObservable() != null) {
                ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                        .flatMap(rxBleConnection -> rxBleConnection.setupNotification(Constants.NOTIFY_UUID))
//                            .doOnNext(rxBleConnection-> this.rxBleConnection = rxBleConnection)
                        .doOnNext(notificationObservable -> {
                            // Notification has been set up ????????????????????????????????????
                            ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                                    .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.HANDSHAKE, byteTime).getBytes()))
                                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            characteristicValue -> {
                                                // Characteristic value confirmed.
                                                Utils.log("shake hands:" + characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                            },
                                            throwable -> {
                                                // Handle an error here.
                                            }
                                    ));
                        })
                        .flatMap(notificationObservable -> notificationObservable) // <-- Notification has been set up, now observe value changes.
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                bytes -> {
                                    // Given characteristic has been changes, here is the value.
                                    String result = Utils.bytesToHexString(bytes); //???????????????
                                    Utils.log("notificationObservable:" + Utils.bytesToHexString(bytes));

                                    if (Utils.bytesToHexString(bytes).startsWith("FF 05 A0 00")) {
                                        Utils.log("????????????");
                                        // ??????????????????
                                        firmwareVersion = (float) bytes[4] + ((float) bytes[5] / 100);
                                        Utils.log("firmwareVersion:" + firmwareVersion);
                                        checkDfu(firmwareVersion);
                                        // ??????1min??????????????????
                                        rxTimerUtil = new RxTimerUtil();
                                        rxTimerUtil.interval(60000, new RxTimerUtil.IRxNext() {
                                            @Override
                                            public void doNext(long number) {
                                                if (!isConnect) {
                                                    return;
                                                }
//                                            Utils.log("a loop");
                                                if (null != getActivity() && ((MyApp) getActivity().getApplication()).getConnectionObservable() != null) {
                                                    ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                                                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.ElECTRIC, eleByte).getBytes()))
                                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(
                                                                    characteristicValue -> {
                                                                        // Characteristic value confirmed.
                                                                        // Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                                                    },
                                                                    throwable -> {
                                                                        // Handle an error here.
                                                                    }
                                                            ));
                                                }
                                            }
                                        });

                                        // ??????????????????????????????
                                        byte[] unuploaded = DataTrans.subByte(bytes, 6, 2);
                                        unuploadedQuantity = DataTrans.bytesToInt(unuploaded, 0);
                                        // ????????????????????????????????????0??????????????????
                                        if (unuploadedQuantity != 0) {
                                            readRecord();
                                        }
                                        // ??????????????????
                                        syncSealName(bleName);

                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 AF")) {
                                        String batteryString = Utils.bytesToHexString(bytes).substring(9, 11);
                                        int batteryInt = Integer.parseInt(batteryString, 16);
                                        Utils.log("batteryInt:" + batteryInt);
                                        // ??????ui,????????????
                                        electric_ll.setVisibility(View.VISIBLE);
                                        tv_battery.setText(String.valueOf(batteryInt + "%"));
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 08 A2")) {
                                        // ??????????????????????????????????????????????????????
                                        // ????????????

                                        if (lastTime != null) {
                                            Long intervalTime = System.currentTimeMillis() - lastTime;
                                            if (intervalTime < 200) {
                                                vibrator.vibrate(3000);
                                                tooFastTip();
                                                // ????????????
                                                lockSeal();
                                            }
                                        }

                                        lastTime = System.currentTimeMillis();

                                        String allString = Utils.bytesToHexString(bytes);
//                                        String stampNumberHexString = allString.substring(9, 11) + allString.substring(6, 8);
                                        String stampNumberHexString = allString.substring(12, 14) + allString.substring(9, 11);
                                        stampNumber = Integer.valueOf(stampNumberHexString, 16);
                                        // ????????????
                                        String timeHexString = allString.substring(15, 32);
                                        String timeStamp = DateUtils.hexTimeToTimeStamp(timeHexString);
                                        Utils.log(timeStamp);
                                        refreshTimes();
                                        uploadStampRecord(stampNumber, timeStamp);
                                        Utils.log("stampNumber" + stampNumber + "   " + "timeStamp" + timeStamp);
                                        EventBus.getDefault().post(new MessageEvent("take_pic", "take_pic"));
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 05 A1 00")) {
                                        // ????????????????????????????????????????????????
                                        byte[] restTime = DataTrans.subByte(bytes, 4, 4);
                                        startNumber = DataTrans.bytesToInt(restTime, 0);
                                        Utils.log("startNumber" + startNumber);
                                        startSeal = true;
                                        //??????????????????
                                        Date time = new Date(System.currentTimeMillis());
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String startTime = dateFormat.format(time);
                                        //??????????????????
                                        String nowTime = DateUtils.dateToStamp2(startTime);
                                        startPost(nowTime);

                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 A7 ")) {
                                        int pressTime = bytes[3];
                                        Utils.log(pressTime + "");
                                        EventBus.getDefault().post(new MessageEvent("ble_time_press", pressTime + ""));

                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 B3 ")) {
                                        int pressTime = bytes[3];
                                        Utils.log(pressTime + "");
                                        EventBus.getDefault().post(new MessageEvent("ble_time_delay", pressTime + ""));

                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 B6 ")) {
                                        int voiceState = bytes[3];
                                        Utils.log(voiceState + "");
                                        EventBus.getDefault().post(new MessageEvent("ble_read_voice", voiceState + ""));

                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 05 A4 00 ")) {
                                        byte[] pwdCodeBytes = DataTrans.subByte(bytes, 4, 4);
                                        String pwdCode = DataTrans.bytesToInt(pwdCodeBytes, 0) + "";
                                        EventBus.getDefault().post(new MessageEvent("ble_add_pwd", pwdCode + ""));
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 B1 00 ")) {  //????????????????????????
                                        EventBus.getDefault().post(new MessageEvent("ble_change_stamp_count", "success"));
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 B2 00 ")) {
                                        EventBus.getDefault().post(new MessageEvent("ble_delete_pwd_user", "success"));
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 A5 00 ")) {
                                        EventBus.getDefault().post(new MessageEvent("ble_reset", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 07 AA 01")){
                                        Log.e("TAG","?????????????????????!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("first_fingerprint", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 07 AA 02")){
                                        Log.e("TAG","?????????????????????!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("second_fingerprint", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 07 AA 03")){
                                        Log.e("TAG","?????????????????????!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("third_fingerprint", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 07 AA 04")){
                                        Log.e("TAG","??????????????????!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("result", result));
                                        EventBus.getDefault().post(new MessageEvent("bytes", bytes));
                                        EventBus.getDefault().post(new MessageEvent("add_fingerprint", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 01 AB 00")){
                                        //??????????????????
                                        Log.e("TAG","??????????????????!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("delete_fingerprint","success"));
                                    }else if(Utils.bytesToHexString(bytes).startsWith("FF 01 AC 00")){
                                        //????????????????????????
                                        Log.e("TAG","??????????????????????????????!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("edit_update_fingerprint","success"));
                                    }
                                    // ????????????
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 06 A8 80")) {
                                        // ??????a8???seal
                                        sendDataToSealAfterIllegal();

                                        // ???????????????????????????
                                        sendIllegalToServer();

                                        if (firmwareVersion <= 3.02) {
                                            // ??????????????????
                                            refreshTimes();
                                            // ??????????????????????????????????????????????????????
                                            sendIllegalRecordToServer();
                                        }
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 0E A3")) {
                                        // ????????????????????????
                                        unuploadedQuantity--; // ????????????????????????????????????
                                        Utils.log("unuploadedQuantity:" + unuploadedQuantity);
                                        // ???????????????????????????

                                        // byte[3] ???????????? ????????????
                                        byte[] recordSeq = DataTrans.subByte(bytes, 3, 1);
                                        int recordSeqInt = DataTrans.bytesToInt(recordSeq, 0);
                                        Utils.log("recordSeqInt" + recordSeqInt);

                                        // byte[4] ???????????????stampType???
                                        byte[] stampTypeBytes = DataTrans.subByte(bytes, 4, 1);
                                        stampType = DataTrans.bytesToInt(stampTypeBytes, 0);


                                        // byte[5] - byte[6] ???????????????stampSeqNumber???
                                        byte[] stampSeqNumberBytes = DataTrans.subByte(bytes, 5, 2);
                                        stampSeqNumber = DataTrans.bytesToInt(stampSeqNumberBytes, 0);

                                        // byte[7] - byte[10] ???????????????stampSeqNumber???
                                        byte[] userNumberBytes = DataTrans.subByte(bytes, 7, 4);
                                        userNumber = DataTrans.bytesToInt(userNumberBytes, 0);

                                        // byte[11] - byte[16] ???????????????stampTime???
//                                    byte[] stampTimeBytes = DataTrans.subByte(bytes, 11, 6);
                                        String stampTimeHex = Utils.bytesToHexString(bytes).substring(33, 50);
                                        stampTime = DateUtils.hexTimeToTimeStamp(stampTimeHex);

                                        // ????????????????????????
                                        UploadHistoryRecord.RecordListBean recordListBean = new UploadHistoryRecord.RecordListBean();
                                        recordListBean.setStampType(stampType);
                                        recordListBean.setUserNumber(userNumber);
                                        recordListBean.setStampSeqNumber(stampSeqNumber);
                                        recordListBean.setStampTime(stampTime);
                                        recordListBeanList.add(recordListBean);

                                        // ??????????????????
                                        deleteRecord();

                                        if (unuploadedQuantity != 0) {
                                            // ????????????????????????
                                            // ???????????????
                                            readRecord();
                                        } else {
                                            // unuploadedQuantity == 0; ??????????????????????????????
                                            // ?????????????????????
                                            uploadHistoryStampRecord();
                                        }
                                    }
                                },
                                throwable -> {
                                    // Handle an error here.
                                    Utils.log("notificationObservable: error" + throwable.getLocalizedMessage());
                                }
                        ));
            } else {
                Utils.log("null null null null null null null null null null null null null null ");
            }
        } else {

            // ??????
            // ??????

            String shakeHandString = "HandShake/2yK39b";
            byte[] shakeHandBytes = shakeHandString.getBytes();
            ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                    .flatMap(rxBleConnection -> rxBleConnection.setupNotification(Constants.NOTIFY_UUID))
//                            .doOnNext(rxBleConnection-> this.rxBleConnection = rxBleConnection)
                    .doOnNext(notificationObservable -> {
                        // Notification has been set up ????????????????????????????????????
                        Utils.log("Notification has been set up");
                        ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, shakeHandBytes))
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        characteristicValue -> {
                                            // Characteristic value confirmed.
//                                                    Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                        },
                                        throwable -> {
                                            // Handle an error here.
                                        }
                                ));
                    })
                    .flatMap(notificationObservable -> notificationObservable) // <-- Notification has been set up, now observe value changes.
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            bytes -> {
                                // Given characteristic has been changes, here is the value.
                                Utils.log("notificationObservable:" + Utils.bytesToHexString(bytes));
                                String strReturned = new String(bytes);
                                Utils.log("str:" + strReturned);

                                // ??????????????????????????????????????????????????????????????????????????????
                                if (strReturned.equals("H1")) {
                                    Utils.log("????????????");

                                    String superManager = "ADMIN1";
                                    byte[] superManagerBytes = superManager.getBytes();
                                    ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, superManagerBytes))
                                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    characteristicValue -> {
                                                        // Characteristic value confirmed.
                                                        // Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                                    },
                                                    throwable -> {
                                                        // Handle an error here.
                                                    }
                                            ));
                                } else if (strReturned.equals("A1")) {
                                    // ???????????????????????????
                                    Utils.log("???????????????????????????");
                                } else if (strReturned.equals("U1")) {
                                    // ????????????
                                    Utils.log("????????????");
                                    // ?????????????????????super?????????????????????
                                    EventBus.getDefault().post(new MessageEvent("super_ble_launch", "success"));
                                } else if (strReturned.equals("U0")) {
                                    // ????????????
                                    Utils.log("????????????");
                                    // ?????????????????????super?????????????????????
                                    EventBus.getDefault().post(new MessageEvent("super_ble_launch", "failure"));
                                } else if (strReturned.equals("C1") || strReturned.equals("o1")) {
                                    // ??????????????????
//                                    Utils.log("??????????????????");
//                                    currentStampTimes++;
//                                    tv_times_done.setText(currentStampTimes + "");
//                                    tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");

                                    refreshTimes();
                                    uploadStampRecord(0, null);
                                    EventBus.getDefault().post(new MessageEvent("take_pic", "take_pic"));
                                    // ?????????o1????????????????????????????????????
                                    if (strReturned.equals("o1")) {
                                        sendIllegalToServer();
                                    }
                                }else if (strReturned.equals("K1")){
                                    showToast("????????????");
                                }else if (strReturned.equals("K0")){
                                    showToast("????????????");
                                }
                            },
                            throwable -> {
                                // Handle an error here.
                                Utils.log("notificationObservable: error" + throwable.getLocalizedMessage());
                            }
                    ));
        }
    }


    private void sendDataToSealAfterIllegal() {
        byte[] illegalBytes;
        illegalBytes = new byte[]{0};

        ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.ILLEGAL, illegalBytes).getBytes()))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        characteristicValue -> {
                            // Characteristic value confirmed.
                            Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                ));
    }

    /**
     * ???????????????????????????
     */
    private void sendIllegalToServer() {
        //????????????ID?????????
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealId", EasySP.init(getActivity()).getString("currentSealId"));
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.ILLEGAL, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("sendIllegalToServer():" + result);
            }
        });
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    private void sendIllegalRecordToServer() {
        uploadStampRecord(stampNumber + 1, System.currentTimeMillis() + "");
    }

    /***
     * ????????????????????????
     */
    private void setAdmin0() {
        String superManager = "ADMIN0";
        byte[] superManagerBytes = superManager.getBytes();
        ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, superManagerBytes))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        characteristicValue -> {
                            // Characteristic value confirmed.
                            // Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                ));
    }

    @Override
    public void onNetDisconnected() {
        Utils.log("onNetDisconnected");
        ((MyApp) getActivity().getApplication()).removeAllDisposable();
        ((MyApp) getApplication()).setConnectionObservable(null);
        stampTag = false;

        initWhenDisconnectBle();

    }

    /**
     * ????????????????????????
     */
    private void initWhenDisconnectBle() {
        currentStampTimes = 0;
//        tv_times_done.setText("0");
//        tv_times_left.setText("0");
        electric_ll.setVisibility(View.GONE);
        tv_ble_name.setText("??????????????????");
        //??????????????????????????????????????????????????????
        sealImg_iv.setImageDrawable(null);
        sealImg_iv.setBackgroundResource(R.drawable.seal_img);
//        tv_stamp_reason.setText("????????????????????????");
//        tv_expired_time.setText("??????");
        tv_address.setText("??????????????????");
        FloatingView.get().remove();
        startSeal = false;
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        Utils.log("onNetConnected");
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            currentLocation = location;
            Utils.log("888enableEnclosure:" + EasySP.init(getActivity()).getBoolean("enableEnclosure", false) + "");
            Utils.log("stampTag" + stampTag);
            if (stampTag && EasySP.init(getActivity()).getBoolean("enableEnclosure", false)) {
                // ???????????????????????????
                Utils.log("distance:" + "start");
                double distance = Utils.distanceOfTwoPoints(location.getLatitude(), location.getLongitude(), Double.parseDouble(EasySP.init(getActivity()).getString("latitude")), Double.parseDouble(EasySP.init(getActivity()).getString("longitude")));
                Utils.log("distance:" + distance + "");
                double scope = Double.parseDouble(EasySP.init(getActivity()).getString("scope"));

                if (distance * 1000 > scope) {
                    // ?????????????????????

                    Utils.log("distance > scope");
                    ((MyApp) getActivity().getApplication()).removeAllDisposable();
                    ((MyApp) getApplication()).setConnectionObservable(null);
                    stampTag = false;

                    initWhenDisconnectBle();
                    handler.removeCallbacksAndMessages(null);
                    showToast("????????????????????????????????????");
                    triggeredEnclosure();
                }
            } else {
                // ??????currentAddress
                getAddress(location.getLatitude(), location.getLongitude());
                Utils.log(location.getLatitude() + "");
            }

            mLocationClient.stop();
        }
    }

    /**
     * ??????
     */
    private void startLocate() {
        mLocationClient = new LocationClient(getActivity().getApplicationContext());     //??????LocationClient???
        mLocationClient.registerLocationListener(myListener);    //??????????????????
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//?????????????????????????????????????????????????????????????????????????????????
        option.setCoorType("bd09ll");//???????????????gcj02???????????????????????????????????????
        int span = 5000;
        option.setScanSpan(span);//???????????????0???????????????????????????????????????????????????????????????????????????1000ms???????????????
        option.setIsNeedAddress(true);//?????????????????????????????????????????????????????????
        option.setOpenGps(true);//???????????????false,??????????????????gps
        option.setLocationNotify(true);//???????????????false??????????????????GPS???????????????1S/1???????????????GPS??????
        option.setIsNeedLocationDescribe(true);//???????????????false??????????????????????????????????????????????????????BDLocation.getLocationDescribe?????????????????????????????????????????????????????????
        option.setIsNeedLocationPoiList(true);//???????????????false?????????????????????POI??????????????????BDLocation.getPoiList?????????
        option.setIgnoreKillProcess(false);//???????????????true?????????SDK???????????????SERVICE?????????????????????????????????????????????stop?????????????????????????????????????????????
        option.SetIgnoreCacheException(false);//???????????????false?????????????????????CRASH?????????????????????
        option.setEnableSimulateGps(false);//???????????????false???????????????????????????GPS???????????????????????????
        mLocationClient.setLocOption(option);
        //????????????
        mLocationClient.start();
    }

    /**
     * ???????????????????????????????????????
     *
     * @param latitude
     * @param longitude
     */
    public void getAddress(final double latitude, final double longitude) {
        GeoCoder geoCoder = GeoCoder.newInstance();
        final LatLng latLng = new LatLng(latitude, longitude);
        listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //?????????????????????
//                    Toast.makeText(GeographicalFenceActivity.this, "????????????", Toast.LENGTH_LONG).show();
                    return;
                }
                currentAddress = reverseGeoCodeResult.getAddress() + reverseGeoCodeResult.getSematicDescription();
                Utils.log(currentAddress);
                if (isConnect) {
                    tv_address.setText(currentAddress);
                }
            }
        };
        //????????????????????????????????????
        geoCoder.setOnGetGeoCodeResultListener(listener);
        //???????????????????????????
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        //??????
        geoCoder.destroy();
    }


    /**
     * ????????????
     */
    @SuppressLint("CheckResult")
    private void permissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //?????????????????????
        rxPermissions.requestEachCombined(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Utils.log("accept");
//                            startLocate();
                            startActivityForResult(intent, Constants.TO_NEARBY_DEVICE);
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("????????????????????????");
                        } else {
                            showToast("??????????????????????????????????????????>????????????>????????????????????????");
                        }
                    }
                });
    }

    /**
     * ??????????????????
     */
    private void triggeredEnclosure() {
        //????????????ID?????????
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealId", EasySP.init(getActivity()).getString("currentSealId"));
        hashMap.put("address", currentAddress);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.TRIGGERED_ENCLOSURE, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("triggeredEnclosure():" + result);
            }
        });
    }


    private void compressAndUpload(File fileByUri) {
        Luban.with(getActivity())
                .load(fileByUri)
                .ignoreBy(100)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO ???????????????????????????????????????????????? loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO ??????????????????????????????????????????????????????
                        //????????????
                        uploadPic(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO ????????????????????????????????????
                        Log.e("TAG", e + "");
                    }
                }).launch();

    }

    /**
     * ???????????????????????? (category -> 5:??????????????????  4:????????????????????????)????????????????????????
     *
     * @param file
     */
    private void uploadPic(File file) {
//        loadingView.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        Utils.log(file.length() + "");
        hashMap.put("category", 5);
        hashMap.put("file", file);
        HttpUtil httpUtil = new HttpUtil(getActivity());
        httpUtil.upLoadFile(getActivity(), HttpUrl.UPLOADIMAGE, hashMap, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                Utils.log(result.toString());
                Gson gson = new Gson();
                final ResponseInfo<LoadImageData> responseInfo = gson.fromJson(result.toString(), new TypeToken<ResponseInfo<LoadImageData>>() {
                }.getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    String str = responseInfo.getData().getFileName();
                    if (picsList == null) {
                        picsList = new ArrayList<>();
                    }
                    picsList.add(str);
                    loadingView.cancel();
                    Log.e("TAG", "??????????????????!!!!!!!!!!!!!!!!!!!!");
                    Utils.log("??????????????????");

//                    totalPage = allFileName.size() / 9;
//                    currentPage = totalPage;

                    // ??????????????????
//                    page.setText((currentPage + 1) + "????????????" + (totalPage + 1) + "??????");

//                    showCurrentPagePic();

//                    // ????????????
//                    permissions();

                    if ((allPic.size() - uploadPicIndex - 1) != 0) {
                        String tipString = "????????????" + (uploadPicIndex + 1) + "???????????????" + (allPic.size() - uploadPicIndex - 1) + "???";
                        Toast.makeText(getActivity(), tipString, Toast.LENGTH_SHORT).show();
                    }

                    // uploadPicIndex??????
                    uploadPicIndex++;
                    // ???????????????uploadPicIndex???????????????????????????
                    if (uploadPicIndex < allPic.size()) {
                        File file = new File(allPic.get(uploadPicIndex));
                        compressAndUpload(file);
                    }

                    if (uploadPicIndex == allPic.size()) {
                        Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
                    }

                    // ?????????????????????????????????
                    if (uploadPicIndex == allPic.size()) {
                        uploadRecord();
                    }

//                    success = true;
                } else {
                    loadingView.cancel();
//                    success = false;
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                loadingView.cancel();
//                success = false;
                showToast(errorMsg);
                Utils.log(errorMsg);
                Log.e("TAG", "??????????????????????????????........");
            }
        });
    }


    /**
     * ??????????????????
     */
    private void uploadRecord() {
//        loadingView.show();
        Utils.log("causeId" + causeId);
        AddUseSealApplyBean bean = new AddUseSealApplyBean();
        bean.setApplyId(causeId);
        bean.setImgList(picsList);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.UPDATESTAMPIMAGE, 2, null, bean, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                loadingView.cancel();
                Log.e("TAG", e + "????????????????????????????????????!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("??????????????????result:" + result);
                picsList.clear();
                allPic.clear();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
//                if (responseInfo.getCode() == 0) {
//                    if (responseInfo.getData()) {
//                        loadingView.cancel();
//                        if (type == 321){      //?????????????????????????????????????????????????????????
//                            Intent intent = new Intent(geta.this,MyApplyActivity.class);
//                            intent.putExtra("id", id);
//                            setResult(222);
//                            startActivity(intent);
//                        }else {
//                            setResult(222);
//                        }
//                        finish();
//                        Looper.prepare();
//                        showToast("????????????");
//                        Looper.loop();
//                    }
//                } else {
//                    loadingView.cancel();
//                    Looper.prepare();
//                    showToast(responseInfo.getMessage());
//                    Looper.loop();
//                }
            }
        });
    }


    @SuppressLint("CheckResult")
    private void lockSeal() {
        // ???????????????????????????
        byte[] select_press_time = new byte[]{0};
        ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.LOCK, select_press_time).getBytes()))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        characteristicValue -> {
                            // Characteristic value confirmed.
                            Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                ));
    }


    private void tooFastTip() {
        final CommonDialog commonDialog = new CommonDialog(getActivity(), "????????????", "????????????????????????????????????????????????????????????", "??????");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dialog.dismiss();
            }
        });
    }


    private void goToDfuPage() {
        final CustomDialog commonDialog = new CustomDialog(getActivity(), "??????", "??????????????????????????????", "??????");
        commonDialog.cancel.setText("??????");
        commonDialog.dialog.setCancelable(false);
        commonDialog.showDialog();
        commonDialog.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("rihgt");
                // confirm
                startActivity(new Intent(getActivity(), DfuActivity.class));
                commonDialog.dialog.dismiss();
            }
        });
        commonDialog.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("left");
                // cancel
                // ????????????
                ((MyApp) getActivity().getApplication()).removeAllDisposable();
                ((MyApp) getApplication()).setConnectionObservable(null);
                commonDialog.dialog.dismiss();
            }
        });

    }
}
