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
 * 印章详情
 */
public class SealDetailActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.back_tv)
    TextView back_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_seal_detail);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        title_tv.setText("用章详情");
        set_back_ll.setVisibility(View.VISIBLE);
        back_tv.setText("返回");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
