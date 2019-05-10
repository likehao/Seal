package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.hjq.toast.ToastUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.SealData;
import cn.fengwoo.sealsteward.entity.UpdateEnclosureData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 地理围栏
 */
public class GeographicalFenceActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.et_address)
    EditText mEtAddress;
    @BindView(R.id.widthSeekBar)
    SeekBar mWidthSeekBar;

    @BindView(R.id.tv_radius)
    TextView tv_radius;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_save)
    TextView tvSave;

    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.search_ll)
    LinearLayout search_ll;

    private Intent intent;
    private LocationClient locationClient = null;
    private String locationAddress; //定位地址
    private BaiduMap baiduMap = null;
    private Double latitude, longitude;

    private String currentAddr;
    private String selectAddress;
    private LatLng selectPoint;
    private int radioInt = 1000;
    private String scopeString, sealIdString, longitudeString, latitudeString, addressString;
    private OnGetGeoCoderResultListener listener;

    private LatLng latLng;

    private SuggestionSearch mSuggestionSearch;

    private ArrayList<String> arrayList = new ArrayList<String>();

    private ArrayAdapter<String> arrayAdapter;

    private List<SuggestionResult.SuggestionInfo> suggestionInfos = new ArrayList<>();

    OnGetSuggestionResultListener onGetSuggestionResultListener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
            //处理sug检索结果
//            Utils.log("suggestionResult:" + suggestionResult.describeContents());
//            Utils.log("suggestionResult:" + suggestionResult.getAllSuggestions().get(0).getAddress());

            arrayList.clear();
            if (suggestionInfos != null) {
                suggestionInfos.clear();
            }

            suggestionInfos = suggestionResult.getAllSuggestions();
            if (suggestionInfos != null) {
                for (SuggestionResult.SuggestionInfo s : suggestionResult.getAllSuggestions()) {
                    arrayList.add(s.getKey());
                }
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_geographical_fence);
        ButterKnife.bind(this);
        initData();
        initView();

    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("取消");
        title_tv.setVisibility(View.GONE);
        search_ll.setVisibility(View.VISIBLE);
        //使用drawableLeft设置图片大小
        Drawable drawable = getResources().getDrawable(R.drawable.head_search);
        drawable.setBounds(5, 0, 40, 40);
        et_search.setCompoundDrawables(drawable, null, null, null);

        edit_tv.setOnClickListener(this);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // s为空时，lv设置为不可见
                if (TextUtils.isEmpty(s.toString())) {
                    lv.setVisibility(View.GONE);
                } else {
                    lv.setVisibility(View.VISIBLE);
                }

                Utils.log(s.toString());
                // et_search上面的内容改变，搜索地理名字
                mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
                        .city("深圳")
                        .keyword(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                latLng = suggestionInfos.get(position).getPt();
//                OverlayOptions ooCircle1 = new CircleOptions().fillColor(0xAA6495ED)
//                        .center(latLng).stroke(new Stroke(5, 0xAA00FF00))
//                        .radius(Integer.parseInt(scopeString));
//                baiduMap.addOverlay(ooCircle1);

                MapStatus.Builder builder = new MapStatus.Builder();
                LatLng latLng = suggestionInfos.get(position).getPt();
                builder.target(latLng).zoom(14.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                lv.setVisibility(View.GONE);
                //    et_search.setText(arrayList.get(position));
            }
        });

        //初始化地图
        baiduMap = mapView.getMap();

        UiSettings settings = baiduMap.getUiSettings();
        settings.setAllGesturesEnabled(true);   // false 关闭一切手势操作
        settings.setOverlookingGesturesEnabled(false);//屏蔽双指下拉时变成3D地图
        settings.setRotateGesturesEnabled(false);//屏蔽旋转

//        title_tv.setText("设置地理围栏");
        initLocationOption();
        mWidthSeekBar.setOnSeekBarChangeListener(this);

        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                selectPoint = point;
                baiduMap.clear();
                // 添加圆
                OverlayOptions ooCircle = new CircleOptions().fillColor(0xAA6495ED)
                        .center(point).stroke(new Stroke(5, 0xAA00FF00))
                        .radius(radioInt);
                baiduMap.addOverlay(ooCircle);
                LatLng llA = point;
                getAddress(point.latitude, point.longitude);

            }

            public boolean onMapPoiClick(MapPoi poi) {
                return false;
            }
        });

        if (longitudeString != null) {
            latLng = new LatLng(Double.parseDouble(latitudeString), Double.parseDouble(longitudeString));

            OverlayOptions ooCircle1 = new CircleOptions().fillColor(0xAA6495ED)
                    .center(latLng).stroke(new Stroke(5, 0xAA00FF00))
                    .radius(Integer.parseInt(scopeString));
            baiduMap.addOverlay(ooCircle1);
        }
    }

    private void initData() {
        mWidthSeekBar.setProgress(1);
        scopeString = getIntent().getStringExtra("scope");
        sealIdString = getIntent().getStringExtra("sealId");

        longitudeString = getIntent().getStringExtra("longitude");
        latitudeString = getIntent().getStringExtra("latitude");
        addressString = getIntent().getStringExtra("address");


        if (addressString != null && addressString.length() > 1) {
            currentAddr = addressString;
            mEtAddress.setText(currentAddr);

            mWidthSeekBar.setProgress(Integer.parseInt(scopeString) / 1000);
            tv_radius.setText(Integer.parseInt(scopeString) / 1000 + "km");
        }

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(onGetSuggestionResultListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.edit_tv:
                et_search.setText("");
//                et_search.setFocusable(false);
                lv.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 初始化定位参数配置
     */

    private void initLocationOption() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //检查是否开启GPS或网络
        if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            //开启GPS再返回界面
            intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        //定位初始化
        locationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        locationClient.registerLocationListener(locationListener);

        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");

        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);

        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);

        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);

        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);

        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);

        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);

        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);

        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);

        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);

        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);

        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();

        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);

        locationClient.setLocOption(locationOption);
//        ToastUtils.show("点击地图设置初始位置");
        if (addressString != null && addressString.length() > 1) {
            MapStatus.Builder builder = new MapStatus.Builder();
            LatLng latLng = new LatLng(Double.parseDouble(latitudeString), Double.parseDouble(longitudeString));
            builder.target(latLng).zoom(14.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        } else {
            //开始定位
            ToastUtils.show("点击地图设置初始位置");
//            ToastUtils.show("点击地图设置初始位置");
            // 开启定位
            permissions();
        }
    }


    /**
     * 申请权限
     */
    @SuppressLint("CheckResult")
    private void permissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //添加需要的权限
        rxPermissions.requestEachCombined(Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Utils.log("accept");
                            locationClient.start();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });
    }


    /**
     * 定位回调
     */
    private BDAbstractLocationListener locationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            currentAddr = bdLocation.getAddrStr();
            Utils.log(currentAddr);

            //获取定位类型、定位错误返回码
            int errorCode = bdLocation.getLocType();
            if (errorCode == BDLocation.TypeGpsLocation) {
                // GPS定位结果
                showToast(bdLocation.getAddrStr());
            } else if (errorCode == BDLocation.TypeNetWorkLocation) {
                // 网络定位结果
                Log.i("TAG", "网络定位成功!!!!!!!!!!!!!!!!!");

            } else if (errorCode == BDLocation.TypeOffLineLocation) {
                // 离线定位结果
                showToast(bdLocation.getAddrStr());
            } else if (errorCode == BDLocation.TypeServerError) {
                showToast("服务器错误，请检查");
                return;
            } else if (errorCode == BDLocation.TypeNetWorkException) {
                showToast("网络错误，请检查");
                return;
            } else if (errorCode == BDLocation.TypeCriteriaException) {
                showToast("手机模式错误，请检查是否飞行");
                return;
            }
            //获取经纬度信息
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            latLng = new LatLng(latitude, longitude);
            getAddress(latitude, longitude);
        }

        /**
         *  回调定位诊断信息
         * @param locType    定位类型
         * @param diagnosticType   诊断类型（1-9）
         * @param diagnosticMessage  具体诊断信息说明
         */
        @Override
        public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
            if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_GPS) {

                //建议打开GPS

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_WIFI) {

                //建议打开wifi，不必连接，这样有助于提高网络定位精度！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_LOC_PERMISSION) {

                //定位权限受限，建议提示用户授予APP定位权限！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_NET) {

                //网络异常造成定位失败，建议用户确认网络状态是否异常！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CLOSE_FLYMODE) {

                //手机飞行模式造成定位失败，建议用户关闭飞行模式后再重试定位！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_INSERT_SIMCARD_OR_OPEN_WIFI) {

                //无法获取任何定位依据，建议用户打开wifi或者插入sim卡重试！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_OPEN_PHONE_LOC_SWITCH) {

                //无法获取有效定位依据，建议用户打开手机设置里的定位开关后重试！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_SERVER_FAIL) {

                //百度定位服务端定位失败
                //建议反馈location.getLocationID()和大体定位时间到loc-bugs@baidu.com

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_FAIL_UNKNOWN) {

                //无法获取有效定位依据，但无法确定具体原因
                //建议检查是否有安全软件屏蔽相关定位权限
                //或调用重新启动后重试！

            }
            super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);
        }

    };

    /**
     * 根据经纬度反编译定位更精准
     *
     * @param latitude
     * @param longitude
     */
    public void getAddress(final double latitude, final double longitude) {
        GeoCoder geoCoder = GeoCoder.newInstance();
        final LatLng latLng = new LatLng(latitude, longitude);
        listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    Toast.makeText(GeographicalFenceActivity.this, "定位失败", Toast.LENGTH_LONG).show();
                    return;
                }
                currentAddr = reverseGeoCodeResult.getAddress();
                locationAddress = reverseGeoCodeResult.getAddress() + reverseGeoCodeResult.getSematicDescription();
                if (locationAddress != null && !"".equals(locationAddress) && !"null".equals(locationAddress)) {


                    //设定中心坐标
                    LatLng latLng = new LatLng(latitude, longitude);
                    //地图状态
                    MapStatus mapStatus = new MapStatus.Builder()
                            .target(latLng)
                            .zoom(14)
                            .build();
                    //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                    MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
                    //改变地图状态
                    baiduMap.setMapStatus(mapStatusUpdate);
                    //构建marker图标
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.location);
                    //创建标记marker
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(bitmapDescriptor);
                    //添加marker到地图
//                    baiduMap.addOverlay(markerOptions);

                    mEtAddress.setText(currentAddr);

                    Utils.log("currentAddr" + currentAddr);
                 /*   //关闭定位
                    locationClient.stop();
                    locationClient.unRegisterLocationListener(locationListener);*/
                } else {
                    showToast("获取定位地址失败");
                }
            }
        };
        geoCoder.setOnGetGeoCodeResultListener(listener);
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        geoCoder.destroy();
    }

    @Override
    protected void onResume() {
        //在activity执行onResume时必须调用mMapView. onResume ()
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时必须调用mMapView. onPause ()
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时必须调用mMapView.onDestroy()
        mapView.onDestroy();
        super.onDestroy();
        mSuggestionSearch.destroy();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        radioInt = progress * 1000;
        tv_radius.setText(progress + "km");

        // init
        baiduMap.clear();

        if (selectPoint == null) {
            selectPoint = latLng;
        }

        Utils.log(progress + "");
        // 添加圆
        OverlayOptions ooCircle = new CircleOptions().fillColor(0xAA6495ED)
                .center(selectPoint).stroke(new Stroke(5, 0xAA00FF00))
                .radius(radioInt);

        baiduMap.addOverlay(ooCircle);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private void updateEnclosure() {

        if (null == selectPoint) {
            finish();
            return;
        }

        Utils.log("updateEnclosure");
        showLoadingView();
        UpdateEnclosureData updateEnclosureData = new UpdateEnclosureData();

        updateEnclosureData.setAddress(currentAddr);
        updateEnclosureData.setEnableFlag(true);
        updateEnclosureData.setLatitude(selectPoint.latitude);
        updateEnclosureData.setLongitude(selectPoint.longitude);
        updateEnclosureData.setScope(String.valueOf(radioInt));
        updateEnclosureData.setSealId(sealIdString);


        HttpUtil.sendDataAsync(GeographicalFenceActivity.this, HttpUrl.SEAL_UPDATE_ENCLOSURE, 5, null, updateEnclosureData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                cancelLoadingView();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("updateEnclosure:" + result);

                JSONObject jsonObject = null;
                JSONObject jsonObject2 = null;
                try {
                    jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("message");
                    if (state.equals("成功")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cancelLoadingView();
                                setResult(99);
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @OnClick({R.id.tv_cancel, R.id.tv_save, R.id.set_back_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_save:
                updateEnclosure();
                break;
            case R.id.set_back_ll:
                finish();
                break;
        }
    }
}
