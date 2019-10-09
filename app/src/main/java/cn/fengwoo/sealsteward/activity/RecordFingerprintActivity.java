package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 录入指纹
 */
public class RecordFingerprintActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.finger_back_bt)
    Button finger_back_bt;
    @BindView(R.id.fingerprint_iv)
    ImageView fingerprint_iv;
    @BindView(R.id.fingerprint_count_tv)
    TextView fingerprint_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_fingerprint2);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        back.setVisibility(View.VISIBLE);
        title.setText("录入指纹");
        back.setOnClickListener(this);
        finger_back_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.finger_back_bt:
                finish();
                break;
        }
    }

    //蓝牙返回添加指纹成功指令监听
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.msgType){
            case "first_fingerprint":
                fingerprint_tv.setText("请第2次录入指纹");
                break;
            case "second_fingerprint":
                fingerprint_tv.setText("请第3次录入指纹");
                break;
            case "third_fingerprint":
                fingerprint_tv.setText("请第4次录入指纹");
                break;
            case "add_fingerprint":
                fingerprint_iv.setBackgroundResource(R.drawable.fingerprint);
                finger_back_bt.setVisibility(View.VISIBLE);
                fingerprint_tv.setText("指纹录入完成");
                break;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
