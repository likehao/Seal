package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 查看公司详情
 */
public class CompanyDetailActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)LinearLayout set_back_ll;
    @BindView(R.id.title_tv)TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("公司详情");
        edit_tv.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }
}
