package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

public class PersonCenterActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)LinearLayout set_back_ll;
    @BindView(R.id.title_tv)TextView title_tv;
    @BindView(R.id.my_QRCode_rl)
    RelativeLayout my_QRCode_rl;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_center);

        ButterKnife.bind(this);
        setListener();
        initView();
    }

    private void initView() {
        title_tv.setText("资料");
        set_back_ll.setVisibility(View.VISIBLE);
    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
        my_QRCode_rl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.my_QRCode_rl:
                intent = new Intent(PersonCenterActivity.this, MyQRCodeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
