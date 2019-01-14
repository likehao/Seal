package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.SealAdapter;
import cn.fengwoo.sealsteward.database.SealItemBean;
import cn.fengwoo.sealsteward.utils.Utils;

/**
 * 附近设备
 */
public class NearbyDeviceActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.add_iv)
    ImageView add_iv;
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
    private ArrayAdapter<String> arrayAdapter = null;
    private List<SealItemBean> lists;
    private SealAdapter adapter;
    private Intent intent;
    boolean b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_device);

        ButterKnife.bind(this);
        initView();
        initCheck();
        initData();
        setListener();
    }

    private void initView() {
        scan_ll.setVisibility(View.GONE);
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
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //获取蓝牙适配器
        //请求打开蓝牙
        Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //请求开启蓝牙
        this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
    }

    private void initData() {
        //注册广播接收器
        registerReceiver(registerReceiver, Utils.intentFilter());
        //开始扫描
        startDiscovery();
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
    private void addBluetooth(BluetoothDevice device) {
        //蓝牙设备名字和Mac地址
        String deviceName = device.getName();
        String deviceMac = device.getAddress();
        //过滤蓝牙
        if (deviceName != null && deviceName.equals("BLE-baihe")) {
            String itemName = deviceName + "->" + deviceMac;
            if (!arrayList.contains(itemName)) {
                arrayList.add(itemName);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.on_off_checkBox:
                if (b){
                    mBluetoothAdapter.enable();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        intent = new Intent(this, AddSealActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(registerReceiver);
        super.onDestroy();
    }
}
