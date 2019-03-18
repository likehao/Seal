package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

public class SelectSealRecodeActivity extends BaseActivity {


    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.set_back_ll)
    LinearLayout setBackLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_seal_recode);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        titleTv.setText("查询盖章记录");
        setBackLl.setVisibility(View.VISIBLE);
        setBackLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
