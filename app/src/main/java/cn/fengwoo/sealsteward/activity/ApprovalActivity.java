package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

/**
 * 审批进度
 */
public class ApprovalActivity extends BaseActivity {
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("审批进度");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
