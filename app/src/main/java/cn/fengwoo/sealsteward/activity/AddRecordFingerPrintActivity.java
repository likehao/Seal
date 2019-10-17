package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.AddPwdUserUpload;
import cn.fengwoo.sealsteward.entity.AddPwdUserUploadReturn;
import cn.fengwoo.sealsteward.entity.PwdUserListItem;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
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
 * 添加指纹用户
 */
public class AddRecordFingerPrintActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.finger_user_rl)
    RelativeLayout finger_user_rl;
    @BindView(R.id.finger_user_tv)
    TextView finger_user;
    @BindView(R.id.finger_use_time_et)
    EditText useTime;   //使用次数
    @BindView(R.id.finger_bt)
    Button finger_bt;
    @BindView(R.id.finger_failTime_ll)
    LinearLayout finger_failTime_ll;   //失效时间
    @BindView(R.id.finger_failTime)
    TextView failTime;
    private Intent intent;
    private String format; //选择的时间
    private String userId, userName;
    private Boolean timeB = true;
    private byte[] startFingerByte;
    private PwdUserListItem pwdUserListItem;
    private String fingerFailTime;
    private ResponseInfo<AddPwdUserUploadReturn> responseInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_finger_print);

        ButterKnife.bind(this);
        initView();
        getData();
    }

    private void initView() {
        intent = new Intent();
        title.setText("添加指纹用户");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        finger_user_rl.setOnClickListener(this);
        finger_failTime_ll.setOnClickListener(this);
        finger_bt.setOnClickListener(this);
    }

    private void getData() {
        //点击编辑传递过来的值
        pwdUserListItem = (PwdUserListItem) getIntent().getSerializableExtra("pwdUserListItem");
        if (pwdUserListItem != null) {
            finger_user.setText(pwdUserListItem.getUserName());   //使用人
            useTime.setText(pwdUserListItem.getStampCount() + "");  //使用次数
            failTime.setText(DateUtils.getDateString(pwdUserListItem.getExpireTime()));  //失效时间
            title.setText("编辑指纹用户");
            finger_bt.setText("提交");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.finger_user_rl:
                intent.setClass(this, SelectSinglePeopleActivity.class);
                startActivityForResult(intent, 123);
                break;
            case R.id.finger_failTime_ll:
                selectTime();
                break;
            case R.id.finger_bt:
                if (check()) {
                    if (pwdUserListItem == null) {
                        submit();  //添加指纹
                    } else {
                        //编辑指纹
                        editFingerCount();
                    }
                }

                break;
        }
    }

    /**
     * 检查数据是否为空
     *
     * @return
     */
    private boolean check() {
        Date nowTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(nowTime);
        if (TextUtils.isEmpty(finger_user.getText().toString())) {
            showToast("请选择使用人");
            return false;
        }
        if (TextUtils.isEmpty(useTime.getText().toString())) {
            showToast("请输入使用次数");
            return false;
        }
        if (TextUtils.isEmpty(failTime.getText().toString())) {
            showToast("请选择失效时间");
            return false;
        } else {
            timeB = DateUtils.isDateOneBigger(failTime.getText().toString(), time);
            if (!timeB) {
                showToast("您选择的时间已过期");
                return false;
            }
        }

        return true;
    }

    /**
     * 提交录入指纹
     */
    private void submit() {
        AddPwdUserUpload addPwdUserUpload = new AddPwdUserUpload();
        try {
            addPwdUserUpload.setExpireTime(DateUtils.dateToStamp(failTime.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        addPwdUserUpload.setStampCount(Integer.valueOf(useTime.getText().toString()));
        addPwdUserUpload.setUserId(userId);
        addPwdUserUpload.setUserType(2);
        addPwdUserUpload.setSealId(EasySP.init(this).getString("currentSealId"));

        HttpUtil.sendDataAsync(AddRecordFingerPrintActivity.this, HttpUrl.ADD_PWD_USER, 2, null, addPwdUserUpload, new Callback() {
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
                    String count = useTime.getText().toString().trim();  //使用次数
                    try {
                        startFingerByte = CommonUtil.recordFingerprint(Integer.parseInt(count), DateUtils.dateToStamp(format)); //                                                                                                                                                                                                                                                                                                                                                                                                               次数和失效时间
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
                    intent = new Intent(AddRecordFingerPrintActivity.this, RecordFingerprintActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }

            }
        });
    }

    /**
     * 编辑指纹权限
     */
    private void editFingerCount() {
        //指纹代码1byte ,使用次数2bytes, 失效时间6bytes
        int fingerCode = pwdUserListItem.getFingerprintCode();
        String count = useTime.getText().toString().trim();
        try {
            fingerFailTime = DateUtils.dateToStamp(failTime.getText().toString().trim());  //转时间戳
        } catch (ParseException e) {
            e.printStackTrace();
        }
        byte[] editFingerBytes = CommonUtil.changeFingerprint(fingerCode, Integer.parseInt(count),fingerFailTime);
        ((MyApp) getApplication()).getDisposableList().add(((MyApp) getApplication()).getConnectionObservable()
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.SETFINGERPRINT, editFingerBytes).getBytes()))
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
     * 时间选择器
     */
    @SuppressLint("SimpleDateFormat")
    private void selectTime() {
        TimePickerView timePicker = new TimePickerBuilder(AddRecordFingerPrintActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format = simpleDateFormat.format(date);  //选择的时间
                //获取当前时间
                Date nowTime = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String nowT = dateFormat.format(nowTime);
                //判断选择的时间是否过期
                Boolean compare = DateUtils.isDateOneBigger(format, nowT);
                if (compare) {
                    failTime.setText(format);
                } else {
                    showToast("您选择的时间已过期");
                }
            }
        })
                .setType(new boolean[]{true, true, true, true, true, false})  //年月日时分秒 的显示与否，不设置则默认全部显示
                .build();
        //   timePicker.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        timePicker.show();
    }

    /**
     * 编辑更新指纹权限
     */
    private void updateFingerprint(int fingerCode){
        Object addPwdUserUploadReturn;
        // responseInfo不为空，获取responseInfo数据
        if (responseInfo != null) {
            addPwdUserUploadReturn = responseInfo.getData();
            responseInfo.getData().setFingerprintCode(fingerCode);
        } else {
            // 把通过选择改的值赋值
            try {
                pwdUserListItem.setExpireTime(Long.parseLong(DateUtils.dateToStamp(failTime.getText().toString())));
                pwdUserListItem.setStampCount(Integer.parseInt(useTime.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            addPwdUserUploadReturn = pwdUserListItem;
        }

        Utils.log("sendDataAsync");

        HttpUtil.sendDataAsync(AddRecordFingerPrintActivity.this, HttpUrl.UPDATE_PWD_USER, 2, null, addPwdUserUploadReturn, new Callback() {
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
                        showToast("添加成功");
                        setResult(RESULT_OK);
                        finish();
                    }
                });

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == 10) {
            userId = data.getStringExtra("id");
            userName = data.getStringExtra("name");
            finger_user.setText(userName);
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) throws ParseException {
        if (event.msgType.equals("edit_update_fingerprint")) {
            updateFingerprint(pwdUserListItem.getFingerprintCode());      //编辑更新指纹权限
        }
    }
}
