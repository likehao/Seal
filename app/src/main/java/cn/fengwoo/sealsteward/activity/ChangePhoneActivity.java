package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
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
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 重置账号
 */
public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.change_phone_et)
    EditText new_phone;
    @BindView(R.id.code_et)
    EditText code_et;
    @BindView(R.id.changePhone_pwd_et)
    EditText pwd;
    @BindView(R.id.confirm_changePhone_pwd_et)
    EditText confirm_pwd;
    @BindView(R.id.send_code_bt)
    Button send_code_bt;
    @BindView(R.id.save_bt)
    Button save_bt;
    String phone, password, confirmPwd, code;  //新手机号码，密码，确认密码，验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("重置账号");
        set_back_ll.setOnClickListener(this);
        send_code_bt.setOnClickListener(this);
        save_bt.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.send_code_bt:
                //判断电话号码
                if (new_phone.getText().toString().trim().length() != 11) {
                    showToast("请输入11位数电话号码");
                    return;
                } else {
                    //发送获取验证码
                    sendCode();
                }
                break;
            case R.id.save_bt:
                save();
                break;
        }
    }

    private void save() {
        if (check()) {
            saveChange();
        }
    }

    /**
     * 获取验证码
     */
    private void sendCode() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HashMap<String , String> hashMap = new HashMap<>();
                hashMap.put("mobilePhone",new_phone.getText().toString().trim());
                hashMap.put("type",3+"");
                HttpUtil.sendDataAsync(ChangePhoneActivity.this, HttpUrl.SENDVERIFICATIONCODE, 1, hashMap, null, new Callback() {
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
            send_code_bt.setEnabled(false);
            send_code_bt.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            send_code_bt.setEnabled(true);
            send_code_bt.setText("重新发送");
        }
    };

    /**
     * 检查修改条件
     *
     * @return
     */
    private boolean check() {
        phone = new_phone.getText().toString().trim();
        password = pwd.getText().toString().trim();
        confirmPwd = confirm_pwd.getText().toString().trim();
        code = code_et.getText().toString().trim();

        if (phone.length() != 11) {
            showToast("请输入11位数电话号码");
            return false;
        }
        if (code.length() == 0) {
            showToast("请输入验证码");
            return false;
        }
        if (password.length() == 0) {
            showToast("请输入6-18位密码");
            return false;
        }
        if (password.length() < 6) {
            showToast("密码必须大于6位数");
            return false;
        }
        if (!password.equals(confirmPwd)) {
            showToast("请确认输入密码是否一致");
            return false;
        }
        return true;
    }

    /**
     * 确认修改账户手机号码
     */
    private void saveChange() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mobilePhone", phone);
        hashMap.put("type", "3");
        hashMap.put("code", code);
        hashMap.put("newPassword", password);
        HttpUtil.sendDataAsync(this, HttpUrl.RESETACCOUNT, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "修改账户手机号错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                final ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        //更新存储电话
                        LoginData data = CommonUtil.getUserData(ChangePhoneActivity.this);
                        if (data != null) {
                            data.setMobilePhone(phone);
                            CommonUtil.setUserData(ChangePhoneActivity.this, data);
                        }
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
}
