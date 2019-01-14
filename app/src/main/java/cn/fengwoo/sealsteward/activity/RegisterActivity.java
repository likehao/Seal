package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.gyf.barlibrary.ImmersionBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.HideKeyBroadUtils;

/**
 * 注册
 */
public class RegisterActivity extends Base2Activity implements View.OnClickListener{

    @BindView(R.id.register_next_bt)
    Button register_next_bt;
    @BindView(R.id.register_back_iv)
    ImageView register_back_iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        setClickListener();
    }

    private void setClickListener() {
        register_next_bt.setOnClickListener(this);
        register_back_iv.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_next_bt:
                Intent intent = new Intent(RegisterActivity.this,SetPasswordActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.register_back_iv:
                finish();
                break;
        }
    }
}
