package cn.fengwoo.sealsteward.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import cn.fengwoo.sealsteward.utils.Utils;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * 关于
 */
public class DfuActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.title_tv)TextView title_tv;
    @BindView(R.id.set_back_ll)LinearLayout set_back_ll;

    @BindView(R.id.test)Button test;


//    private BluetoothService mBluetoothService;
    private String dfu_macAddress;

    private Uri uri = null;
    private String path;
    public static final String path1 = "/sdcard/SealDownImage/";// sd路径


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
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.test:


                path = "file://" + path1 + "a.zip";
                uri = Uri.parse(path);

//                tv_show.setText(path);
//                if (requestCode == 1) {
//                    progressBar.setVisibility(View.VISIBLE);

// You may use the controller to pause, resume or abort the DFU process.

                    dfu(uri);


                break;
        }
    }




    private void dfu(Uri uri) {
        final DfuServiceInitiator starter = new DfuServiceInitiator(dfu_macAddress)
//                .setDeviceName(mBluetoothService.getName())
                .setKeepBond(true);


// If you want to have experimental buttonless DFU feature supported call additionally:
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
// but be aware of this: https://devzone.nordicsemi.com/question/100609/sdk-12-bootloader-erased-after-programming/
// and other issues related to this experimental service.

// Init packet is required by Bootloader/DFU from SDK 7.0+ if HEX or BIN file is given above.
// In case of a ZIP file, the init packet (a DAT file) must be included inside the ZIP file.
//            if (mFileType == DfuService.TYPE_AUTO)
        starter.setZip(uri, path);
        Logger.d("uri:" + uri);
        Logger.d("path:" + path);
//            else {
//                starter.setBinOrHex(mFileType, mFileStreamUri, mFilePath).setInitFile(mInitFileStreamUri, mInitFilePath);
//            }
        final DfuServiceController controller = starter.start(this, DfuService.class);
    }

    private final DfuProgressListener dfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
//          progressBar.setIndeterminate(true);
//          mTextPercentage.setText(R.string.dfu_status_connecting);
            Log.i("TEST", "onDeviceConnecting: " + deviceAddress);
            Logger.d("TEST" + "onDeviceConnecting: " + deviceAddress);

        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            Log.i("TEST", "onDeviceConnected: " + deviceAddress);
            Logger.d("TEST" + "onDeviceConnected: " + deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
//          progressBar.setIndeterminate(true);
//          mTextPercentage.setText(R.string.dfu_status_starting);
            Log.i("TEST", "onDfuProcessStarting: " + deviceAddress);
            Logger.d("TEST" + "onDfuProcessStarting: " + deviceAddress);


        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Log.i("TEST", "onDfuProcessStarted: " + deviceAddress);
            Logger.d("TEST" + "onDfuProcessStarted: " + deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Log.i("TEST", "onEnablingDfuMode: " + deviceAddress);
            Logger.d("TEST" + "onEnablingDfuMode: " + deviceAddress);
//            mBluetoothService.scanAndConnect5("FF:FF:FF:FF:FF:FF", 15000);
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
            Log.i("TEST", "onProgressChanged: " + deviceAddress + "百分比" + percent + ",speed "
                    + speed + ",avgSpeed " + avgSpeed + ",currentPart " + currentPart
                    + ",partTotal " + partsTotal);

            Logger.d("TEST" + "onProgressChanged: " + deviceAddress + "百分比" + percent + ",speed "
                    + speed + ",avgSpeed " + avgSpeed + ",currentPart " + currentPart
                    + ",partTotal " + partsTotal);
//            tv_show.setText("升级进度：" + percent + "%");
            Utils.log(percent +"");
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            Log.i("TEST", "onFirmwareValidating: " + deviceAddress);
            Logger.d("TEST", "onFirmwareValidating: " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            Log.i("TEST", "onDeviceDisconnecting: " + deviceAddress);
            Logger.d("TEST", "onDeviceDisconnecting: " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            Log.i("TEST", "onDeviceDisconnected: " + deviceAddress);
            Logger.d("TEST", "onDeviceDisconnected: " + deviceAddress);
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            Log.i("TEST", "onDfuCompleted: " + deviceAddress);
            Logger.d("TEST", "onDfuCompleted: " + deviceAddress);
//          progressBar.setIndeterminate(true);
//            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Log.i("TEST", "onDfuAborted: " + deviceAddress);
            Logger.d("TEST" + "onDfuAborted: " + deviceAddress);
//            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Log.i("TEST", "onError: " + deviceAddress + ",message:" + message);
            Logger.d("TEST" + "onError: " + deviceAddress + ",message:" + message);
//            progressBar.setVisibility(View.GONE);
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
//        if (mBluetoothService != null) {
////            unbindService();
//            //  mBluetoothService = null;
//        }
    }


    private void bindService() {
//        Intent bindIntent = new Intent(this, BluetoothService.class);
//        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }


//    private void unbindService() {
//        this.unbindService(mFhrSCon);
//    }

//
//    private ServiceConnection mFhrSCon = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
//            mBluetoothService.setScanCallback(callback);
//
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

}
