package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.RechargeRecordBean;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 我的卡券
 */
public class MyCardTicketActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.card_ticket_lv)
    ListView listView;
    private CommonAdapter commonAdapter;
    private ArrayList<RechargeRecordBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card_ticket);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title_tv.setText("我的卡券");
        list = new ArrayList<>();
        list.add(new RechargeRecordBean());
        list.add(new RechargeRecordBean());
        commonAdapter = new CommonAdapter<RechargeRecordBean>(MyCardTicketActivity.this,list,R.layout.card_ticket_item) {
            @Override
            public void convert(ViewHolder viewHolder, RechargeRecordBean rechargeRecordBean, int position) {
                viewHolder.setText(R.id.card_money_tv,"￥"+ 240);
                viewHolder.setText(R.id.card_package_tv,"一年套餐包");
                viewHolder.setText(R.id.card_use_tv,"未使用");

            }

        };

    }

    private void initData(){
        commonAdapter.notifyDataSetChanged();
        listView.setAdapter(commonAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }
}
