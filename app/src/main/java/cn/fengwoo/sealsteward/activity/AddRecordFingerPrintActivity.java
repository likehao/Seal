package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddPwdUserUpload;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 添加指纹用户
 */
public class AddRecordFingerPrintActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.finger_user_rl)
    RelativeLayout finger_user_rl;
    @BindView(R.id.finger_user_tv)
    TextView finger_user;
    @BindView(R.id.finger_use_time_et)
    EditText useTime;
    @BindView(R.id.finger_bt)
    Button finger_bt;
    @BindView(R.id.finger_failTime_ll)
    LinearLayout finger_failTime_ll;
    @BindView(R.id.finger_failTime)
    TextView failTime;
    private Intent intent;
    private String format; //选择的时间
    private String userId, userName;
    private Boolean timeB = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_finger_print);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        intent = new Intent();
        title.setText("添加指纹用户");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        finger_user_rl.setOnClickListener(this);
        finger_failTime_ll.setOnClickListener(this);
        finger_bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.finger_user_rl:
                intent.setClass(this, SelectSinglePeopleActivity.class);
                startActivityForResult(intent, 123);
                break;
            case R.id.finger_failTime_ll:
                selectTime();
                break;
            case R.id.finger_bt:
                if (check()) {
                    submit();
                }
                break;
        }
    }

    /**
     * 检查数据是否为空
     *
     * @return
     */
    private boolean check() {
        Date nowTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(nowTime);
        if (TextUtils.isEmpty(finger_user.getText().toString())) {
            showToast("请选择使用人");
            return false;
        }
        if (TextUtils.isEmpty(useTime.getText().toString())) {
            showToast("请输入使用次数");
            return false;
        }
        if (TextUtils.isEmpty(failTime.getText().toString())) {
            showToast("请选择失效时间");
            return false;
        } else {
            timeB = DateUtils.isDateOneBigger(failTime.getText().toString(), time);
            if (!timeB) {
                showToast("您选择的时间已过期");
                return false;
            }
        }

        return true;
    }

    /**
     * 提交录入指纹
     */
    private void submit() {
        AddPwdUserUpload addPwdUserUpload = new AddPwdUserUpload();
        try {
            addPwdUserUpload.setExpireTime(DateUtils.dateToStamp(failTime.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        addPwdUserUpload.setStampCount(Integer.valueOf(useTime.getText().toString()));
        addPwdUserUpload.setUserId(userId);
        addPwdUserUpload.setUserType(2);
        addPwdUserUpload.setSealId(EasySP.init(this).getString("currentSealId"));

        HttpUtil.sendDataAsync(AddRecordFingerPrintActivity.this, HttpUrl.ADD_PWD_USER, 2, null, addPwdUserUpload, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                intent = new Intent(AddRecordFingerPrintActivity.this, RecordFingerprintActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * 时间选择器
     */
    @SuppressLint("SimpleDateFormat")
    private void selectTime() {
        TimePickerView timePicker = new TimePickerBuilder(AddRecordFingerPrintActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format = simpleDateFormat.format(date);  //选择的时间
                //获取当前时间
                Date nowTime = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String nowT = dateFormat.format(nowTime);
                //判断选择的时间是否过期
                Boolean compare = DateUtils.isDateOneBigger(format, nowT);
                if (compare) {
                    failTime.setText(format);
                } else {
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
        if (requestCode == 123 && resultCode == 10) {
            userId = data.getStringExtra("id");
            userName = data.getStringExtra("name");
            finger_user.setText(userName);
        }
    }
}
