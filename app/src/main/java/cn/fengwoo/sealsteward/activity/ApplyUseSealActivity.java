package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

/**
 * 申请用印
 */
public class ApplyUseSealActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.nextBt)
    Button nextBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_use_seal);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("用印申请");
        set_back_ll.setOnClickListener(this);
        nextBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.nextBt:
                Intent intent = new Intent(this,UploadFileActivity.class);
                startActivity(intent);
                break;
        }
    }
}
