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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.MyApp;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 公司转让
 */
public class ChangeCompanyBelongActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.changeBelong_phone_et)
    EditText phone;
    @BindView(R.id.changePhone_code_et)
    EditText code;
    @BindView(R.id.changeBelong_code_bt)
    Button code_bt;
    @BindView(R.id.change_belong_bt)
    Button change_belong_bt;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_company_belong);

        ButterKnife.bind(this);
        initView();
    }
    private void initView(){
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("公司转让");
        set_back_ll.setOnClickListener(this);
        code_bt.setOnClickListener(this);
        change_belong_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.changeBelong_code_bt:
                //判断电话号码
                if (phone.getText().toString().trim().length() != 11) {
                    showToast("请输入11位数电话号码");
                    return;
                } else {
                    //发送获取验证码
                    sendCode();
                }
                break;
            case R.id.change_belong_bt:
                checkVerificationCode();
                break;

        }
    }

    /**
     * 确认提交转让公司
     */
    private void changeBelong(){
        intent = getIntent();
        String companyId = intent.getStringExtra("companyId");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mobilePhone",phone.getText().toString().trim());
        hashMap.put("companyId", companyId);
        HttpUtil.sendDataAsync(this, HttpUrl.CHANGEBELONG, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "转让公司错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}
                .getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()){
                        String loginOut = intent.getStringExtra("转让公司下线");
                        if (loginOut != null){
                            //如果转让的是已经选中的公司，那么转让之后就退出
                            intent = new Intent(ChangeCompanyBelongActivity.this, LoginActivity.class);
                            startActivity(intent);
                            System.exit(0);
                            LoginData.logout(ChangeCompanyBelongActivity.this); //移除退出标记
                            //断开蓝牙
                            ((MyApp) ChangeCompanyBelongActivity.this.getApplication()).removeAllDisposable();
                            ((MyApp) ChangeCompanyBelongActivity.this.getApplication()).setConnectionObservable(null);
                            Log.e("TAG", "转让公司成功。。。退出成功.........");
                        }else {
                            finish();
                        }
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
     * 验证验证码
     */
    private void checkVerificationCode() {

        //提交短信验证码,判断字符串是否为空
        if (TextUtils.isEmpty(phone.getText().toString().trim())) {
            showToast("请输入手机号");
            return;
        } else if (TextUtils.isEmpty(code.getText().toString().trim())) {
            showToast("请输入验证码");
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(HttpUrl.URL + HttpUrl.CHECKVERIFICATIONCODE + "?mobilePhone=" + phone.getText().toString().trim() + "&type=" + 4 + "&code=" + code.getText().toString().trim())
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
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        changeBelong();
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
     * 获取验证码
     */
    private void sendCode() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //创建okHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建请求
                Request request = new Request.Builder()
                        .url(HttpUrl.URL + HttpUrl.SENDVERIFICATIONCODE + "?mobilePhone=" + phone.getText().toString().trim() + "&type=" + 4)
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
     * 验证码倒计时
     */
    CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            code_bt.setEnabled(false);
            code_bt.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            code_bt.setEnabled(true);
            code_bt.setText("重新发送");
        }
    };
}
