package cn.fengwoo.sealsteward.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 修改个人信息
 */
public class ChangeInformationActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.cancel_ll)
    LinearLayout cancel_ll;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.delete_ll)
    LinearLayout delete_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.information_et)
    EditText information_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        cancel_ll.setVisibility(View.VISIBLE);
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("完成");
        title_tv.setText("修改信息");
        cancel_ll.setOnClickListener(this);
        delete_ll.setOnClickListener(this);
     //   information_et.setCursorVisible(false);  //隐藏光标
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_ll:
                finish();
                break;
            case R.id.delete_ll:
                information_et.getText().clear(); //清空内容
                break;
        }
    }
}
