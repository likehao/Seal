package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 修改启动密码和修改按键密码
 */
public class StartPasswordActivity extends BaseActivity {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.et_old_pwd)
    EditText etOldPwd;
    @BindView(R.id.et_new_pwd)
    EditText etNewPwd;
    @BindView(R.id.et_new_pwd_check)
    EditText etNewPwdCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_password);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        String startStr = intent.getStringExtra("startPsd");
        if (startStr != null && startStr.equals("startPsd")) {
            title_tv.setText("修改启动密码");
        } else {
            title_tv.setText("修改按键密码");
        }
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        etOldPwd.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        etNewPwd.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        etNewPwdCheck.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("完成");
        edit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = etOldPwd.getText().toString().trim();
                String newPwd = etNewPwd.getText().toString().trim();
                String newPwdCheck = etNewPwdCheck.getText().toString().trim();
                if (!newPwd.equals(newPwdCheck)) {
                    showToast("新密码与确认密码不同，请重新输入。");
                    return;
                }

                if (title_tv.getText().equals("修改启动密码")) {
                    // 修改启动密码
                    changePwd("SPASSWD=" + oldPwd + newPwd);
                } else {
                    // 修改按键密码
                    changePwd("KPASSWD=" + oldPwd + newPwd);
                }
                finish();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void changePwd(String str) {
        String pwd = str;
        Utils.log(str);
        byte[] pwdBytes = pwd.getBytes();
        ((MyApp) getApplication()).getDisposableList().add(((MyApp) getApplication()).getConnectionObservable()
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, pwdBytes))
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
}
