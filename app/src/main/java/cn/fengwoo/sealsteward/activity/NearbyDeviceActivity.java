package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.squareup.picasso.Picasso;
import com.white.easysp.EasySP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.SealAdapter;
import cn.fengwoo.sealsteward.database.SealItemBean;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealData;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
    private Disposable disposable;
    private boolean isAddNewSeal;

    private ResponseInfo<List<SealData>> responseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_device);

        ButterKnife.bind(this);
        getIntentData();
        initView();
        initCheck();

        // 不是增加印章的情况时，访问后台拿seal list
        if (!isAddNewSeal) {
            getSealList();
        }

        initData();
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

    }

    private void scanBle() {
        // ble 设备扫描
        scanSubscription = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        scanResult -> {
                            addScanResult(scanResult);
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );

    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("附近的设备");
    /*    switchButton = findViewById(R.id.switch_bt);
        switchButton.setChecked(true);*/

    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
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
//        //注册广播接收器
//        registerReceiver(registerReceiver, Utils.intentFilter());
//        //开始扫描
//        startDiscovery();

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

    /**
     * 注册广播
     */
    private BroadcastReceiver registerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                showToast("正在扫描到列表...");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device != null) {
                            addBluetooth(device);
                        }
                    }
                });
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                showToast("扫描结束.");
            }
        }
    };


    //扫描到蓝牙添加到列表中
    private void addScanResult(ScanResult scanResult) {
        //蓝牙设备名字和Mac地址
        String deviceName = scanResult.getBleDevice().getName();
        String deviceMac = scanResult.getBleDevice().getMacAddress();
        //过滤蓝牙
        if (deviceName != null && (deviceName.equals("BLE-baihe") || deviceName.contains("BHQKL"))) {
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
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(thisMac)) {
                hasTheSealTag = true;
            }
        }
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


    //扫描到蓝牙添加到列表中
    private void addBluetooth(BluetoothDevice device) {
//        //蓝牙设备名字和Mac地址
//        String deviceName = device.getName();
//        String deviceMac = device.getAddress();
//        //过滤蓝牙
//        if (deviceName != null && deviceName.equals("BLE-baihe")) {
//            String itemName = "";
//            if (!isAddNewSeal) {
//                // 如果不是增加新设备的情况，如果seal list里存在这个seal，替换掉名字
////                for (SealData sealData : responseInfo.getData()) {
////                    if (sealData.getMac().equals(device.getAddress())) {
////                        itemName = sealData.getName();
////                    }
////                }
//                if (hasTheSeal(device.getAddress())) {
//                    itemName = getNameFromList(device.getAddress());
//                } else {
//                    itemName = deviceName + "->" + deviceMac;
//                }
//            } else {
//                itemName = deviceName + "->" + deviceMac;
//            }
//            if (!arrayList.contains(itemName)) {
//                arrayList.add(itemName);
//                arrayAdapter.notifyDataSetChanged();
//            }
//        }
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
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Utils.log(scanResultsList.get(position).getBleDevice().getMacAddress());

        String thisMac = scanResultsList.get(position).getBleDevice().getMacAddress();

        if (!hasTheSeal(thisMac)) {
            showToast("你没有权限操作这个印章。");
            return;
        }





        String macAddress = scanResultsList.get(position).getBleDevice().getMacAddress();
        RxBleDevice device = rxBleClient.getBleDevice(macAddress);
        disposable = device.establishConnection(false) // <-- autoConnect flag
                .subscribe(
                        rxBleConnection -> {
                            // All GATT operations are done through the rxBleConnection.


                            // sava dataProtocolVersion
                            // 根据 ble 名字来判断 dataProtocolVersion
                            if(scanResultsList.get(position).getBleDevice().getName().contains("BHQKL")){
                                EasySP.init(this).putString("dataProtocolVersion", "3");
                            }else{
                                EasySP.init(this).putString("dataProtocolVersion", "2");
                            }

                            // save mac
                            EasySP.init(this).putString("mac", scanResultsList.get(position).getBleDevice().getMacAddress());


                            if (isAddNewSeal) {
                                intent = new Intent(this, AddSealActivity.class);
                                intent.putExtra("mac", macAddress);
                                startActivity(intent);
                                finish();
                            } else {
                                finish();
                            }
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );
    }

    @Override
    public void onDestroy() {
//        unregisterReceiver(registerReceiver);
        super.onDestroy();
        if (scanSubscription != null){
            scanSubscription.dispose();
        }
    }
}
