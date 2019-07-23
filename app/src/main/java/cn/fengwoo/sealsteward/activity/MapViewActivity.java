package cn.fengwoo.sealsteward.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 查看地图
 */
public class MapViewActivity extends BaseActivity {

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.look_mapView)
    MapView look_mapView;
    private BaiduMap baiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        setContentView(R.layout.activity_map_view);

        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        back.setVisibility(View.VISIBLE);
        baiduMap = look_mapView.getMap();
        //设置地图缩放级别
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18.0f);
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        BaiduMapOptions options = new BaiduMapOptions();
        //设置地图模式为卫星地图
        options.mapType(BaiduMap.MAP_TYPE_SATELLITE);
        //创建mapview对象
        MapView mapView = new MapView(this,options);
        //设置mapview
        setContentView(mapView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        look_mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        look_mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        look_mapView.onDestroy();
    }
}
