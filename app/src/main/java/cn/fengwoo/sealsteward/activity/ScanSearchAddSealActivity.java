package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

/**
 * 扫描搜索添加印章
 */
public class ScanSearchAddSealActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.search_near_device_ll)
    LinearLayout search_near_device_ll;
    @BindView(R.id.scan_add_seal_ll)
    LinearLayout scan_add_seal_ll;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_search_add_seal);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加印章");
        set_back_ll.setOnClickListener(this);
        search_near_device_ll.setOnClickListener(this);
        scan_add_seal_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_near_device_ll:
                intent = new Intent(this,NearbyDeviceActivity.class);
                startActivity(intent);
                break;
            case R.id.scan_add_seal_ll:
                intent = new Intent(this,ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.set_back_ll:
                finish();
                break;
        }
    }
}
