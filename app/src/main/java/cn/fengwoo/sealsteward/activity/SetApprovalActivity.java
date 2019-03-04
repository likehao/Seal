package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

public class SetApprovalActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.one_approval_ll)
    LinearLayout one_approval_ll;
    @BindView(R.id.two_approval_ll)
    LinearLayout two_approval_ll;
    @BindView(R.id.copy_to_ll)
    LinearLayout copy_to_ll;
    @BindView(R.id.add_approval_bt)
    Button add_approval_bt;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_approval);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("设置审批流");
        set_back_ll.setOnClickListener(this);
        one_approval_ll.setOnClickListener(this);
        two_approval_ll.setOnClickListener(this);
        copy_to_ll.setOnClickListener(this);
        add_approval_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.one_approval_ll:
                intent = new Intent(this, PersonInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.two_approval_ll:
                intent = new Intent(this, PersonInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.copy_to_ll:
                intent = new Intent(this, PersonInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.add_approval_bt:
                Intent intent = new Intent(this, OrganizationalStructureActivity.class);
                startActivity(intent);
                break;
        }
    }
}
