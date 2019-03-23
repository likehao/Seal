package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 申请详情
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
    @BindView(R.id.detail_sealName_tv)
    TextView detailSealNameTv;
    @BindView(R.id.detail_count_tv)
    TextView detailCountTv;
    @BindView(R.id.detail_failTime_tv)
    TextView detailFailTimeTv;
    @BindView(R.id.detail_cause_et)
    EditText detailCauseEt;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_seal_apply);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("申请详情");
        set_back_ll.setOnClickListener(this);
        look_file_bt.setOnClickListener(this);
        intent = getIntent();
        int waitInt = intent.getIntExtra("waitAgree", 0);
        if (waitInt == 1) {
            go_approval_tv.setVisibility(View.VISIBLE);
            go_approval_tv.setOnClickListener(this);
        }
        detailCauseEt.setEnabled(false);
        detailCauseEt.setCursorVisible(false);  //隐藏光标
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        String sealName = intent.getStringExtra("sealName");
        Integer count = intent.getIntExtra("count",0);
        String failTime = intent.getStringExtra("failTime");
        String cause = intent.getStringExtra("cause");
        detailSealNameTv.setText(sealName);
        detailCountTv.setText(count+"");
        detailFailTimeTv.setText(failTime);
        detailCauseEt.setText(cause);
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
                intent = new Intent(UseSealApplyActivity.this, ApprovalConfirmActivity.class);
                startActivity(intent);
                break;

        }
    }
}
