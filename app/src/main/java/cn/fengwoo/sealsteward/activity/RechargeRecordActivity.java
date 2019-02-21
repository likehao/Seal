package cn.fengwoo.sealsteward.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RechargeRecordAdapter;
import cn.fengwoo.sealsteward.entity.RechargeRecordData;

/**
 * 充值记录
 */
public class RechargeRecordActivity extends BaseActivity {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.recharge_record_lv)
    ListView recharge_record_lv;
    RechargeRecordAdapter recordAdapter;
    List<RechargeRecordData> recordDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_record);

        ButterKnife.bind(this);
        initView();
        getRechagreRecord();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("充值记录");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获取充值记录信息
     */
    private void getRechagreRecord(){
        recordDataList = new ArrayList<>();
        recordDataList.add(new RechargeRecordData("2019-02-21 14:00:00","研发章","研发部","包月","2020-01-01 00:00:00","240"));
        recordDataList.add(new RechargeRecordData("2019-02-21 14:00:00","研发章","研发部","包月","2020-01-01 00:00:00","240"));
        recordDataList.add(new RechargeRecordData("2019-02-21 14:00:00","研发章","研发部","包月","2020-01-01 00:00:00","240"));
        recordAdapter = new RechargeRecordAdapter(recordDataList,this);
        recharge_record_lv.setAdapter(recordAdapter);
    }
}
