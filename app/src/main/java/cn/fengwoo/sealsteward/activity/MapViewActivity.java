package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

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
        title.setText("盖章地址");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        baiduMap = look_mapView.getMap();
        LatLng latLng = new LatLng(31.227, 121.481);
        //坐标转换
        CoordinateConverter converter= new CoordinateConverter();
        converter.coord(latLng);
        converter.from(CoordinateConverter.CoordType.COMMON);
        LatLng convertLatLng = converter.convert();
        //覆盖物
        OverlayOptions options = new MarkerOptions()
                .position(convertLatLng)  //设置marker的位置
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))  //设置marker图标
                .zIndex(4)           //设置marker所在层级
                .draggable(true);    //设置手势拖拽
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(options);
        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
        baiduMap.animateMapStatus(statusUpdate);

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
