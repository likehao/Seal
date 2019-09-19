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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
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
    private CommonAdapter commonAdapter;
    private String sealConnect;


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
//        if (!isAddNewSeal) {
        if (true) {
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
//                Toast.makeText(NearbyDeviceActivity.this, e + "", Toast.LENGTH_SHORT).show();
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
//                        showToast(responseInfo.getMessage());
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
        sealConnect = getIntent().getStringExtra("应用模块连上就finish");
        if (isAddNewSeal) {
            title_tv.setText("添加印章");
            tv_repair.setVisibility(View.GONE);
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
                            Utils.log("scanResult:" + scanResult.getBleDevice().getName() + "   mac:" + scanResult.getBleDevice().getMacAddress());
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
        title_tv.setText("附近设备");
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
 /*       arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        seal_lv.setAdapter(arrayAdapter);*/

        commonAdapter = new CommonAdapter<String>(this, arrayList, R.layout.near_bluetooth_item) {
            @Override
            public void convert(ViewHolder viewHolder, String s, int i) {
                viewHolder.setText(R.id.bluetooth_tv, s);
            }

        };
        seal_lv.setAdapter(commonAdapter);
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
        if (deviceName != null && (deviceName.equals("BLE-baihe") || deviceName.contains("BHQKL"))) {
//        if (true) {
            String itemName = "";
            if (!isAddNewSeal) {
//            if (true) {
                // 如果不是增加新设备的情况，如果seal list里存在这个seal，替换掉名字
                if (hasTheSeal(deviceMac)) {
                    itemName = getNameFromList(deviceMac);
                } else {
                    itemName = deviceName + "->" + deviceMac;
                }
            } else {
//                itemName = deviceName + "->" + deviceMac;
                if (hasTheSeal(deviceMac)) {
                    return;
//                    itemName = getNameFromList(deviceMac);
                } else {
                    itemName = deviceName + "->" + deviceMac;
                }
            }
            if (!arrayList.contains(itemName)) {
                arrayList.add(itemName);
                scanResultsList.add(scanResult);
                commonAdapter.notifyDataSetChanged();
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
                break;
            }
        }
        return name;
    }

    /**
     * 遍历MAC拿到印模传回带到首页工作台
     *
     * @param mac
     * @return
     */
    private String getSealPrint(String mac) {
        String sealPrint = "";
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(mac)) {
                sealPrint = sealData.getSealPrint();
                break;
            }
        }
        return sealPrint;
    }

    private String getSealIdFromList(String thisMac) {
        String id = "";
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(thisMac)) {
                id = sealData.getId();
                break;
            }
        }
        return id;
    }

    private SealData getSealDataFromList(String thisMac) {
//        String id = "";
        SealData mSealData = new SealData();
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(thisMac)) {
                mSealData = sealData;
                break;
            }
        }
        return mSealData;
    }


    private String getSealNameFromList(String thisMac) {
        String name = "";
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(thisMac)) {
                name = sealData.getName();
                break;
            }
        }
        return name;
    }

    private SealData.SealEnclosureBean getSealEnclosure(String thisMac) {
        SealData.SealEnclosureBean sealEnclosureBean = new SealData.SealEnclosureBean();
        for (SealData sealData : responseInfo.getData()) {
            if (sealData.getMac().equals(thisMac)) {
                if (sealData != null) {
                    sealEnclosureBean = sealData.getSealEnclosure();
                    break;
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
                    break;
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
                startActivityForResult(new Intent(this, RepairActivity.class), Constants.TO_NEARBY_DEVICE);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String appkey = EasySP.init(this).getString("appkey");
        OkGo.<String>get(HttpUrl.URL + HttpUrl.DEVICE_ACCESS)
                .params("appKey", appkey.length() == 0 ? Constants.key : appkey)
                .params("mac", scanResultsList.get(position).getBleDevice().getMacAddress())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
//                                Utils.log("success:" + response.body().toString());
                        String result = response.body().toString();
                        if (result.contains("成功")) {
                            itemClick(position);
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                showToast(jsonObject.getString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        showToast("设备接入失败，请检查是否接入了网络");
                    }
                });
        }


    private void itemClick(int position){
        // 断开蓝牙
        ((MyApp) getApplication()).removeAllDisposable();
        ((MyApp) getApplication()).setConnectionObservable(null);

        showLoadingView();
        Utils.log(scanResultsList.get(position).getBleDevice().getMacAddress());

        String thisMac = scanResultsList.get(position).getBleDevice().getMacAddress();

        EasySP.init(this).getString("mac", thisMac);
//        EasySP.init(this).getString("mac", "00:15:84:00:01:67");

        if (!isAddNewSeal) {
            if (responseInfo != null && !hasTheSeal(thisMac)) {
//            showToast("你没有权限操作这个印章。");
                showDialog(thisMac);
                cancelLoadingView();
                // 停止扫描
                if (scanSubscription != null) {
                    scanSubscription.dispose();
                }
                return;
            }
        }

        if (!isAddNewSeal) {
            SealData sealData = getSealDataFromList(scanResultsList.get(position).getBleDevice().getMacAddress());
            long nowTime = System.currentTimeMillis();
            long expiredTime = sealData.getServiceTime();
            double dayCount = (double) (expiredTime - nowTime) / (1000 * 3600 * 24);
//        if(System.currentTimeMillis())
            if ((sealData.getServiceType() == 1) && (dayCount < 7) && (dayCount > 0)) {
                showServiceTip1(sealData, position);
                return;
            }

            if ((sealData.getServiceType() == 1) && (dayCount < 0)) {
                showServiceTip2(sealData, position);
                return;
            }
        }
        connectBle(position);
    }

    /**
     * 连接设备
     * @param position
     */
    private void connectBle(int position) {
        // 连接ble设备

        String macAddress = scanResultsList.get(position).getBleDevice().getMacAddress();

        RxBleDevice device = rxBleClient.getBleDevice(macAddress);
//        RxBleDevice device = rxBleClient.getBleDevice("00:15:84:00:01:67");

        ((MyApp) getApplication()).setRxBleDevice(device);

        if (scanResultsList.get(position).getBleDevice().getName().contains("BHQKL")) {
            // 用三期seal时不自动连接
            connectionObservable = prepareConnectionObservableWithoutAutoConnect(device);
        } else {
            // 用二期seal时不自动连接
            connectionObservable = prepareConnectionObservable(device);
        }

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

                                EasySP.init(this).putString("currentSealName", getSealNameFromList(scanResultsList.get(position).getBleDevice().getMacAddress()));

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

                                //应用模块功能连上后直接finish
                                if (sealConnect != null && sealConnect.equals("应用模块连上就finish")) {
                                    Intent intent = new Intent();
                                    intent.putExtra("bleName", getNameFromList(macAddress));
                                    intent.putExtra("sealPrint", getSealPrint(macAddress));
                                    intent.putExtra("applicationConnect","applicationConnect");
                                    setResult(Constants.TO_NEARBY_DEVICE, intent);
                                    finish();
                                }else {

                                    Intent intent = new Intent();
                                    intent.putExtra("bleName", getNameFromList(macAddress));
                                    intent.putExtra("sealPrint", getSealPrint(macAddress));
                                    setResult(Constants.TO_NEARBY_DEVICE, intent);
                                    finish();
                                }
                            }

                        },
                        throwable -> {
                            // Handle an error here.
                            Utils.log(throwable.getMessage());
                        }
                );
        ((MyApp) getApplication()).getDisposableList().add(connectDisposable);

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

    private Observable<RxBleConnection> prepareConnectionObservableWithoutAutoConnect(RxBleDevice bleDevice) {
        return bleDevice
                .establishConnection(false)
//                .retry(4)
//                .delay(1, TimeUnit.SECONDS)
                .takeUntil(disconnectTriggerSubject)
                .compose(ReplayingShare.instance());
    }


    /**
     * 确认是否删除
     */
    private void showDialog(String macAddress) {
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
                intent = new Intent(NearbyDeviceActivity.this, AddSealActivity.class);
                intent.putExtra("isAddNewSeal", true);
                intent.putExtra("mac", macAddress);
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

    /**
     *
     */
    private void showServiceTip1(SealData sealData, int position) {
        cancelLoadingView();

        String tipString = "此印章服务，将于" + "" + Utils.getDateToString(sealData.getServiceTime(), "yyyy-MM-dd HH:mm:ss") + "过期，请及时充值";
        final CustomDialog commonDialog = new CustomDialog(this, "提示", tipString, "去充值");
        commonDialog.cancel.setText("知道了");
        commonDialog.showDialog();
        commonDialog.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("left" + "cancel" + "去充值");

                intent = new Intent(NearbyDeviceActivity.this, PayActivity.class);
                intent.putExtra("sealId", sealData.getId());
                startActivity(intent);
                finish();
                commonDialog.dialog.dismiss();


            }
        });
        commonDialog.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.log("rihgt");
                connectBle(position);
                commonDialog.dialog.dismiss();
            }
        });
    }

    /**
     *
     */
    private void showServiceTip2(SealData sealData, int position) {
        cancelLoadingView();

        String tipString = "此印章服务，已于" + "" + Utils.getDateToString(sealData.getServiceTime(), "yyyy-MM-dd HH:mm:ss") + "过期，请充值";
        final CustomDialog commonDialog = new CustomDialog(this, "提示", tipString, "知道了");
        commonDialog.cancel.setText("去充值");
        commonDialog.showDialog();
        commonDialog.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("rihgt");
                commonDialog.dialog.dismiss();
            }
        });
        commonDialog.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("left" + "cancel" + "去充值");

                intent = new Intent(NearbyDeviceActivity.this, PayActivity.class);
                intent.putExtra("sealId", sealData.getId());
                startActivity(intent);
                finish();
                commonDialog.dialog.dismiss();
            }
        });
    }
}
