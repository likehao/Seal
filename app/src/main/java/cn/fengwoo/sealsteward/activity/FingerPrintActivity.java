package cn.fengwoo.sealsteward.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nestia.biometriclib.BiometricPromptManager;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import de.hdodenhof.circleimageview.CircleImageView;


public class FingerPrintActivity extends Activity {

    @BindView(R.id.tv_switch_login)
    TextView tvSwitchLogin;
    @BindView(R.id.tv_switch_user)
    TextView tvSwitchUser;
    @BindView(R.id.headImg_cir)
    CircleImageView headImgCir;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    private BiometricPromptManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        ButterKnife.bind(this);

        mManager = BiometricPromptManager.from(this);

        initView();
    }

    private void initView() {
        tvTel.setText(CommonUtil.getUserData(this).getMobilePhone());
        String headPortrait = CommonUtil.getUserData(this).getHeadPortrait();
        String headPortraitPath = "file://" + HttpDownloader.path + headPortrait;
        Picasso.with(this).load(headPortraitPath).into(headImgCir);
    }
    @TargetApi(28)
    private void identify() {
        if (mManager.isBiometricPromptEnable()) {
            mManager.authenticate(new BiometricPromptManager.OnBiometricIdentifyCallback() {
                @Override
                public void onUsePassword() {
//                    Toast.makeText(FingerPrintActivity.this, "onUsePassword", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSucceeded() {
                   finish();

//                    Toast.makeText(FingerPrintActivity.this, "onSucceeded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed() {

//                    Toast.makeText(FingerPrintActivity.this, "onFailed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int code, String reason) {

//                    Toast.makeText(FingerPrintActivity.this, "onError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {

//                    Toast.makeText(FingerPrintActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @OnClick({R.id.tv_switch_login, R.id.tv_switch_user, R.id.tv_login})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_switch_login:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_switch_user:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_login:
                identify();
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // 按下BACK，同时没有重复
            return true;
//            Log.d(TAG, "onKeyDown()");
        }
        return super.onKeyDown(keyCode, event);
    }
}
