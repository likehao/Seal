package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.view.MessagePopuwindow;

/**
 * 详情
 */
public class SeeRecordActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.message_more_iv)
    ImageView message_more_iv;
    MessagePopuwindow messagePopuwindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_record);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        title_tv.setText("详情");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        message_more_iv.setVisibility(View.VISIBLE);
        message_more_iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.message_more_iv:
                messagePopuwindow = new MessagePopuwindow(this,2);
                messagePopuwindow.showPopuwindow(v);
                break;
        }
    }
}
