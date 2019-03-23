package cn.fengwoo.sealsteward.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.internal.RxBleLog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.squareup.picasso.Picasso;
import com.white.easysp.EasySP;
import com.youth.banner.Banner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.ApplyCauseActivity;
import cn.fengwoo.sealsteward.activity.ApprovalRecordActivity;
import cn.fengwoo.sealsteward.activity.MyApplyActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.entity.BannerData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
import cn.fengwoo.sealsteward.utils.GlideImageLoader;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class MainFragment extends Fragment implements View.OnClickListener {

    private View view;
    //设置图片资源:url或本地资源
    String[] images = new String[]{"url", "url"};
    String[] titles = new String[]{"我是广告，全场2折起", "全场2折起"};
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
    LoadingView loadingView;

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
                startActivity(intent);
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
                    // 三期
                    // 握手

                    Utils.log("EasySP.init(getActivity()).getString(\"dataProtocolVersion\").equals(\"3\")");


                    RxBleClient rxBleClient = RxBleClient.create(getActivity());
                    rxBleClient.setLogLevel(RxBleLog.VERBOSE);
                    RxBleDevice device = rxBleClient.getBleDevice(EasySP.init(getActivity()).getString("mac"));

                    byte[] byteTime = CommonUtil.getDateTime();

                    device.establishConnection(false)
                            .flatMap(rxBleConnection -> rxBleConnection.setupNotification(Constants.NOTIFY_UUID))
                            .doOnNext(notificationObservable -> {
                                // Notification has been set up
                                Utils.log("********   doOnNext    *************");

                                Utils.log(Utils.bytesToHexString(new DataProtocol(CommonUtil.HANDSHAKE, byteTime).getBytes()));

                            })
                            .flatMap(notificationObservable -> notificationObservable) // <-- Notification has been set up, now observe value changes.
                            .subscribe(
                                    bytes -> {
                                        // Given characteristic has been changes, here is the value.
                                        Utils.log("notificationObservable:" + Utils.bytesToHexString(bytes));
                                    },
                                    throwable -> {
                                        Utils.log("notificationObservable: error" + throwable.getLocalizedMessage());

                                        // Handle an error here.
                                    }
                            );


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Utils.log("write");
                            device.establishConnection(false)
//                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, Utils.createShakeHandsData()))
                                    .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.HANDSHAKE, byteTime).getBytes()))
                                    .subscribe(
                                            characteristicValue -> {
                                                // Characteristic value confirmed.
                                                Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                            },
                                            throwable -> {
                                                // Handle an error here.
                                            }
                                    );

                        }
                    },3000);


//                    device.establishConnection(false)
//                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, Utils.createShakeHandsData()))
//                            .subscribe(
//                                    characteristicValue -> {
//                                        // Characteristic value confirmed.
//                                        Utils.log(characteristicValue.length +" : " + Utils.bytesToHexString(characteristicValue));
//                                    },
//                                    throwable -> {
//                                        // Handle an error here.
//                                    }
//                            );


//                    device.establishConnection(false)
//                            .flatMapSingle(rxBleConnection -> rxBleConnection.readCharacteristic(Constants.WRITE_UUID)
//                                    .doOnNext(bytes -> {
//                                        // Process read data.
//                                    })
//                                    .flatMap(bytes -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, Utils.createShakeHandsData()))
//                            )
//                            .subscribe(
//                                    writeBytes -> {
//                                        // Written data.
//                                    },
//                                    throwable -> {
//                                        // Handle an error here.
//                                    }
//                            );

                    // 获取电量


//                    device.establishConnection(false)
//                            .flatMapSingle(rxBleConnection -> rxBleConnection.readCharacteristic(Constants.))
//                            .subscribe(
//                                    characteristicValue -> {
//                                        // Read characteristic value.
//                                    },
//                                    throwable -> {
//                                        // Handle an error here.
//                                    }
//                            );


                } else {

                }


                break;
        }
    }
}
