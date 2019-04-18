package cn.fengwoo.sealsteward.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.white.easysp.EasySP;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DfuService;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.Utils;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * 关于
 */
public class DfuActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.test)
    Button test;

    private String dfu_macAddress;
    private Uri uri = null;
    private String path; // 固件包路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfu);
        ButterKnife.bind(this);
        initView();
        dfu_macAddress = EasySP.init(this).getString("mac");
        Utils.log(dfu_macAddress);
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("固件升级");
        set_back_ll.setOnClickListener(this);
        test.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.test:
                path = "file://" + HttpDownloader.path + "a.zip";
                uri = Uri.parse(path);
                dfu(uri);
                break;
        }
    }

    private void dfu(Uri uri) {
        final DfuServiceInitiator starter = new DfuServiceInitiator(dfu_macAddress).setKeepBond(true);
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        starter.setZip(uri, path);
        Logger.d("uri:" + uri);
        Logger.d("path:" + path);
        final DfuServiceController controller = starter.start(this, DfuService.class);
    }

    private final DfuProgressListener dfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            Logger.d("TEST" + "onDeviceConnecting: " + deviceAddress);
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            Logger.d("TEST" + "onDeviceConnected: " + deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            Logger.d("TEST" + "onDfuProcessStarting: " + deviceAddress);
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Logger.d("TEST" + "onDfuProcessStarted: " + deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Logger.d("TEST" + "onEnablingDfuMode: " + deviceAddress);
            dfu_macAddress = "FF:FF:FF:FF:FF:FF";
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dfu(uri);
                }
            }, 8000);
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            Logger.d("TEST" + "onProgressChanged: " + deviceAddress + "百分比" + percent + ",speed "
                    + speed + ",avgSpeed " + avgSpeed + ",currentPart " + currentPart
                    + ",partTotal " + partsTotal);
//            tv_show.setText("升级进度：" + percent + "%");
            Utils.log(percent + "");
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            Logger.d("TEST", "onFirmwareValidating: " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            Logger.d("TEST", "onDeviceDisconnecting: " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            Logger.d("TEST", "onDeviceDisconnected: " + deviceAddress);
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            Logger.d("TEST", "onDfuCompleted: " + deviceAddress);
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Logger.d("TEST" + "onDfuAborted: " + deviceAddress);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Logger.d("TEST" + "onError: " + deviceAddress + ",message:" + message);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
