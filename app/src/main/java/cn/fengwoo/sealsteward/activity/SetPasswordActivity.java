package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

/**
 * 设置密码
 */
public class SetPasswordActivity extends Base2Activity implements View.OnClickListener {

    @BindView(R.id.register_bt)
    Button register_bt;
    @BindView(R.id.set_password_back_iv)
    ImageView set_password_back_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        ButterKnife.bind(this);
        initData();
        setClickListener();

    }

    private void initData() {
        //判断是否点击的忘记密码
        Intent intent = getIntent();
        String foreget_next = intent.getStringExtra("foreget_next");
        if ("foreget".equals(foreget_next)) {
            register_bt.setText("提交");
        }
    }

    private void setClickListener() {
        register_bt.setOnClickListener(this);
        set_password_back_iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_bt:
                Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.set_password_back_iv:
                finish();
                break;
        }
    }
}
