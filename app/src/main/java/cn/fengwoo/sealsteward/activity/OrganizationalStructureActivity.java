package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

/**
 * 组织架构
 */
public class OrganizationalStructureActivity extends BaseActivity implements View.OnClickListener{


    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.add_iv)
    ImageView add_iv;
    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizational_structure);

        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        scan_ll.setVisibility(View.GONE);
        add_iv.setVisibility(View.GONE);
        title_tv.setText("组织架构");
    }

    private void setListener(){
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
