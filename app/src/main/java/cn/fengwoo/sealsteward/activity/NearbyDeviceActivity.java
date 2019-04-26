package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.white.easysp.EasySP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.SealAdapter;
import cn.fengwoo.sealsteward.database.SealItemBean;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReplayingShare;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.CustomDialog;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 附近设备
 */
public class NearbyDeviceActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.on_off_checkBox)
    CheckBox on_off_checkBox;
    @BindView(R.id.seal_lv)
    ListView seal_lv;

    @BindView(R.id.tv_repair)
    TextView tv_repair;

    private com.suke.widget.SwitchButton switchButton;
    //当前安卓是否支持
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1000;
    private static final int REQUEST_COARSE_LOCATION = 0;
    //蓝牙适配器
    private static BluetoothAdapter mBluetoothAdapter = null;
    //listview绑定的数组
    private ArrayList<String> arrayList = new ArrayList<String>();

    private List<ScanResult> scanResultsList = new ArrayList<>();

    private ArrayAdapter<String> arrayAdapter = null;
    private List<SealItemBean> lists;
    private SealAdapter adapter;
    private Intent intent;
    boolean b;

    private RxBleClient rxBleClient;
    private Disposable scanSubscription;
    private Disposable connectDisposable;
    private boolean isAddNewSeal;
    private ResponseInfo<List<SealData>> responseInfo;

    private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();

    private Observable<RxBleConnection> connectionObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_device);

        ButterKnife.bind(this);
        initView();
        getIntentData();
        initCheck();
        initData();
        // 不是增加印章的情况时，访问后台拿seal list
        if (!isAddNewSeal) {
            getSealList();
        } else {
            scanBle();
        }

        setListener();
//        scanBle();
    }

    private void getSealList() {
        loadingView.show();
        HttpUtil.sendDataAsync(this, HttpUrl.SEAL_LIST, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                Toast.makeText(NearbyDeviceActivity.this, e + "", Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG", "获取个人信息数据失败........");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Utils.log("getSealList" + result);
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<SealData>>>() {
                }.getType());
                try {
                    JSONObject object = new JSONObject(result);
                    if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                        //更新
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scanBle();
                            }
                        });
                        loadingView.cancel();
                    } else {
                        //更新
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scanBle();
                            }
                        });
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(responseInfo.getMessage());
                        Looper.loop();
                        Log.e("TAG", "获取个人信息数据失败........");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getIntentData() {
        isAddNewSeal = getIntent().getBooleanExtra("isAddNewSeal", false);
        if (isAddNewSeal) {
            title_tv.setText("添加印章");
        }
    }

    private void scanBle() {
        // ble 设备扫描
        scanSubscription = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        scanResult -> {
                            addScanResult(scanResult);
                            Utils.log("mac:" + scanResult.getBleDevice().getMacAddress());
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("连接印章");
    /*    switchButton = findViewById(R.id.switch_bt);
        switchButton.setChecked(true);*/

    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
        tv_repair.setOnClickListener(this);
        seal_lv.setOnItemClickListener(this);
        b = on_off_checkBox.isChecked();
    }

    /**
     * 初始化蓝牙权限
     */
    private void initCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    showToast("蓝牙6.0需要该权限");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                return;
            }
        }
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //获取蓝牙适配器
        //请求打开蓝牙
        Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //请求开启蓝牙
        this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
    }

    private void initData() {


        rxBleClient = RxBleClient.create(this);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        seal_lv.setAdapter(arrayAdapter);


    }

    /**
     * 开启扫描
     */
    private void startDiscovery() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.startDiscovery();
        }
    }

    /**
     * 停止扫描
     */
    private void stopDiscovery() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }


    //扫描到蓝牙添加到列表中
    private void addScanResult(ScanResult scanResult) {
        //蓝牙设备名字和Mac地址
        String deviceName = scanResult.getBleDevice().getName();
        String deviceMac = scanResult.getBleDevice().getMacAddress();
        //过滤蓝牙
//        if (deviceName != null && (deviceName.equals("BLE-baihe") || deviceName.contains("BHQKL"))) {
        if (true) {
            String itemName = "";
            if (!isAddNewSeal) {
                // 如果不是增加新设备的情况，如果seal list里存在这个seal，替换掉名字
                if (hasTheSeal(deviceMac)) {
                    itemName = getNameFromList(deviceMac);
                } else {
                    itemName = deviceName + "->" + deviceMac;
                }
            } else {
                itemName = deviceName + "->" + deviceMac;
            }
            if (!arrayList.contains(itemName)) {
                arrayList.add(itemName);
                scanResultsList.add(scanResult);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean hasTheSeal(String thisMac) {
        boolean hasTheSealTag = false;
        if (responseInfo.getCode() == 0) {
            for (SealData sealData : responseInfo.getData()) {
                if (sealData.getMac().equals(thisMac)) {
                    hasTheSealTag = true;
                }
            }
        }
//        else {
//            hasTheSealTag = false; // 如果responseInfo为空时，hasTheSealTag为false
//        }
        return hasTheSealTag;
    }

    private String getNameFromList(String thisMac) {
        String name = "";
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(thisMac)) {
                name = sealData.getName();
            }
        }
        return name;
    }

    private String getSealIdFromList(String thisMac) {
        String id = "";
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(thisMac)) {
                id = sealData.getId();
            }
        }
        return id;
    }


    private SealData.SealEnclosureBean getSealEnclosure(String thisMac) {
        SealData.SealEnclosureBean sealEnclosureBean = new SealData.SealEnclosureBean();
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(thisMac)) {
                if (sealData != null) {
                    sealEnclosureBean = sealData.getSealEnclosure();
                }
            }
        }
        return sealEnclosureBean;
    }

    private boolean getTargetSealEnableEnclosure(String thisMac) {
        boolean enableEnclosure = false;
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(thisMac)) {
                if (sealData != null) {
                    enableEnclosure = sealData.isEnableEnclosure();
                }
            }
        }
        return enableEnclosure;
    }

    //扫描到蓝牙添加到列表中
    private void addBluetooth(BluetoothDevice device) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.on_off_checkBox:
                if (b) {
                    mBluetoothAdapter.enable();
                }
                break;
            case R.id.tv_repair:
                if (scanSubscription != null) {
                    scanSubscription.dispose();
                }
                startActivityForResult(new Intent(this, RepairActivity.class),Constants.TO_NEARBY_DEVICE);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 断开蓝牙
        ((MyApp) getApplication()).removeAllDisposable();
        ((MyApp) getApplication()).setConnectionObservable(null);

        showLoadingView();
        Utils.log(scanResultsList.get(position).getBleDevice().getMacAddress());

        String thisMac = scanResultsList.get(position).getBleDevice().getMacAddress();

        EasySP.init(this).getString("mac", thisMac);
//        EasySP.init(this).getString("mac", "00:15:84:00:01:67");

        if (responseInfo!=null&&!hasTheSeal(thisMac)) {
//            showToast("你没有权限操作这个印章。");
            showDialog();
            cancelLoadingView();
            // 停止扫描
            if (scanSubscription != null) {
                scanSubscription.dispose();
            }
            return;
        }


        // 连接ble设备

        String macAddress = scanResultsList.get(position).getBleDevice().getMacAddress();

        RxBleDevice device = rxBleClient.getBleDevice(macAddress);
//        RxBleDevice device = rxBleClient.getBleDevice("00:15:84:00:01:67");

        ((MyApp) getApplication()).setRxBleDevice(device);

        connectionObservable = prepareConnectionObservable(device);

        ((MyApp) getApplication()).setConnectionObservable(connectionObservable);

        connectDisposable = connectionObservable // <-- autoConnect flag
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        rxBleConnection -> {
                            // All GATT operations are done through the rxBleConnection.

                            cancelLoadingView();

                            Utils.log("connected");
                            // sava dataProtocolVersion
                            // 根据 ble 名字来判断 dataProtocolVersion
                            if (scanResultsList.get(position).getBleDevice().getName().contains("BHQKL")) {
                                EasySP.init(this).putString("dataProtocolVersion", "3");
                            } else {
                                EasySP.init(this).putString("dataProtocolVersion", "2");
                            }


                            // save mac & seal id
                            EasySP.init(this).putString("mac", scanResultsList.get(position).getBleDevice().getMacAddress());
//                            EasySP.init(this).putString("currentSealId", getSealIdFromList(scanResultsList.get(position).getBleDevice().getMacAddress()));
                            if (!isAddNewSeal) {
                                // 不是添加新的印章，就是连接原来的seal,保存sealId
                                EasySP.init(this).putString("currentSealId", getSealIdFromList(scanResultsList.get(position).getBleDevice().getMacAddress()));

                                EasySP.init(this).putBoolean("enableEnclosure", getTargetSealEnableEnclosure(scanResultsList.get(position).getBleDevice().getMacAddress()));

                                if (getSealEnclosure(scanResultsList.get(position).getBleDevice().getMacAddress()) != null) {
                                    EasySP.init(this).putString("scope", getSealEnclosure(scanResultsList.get(position).getBleDevice().getMacAddress()).getScope() + "");
                                    EasySP.init(this).putString("latitude", getSealEnclosure(scanResultsList.get(position).getBleDevice().getMacAddress()).getLatitude() + "");
                                    EasySP.init(this).putString("longitude", getSealEnclosure(scanResultsList.get(position).getBleDevice().getMacAddress()).getLongitude() + "");
                                }
                            }

                            if (isAddNewSeal) {
                                intent = new Intent(this, AddSealActivity.class);
                                intent.putExtra("mac", macAddress);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra("bleName", getNameFromList(macAddress));
                                setResult(Constants.TO_NEARBY_DEVICE, intent);
                                finish();
                            }
                        },
                        throwable -> {
                            // Handle an error here.
                            Utils.log(throwable.getMessage());
                        }
                );
        ((MyApp) getApplication()).getDisposableList().add(connectDisposable);

//        ((MyApp) getApplication()).setConnectDisposable(connectDisposable);
    }

    @Override
    public void onDestroy() {
//        unregisterReceiver(registerReceiver);
        if (scanSubscription != null) {
            scanSubscription.dispose();
        }
        super.onDestroy();


        rxBleClient = null;
    }


    private Observable<RxBleConnection> prepareConnectionObservable(RxBleDevice bleDevice) {
        return bleDevice
                .establishConnection(true)
//                .retry(4)
//                .delay(1, TimeUnit.SECONDS)
                .takeUntil(disconnectTriggerSubject)
                .compose(ReplayingShare.instance());
    }




    /**
     * 确认是否删除
     */
    private void showDialog() {
        final CustomDialog commonDialog = new CustomDialog(this, "提示", "您连接的印章不属于您所在的公司", "切换公司");
        commonDialog.cancel.setText("添加印章");
        commonDialog.showDialog();
        commonDialog.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Utils.log("rihgt");
                intent = new Intent(NearbyDeviceActivity.this, MyCompanyActivity.class);
                startActivity(intent);
                finish();
            }
        });
        commonDialog.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("left");
                if (!Utils.hasThePermission(NearbyDeviceActivity.this, Constants.permission1)) {
                    return;
                }
                intent = new Intent(NearbyDeviceActivity.this, NearbyDeviceActivity.class);
                intent.putExtra("isAddNewSeal", true);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.TO_NEARBY_DEVICE && resultCode == Constants.TO_NEARBY_DEVICE) {
            String bleName = data.getStringExtra("bleName");
            Intent intent = new Intent();
            intent.putExtra("bleName", bleName);
            setResult(Constants.TO_NEARBY_DEVICE, intent);
            finish();
        }
    }
}
