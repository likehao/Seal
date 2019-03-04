package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 添加印章
 */
public class AddSealActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_add_seal);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        scan_ll.setVisibility(View.GONE);
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加印章");
        set_back_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }
}
