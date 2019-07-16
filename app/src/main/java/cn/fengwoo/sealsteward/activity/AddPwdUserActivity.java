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
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.AddCompanyInfo;
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
 * 编辑密码用户
 */
public class AddPwdUserActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.rl_select_ppl)
    RelativeLayout rlSelectPpl;
    @BindView(R.id.et_use_times)
    EditText etUseTimes;
    @BindView(R.id.tv_expired_time)
    TextView tvExpiredTime;
    @BindView(R.id.expired_time)
    RelativeLayout expiredTime;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private String userId = "";
    private String userName = "";
    String format; //选择的时间
    private byte[] startAllByte;
    private byte[] updateCountByte;
    private String user_name = "";
    private String stamp_count = "";
    private String expired_time = "";
    private String user_number = "";

    ResponseInfo<AddPwdUserUploadReturn> responseInfo;
    PwdUserListItem pwdUserListItem;
    private Boolean timeB = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pwd_user);
        ButterKnife.bind(this);
        initView();
        getIntentData();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加密码用户");
        set_back_ll.setOnClickListener(this);
    }

    private void getIntentData() {
        pwdUserListItem = (PwdUserListItem) getIntent().getSerializableExtra("pwdUserListItem");
        if (pwdUserListItem == null) {
            return;
        }
        tvUser.setText(pwdUserListItem.getUserName());
        tvExpiredTime.setText(DateUtils.getDateString(pwdUserListItem.getExpireTime()));
        etUseTimes.setText(pwdUserListItem.getStampCount() + "");
        title_tv.setText("编辑密码用户");
        Date nowTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(nowTime);
        timeB = DateUtils.isDateOneBigger(tvExpiredTime.getText().toString(), time);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }


    @SuppressLint("CheckResult")
    @OnClick({R.id.rl_select_ppl, R.id.expired_time, R.id.btn_submit})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_select_ppl:
                intent.setClass(this, SelectSinglePeopleActivity.class);
                startActivityForResult(intent, 123);
                break;
            case R.id.expired_time:
                selectTime();
                break;
            case R.id.btn_submit:
                // 如果pwdUserListItem为空，执行原来逻辑
                if (timeB) {
                    if (pwdUserListItem == null) {
                        submit();
                    } else {
                        // 发送命令给ble设备改变次数
//                    updateUser(pwdUserListItem.getUserNumber() + "");

                        String sealCount = etUseTimes.getText().toString().trim();
                        if (TextUtils.isEmpty(format)) {
                            format = tvExpiredTime.getText().toString().trim();
                        }
                        try {
                            updateCountByte = CommonUtil.changeTimes(pwdUserListItem.getUserNumber() + "", Integer.valueOf(sealCount), DateUtils.dateToStamp(format));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        ((MyApp) getApplication()).getDisposableList().add(((MyApp) getApplication()).getConnectionObservable()
                                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.CHANGEPWDPOWER, updateCountByte).getBytes()))
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
                } else {
                    showToast("您选择的时间已过期");
                }

                break;
        }
    }


    private void submit() {
        loadingView.show();
        AddPwdUserUpload addPwdUserUpload = new AddPwdUserUpload();
        try {
            addPwdUserUpload.setExpireTime(DateUtils.dateToStamp(tvExpiredTime.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        addPwdUserUpload.setStampCount(Integer.valueOf(etUseTimes.getText().toString()));
        addPwdUserUpload.setUserId(userId);
        addPwdUserUpload.setUserType(1); // 这个type为1
        addPwdUserUpload.setSealId(EasySP.init(this).getString("currentSealId"));

        HttpUtil.sendDataAsync(AddPwdUserActivity.this, HttpUrl.ADD_PWD_USER, 2, null, addPwdUserUpload, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                loadingView.cancel();

                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<AddPwdUserUploadReturn>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData() != null) {
                        loadingView.cancel();
//                        finish();
                        runOnUiThread(new Runnable() {
                            @SuppressLint("CheckResult")
                            @Override
                            public void run() {
                                // 发送pwd给seal
                                String pwd = responseInfo.getData().getPassword();
                                String sealCount = etUseTimes.getText().toString().trim();

                                try {
                                    startAllByte = CommonUtil.addPwd(pwd, Integer.valueOf(sealCount), DateUtils.dateToStamp(format));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                ((MyApp) getApplication()).getDisposableList().add(((MyApp) getApplication()).getConnectionObservable()
                                        .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.ADDPRESSPWD, startAllByte).getBytes()))
                                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                characteristicValue -> {
                                                    // Characteristic value confirmed.
                                                },
                                                throwable -> {
                                                    // Handle an error here.
                                                }
                                        ));
                            }
                        });
                        Looper.prepare();
//                        showToast("添加成功");
                        Looper.loop();
                    } else {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(responseInfo.getMessage());
                        Looper.loop();
                    }
                } else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }

            }
        });
    }


    /**
     * 时间选择器
     */
    @SuppressLint("SimpleDateFormat")
    private void selectTime() {
        TimePickerView timePicker = new TimePickerBuilder(AddPwdUserActivity.this, new OnTimeSelectListener() {
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
                    tvExpiredTime.setText(format);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == 10) {
            userId = data.getStringExtra("id");
            userName = data.getStringExtra("name");
            tvUser.setText(userName);
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
        Utils.log("Subscribe");
        // read press time
        if (event.msgType.equals("ble_add_pwd")) {
            // 拿到密码代码，准备更新离线用户信息（“POST /seal/updateofflineuser”）
            String pwdCode = event.msg;
            updateUser(pwdCode);
        } else if (event.msgType.equals("ble_change_stamp_count")) {
            // 修改次数成功，发送网络请求通知服务器
            updateUser(pwdUserListItem.getUserNumber() + "");
        }
    }

    private void updateUser(String pwdCode) {
        Object addPwdUserUploadReturn;
        // responseInfo不为空，获取responseInfo数据
        if (responseInfo != null) {
            addPwdUserUploadReturn = responseInfo.getData();
            responseInfo.getData().setUserNumber(pwdCode);
        } else {
            // 把通过选择改的值赋值
            try {
                pwdUserListItem.setExpireTime(Long.parseLong(DateUtils.dateToStamp(tvExpiredTime.getText().toString())));
                pwdUserListItem.setStampCount(Integer.parseInt(etUseTimes.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            addPwdUserUploadReturn = pwdUserListItem;

        }


        Utils.log("sendDataAsync");

        HttpUtil.sendDataAsync(AddPwdUserActivity.this, HttpUrl.UPDATE_PWD_USER, 2, null, addPwdUserUploadReturn, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log("更新离线用户信息return:" + e.toString());
                loadingView.cancel();
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("更新离线用户信息return:" + result);
                loadingView.cancel();
//                Looper.prepare();
//
//                Looper.loop();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("添加成功");
                        setResult(RESULT_OK);
                        finish();
                    }
                });


//                Gson gson = new Gson();
//                ResponseInfo<AddPwdUserUploadReturn> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<AddPwdUserUploadReturn>>() {
//                }.getType());
//                if (responseInfo.getCode() == 0) {
//                    if (responseInfo.getData() != null) {
//                        loadingView.cancel();
//
//                        Looper.prepare();
//                        showToast("更新成功");
//                        Looper.loop();
//                    } else {
//                        loadingView.cancel();
//                        Looper.prepare();
//                        showToast(responseInfo.getMessage());
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

}
