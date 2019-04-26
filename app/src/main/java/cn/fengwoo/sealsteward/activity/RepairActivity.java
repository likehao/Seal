package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.white.easysp.EasySP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReplayingShare;
import cn.fengwoo.sealsteward.utils.Utils;
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
 * 关于
 */
public class RepairActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.et_filter)
    EditText etFilter;
    @BindView(R.id.seal_lv)
    ListView sealLv;
    private ResponseInfo<List<SealData>> responseInfo;
    private ArrayAdapter<String> arrayAdapter = null;
    private ArrayList<String> arrayList = new ArrayList<String>();
    private List<SealData> sealDataList = new ArrayList<>();



    private RxBleClient rxBleClient;
    private Disposable scanSubscription;
    private Disposable connectDisposable;
    private Observable<RxBleConnection> connectionObservable;
    private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();

    private boolean isAddNewSeal = false;

    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);
        ButterKnife.bind(this);
        initData();
        initView();
        getSealList();
    }

    private void initData() {
        rxBleClient = RxBleClient.create(this);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        sealLv.setAdapter(arrayAdapter);
        sealLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Utils.log(sealDataList1.get(position).getMac());
                connectSeal(sealDataList.get(position).getMac(),sealDataList.get(position));
            }
        });
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("印章列表");
        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.log(s.toString());
                // filter
                 ArrayList<String> arrayList1 = new ArrayList<String>();
                 List<SealData> sealDataList1 = new ArrayList<>();

                for (SealData sealData : sealDataList) {
                    if (sealData.getName().contains(s)) {
                        arrayList1.add(sealData.getName());
                        sealDataList1.add(sealData);
                    }
                }
                arrayAdapter = new ArrayAdapter<String>(RepairActivity.this, android.R.layout.simple_list_item_1, arrayList1);
                sealLv.setAdapter(arrayAdapter);
                sealLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Utils.log(sealDataList1.get(position).getMac());
                        connectSeal(sealDataList1.get(position).getMac(),sealDataList1.get(position));
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


   private void connectSeal(String mac,SealData sealData) {

       // 断开蓝牙
//       ((MyApp) getApplication()).removeAllDisposable();
//       ((MyApp) getApplication()).setConnectionObservable(null);

       showLoadingView();

       String thisMac = mac;
       EasySP.init(this).getString("mac", thisMac);



       // 连接ble设备
       String macAddress = mac;
       RxBleDevice device = rxBleClient.getBleDevice(macAddress);
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
                           if (sealData.getDataProtocolVersion().equals("2.0")) {
                               EasySP.init(this).putString("dataProtocolVersion", "2");
                           } else {
                               EasySP.init(this).putString("dataProtocolVersion", "3");
                           }

                           // save mac & seal id
                           EasySP.init(this).putString("mac", mac);

                           if (!isAddNewSeal) {
                               // 不是添加新的印章，就是连接原来的seal,保存sealId
                               EasySP.init(this).putString("currentSealId", sealData.getId());

                               EasySP.init(this).putBoolean("enableEnclosure", sealData.isEnableEnclosure());

                               if (sealData.getSealEnclosure() != null) {
                                   EasySP.init(this).putString("scope", sealData.getSealEnclosure().getScope() + "");
                                   EasySP.init(this).putString("latitude", sealData.getSealEnclosure().getLatitude() + "");
                                   EasySP.init(this).putString("longitude", sealData.getSealEnclosure().getLongitude() + "");
                               }
                           }

                           if (isAddNewSeal) {
                               intent = new Intent(this, AddSealActivity.class);
                               intent.putExtra("mac", macAddress);
                               startActivity(intent);
                               finish();
                           } else {
                               Intent intent = new Intent();
                               intent.putExtra("bleName", sealData.getName());
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
    }



    private void getSealList() {
        loadingView.show();
        HttpUtil.sendDataAsync(this, HttpUrl.SEAL_LIST, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                Toast.makeText(RepairActivity.this, e + "", Toast.LENGTH_SHORT).show();
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
                                for (SealData sealData : responseInfo.getData()) {
                                    arrayList.add(sealData.getName());
                                    sealDataList.add(sealData);
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        loadingView.cancel();
                    } else {
                        //更新
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                scanBle();
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

    @OnClick({R.id.set_back_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }


    private Observable<RxBleConnection> prepareConnectionObservable(RxBleDevice bleDevice) {
        return bleDevice
                .establishConnection(true)
//                .retry(4)
//                .delay(1, TimeUnit.SECONDS)
                .takeUntil(disconnectTriggerSubject)
                .compose(ReplayingShare.instance());
    }

}
