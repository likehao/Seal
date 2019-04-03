package cn.fengwoo.sealsteward.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polidea.rxandroidble2.RxBleConnection;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tianma.netdetector.lib.NetStateChangeObserver;
import com.tianma.netdetector.lib.NetStateChangeReceiver;
import com.tianma.netdetector.lib.NetworkType;
import com.white.easysp.EasySP;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.ApplyCauseActivity;
import cn.fengwoo.sealsteward.activity.ApprovalRecordActivity;
import cn.fengwoo.sealsteward.activity.MyApplyActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.bean.UploadHistoryRecord;
import cn.fengwoo.sealsteward.entity.BannerData;
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
import cn.fengwoo.sealsteward.utils.NetUtil;
import cn.fengwoo.sealsteward.utils.RxTimerUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.mob.tools.utils.DeviceHelper.getApplication;

public class MainFragment extends Fragment implements View.OnClickListener ,NetStateChangeObserver {

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

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 66:
                    tv_times_done.setText(currentStampTimes + "");
                    Utils.log(String.valueOf("******************************************"));
                    tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");
                    // 如果盖完了，次数为0时，断开蓝牙
                    if (tv_times_left.getText().toString().trim().equals("0")) {
//                            ((MyApp) getActivity().getApplication()).getConnectDisposable().dispose();
                        ((MyApp) getActivity().getApplication()).removeAllDisposable();
                        ((MyApp) getApplication()).setConnectionObservable(null);
                        currentStampTimes = 0;
                    }
                    break;
            }
            return false;
        }
    });

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //初始化
        SDKInitializer.initialize(getActivity().getApplicationContext());
        view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        ButterKnife.bind(this, view);
        initView();
        initBanner();
        setListener();
//        permissions();
        return view;
    }

    private void initView() {
        loadingView = new LoadingView(getActivity());
        //  title_tv.setText(CommonUtil.getUserData(getActivity()).getCompanyName());
    }

    private void setListener() {
        sealDevice_rl.setOnClickListener(this);
        needSeal_rl.setOnClickListener(this);
        useSealApply_rl.setOnClickListener(this);
        approval_record_rl.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 广告轮播图
     */
    private void initBanner() {
        loadingView.show();
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.BANNER, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                List<Integer> imgList = new ArrayList<Integer>();
                imgList.add(R.drawable.default_banner);
                loadBanner(imgList);
                Looper.prepare();
                Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @SuppressLint("SdCardPath")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<BannerData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<BannerData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    Log.e("ATG", "获取广告图成功!!!!!!!!!!!!!!");
                    loadingView.cancel();
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
                            HttpDownloader.downloadImage(getActivity(), 8, imgName, new DownloadImageCallback() {
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
                    loadingView.cancel();
                    Log.e("ATG", "获取广告图失败!!!!!!!!!!!!!!");
                    Looper.prepare();
                    Toast.makeText(getActivity(), responseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }

            }
        });
    }

    private void loadBanner(List<?> list) {
        banner.setImageLoader(new GlideImageLoader());
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                banner.setImages(list);
                //banner设置方法全部调用完毕时最后调用
                banner.start();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sealDevice_rl:
                intent = new Intent(getActivity(), NearbyDeviceActivity.class);
                intent.putExtra("isAddNewSeal", false);

                startActivityForResult(intent, Constants.TO_NEARBY_DEVICE);
                break;
            case R.id.needSeal_rl:
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                intent = new Intent(getActivity(), ApplyCauseActivity.class);
                startActivityForResult(intent, Constants.TO_WANT_SEAL);
                break;
            case R.id.useSealApply_rl:
                intent = new Intent(getActivity(), MyApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.approval_record_rl:
                intent = new Intent(getActivity(), ApprovalRecordActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 增加体验
     */
    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
        NetUtil.registerNetConnChangedReceiver(getActivity());
        NetUtil.addNetConnChangedListener(netConnChangedListener);
        NetStateChangeReceiver.registerObserver(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
        NetUtil.unregisterNetConnChangedReceiver(getActivity());
        NetUtil.removeNetConnChangedListener(netConnChangedListener);
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
                systemTimeStampString = responseInfo.getData();
                Utils.log("timeStampString" + systemTimeStampString);
                setNotification();
            }
        });


    }


    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.TO_NEARBY_DEVICE:
                if (resultCode != Constants.TO_NEARBY_DEVICE) {
                    return;
                }

                // 显示ble设备名字
                String bleName = data.getStringExtra("bleName");
                tv_ble_name.setText(bleName);

                // 开启定位
                permissions();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // 获取系统时间
                        // 获取成功后setNotification();
                        getSystemTime();

                    }
                }, 3000);

                break;


            case Constants.TO_WANT_SEAL:
                if (resultCode != Constants.TO_WANT_SEAL) {
                    return;
                }
                String expireTime = data.getStringExtra("expireTime");
                availableCount = data.getStringExtra("availableCount");
                stampReason = data.getStringExtra("stampReason");
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
                Utils.log(result);
                currentStampTimes++;
                Utils.log(String.valueOf(currentStampTimes));
//                handler.sendEmptyMessage(66);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_times_done.setText(currentStampTimes + "");
                        Utils.log(String.valueOf("******************************************" + tv_times_done.getText()));
//                        showToast("asdf");
                        tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");
                        // 如果盖完了，次数为0时，断开蓝牙
                        if (tv_times_left.getText().toString().trim().equals("0")) {
//                            ((MyApp) getActivity().getApplication()).getConnectDisposable().dispose();
                            ((MyApp) getActivity().getApplication()).removeAllDisposable();
                            ((MyApp) getApplication()).setConnectionObservable(null);
                            currentStampTimes = 0;
                        }
                    }
                });
            }
        });
    }


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

    @SuppressLint("CheckResult")
    private void setNotification() {
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
                                Utils.log("notificationObservable:" + Utils.bytesToHexString(bytes));

                                if (Utils.bytesToHexString(bytes).startsWith("FF 05 A0 00")) {
                                    Utils.log("握手成功");
                                    // 每隔1min定时获取电量
                                    new RxTimerUtil().interval(60000, new RxTimerUtil.IRxNext() {
                                        @Override
                                        public void doNext(long number) {
                                            Utils.log("a loop");
                                            if (((MyApp) getActivity().getApplication()).getConnectionObservable() != null) {
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

                                } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 AF")) {
                                    String batteryString = Utils.bytesToHexString(bytes).substring(9, 11);
                                    int batteryInt = Integer.parseInt(batteryString, 16);
                                    Utils.log("batteryInt:" + batteryInt);
                                    // 刷新ui,赋值电量
                                    electric_ll.setVisibility(View.VISIBLE);
                                    tv_battery.setText(String.valueOf(batteryInt));
                                } else if (Utils.bytesToHexString(bytes).startsWith("FF 08 A2")) {
                                    // 印章主动上报消息通知手机发生盖章行为
                                    // 盖章序号
                                    String allString = Utils.bytesToHexString(bytes);
                                    String stampNumberHexString = allString.substring(9, 11) + allString.substring(6, 8);
                                    int stampNumber = Integer.valueOf(stampNumberHexString, 16);
                                    // 盖章时间
                                    String timeHexString = allString.substring(15, 32);
                                    String timeStamp = DateUtils.hexTimeToTimeStamp(timeHexString);
                                    Utils.log(timeStamp);

                                    uploadStampRecord(stampNumber, timeStamp);

                                } else if (Utils.bytesToHexString(bytes).startsWith("FF 05 A1 00")) {
                                    // 启动印章成功后，获取“启动序号”
                                    byte[] restTime = DataTrans.subByte(bytes, 4, 4);
                                    startNumber = DataTrans.bytesToInt(restTime, 0);
                                    Utils.log("startNumber" + startNumber);
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
                                } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 B1 00 ")) {
                                    EventBus.getDefault().post(new MessageEvent("ble_change_stamp_count", "success"));
                                } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 B2 00 ")) {
                                    EventBus.getDefault().post(new MessageEvent("ble_delete_pwd_user", "success"));
                                } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 A5 00 ")) {
                                    EventBus.getDefault().post(new MessageEvent("ble_reset", "success"));
                                } else if (Utils.bytesToHexString(bytes).startsWith("FF 0E A3")) {
                                    // 成功读到一条记录
                                    unuploadedQuantity--; // 未上传的盖章记录数量减一
                                    // 解析数据，存入对象

                                    // byte[3] 记录序号 暂时没用
                                    byte[] recordSeq = DataTrans.subByte(bytes, 3, 1);
                                    int recordSeqInt = DataTrans.bytesToInt(recordSeq, 0);
                                    Utils.log("recordSeqInt" + recordSeqInt);

                                    // byte[4] 启动类型（stampType）
                                    byte[] stampTypeBytes = DataTrans.subByte(bytes, 4, 1);
                                    stampType = DataTrans.bytesToInt(stampTypeBytes, 0);

                                    // byte[5] - byte[8] 启动序号（userNumber）
                                    byte[] userNumberBytes = DataTrans.subByte(bytes, 5, 4);
                                    userNumber = DataTrans.bytesToInt(userNumberBytes, 0);

                                    // byte[9] - byte[10] 盖章序号（stampSeqNumber）
                                    byte[] stampSeqNumberBytes = DataTrans.subByte(bytes, 9, 2);
                                    stampSeqNumber = DataTrans.bytesToInt(stampSeqNumberBytes, 0);

                                    // byte[11] - byte[16] 盖章时间（stampTime）
                                    byte[] stampTimeBytes = DataTrans.subByte(bytes, 11, 16);
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
                                } else if (strReturned.equals("C1")) {
                                    // 盖章次数加一
                                    Utils.log("盖章次数加一");
//                                    currentStampTimes++;
//                                    tv_times_done.setText(currentStampTimes + "");
//                                    tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");
                                    uploadStampRecord(0, null);
                                }
                            },
                            throwable -> {
                                // Handle an error here.
                                Utils.log("notificationObservable: error" + throwable.getLocalizedMessage());
                            }
                    ));
        }
    }

    @Override
    public void onNetDisconnected() {
        Utils.log("onNetDisconnected");
        ((MyApp) getActivity().getApplication()).removeAllDisposable();
        ((MyApp) getApplication()).setConnectionObservable(null);

        currentStampTimes = 0;
        tv_times_done.setText("0");
        tv_times_left.setText("0");
        electric_ll.setVisibility(View.GONE);
        tv_ble_name.setText("暂无连接印章");
        tv_stamp_reason.setText("暂无用印申请事由");
        tv_expired_time.setText("暂无");
        tv_address.setText("暂无定位信息");

    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        Utils.log("onNetConnected");
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            currentLocation = location;
            // 获取currentAddress
            getAddress(location.getLatitude(), location.getLongitude());
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
                tv_address.setText(currentAddress);
            }
        };
        geoCoder.setOnGetGeoCodeResultListener(listener);
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        geoCoder.destroy();
    }


    /**
     * 申请权限
     */
    @SuppressLint("CheckResult")
    private void permissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //添加需要的权限
        rxPermissions.requestEachCombined(Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Utils.log("accept");
                            startLocate();

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });
    }

    private NetUtil.NetConnChangedListener netConnChangedListener = new NetUtil.NetConnChangedListener() {
        @Override
        public void onNetConnChanged(NetUtil.ConnectStatus connectStatus) {
            Utils.log("connectStatus:" + connectStatus.toString());
        }
    };
}
