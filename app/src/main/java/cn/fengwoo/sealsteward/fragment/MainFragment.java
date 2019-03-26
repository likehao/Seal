package cn.fengwoo.sealsteward.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.internal.RxBleLog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.squareup.picasso.Picasso;
import com.white.easysp.EasySP;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.AddCompanyActivity;
import cn.fengwoo.sealsteward.activity.AddSealActivity;
import cn.fengwoo.sealsteward.activity.ApplyCauseActivity;
import cn.fengwoo.sealsteward.activity.ApprovalRecordActivity;
import cn.fengwoo.sealsteward.activity.MyApplyActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.AddCompanyInfo;
import cn.fengwoo.sealsteward.entity.BannerData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.StampUploadRecordData;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
import cn.fengwoo.sealsteward.utils.DataTrans;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.GlideImageLoader;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.RxTimerUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.fengwoo.sealsteward.view.MyApp;
import cn.qqtheme.framework.util.LogUtils;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Connection;
import okhttp3.Response;
import okhttp3.internal.Util;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements View.OnClickListener {

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
    @BindView(R.id.home_companyName_tv)
    TextView home_companyName_tv;
    @BindView(R.id.tv_battery)
    TextView tv_battery;

    @BindView(R.id.tv_times_done)
    TextView tv_times_done;

    @BindView(R.id.tv_times_left)
    TextView tv_times_left;

    LoadingView loadingView;
    private RxBleConnection rxBleConnection;
    private String availableCount = "0"; // 剩余次数
    private int startNumber; // 启动序号
    private int currentStampTimes = 0; // 现在盖章次数


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        ButterKnife.bind(this, view);
        initView();
        initBanner();
        setListener();


        return view;
    }

    private void initView() {
        loadingView = new LoadingView(getActivity());
        home_companyName_tv.setText(CommonUtil.getUserData(getActivity()).getCompanyName());
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
                    Bitmap bitmap = null;
                    String imgName = null;
                    int count = responseInfo.getData().size();   //banner图片总数
                    //设置图片集合
                    for (int i = 0; i < count; i++) {
                        imgName = responseInfo.getData().get(i).getImageFile();
                        HttpDownloader.downLoadImg(getActivity(), 8, responseInfo.getData().get(i).getImageFile());
                        bitmap = HttpDownloader.readFile(imgName);
//                        if (bitmap == null) {
//                            HttpDownloader.downLoadImg(getActivity(), 8, responseInfo.getData().get(i).getImageFile());
//                        } else {
//                            //设置图片加载器
//                            banner.setImageLoader(new GlideImageLoader());
//                            imageViews.add(bitmap);
//                        }
                        //设置图片加载器
                        banner.setImageLoader(new GlideImageLoader());
                        imageViews.add("file://" + "/sdcard/SealDownImage/" + imgName);
                    }

                    Integer integer = imageViews.size();
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            banner.setImages(imageViews);
                            //banner设置方法全部调用完毕时最后调用
                            banner.start();
                        }
                    });
                } else {
                    loadingView.cancel();
                    Log.e("ATG", "获取广告图失败!!!!!!!!!!!!!!");
                    Looper.prepare();
                    Toast.makeText(getActivity(), responseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

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
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.TO_NEARBY_DEVICE:
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {
                    if (resultCode != Constants.TO_NEARBY_DEVICE) {
                        return;
                    }
                    // 三期
                    // 握手

                    Utils.log("EasySP.init(getActivity()).getString(\"dataProtocolVersion\").equals(\"3\")");
                    RxBleClient rxBleClient = RxBleClient.create(getActivity());
                    rxBleClient.setLogLevel(RxBleLog.VERBOSE);
                    RxBleDevice device = rxBleClient.getBleDevice(EasySP.init(getActivity()).getString("mac"));

                    byte[] byteTime = CommonUtil.getDateTime();
                    byte[] eleByte = new byte[]{0};

                    device.establishConnection(false);
                    ((MyApp) getActivity().getApplication()).getConnectionObservable()
                            .flatMap(rxBleConnection -> rxBleConnection.setupNotification(Constants.NOTIFY_UUID))
//                            .doOnNext(rxBleConnection-> this.rxBleConnection = rxBleConnection)
                            .doOnNext(notificationObservable -> {
                                // Notification has been set up ，监听设置成功，然后握手
                                ((MyApp) getActivity().getApplication()).getConnectionObservable()
                                        .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.HANDSHAKE, byteTime).getBytes()))
                                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                characteristicValue -> {
                                                    // Characteristic value confirmed.
//                                                    Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                                },
                                                throwable -> {
                                                    // Handle an error here.
                                                }
                                        );
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
                                                    ((MyApp) getActivity().getApplication()).getConnectionObservable()
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
                                                            );
                                                }
                                            });
                                        } else if (Utils.bytesToHexString(bytes).startsWith("FF 01 AF")) {
                                            String batteryString = Utils.bytesToHexString(bytes).substring(9, 11);
                                            int batteryInt = Integer.parseInt(batteryString, 16);
                                            Utils.log("batteryInt:" + batteryInt);
                                            // 刷新ui,赋值电量
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

                                        }
                                   else if (Utils.bytesToHexString(bytes).startsWith("FF 01 B6 ")) {
                                            int voiceState = bytes[3];
                                            Utils.log(voiceState + "");
                                            EventBus.getDefault().post(new MessageEvent("ble_read_voice", voiceState + ""));

                                        }
                                    },
                                    throwable -> {
                                        // Handle an error here.
                                        Utils.log("notificationObservable: error" + throwable.getLocalizedMessage());
                                    }
                            );
                } else {
                }
                break;


            case Constants.TO_WANT_SEAL:
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {
                    if (resultCode != Constants.TO_WANT_SEAL) {
                        return;
                    }

                    String expireTime = data.getStringExtra("expireTime");
                    availableCount = data.getStringExtra("availableCount");

                    // 初始化首页的已盖次数和剩余次数
                    tv_times_done.setText(currentStampTimes + "");
                    tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");

                    // 三期
                    // 启动印章
                    Utils.log("case Constants.TO_WANT_SEAL:");
                    byte[] startAllByte = CommonUtil.startData(Integer.valueOf(availableCount), expireTime);
                    ((MyApp) getActivity().getApplication()).getConnectionObservable()
                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.START, startAllByte).getBytes()))
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    characteristicValue -> {
                                        // Characteristic value confirmed.
                                    },
                                    throwable -> {
                                        // Handle an error here.
                                    }
                            );
                }
                break;
        }
    }

    private void uploadStampRecord(int stampNumber, String timeStamp) {
        StampUploadRecordData stampUploadRecordData = new StampUploadRecordData();
        stampUploadRecordData.setAddress("asdf"); //
        stampUploadRecordData.setApplyId(EasySP.init(getActivity()).getString("currentApplyId"));
        stampUploadRecordData.setLatitude(22.222); //
        stampUploadRecordData.setLongitude(22.222); //
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_times_done.setText(currentStampTimes + "");
                        tv_times_left.setText((Integer.parseInt(availableCount) - currentStampTimes) + "");
                    }
                });
            }
        });
    }


    public void showToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }
}
