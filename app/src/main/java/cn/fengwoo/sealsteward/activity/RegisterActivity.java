package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.Base2Activity;
import cn.fengwoo.sealsteward.utils.FromToJson;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 注册
 */
public class RegisterActivity extends Base2Activity implements View.OnClickListener {

    @BindView(R.id.register_next_bt)
    Button register_next_bt;
    @BindView(R.id.register_back_iv)
    ImageView register_back_iv;
    @BindView(R.id.send_code_bt)  //获取验证码
            Button send_code_bt;
    @BindView(R.id.register_phone_et)
    EditText register_phone_et;
    @BindView(R.id.verificationCode_et) //验证码
            EditText verificationCode_et;

    @BindView(R.id.tv_items)
    TextView tv_items;

    FromToJson fromToJson = new FromToJson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //      requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        setClickListener();
    }

    private void setClickListener() {
        register_next_bt.setOnClickListener(this);
        register_back_iv.setOnClickListener(this);
        send_code_bt.setOnClickListener(this);
        tv_items.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_next_bt:
                checkVerificationCode();
                break;
            case R.id.register_back_iv:
                finish();
                break;
            case R.id.send_code_bt:
                //判断电话号码
                if (register_phone_et.getText().toString().trim().length() != 11) {
                    showToast("请输入11位数电话号码");
                    return;
                } else {
                    //发送获取验证码
                    sendMsg();
                }
                break;
            case R.id.tv_items:
                Intent intent = new Intent(RegisterActivity.this, MyWebViewActivity.class);
                intent.putExtra("type", MyWebViewActivity.Type.USER_ARGEEMENT_NOOPER);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void sendMsg() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HashMap<String , String> hashMap = new HashMap<>();
                hashMap.put("mobilePhone",register_phone_et.getText().toString().trim());
                hashMap.put("type",1+"");
                HttpUtil.sendDataAsync(RegisterActivity.this, HttpUrl.SENDVERIFICATIONCODE, 1, hashMap, null, new Callback() {
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
                        Gson gson = new Gson();
                        ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                        }.getType());
                        if (responseInfo.getCode() == 0) {
                            if (responseInfo.getData()) {
                                timer.start();
                                Log.e("TAG", "获取验证码成功!!!!!!!!!!!!!!!!");
                                Looper.prepare();
                                showToast("验证码已发送");
                                Looper.loop();
                            }
                        } else {
                            Looper.prepare();
                            showToast(responseInfo.getMessage());
                            Looper.loop();
                        }
                    }
                });
            }
        });
    }

    /**
     * 验证验证码
     */
    private void checkVerificationCode() {
        //提交短信验证码,判断字符串是否为空
        if (TextUtils.isEmpty(register_phone_et.getText().toString().trim())) {
            showToast("请输入手机号");
            return;
        } else if (TextUtils.isEmpty(verificationCode_et.getText().toString().trim())) {
            showToast("请输入验证码");
            return;
        }
        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put("mobilePhone",register_phone_et.getText().toString().trim());
        hashMap.put("type",1+"");
        hashMap.put("code",verificationCode_et.getText().toString().trim());
        HttpUtil.sendDataAsync(RegisterActivity.this, HttpUrl.CHECKVERIFICATIONCODE, 1, hashMap, null, new Callback() {
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
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        //验证成功之后跳转到设置密码页面
                        Intent intent = new Intent(RegisterActivity.this, SetPasswordActivity.class);
                        intent.putExtra("phone", register_phone_et.getText().toString()); //传递输入的电话号码
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

    /**
     * 验证码倒计时
     */
    CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            send_code_bt.setEnabled(false);
            send_code_bt.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            send_code_bt.setEnabled(true);
            send_code_bt.setText("重新发送");
        }
    };

}
