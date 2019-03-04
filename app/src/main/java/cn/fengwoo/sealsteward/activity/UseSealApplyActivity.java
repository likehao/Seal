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

/**
 * 用印申请
 */
public class UseSealApplyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.look_file_bt)
    Button look_file_bt;
    @BindView(R.id.go_approval_tv)
    TextView go_approval_tv;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_seal_apply);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("用印申请");
        set_back_ll.setOnClickListener(this);
        look_file_bt.setOnClickListener(this);
        intent = getIntent();
        int waitInt = intent.getIntExtra("waitAgree",0);
        if (waitInt == 1) {
            go_approval_tv.setVisibility(View.VISIBLE);
            go_approval_tv.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.look_file_bt:
                intent = new Intent(UseSealApplyActivity.this, FileActivity.class);
                startActivity(intent);
                break;
            case R.id.go_approval_tv:
                intent = new Intent(UseSealApplyActivity.this,ApprovalConfirmActivity.class);
                startActivity(intent);
                break;
        }
    }

}
