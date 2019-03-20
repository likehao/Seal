package cn.fengwoo.sealsteward.activity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 地理围栏
 */
public class GeographicalFenceActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    private Intent intent;
    private LocationClient locationClient = null;
    private String locationAddress; //定位地址
    private BaiduMap baiduMap = null;
    private Double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_geographical_fence);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("设置地理围栏");
        initLocationOption();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
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
        //开始定位
        locationClient.start();
    }

    /**
     * 定位回调
     */
    private BDAbstractLocationListener locationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            //获取定位类型、定位错误返回码
            int errorCode = bdLocation.getLocType();
            if (errorCode == BDLocation.TypeGpsLocation) {
                // GPS定位结果
                showToast(bdLocation.getAddrStr());
            } else if (errorCode == BDLocation.TypeNetWorkLocation) {
                // 网络定位结果
                Log.i("TAG","网络定位成功!!!!!!!!!!!!!!!!!");

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
            getAddress(latitude,longitude);
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
     * @param latitude
     * @param longitude
     */
    public void getAddress(final double latitude, final double longitude){
        GeoCoder geoCoder = GeoCoder.newInstance();
        final com.baidu.mapapi.model.LatLng latLng = new com.baidu.mapapi.model.LatLng(latitude,longitude);
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR){
                    //没有检索到结果
                    Toast.makeText(GeographicalFenceActivity.this,"定位失败",Toast.LENGTH_LONG).show();
                    return;
                }
                locationAddress = reverseGeoCodeResult.getAddress() + reverseGeoCodeResult.getSematicDescription();
                if (locationAddress != null && !"".equals(locationAddress) && !"null".equals(locationAddress)){

                    //初始化地图
                    baiduMap = mapView.getMap();
                    //设定中心坐标
                    LatLng latLng = new LatLng(latitude,longitude);
                    //地图状态
                    MapStatus mapStatus = new MapStatus.Builder()
                            .target(latLng)
                            .zoom(18)
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
                    baiduMap.addOverlay(markerOptions);

                 /*   //关闭定位
                    locationClient.stop();
                    locationClient.unRegisterLocationListener(locationListener);*/
                }else {
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
    }

}
