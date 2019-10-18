package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.AddPwdUserUpload;
import cn.fengwoo.sealsteward.entity.AddPwdUserUploadReturn;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
import cn.fengwoo.sealsteward.utils.DataTrans;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 录入指纹
 */
public class RecordFingerprintActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.finger_back_bt)
    Button finger_back_bt;
    @BindView(R.id.fingerprint_iv)
    ImageView fingerprint_iv;
    @BindView(R.id.fingerprint_count_tv)
    TextView fingerprint_tv;
    private String expireTime,userId,format;
    private int stampCount;
    private ResponseInfo<AddPwdUserUploadReturn> responseInfo;
    private byte[] startFingerByte;
    public static final int SUCCESS_CODE_FINGERPRINT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_fingerprint2);

        ButterKnife.bind(this);
        initView();
        initData();
        addFingerprint();
    }

    private void initView() {
        back.setVisibility(View.VISIBLE);
        title.setText("录入指纹");
        back.setOnClickListener(this);
        finger_back_bt.setOnClickListener(this);
    }

    private void initData(){
        Intent intent = getIntent();
        expireTime = intent.getStringExtra("expireTime");
        stampCount = intent.getIntExtra("stampCount",0);
        userId = intent.getStringExtra("userId");
        format = intent.getStringExtra("format");
    }

    /**
     * 添加指纹
     */
    private void addFingerprint(){
        AddPwdUserUpload addPwdUserUpload = new AddPwdUserUpload();
        addPwdUserUpload.setExpireTime(expireTime);
        addPwdUserUpload.setStampCount(stampCount);
        addPwdUserUpload.setUserId(userId);
        addPwdUserUpload.setUserType(2);
        addPwdUserUpload.setSealId(EasySP.init(this).getString("currentSealId"));

        HttpUtil.sendDataAsync(RecordFingerprintActivity.this, HttpUrl.ADD_PWD_USER, 2, null, addPwdUserUpload, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<AddPwdUserUploadReturn>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {

                    try {
                        startFingerByte = CommonUtil.recordFingerprint(stampCount, DateUtils.dateToStamp(format)); //                                                                                                                                                                                                                                                                                                                                                                                                               次数和失效时间
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    byte[] bytes = new DataProtocol(CommonUtil.RECORDING, startFingerByte).getBytes();
//                    String str = Utils.bytesToHexString(bytes); //发送的指令

                    ((MyApp) getApplication()).getDisposableList().add(((MyApp) getApplication()).getConnectionObservable()
                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, bytes))
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    characteristicValue -> {
                                        Utils.log("指纹fingerprint");
                                        // Characteristic value confirmed.
                                    },
                                    throwable -> {
                                        Utils.log("指纹error");
                                        // Handle an error here.
                                    }
                            ));
                }else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
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
            case R.id.finger_back_bt:
                finish();
                break;
        }
    }

    String result;
    byte[] bytes;
    //蓝牙返回添加指纹成功指令监听
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.msgType){
            case "first_fingerprint":
                fingerprint_tv.setText("请第2次录入指纹");
                break;
            case "second_fingerprint":
                fingerprint_tv.setText("请第3次录入指纹");
                break;
            case "third_fingerprint":
                fingerprint_tv.setText("请第4次录入指纹");
                break;
            case "result":
                result = event.msg;
                break;
            case "bytes":
                bytes = event.bytes;
                break;
            case "add_fingerprint":
                updateFingerprint();      //录入成功更新指纹权限
                break;
        }
    }

    /**
     * 录入成功更新指纹信息
     */
    private void updateFingerprint(){
        AddPwdUserUploadReturn addPwdUserUploadReturn = new AddPwdUserUploadReturn();
        addPwdUserUploadReturn.setFingerprintCode(Integer.valueOf(result.substring(9,10)));
        addPwdUserUploadReturn.setExpireTime(Long.parseLong(expireTime));
        addPwdUserUploadReturn.setSealId(responseInfo.getData().getSealId());
        addPwdUserUploadReturn.setUserId(userId);
        addPwdUserUploadReturn.setId(responseInfo.getData().getId());
        addPwdUserUploadReturn.setStampCount(stampCount);
        //截取数组转int
        byte[] b = DataTrans.subByte(bytes,5,4);
        int i = DataTrans.bytesToInt(b,0);
        addPwdUserUploadReturn.setUserNumber(i);
        addPwdUserUploadReturn.setUserType(2);

        Utils.log("sendDataAsync");

        HttpUtil.sendDataAsync(RecordFingerprintActivity.this, HttpUrl.UPDATE_PWD_USER, 2, null, addPwdUserUploadReturn, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log("更新指纹用户信息return:" + e.toString());
                loadingView.cancel();
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("更新指纹用户信息return:" + result);
                loadingView.cancel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fingerprint_iv.setBackgroundResource(R.drawable.fingerprint);
                        finger_back_bt.setVisibility(View.VISIBLE);
                        fingerprint_tv.setText("指纹录入完成");
                        setResult(SUCCESS_CODE_FINGERPRINT);
                        finish();
                    }
                });

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
