package cn.fengwoo.sealsteward.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.white.easysp.EasySP;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DfuService;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.Utils;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * 关于
 */
public class DfuActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.test)
    Button test;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_content)
    TextView tvContent;

    @BindView(R.id.btn_dfu)
    Button btnDfu;

    private String dfu_macAddress;
    private Uri uri = null;
    private String path; // 固件包路径
    private ProgressDialog pd;

    private String dfu_version;
    private String dfu_file_name;
    private String dfu_content;

    private boolean isSecDfu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfu);
        ButterKnife.bind(this);



        initData();
        initView();

//        downloadZip();
        Utils.log(dfu_macAddress);
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("固件升级");
        tvVersion.setText("版本：" + dfu_version);
        tvContent.setText(dfu_content);
    }

    private void initData() {
        dfu_macAddress = EasySP.init(this).getString("mac");
        dfu_file_name = EasySP.init(this).getString("dfu_file_name");
        dfu_version = EasySP.init(this).getString("dfu_version");
        dfu_content = EasySP.init(this).getString("dfu_content");

    }

    private void downloadZip() {
        HttpDownloader.downloadDfuZip(this, dfu_file_name, new DownloadImageCallback() {
            @Override
            public void onResult(String fileName) {
                super.onResult(fileName);
                Utils.log("filename:" + fileName);
                path = "file://" + HttpDownloader.path + dfu_file_name;
                uri = Uri.parse(path);
                dfu(uri);
//                showToast("try");
            }
        });
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
            Logger.d("TEST" + "onEnablingDfuMode: isSecDfu" + deviceAddress);
            dfu_macAddress = "FF:FF:FF:FF:FF:FF";
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dfu(uri);
//                    showToast("retry");
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
            pd.setProgress(percent);
            isSecDfu = true;
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
            showToast("固件升级完成");
            pd.dismiss();
            boolean result = Utils.deleteFile(path);
            Utils.log("" + result);
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Logger.d("TEST" + "onDfuAborted: " + deviceAddress + "isSecDfu" + isSecDfu);
            if (isSecDfu) {
                showToast("升级失败，请重试。");
                pd.dismiss();
            }

        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Logger.d("TEST" + "onError: " + deviceAddress + ",message:" + message + "isSecDfu" + isSecDfu);
//            showToast("TEST" + "onError: " + deviceAddress + ",message:" + message + "isSecDfu" + isSecDfu);

            if (isSecDfu) {
                showToast("升级失败，请重试。");
                pd.dismiss();
            }
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


    private void showProgress() {
        // 进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("固件升级中...");
        pd.setCanceledOnTouchOutside(false);
        pd.setMax(100);
        pd.setCancelable(false);
        // 监听返回键--防止下载的时候点击返回
        pd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    Toast.makeText(DfuActivity.this, "正在固件升级，请稍后", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });
        pd.show();
    }

    @OnClick({R.id.test, R.id.btn_dfu,R.id.set_back_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btn_dfu:
                downloadZip();
                showProgress();
                break;
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.test:
//                path = "file://" + HttpDownloader.path + "a.zip";
//                uri = Uri.parse(path);
//                dfu(uri);
//                showProgress();
                break;
        }
    }
}
