package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;

/**
 * 申请用印
 */
public class ApplyUseSealActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.nextBt)
    Button nextBt;
    @BindView(R.id.seal_name_rl)  //申请的印章名称
    RelativeLayout seal_name_rl;
    @BindView(R.id.apply_time_rl)
    RelativeLayout apply_time_rl;  //申请的次数
    @BindView(R.id.failTime_rl)
    RelativeLayout failTime_rl;   //失效时间
    @BindView(R.id.apply_cause_et)
    EditText apply_cause_et;   //申请事由
    private final static int SELECTSEALREQUESTCODE = 123;  //选择印章结果码
    @BindView(R.id.sealName_TV)
    TextView sealName_TV;
    String sealName;
    @BindView(R.id.apply_sign_ll)
    LinearLayout apply_sign_ll;
    @BindView(R.id.fail_time_tv)
    TextView fail_time_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_use_seal);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("申请用印");
        set_back_ll.setOnClickListener(this);
        nextBt.setOnClickListener(this);
        seal_name_rl.setOnClickListener(this);
        failTime_rl.setOnClickListener(this);
        apply_sign_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.nextBt:
                Intent intent = new Intent(this, UploadFileActivity.class);
                startActivity(intent);
                break;
            case R.id.seal_name_rl:
                intent = new Intent(this,OrganizationalManagementActivity.class);
                startActivityForResult(intent,SELECTSEALREQUESTCODE);
                break;
            case R.id.apply_sign_ll:
                intent = new Intent(this,MySignActivity.class);
                startActivity(intent);
                break;
            case R.id.failTime_rl:
                selectTime();
                break;

        }
    }

    /**
     * 时间选择器
     */
    @SuppressLint("SimpleDateFormat")
    private void selectTime(){
        TimePickerView timePicker = new TimePickerBuilder(ApplyUseSealActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
                String format = simpleDateFormat.format(date);  //选择的时间
                //获取当前时间
                Date nowTime = new Date();
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
                String nowT = dateFormat.format(nowTime);
                //判断选择的时间是否过期
                Boolean compare = DateUtils.isDateOneBigger(format,nowT);
                if (compare) {
                    fail_time_tv.setText(format);
                }else {
                    showToast("您选择的时间已过期");
                }
            }
        })
                .setType(new boolean[]{true, true, true, true, true, false})  //年月日时分秒 的显示与否，不设置则默认全部显示
                .build();
     //   timePicker.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        timePicker.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTSEALREQUESTCODE && resultCode == RESULT_OK){
            //获取选择的印章名称
            if (data != null){
                sealName = data.getStringExtra("name");
                sealName_TV.setText(sealName);
            }
        }
    }
}
