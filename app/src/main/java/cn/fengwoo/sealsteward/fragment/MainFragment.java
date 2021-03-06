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
    //设置图片集合
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
    ImageView sealImg_iv;  //印模
    @BindView(R.id.company)
    TextView company_name;
    LoadingView loadingView;
    private RxBleConnection rxBleConnection;
    private String availableCount = "0"; // 剩余次数
    private String stampReason = ""; // 盖章事由
    private int startNumber; // 启动序号
    private int currentStampTimes = 0; // 现在盖章次数
    /**
     * 百度定位获取
     */
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private BDLocation currentLocation;
    private String currentAddress = "";
    private OnGetGeoCoderResultListener listener;
    private int unuploadedQuantity; // 未上传的盖章记录数量
    private UploadHistoryRecord mUploadHistoryRecord = new UploadHistoryRecord();
    private List<UploadHistoryRecord.RecordListBean> recordListBeanList = new ArrayList<>();
    private String stampTime; // 盖章时间
    private int stampSeqNumber; // 盖章序号
    private int stampType; // 启动类型
    private int userNumber; // 启动序号(密码代码）
    private String systemTimeStampString;
    private String waitId;
    private boolean stampTag = false;
    private boolean isConnect = false; // 是否连接蓝牙
    private RxTimerUtil rxTimerUtil;
    private int stampNumber;
    private List<String> picsList;
    private int uploadPicIndex = 0;
    private List<String> allPic;
    private String causeId = ""; // 事由id
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
//                    // 如果盖完了，次数为0时，断开蓝牙
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

        //初始化
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

        //设置banner控件为圆角
        ViewStyleSetter viewStyleSetter = new ViewStyleSetter(banner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewStyleSetter.setRound(20);//圆角弧度
        }
    }


    //悬浮窗点击事件
    private MagnetViewListener magnetViewListener = new MagnetViewListener() {
        @Override
        public void onRemove(FloatingMagnetView magnetView) {

        }

        @Override
        public void onClick(FloatingMagnetView magnetView) {
            if (startSeal){
                showToast("印章已启动,请勿重复启动");
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
     * 广告轮播图
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
                Log.e("ATG", "获取广告图失败!!!!!!!!!!!!!!");
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
                    Log.e("ATG", "获取广告图成功!!!!!!!!!!!!!!");
                    imageViews.clear();
                    int count = responseInfo.getData().size();   //banner图片总数
                    //设置图片集合
                    for (int i = 0; i < count; i++) {
                        String imgName = responseInfo.getData().get(i).getImageFile();
                        //先从本地缓存中读取
                        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(imgName);
                        if (bitmap != null) {
                            imageViews.add("file://" + HttpDownloader.path + imgName);
                        } else {
                            //没有则下载
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
                    Log.e("ATG", "获取广告图失败!!!!!!!!!!!!!!");
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
                    //banner设置方法全部调用完毕时最后调用
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
                    ToastUtils.show("蓝牙没有打开，请开启蓝牙");
                    return;
                }

                // 断开蓝牙
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
                        showToast("请确保开启了定位服务");
                        return;
                    }

                    // 断开蓝牙
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
                    showToast("您暂无公司，请添加公司或者加入其他公司后重试");
                }
                break;
            case R.id.useSealApply_rl:
                if (Utils.isHaveCompanyId(getActivity())) {
                    EasySP.init(getActivity()).putString("currentSealName", "");
                    intent = new Intent(getActivity(), ApplyUseSealActivity.class);
                    startActivity(intent);
                } else {
                    showToast("您暂无公司，请添加公司或者加入其他公司后重试");
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
                    showToast("您暂无公司，请添加公司或者加入其他公司后重试");
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
     * 查看记录
     */
    private void getApplyDetail() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", EasySP.init(getActivity()).getString("currentApplyId"));
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.APPLYDETAIL, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "查看详情错误错误!!!!!!!!!!!!!!!!!!!");
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
//                    intent.putExtra("status", status);    //传递状态值弹出不同的popuwindow
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
                    Toast.makeText(getActivity(), "暂无盖章", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });

    }

    private void getMessage() {
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.MESSAGE, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
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
                                    //显示消息数
                                    int type = messageData.getType();
                                    int msgNum = messageData.getUnreadCount();
                                    String id = messageData.getId();
                                    if (type == 4) {
                                        waitId = id;
                                        if (msgNum != 0) {
                                            msg_num_tv.setVisibility(View.VISIBLE);    //待我审批
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
                    Log.e("TAG", "获取消息成功!!!!!!!!!!!!!!!!");
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
     * 增加体验
     */
    @Override
    public void onStart() {
        super.onStart();
        Utils.log("00000 00000 onStart");

//        EventBus.getDefault().register(this);
        //开始轮播
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
        //结束轮播
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
        //销毁悬浮窗
        FloatingView.get().remove();
    }

    /**
     * 处理注册事件
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        if (s.equals("待我审批消息")) {
            getMessage();
        }
        if (s.equals("connect_seal")) {
            intent = new Intent(getActivity(), NearbyDeviceActivity.class);
            intent.putExtra("isAddNewSeal", false);
            intent.putExtra("应用模块连上就finish", "应用模块连上就finish");
            permissions();
        }
        if (s.equals("切换公司")) {
            company_name.setText(CommonUtil.getUserData(getActivity()).getCompanyName());
        }
    }

    private void getSystemTime() {
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.SYSTEM_TIME, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误!!!!!!!!!!!!!!!!!!!");
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
                Log.e("TAG", e + "错误错误!!!!!!!!!!!!!!!!!!!");
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
                            showToast("有最新固件，请升级。");
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
                // 断开蓝牙
                ((MyApp) getApplication()).removeAllDisposable();
                ((MyApp) getApplication()).setConnectionObservable(null);

                allPic = new ArrayList<>();

                if (data == null) {
                    return;
                }

                allPic = data.getStringArrayListExtra("photoList");
                Utils.log("strList.size()" + allPic.size());

                // 上传照片
                // 从array里一个个拿数据出来上传
                // init index
                uploadPicIndex = 0;

                // 先上第一张
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

                // 显示ble设备名字
                bleName = data.getStringExtra("bleName");
                //应用模块点击选择的印章之后传递过来
                String applicationConnect = data.getStringExtra("applicationConnect");
                //显示印模
                String sealPrint = data.getStringExtra("sealPrint");

                //显示悬浮窗
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
//                // 开启定位
//                permissions();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // 获取系统时间
                        // 获取成功后setNotification();
                        // setNotification();前获取系统时间
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
                        // 跳转到 启动印章 页面。。。。    是否是应用模块进入
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
                    showToast("未能获取到定位信息，请确保打开了定位");
                    // 断开蓝牙
                    ((MyApp) getActivity().getApplication()).removeAllDisposable();
                    ((MyApp) getApplication()).setConnectionObservable(null);
                    return;
                }

                if (tv_address.getText().toString().trim().equals("暂无定位信息")) {
                    showToast("未能获取到定位信息，请确保打开了定位");
                    // 断开蓝牙
                    ((MyApp) getActivity().getApplication()).removeAllDisposable();
                    ((MyApp) getApplication()).setConnectionObservable(null);
                    return;
                }

                stampTag = true;

                // 再次开启定位
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

                // 获取 拍照 方式
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

                // 初始化首页的已盖次数和剩余次数
                currentStampTimes = 0;
                tv_times_done.setText(currentStampTimes + "");
                tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");

                Utils.log(tv_times_done.getText().toString());

                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {

                    // 三期
                    // 启动印章
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
     * 悬浮窗
     */
    public void openFloatingView(int code){
        //显示悬浮窗
//        FloatingView.get().add();     //暂时取消悬浮窗功能
        //获取屏幕宽度
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        //区别MainActivity和MainFragment的显示
        if (code == 1){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(width-170,900,0,0);
            //        layoutParams.gravity = 200;
            //设置悬浮窗
            FloatingView.get()
                    .layoutParams(layoutParams)
                    .icon(R.drawable.seal_hover_button)
                    .listener(magnetViewListener);
        }else if (code == 2){
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(width-170,900,0,0);
            //        layoutParams.gravity = 200;
            //设置悬浮窗
            FloatingView.get()
                    .layoutParams(layoutParams)
                    .icon(R.drawable.seal_hover_button)
                    .listener(magnetViewListener);
        }

    }

    /**
     * 启动
     */
    private void startPost(String startTime){
        StampUploadRecordData stampUploadRecordData = new StampUploadRecordData();
        stampUploadRecordData.setSealId(EasySP.init(getActivity()).getString("currentSealId"));  //印章ID
        stampUploadRecordData.setApplyId(EasySP.init(getActivity()).getString("currentApplyId")); //申请单据ID
        stampUploadRecordData.setStampUser(CommonUtil.getUserData(getActivity()).getId());   //盖章人ID
        stampUploadRecordData.setStartTime(startTime);   //启动时间
        stampUploadRecordData.setStartNo(String.valueOf(startNumber)); //启动序号
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
                //{"code":0,"message":"成功","data":true,"count":0}
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
                    //   {"code":0,"message":"成功","data":{"result":true,"applyClosed":false}}
                    JSONObject jsonObject = new JSONObject(result);
                    String dataString = jsonObject.getString("data");
                    JSONObject jsonObject1 = new JSONObject(dataString);
                    Boolean applyClosed = jsonObject1.getBoolean("applyClosed");
                    //如果单据关闭再次盖章时直接断开蓝牙
                    if (applyClosed) {
                        // 断开蓝牙
                        ((MyApp) getActivity().getApplication()).removeAllDisposable();
                        ((MyApp) getApplication()).setConnectionObservable(null);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonDialog commonDialog = new CommonDialog(getActivity(), "您当前使用的单据已被关闭", "", "确定");
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
//                        // 如果盖完了，次数为0时，断开蓝牙
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
     * 同步印章名称
     */
    private void syncSealName(String bleName) {
        if(bleName != null && !bleName.equals("") && !bleName.contains("BHQKL") && !bleName.contains("baihe")){
            // 判断印章名称长度，最大支持24个汉字
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
                                    Utils.log("设置印章名称->" + Utils.bytesToHexString(characteristicValue));
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
        byte[] targetNumber = new byte[]{1}; // 一条
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

    // 擦除一条历史记录
    private void deleteRecord() {
        byte[] targetNumber = new byte[]{1}; // 一条
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

        // 如果盖完了，次数为0时，断开蓝牙
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
                // 开启定位
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
                            showToast("蓝牙已断开");

                            ((MyApp) getActivity().getApplication()).removeAllDisposable();
                            ((MyApp) getApplication()).setConnectionObservable(null);
                            stampTag = false;

                            initWhenDisconnectBle();
                            // 停止timer
                            rxTimerUtil.cancel();
                            // 清空currentSealId
                            EasySP.init(getActivity()).putString("currentSealId", "");
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );


        if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {

            // 三期
            // 握手

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
                            // Notification has been set up ，监听设置成功，然后握手
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
                                    String result = Utils.bytesToHexString(bytes); //接收的结果
                                    Utils.log("notificationObservable:" + Utils.bytesToHexString(bytes));

                                    if (Utils.bytesToHexString(bytes).startsWith("FF 05 A0 00")) {
                                        Utils.log("握手成功");
                                        // 得到当前版本
                                        firmwareVersion = (float) bytes[4] + ((float) bytes[5] / 100);
                                        Utils.log("firmwareVersion:" + firmwareVersion);
                                        checkDfu(firmwareVersion);
                                        // 每隔1min定时获取电量
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

                                        // 未上传的盖章记录数量
                                        byte[] unuploaded = DataTrans.subByte(bytes, 6, 2);
                                        unuploadedQuantity = DataTrans.bytesToInt(unuploaded, 0);
                                        // 未上传的盖章记录数量不为0，开始读记录
                                        if (unuploadedQuantity != 0) {
                                            readRecord();
                                        }
                                        // 同步印章名称
                                        syncSealName(bleName);

                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 AF")) {
                                        String batteryString = Utils.bytesToHexString(bytes).substring(9, 11);
                                        int batteryInt = Integer.parseInt(batteryString, 16);
                                        Utils.log("batteryInt:" + batteryInt);
                                        // 刷新ui,赋值电量
                                        electric_ll.setVisibility(View.VISIBLE);
                                        tv_battery.setText(String.valueOf(batteryInt + "%"));
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 08 A2")) {
                                        // 印章主动上报消息通知手机发生盖章行为
                                        // 盖章序号

                                        if (lastTime != null) {
                                            Long intervalTime = System.currentTimeMillis() - lastTime;
                                            if (intervalTime < 200) {
                                                vibrator.vibrate(3000);
                                                tooFastTip();
                                                // 锁定印章
                                                lockSeal();
                                            }
                                        }

                                        lastTime = System.currentTimeMillis();

                                        String allString = Utils.bytesToHexString(bytes);
//                                        String stampNumberHexString = allString.substring(9, 11) + allString.substring(6, 8);
                                        String stampNumberHexString = allString.substring(12, 14) + allString.substring(9, 11);
                                        stampNumber = Integer.valueOf(stampNumberHexString, 16);
                                        // 盖章时间
                                        String timeHexString = allString.substring(15, 32);
                                        String timeStamp = DateUtils.hexTimeToTimeStamp(timeHexString);
                                        Utils.log(timeStamp);
                                        refreshTimes();
                                        uploadStampRecord(stampNumber, timeStamp);
                                        Utils.log("stampNumber" + stampNumber + "   " + "timeStamp" + timeStamp);
                                        EventBus.getDefault().post(new MessageEvent("take_pic", "take_pic"));
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 05 A1 00")) {
                                        // 启动印章成功后，获取“启动序号”
                                        byte[] restTime = DataTrans.subByte(bytes, 4, 4);
                                        startNumber = DataTrans.bytesToInt(restTime, 0);
                                        Utils.log("startNumber" + startNumber);
                                        startSeal = true;
                                        //获取当前时间
                                        Date time = new Date(System.currentTimeMillis());
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String startTime = dateFormat.format(time);
                                        //时间转时间戳
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
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 B1 00 ")) {  //编辑密码用户权限
                                        EventBus.getDefault().post(new MessageEvent("ble_change_stamp_count", "success"));
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 B2 00 ")) {
                                        EventBus.getDefault().post(new MessageEvent("ble_delete_pwd_user", "success"));
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 A5 00 ")) {
                                        EventBus.getDefault().post(new MessageEvent("ble_reset", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 07 AA 01")){
                                        Log.e("TAG","第一次录制成功!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("first_fingerprint", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 07 AA 02")){
                                        Log.e("TAG","第二次录制成功!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("second_fingerprint", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 07 AA 03")){
                                        Log.e("TAG","第三次录制成功!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("third_fingerprint", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 07 AA 04")){
                                        Log.e("TAG","指纹录制成功!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("result", result));
                                        EventBus.getDefault().post(new MessageEvent("bytes", bytes));
                                        EventBus.getDefault().post(new MessageEvent("add_fingerprint", "success"));
                                    }
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 01 AB 00")){
                                        //删除指纹成功
                                        Log.e("TAG","删除指纹成功!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("delete_fingerprint","success"));
                                    }else if(Utils.bytesToHexString(bytes).startsWith("FF 01 AC 00")){
                                        //编辑更新指纹权限
                                        Log.e("TAG","编辑更新指纹权限成功!!!!!!!!!!!!!!!!!!!");
                                        EventBus.getDefault().post(new MessageEvent("edit_update_fingerprint","success"));
                                    }
                                    // 违规盖章
                                    else if (Utils.bytesToHexString(bytes).startsWith("FF 06 A8 80")) {
                                        // 发送a8给seal
                                        sendDataToSealAfterIllegal();

                                        // 告诉后台有非法盖章
                                        sendIllegalToServer();

                                        if (firmwareVersion <= 3.02) {
                                            // 盖章次数加一
                                            refreshTimes();
                                            // 把这次非法盖章的盖章记录也上传到后台
                                            sendIllegalRecordToServer();
                                        }
                                    } else if (Utils.bytesToHexString(bytes).startsWith("FF 0E A3")) {
                                        // 成功读到一条记录
                                        unuploadedQuantity--; // 未上传的盖章记录数量减一
                                        Utils.log("unuploadedQuantity:" + unuploadedQuantity);
                                        // 解析数据，存入对象

                                        // byte[3] 记录序号 暂时没用
                                        byte[] recordSeq = DataTrans.subByte(bytes, 3, 1);
                                        int recordSeqInt = DataTrans.bytesToInt(recordSeq, 0);
                                        Utils.log("recordSeqInt" + recordSeqInt);

                                        // byte[4] 启动类型（stampType）
                                        byte[] stampTypeBytes = DataTrans.subByte(bytes, 4, 1);
                                        stampType = DataTrans.bytesToInt(stampTypeBytes, 0);


                                        // byte[5] - byte[6] 盖章序号（stampSeqNumber）
                                        byte[] stampSeqNumberBytes = DataTrans.subByte(bytes, 5, 2);
                                        stampSeqNumber = DataTrans.bytesToInt(stampSeqNumberBytes, 0);

                                        // byte[7] - byte[10] 启动序号（stampSeqNumber）
                                        byte[] userNumberBytes = DataTrans.subByte(bytes, 7, 4);
                                        userNumber = DataTrans.bytesToInt(userNumberBytes, 0);

                                        // byte[11] - byte[16] 盖章时间（stampTime）
//                                    byte[] stampTimeBytes = DataTrans.subByte(bytes, 11, 6);
                                        String stampTimeHex = Utils.bytesToHexString(bytes).substring(33, 50);
                                        stampTime = DateUtils.hexTimeToTimeStamp(stampTimeHex);

                                        // 创建一次盖章记录
                                        UploadHistoryRecord.RecordListBean recordListBean = new UploadHistoryRecord.RecordListBean();
                                        recordListBean.setStampType(stampType);
                                        recordListBean.setUserNumber(userNumber);
                                        recordListBean.setStampSeqNumber(stampSeqNumber);
                                        recordListBean.setStampTime(stampTime);
                                        recordListBeanList.add(recordListBean);

                                        // 准备抹掉一条
                                        deleteRecord();

                                        if (unuploadedQuantity != 0) {
                                            // 还有未上传的记录
                                            // 继续读记录
                                            readRecord();
                                        } else {
                                            // unuploadedQuantity == 0; 此时没有未上传的记录
                                            // 数据上传服务器
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

            // 二期
            // 握手

            String shakeHandString = "HandShake/2yK39b";
            byte[] shakeHandBytes = shakeHandString.getBytes();
            ((MyApp) getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                    .flatMap(rxBleConnection -> rxBleConnection.setupNotification(Constants.NOTIFY_UUID))
//                            .doOnNext(rxBleConnection-> this.rxBleConnection = rxBleConnection)
                    .doOnNext(notificationObservable -> {
                        // Notification has been set up ，监听设置成功，然后握手
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

                                // 握手成功，然后发送“用户识别”命令，设置为超级管理员
                                if (strReturned.equals("H1")) {
                                    Utils.log("握手成功");

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
                                    // 已识别为超级管理员
                                    Utils.log("已识别为超级管理员");
                                } else if (strReturned.equals("U1")) {
                                    // 启动成功
                                    Utils.log("启动成功");
                                    // 二期设备中加入super，用来区别三期
                                    EventBus.getDefault().post(new MessageEvent("super_ble_launch", "success"));
                                } else if (strReturned.equals("U0")) {
                                    // 启动失败
                                    Utils.log("启动失败");
                                    // 二期设备中加入super，用来区别三期
                                    EventBus.getDefault().post(new MessageEvent("super_ble_launch", "failure"));
                                } else if (strReturned.equals("C1") || strReturned.equals("o1")) {
                                    // 盖章次数加一
//                                    Utils.log("盖章次数加一");
//                                    currentStampTimes++;
//                                    tv_times_done.setText(currentStampTimes + "");
//                                    tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");

                                    refreshTimes();
                                    uploadStampRecord(0, null);
                                    EventBus.getDefault().post(new MessageEvent("take_pic", "take_pic"));
                                    // 如果是o1加上，告诉后台有非法盖章
                                    if (strReturned.equals("o1")) {
                                        sendIllegalToServer();
                                    }
                                }else if (strReturned.equals("K1")){
                                    showToast("修改成功");
                                }else if (strReturned.equals("K0")){
                                    showToast("修改失败");
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
     * 告诉后台有非法盖章
     */
    private void sendIllegalToServer() {
        //添加用户ID为参数
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
     * 把这次非法盖章的盖章记录也上传到后台
     */
    private void sendIllegalRecordToServer() {
        uploadStampRecord(stampNumber + 1, System.currentTimeMillis() + "");
    }

    /***
     * 已识别为普通用户
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
     * 断开连接恢复显示
     */
    private void initWhenDisconnectBle() {
        currentStampTimes = 0;
//        tv_times_done.setText("0");
//        tv_times_left.setText("0");
        electric_ll.setVisibility(View.GONE);
        tv_ble_name.setText("暂未连接印章");
        //断开蓝牙时候清空图片再放入初始化图片
        sealImg_iv.setImageDrawable(null);
        sealImg_iv.setBackgroundResource(R.drawable.seal_img);
//        tv_stamp_reason.setText("暂无用印申请事由");
//        tv_expired_time.setText("暂无");
        tv_address.setText("暂无定位信息");
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
                // 正在盖章，计算距离
                Utils.log("distance:" + "start");
                double distance = Utils.distanceOfTwoPoints(location.getLatitude(), location.getLongitude(), Double.parseDouble(EasySP.init(getActivity()).getString("latitude")), Double.parseDouble(EasySP.init(getActivity()).getString("longitude")));
                Utils.log("distance:" + distance + "");
                double scope = Double.parseDouble(EasySP.init(getActivity()).getString("scope"));

                if (distance * 1000 > scope) {
                    // 大于半径，断开

                    Utils.log("distance > scope");
                    ((MyApp) getActivity().getApplication()).removeAllDisposable();
                    ((MyApp) getApplication()).setConnectionObservable(null);
                    stampTag = false;

                    initWhenDisconnectBle();
                    handler.removeCallbacksAndMessages(null);
                    showToast("超出地理围栏，设备已断开");
                    triggeredEnclosure();
                }
            } else {
                // 获取currentAddress
                getAddress(location.getLatitude(), location.getLongitude());
                Utils.log(location.getLatitude() + "");
            }

            mLocationClient.stop();
        }
    }

    /**
     * 定位
     */
    private void startLocate() {
        mLocationClient = new LocationClient(getActivity().getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 5000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        //开启定位
        mLocationClient.start();
    }

    /**
     * 根据经纬度反编译定位更精准
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
                    //没有检索到结果
//                    Toast.makeText(GeographicalFenceActivity.this, "定位失败", Toast.LENGTH_LONG).show();
                    return;
                }
                currentAddress = reverseGeoCodeResult.getAddress() + reverseGeoCodeResult.getSematicDescription();
                Utils.log(currentAddress);
                if (isConnect) {
                    tv_address.setText(currentAddress);
                }
            }
        };
        //设置逆地理编码检索监听器
        geoCoder.setOnGetGeoCodeResultListener(listener);
        //发起逆地理编码检索
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        //释放
        geoCoder.destroy();
    }


    /**
     * 申请权限
     */
    @SuppressLint("CheckResult")
    private void permissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //添加需要的权限
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
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });
    }

    /**
     * 超出围栏报警
     */
    private void triggeredEnclosure() {
        //添加用户ID为参数
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
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        //上传图片
                        uploadPic(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        Log.e("TAG", e + "");
                    }
                }).launch();

    }

    /**
     * 上传图片至服务器 (category -> 5:上传记录图片  4:申请用印上传图片)（单独上传图片）
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
                    Log.e("TAG", "上传图片成功!!!!!!!!!!!!!!!!!!!!");
                    Utils.log("上传图片成功");

//                    totalPage = allFileName.size() / 9;
//                    currentPage = totalPage;

                    // 显示当前页面
//                    page.setText((currentPage + 1) + "页（总共" + (totalPage + 1) + "页）");

//                    showCurrentPagePic();

//                    // 弹出相机
//                    permissions();

                    if ((allPic.size() - uploadPicIndex - 1) != 0) {
                        String tipString = "已上传：" + (uploadPicIndex + 1) + "张，还剩：" + (allPic.size() - uploadPicIndex - 1) + "张";
                        Toast.makeText(getActivity(), tipString, Toast.LENGTH_SHORT).show();
                    }

                    // uploadPicIndex加一
                    uploadPicIndex++;
                    // 继续上传（uploadPicIndex不能大于数组大小）
                    if (uploadPicIndex < allPic.size()) {
                        File file = new File(allPic.get(uploadPicIndex));
                        compressAndUpload(file);
                    }

                    if (uploadPicIndex == allPic.size()) {
                        Toast.makeText(getActivity(), "图片上传完毕", Toast.LENGTH_SHORT).show();
                    }

                    // 上传最后一张图片后提交
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
                Log.e("TAG", "发送图片至服务器失败........");
            }
        });
    }


    /**
     * 记录（提交）
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
                Log.e("TAG", e + "上传记录照片错误错误错误!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("记录（提交）result:" + result);
                picsList.clear();
                allPic.clear();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
//                if (responseInfo.getCode() == 0) {
//                    if (responseInfo.getData()) {
//                        loadingView.cancel();
//                        if (type == 321){      //区别是主页记录进入还是应用里的记录进入
//                            Intent intent = new Intent(geta.this,MyApplyActivity.class);
//                            intent.putExtra("id", id);
//                            setResult(222);
//                            startActivity(intent);
//                        }else {
//                            setResult(222);
//                        }
//                        finish();
//                        Looper.prepare();
//                        showToast("上传成功");
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
        // 发送查询：长按时间
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
        final CommonDialog commonDialog = new CommonDialog(getActivity(), "非法盖章", "您盖得太快了，有点跟不上，印章已被锁定！", "确定");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dialog.dismiss();
            }
        });
    }


    private void goToDfuPage() {
        final CustomDialog commonDialog = new CustomDialog(getActivity(), "提示", "有最新固件，请升级。", "确定");
        commonDialog.cancel.setText("取消");
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
                // 断开蓝牙
                ((MyApp) getActivity().getApplication()).removeAllDisposable();
                ((MyApp) getApplication()).setConnectionObservable(null);
                commonDialog.dialog.dismiss();
            }
        });

    }
}
