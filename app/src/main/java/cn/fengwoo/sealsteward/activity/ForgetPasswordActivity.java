package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

/**
 * 忘记密码
 */
public class ForgetPasswordActivity extends Base2Activity implements View.OnClickListener{

    @BindView(R.id.forgetPassword_next_bt)
    Button forgetPassword_next_bt;
    @BindView(R.id.forget_password_iv)
    ImageView forget_password_iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ButterKnife.bind(this);
        setClickListener();
    }

    private void setClickListener() {
        forget_password_iv.setOnClickListener(this);
        forgetPassword_next_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.forget_password_iv:
                finish();
                break;
            case R.id.forgetPassword_next_bt:
                Intent intent = new Intent(ForgetPasswordActivity.this,SetPasswordActivity.class);
                intent.putExtra("foreget_next","foreget");
                startActivity(intent);
                finish();
                break;
        }
    }
}
