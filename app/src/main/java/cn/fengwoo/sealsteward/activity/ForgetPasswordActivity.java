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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.Base2Activity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 忘记密码
 */
public class ForgetPasswordActivity extends Base2Activity implements View.OnClickListener{

    @BindView(R.id.forgetPassword_next_bt)
    Button forgetPassword_next_bt;
    @BindView(R.id.forget_password_iv)
    ImageView forget_password_iv;
    @BindView(R.id.register_phone_et)
    EditText register_phone_et;   //手机号
    @BindView(R.id.verificationCode_et)
    EditText verificationCode_et;   //验证码
    @BindView(R.id.forget_pwd_getCode_bt)
    Button forget_pwd_getCode_bt;   //获取验证码按钮
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ButterKnife.bind(this);
        setClickListener();
    }

    private void setClickListener() {
        forget_password_iv.setOnClickListener(this);
        forgetPassword_next_bt.setOnClickListener(this);
        forget_pwd_getCode_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.forget_password_iv:
                finish();
                break;
            case R.id.forgetPassword_next_bt:
                checkForgetPwdCode();

                break;
            case R.id.forget_pwd_getCode_bt:
                //判断电话号码
                if (register_phone_et.getText().toString().trim().length() != 11) {
                    showToast("请输入11位数电话号码");
                    return;
                } else {
                    //发送获取验证码
                    sendGetCode();
                }
                break;

        }
    }


    /**
     * 获取验证码
     */
    private void sendGetCode() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //创建okHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建请求
                Request request = new Request.Builder()
                        .url(HttpUrl.URL + HttpUrl.SENDVERIFICATIONCODE + "?mobilePhone=" + register_phone_et.getText().toString().trim() + "&type=" + 1)
                        .get()
                        .build();
                //设置回调
                okHttpClient.newCall(request).enqueue(new Callback() {
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
                        ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){
                        }.getType());
                        //           ResponseInfo<Boolean> responseInfo = fromToJson.fromToJson(result);
                        if (responseInfo.getCode() == 0){
                            if (responseInfo.getData()){
                                timer.start();
                                Looper.prepare();
                                showToast("验证码已发送");
                                Looper.loop();
                                Log.e("TAG","获取验证码成功!!!!!!!!!!!!!!!!");
                            }
                        }else {
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
    private void checkForgetPwdCode(){
        //提交短信验证码,判断字符串是否为空
        if (TextUtils.isEmpty(register_phone_et.getText().toString().trim())){
            showToast("请输入手机号");
            return;
        }else if (TextUtils.isEmpty(verificationCode_et.getText().toString().trim())){
            showToast("请输入验证码");
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(HttpUrl.URL + HttpUrl.CHECKVERIFICATIONCODE + "?mobilePhone=" + register_phone_et.getText().toString().trim() + "&type=" + 1 + "&code=" + verificationCode_et.getText().toString().trim())
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
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
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){
                }.getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()) {
                        //验证成功之后跳转到设置密码页面
                        Intent intent = new Intent(ForgetPasswordActivity.this, SetPasswordActivity.class);
                        intent.putExtra("foreget_next","foreget");
                        intent.putExtra("phone", register_phone_et.getText().toString()); //传递输入的电话号码
                        startActivity(intent);
                        finish();
                    }
                }else {
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
            forget_pwd_getCode_bt.setEnabled(false);
            forget_pwd_getCode_bt.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            forget_pwd_getCode_bt.setEnabled(true);
            forget_pwd_getCode_bt.setText("重新获取验证码");
        }
    };
}
