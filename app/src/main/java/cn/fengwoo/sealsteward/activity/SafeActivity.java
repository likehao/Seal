package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suke.widget.SwitchButton;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.BiometricPromptManager;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
import cn.fengwoo.sealsteward.utils.DialogFactory;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 关于
 */
public class SafeActivity extends BaseActivity implements View.OnClickListener, SwitchButton.OnCheckedChangeListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.switch_voice)
    SwitchButton switchVoice;

    private BiometricPromptManager mManager;
    private BiometricPromptManager biometricPromptManager;

    @TargetApi(28)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
        ButterKnife.bind(this);
        getData();
        initView();

    }

    @TargetApi(28)
    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("账户安全");
        set_back_ll.setOnClickListener(this);
        switchVoice.setOnCheckedChangeListener(this);

//        switchVoice.setChecked(biometricPromptManager.isBiometricSettingEnable("123"));

        String state = EasySP.init(this).getString("finger_print");
        if (state.equals("1")) {
            switchVoice.setChecked(true);
        } else {
            switchVoice.setChecked(false);
        }
    }


    @TargetApi(28)
    @SuppressLint("CheckResult")
    private void getData() {
//        mManager = new BiometricPromptManager(this);
//        biometricPromptManager = new BiometricPromptManager(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(MessageEvent event) {
//        if (event.msgType.equals("ble_read_voice")) {
//            if (event.msg.equals("1")) {
//                switchVoice.setChecked(true);
//            } else {
//                switchVoice.setChecked(false);
//            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    switchVoice.setOnCheckedChangeListener(SafeActivity.this);
//                }
//            }, 1000);
//        }
//    }

    @RequiresApi(api = 28)
    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
//        setData(isChecked);
//        setPassword("123");

        if(isChecked){
            EasySP.init(this).putString("finger_print", "1");
        }else {
            EasySP.init(this).putString("finger_print", "0");
        }
    }



//    @RequiresApi(api = 28)
//    private void setPassword(final String userMobile) {
//        if (!biometricPromptManager.isBiometricSettingEnable(userMobile)) {
//            //校验手机是否支持指纹登陆
//            if (biometricPromptManager.isHardwareDetected()) {
//                if (biometricPromptManager.hasEnrolledFingerprints()) {
//                    //跳转到密码验证页面
//                    checkPassword(userMobile);
//                } else {
//                    //弹框提示是否跳转到系统设置页面
//                    DialogFactory.showIfJumpToSettingsActivity(this);
////                    cbOpenFingerprint.setChecked(biometricPromptManager.isBiometricSettingEnable(userMobile));
//                    switchVoice.setChecked(biometricPromptManager.isBiometricSettingEnable(userMobile));
//                    biometricPromptManager.setBiometricSettingEnable(biometricPromptManager.isBiometricSettingEnable(userMobile), userMobile);
//                }
//            } else {
////                cbOpenFingerprint.setChecked(biometricPromptManager.isBiometricSettingEnable(userMobile));
//                switchVoice.setChecked(biometricPromptManager.isBiometricSettingEnable(userMobile));
//                biometricPromptManager.setBiometricSettingEnable(biometricPromptManager.isBiometricSettingEnable(userMobile), userMobile);
//            }
//        } else {
//            DialogFactory.createWarningDialog(this, 0, "",
//                    "关闭指纹登录吗？", getString(R.string.general_ok),
//                    getString(R.string.general_cancel), 0, new DialogFactory.WarningDialogListener() {
//                        @Override
//                        public void onWarningDialogOK(int id) {
////                            cbOpenFingerprint.setChecked(!biometricPromptManager.isBiometricSettingEnable(userMobile));
//                            switchVoice.setChecked(!biometricPromptManager.isBiometricSettingEnable(userMobile));
//                            biometricPromptManager.setBiometricSettingEnable(!biometricPromptManager.isBiometricSettingEnable(userMobile), userMobile);
//                            biometricPromptManager.clearKey();
//                        }
//
//                        @Override
//                        public void onWarningDialogCancel(int id) {
////                            cbOpenFingerprint.setChecked(biometricPromptManager.isBiometricSettingEnable(userMobile));
//                            switchVoice.setChecked(biometricPromptManager.isBiometricSettingEnable(userMobile));
//                            biometricPromptManager.setBiometricSettingEnable(biometricPromptManager.isBiometricSettingEnable(userMobile), userMobile);
//                        }
//
//                        @Override
//                        public void onWarningDialogMiddle(int id) {
//
//                        }
//                    });
//        }
//    }

//    @TargetApi(28)
//    private void checkPassword(final String userName) {
//        final String password = "asdf";
//        if (TextUtils.isEmpty(password)) {
//            return;
//        }
//        if (password.equals(password)) {
//            //说明密码正确
//            if (mManager.isBiometricPromptEnable()) {
//                mManager.authenticate(KeyProperties.PURPOSE_ENCRYPT, password, new BiometricPromptManager.OnBiometricIdentifyCallback() {
//                    @Override
//                    public void onSucceeded(String password) {
//                        //校验成功之后，保存
//                        mManager.setPassword(password);
//                        biometricPromptManager.setBiometricSettingEnable(!biometricPromptManager.isBiometricSettingEnable(userName), userName);
////                        cbOpenFingerprint.setChecked(biometricPromptManager.isBiometricSettingEnable(userName));
//                        Toast.makeText(SafeActivity.this, "指纹登陆已开通", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailed() {
//                    }
//
//                    @Override
//                    public void onError(int code, String reason) {
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
//            }
//        }
//    }

}
