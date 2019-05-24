package cn.fengwoo.sealsteward.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
    @BindView(R.id.ll_show_button)
    LinearLayout llShowButton;
    @BindView(R.id.tv_current_version)
    TextView tvCurrentVersion;
    @BindView(R.id.ll_hide_button)
    LinearLayout llHideButton;

    private String dfu_macAddress;
    private Uri uri = null;
    private String path; // 固件包路径
    private ProgressDialog pd;

    private String dfu_version;
    private String dfu_file_name;
    private String dfu_content;
    private String dfu_current_version;

    private boolean isSecDfu = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfu);
        ButterKnife.bind(this);

        Toast.makeText(this,"dfu start...................",Toast.LENGTH_LONG).show();
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
        if (!TextUtils.isEmpty(dfu_current_version)) {
            tvCurrentVersion.setText("版本：" + dfu_current_version);
        }
        String state = EasySP.init(this).getString("hasNewDfuVersion", "0");

        if (state.equals("1")) {
            llShowButton.setVisibility(View.VISIBLE);
            llHideButton.setVisibility(View.GONE);
        } else {
            llShowButton.setVisibility(View.GONE);
            llHideButton.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        dfu_macAddress = EasySP.init(this).getString("mac");
        dfu_file_name = EasySP.init(this).getString("dfu_file_name");
        dfu_version = EasySP.init(this).getString("dfu_version");
        dfu_content = EasySP.init(this).getString("dfu_content");
        dfu_current_version = EasySP.init(this).getString("dfu_current_version");

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
//        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
//	starter.setPacketsReceiptNotificationsValue(DfuServiceInitiator.DEFAULT_PRN_VALUE);
            starter.setPacketsReceiptNotificationsValue(10);
//            Log.e(TAG, "Android SDK < 26 (8.0) set PRN to 10");
        } else {
            starter.setPacketsReceiptNotificationsValue(4);
//            starter.setForeground(true);

//            Log.e(TAG, "Android SDK >= 26 (8.0) set PRN to 4");
        }

        // If you want to have experimental buttonless DFU feature supported call additionally:
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);

        starter.setZip(uri, path);
        Logger.d("mac:" + dfu_macAddress  + "   uri:" + uri + "   path:" + path);

        final DfuServiceController controller = starter.start(this, DfuService.class);
    }

    private final DfuProgressListener dfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            Logger.d("TESTS" + "onDeviceConnecting: " + deviceAddress);
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            Logger.d("TESTS" + "onDeviceConnected: " + deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            Logger.d("TESTS" + "onDfuProcessStarting: " + deviceAddress);
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Logger.d("TESTS" + "onDfuProcessStarted: " + deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Logger.d("TESTS" + "onEnablingDfuMode: mac:" + deviceAddress);
            Logger.d("TESTS" + "onEnablingDfuMode: isSecDfu" + isSecDfu);
            dfu_macAddress = "FF:FF:FF:FF:FF:FF";
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Logger.d("TESTS" + "第二次dfu" + deviceAddress);
                    dfu(uri);
//                    showToast("retry");
                }
            }, 8000);
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            Logger.d("TESTS" + "onProgressChanged: " + deviceAddress + "百分比" + percent + ",speed "
                    + speed + ",avgSpeed " + avgSpeed + ",currentPart " + currentPart
                    + ",partTotal " + partsTotal);
//            tv_show.setText("升级进度：" + percent + "%");
            Utils.log(percent + "");
            pd.setProgress(percent);
            isSecDfu = true;
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            Logger.d("TESTS", "onFirmwareValidating: " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            Logger.d("TESTS", "onDeviceDisconnecting: " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            Logger.d("TESTS", "onDeviceDisconnected: " + deviceAddress);
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            Logger.d("TESTS", "onDfuCompleted: " + deviceAddress);
            showToast("固件升级完成");
            pd.dismiss();
            boolean result = Utils.deleteFile(path);
            Utils.log("" + result);
            EasySP.init(DfuActivity.this).putString("hasNewDfuVersion", "0");
            finish();
            handler.removeCallbacksAndMessages(null);
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Logger.d("TESTS" + "onDfuAborted: " + deviceAddress + "isSecDfu" + isSecDfu);
            if (isSecDfu) {
                showToast("升级失败，请重试。");
                pd.dismiss();
            }
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Logger.d("TESTS" + "        onError: " + message + "         message:" + message + "     isSecDfu" + isSecDfu);
//            showToast("TESTS" + "onError: " + deviceAddress + ",message:" + message + "isSecDfu" + isSecDfu);

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

    @OnClick({R.id.test, R.id.btn_dfu, R.id.set_back_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btn_dfu:
                downloadZip();
                showProgress();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showToast("升级失败，请重试。");
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                },150000);
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
