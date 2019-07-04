package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 设置密码
 */
public class SetPasswordActivity extends Base2Activity implements View.OnClickListener {

    @BindView(R.id.register_bt)
    Button register_bt;
    @BindView(R.id.set_password_back_iv)
    ImageView set_password_back_iv;
    @BindView(R.id.set_pwd_et)
    EditText set_pwd_et;
    @BindView(R.id.confirm_pwd_et)
    EditText confirm_pwd_et;
    private String password;
    private String confirmPwd;
    private boolean open = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        ButterKnife.bind(this);
        initData();
        setClickListener();

    }

    private void initData() {
        //判断是否点击的忘记密码
        Intent intent = getIntent();
        String foreget_next = intent.getStringExtra("foreget_next");
        if ("foreget".equals(foreget_next)) {
//            register_bt.setText("完成");
            open = true;
        }
    }

    private void setClickListener() {
        register_bt.setOnClickListener(this);
        set_password_back_iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_bt:
                //提交
                if (checkData()) {
                    //判断是注册密码提交还是忘记密码设置新密码提交
                    if (open){
                        submitNewPwd();
                    }else {
                        submitRegister();
                    }
                }
                break;
            case R.id.set_password_back_iv:
                finish();
                break;
        }
    }
    /**
     * 检验密码数据
     */
    private boolean checkData(){
    //    String passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";  //数字字母正则表达式
        password = set_pwd_et.getText().toString().trim();
        confirmPwd = confirm_pwd_et.getText().toString().trim();
        if (password.length()== 0) {
            showToast("请输入6位以上18位以下密码和数字组合");
            return false;
        }
        if (password.length()< 6) {
            showToast("密码必须大于6位数");
            return false;
        }
        if (!password.equals(confirmPwd)){
            showToast("两次密码不一致");
            return false;
        }
        return true;
    }
    /**
     * 注册
     */
    private void submitRegister() {
        //获取手机号
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(HttpUrl.URL + HttpUrl.REGISTER + "?mobilePhone=" + phone + "&password=" + password)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                showToast(e +"");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        Intent intent = new Intent();
//                        intent.setClass(SetPasswordActivity.this, LoginActivity.class);
                        intent.putExtra("password", password);
//                        startActivity(intent);
                        intent.putExtra("password",password);
//                        startActivity(intent);
                        finish();
                        Log.e("TAG", "注册成功!!!!!!!!!!!!!!!!!!!");
                        Looper.prepare();
                        showToast("注册成功");
                        Looper.loop();
                    }
                } else {
                    Log.e("TAG", "注册失败!!!!!!!!!!!!!");
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

    /**
     * 提交新密码
     */
    private void submitNewPwd() {
        //获取手机号
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        OkHttpClient okHttpClient = new OkHttpClient();
        //设置请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("mobilePhone", phone)
                .add("newPassword", password)
                .build();
        Request request = new Request.Builder()
                .url(HttpUrl.URL + HttpUrl.FORGETPASSWORD)
                .patch(requestBody)
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
                        finish();
                        Log.e("TAG", "设置密码成功!!!!!!!!!!!!!!!!!!!");
                        Looper.prepare();
                        showToast("设置密码成功");
                        Looper.loop();

                    }
                } else {
                    Log.e("TAG", "设置密码失败!!!!!!!!!!!!!");
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }
}
