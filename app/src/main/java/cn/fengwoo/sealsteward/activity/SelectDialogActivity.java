package cn.fengwoo.sealsteward.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.white.easysp.EasySP;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.Utils;

public class SelectDialogActivity extends Activity {

    @BindView(R.id.btn_manual)
    Button btnManual;
    @BindView(R.id.btn_auto)
    Button btnAuto;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private boolean isManual = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dialog);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_manual, R.id.btn_auto, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_manual:
                if (isManual) {
                    return;
                } else {
                    btnManual.setBackgroundResource(R.drawable.login_circle_bg);
                    btnAuto.setBackgroundResource(R.drawable.login_circle_bg_gray);
                    isManual = true;
                }
                break;
            case R.id.btn_auto:
                if (isManual) {
                    btnAuto.setBackgroundResource(R.drawable.login_circle_bg);
                    btnManual.setBackgroundResource(R.drawable.login_circle_bg_gray);
                    isManual = false;
                } else {
                    return;
                }
                break;
            case R.id.btn_confirm:
                // 存入本地选择的类型
                // take_pic_mode 0：手动   1：自动
                Utils.log("ismanual:" + isManual);
                if (isManual) {
                    EasySP.init(this).putInt("take_pic_mode", 0);
                }else {
                    EasySP.init(this).putInt("take_pic_mode", 1);
                }

                setResult(123);
                finish();
                break;
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            // 按下BACK，同时没有重复
//            return true;
////            Log.d(TAG, "onKeyDown()");
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
}
