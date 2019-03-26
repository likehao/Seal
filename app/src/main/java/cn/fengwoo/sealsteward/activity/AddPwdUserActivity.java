package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddCompanyInfo;
import cn.fengwoo.sealsteward.entity.AddPwdUserUpload;
import cn.fengwoo.sealsteward.entity.AddPwdUserUploadReturn;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 关于
 */
public class AddPwdUserActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.rl_select_ppl)
    RelativeLayout rlSelectPpl;
    @BindView(R.id.et_use_times)
    EditText etUseTimes;
    @BindView(R.id.tv_expired_time)
    TextView tvExpiredTime;
    @BindView(R.id.expired_time)
    RelativeLayout expiredTime;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private String userId = "";
    private String userName = "";
    String format; //选择的时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pwd_user);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加密码用户");
        set_back_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }


    @OnClick({R.id.rl_select_ppl, R.id.expired_time, R.id.btn_submit})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_select_ppl:
                intent.setClass(this, SelectSinglePeopleActivity.class);
                startActivityForResult(intent, 123);
                break;
            case R.id.expired_time:
                selectTime();
                break;
            case R.id.btn_submit:
                try {
                    submit();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    private void submit() throws ParseException {
        loadingView.show();
        AddPwdUserUpload addPwdUserUpload = new AddPwdUserUpload();
        addPwdUserUpload.setExpireTime(DateUtils.dateToStamp(format));
        addPwdUserUpload.setStampCount(Integer.valueOf(etUseTimes.getText().toString()));
        addPwdUserUpload.setUserId(userId);
        addPwdUserUpload.setUserType(1);
        addPwdUserUpload.setSealId(EasySP.init(this).getString("currentSealId"));

        HttpUtil.sendDataAsync(AddPwdUserActivity.this, HttpUrl.ADD_PWD_USER, 2, null, addPwdUserUpload, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                showToast(e+"");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                loadingView.cancel();



                Gson gson = new Gson();
                ResponseInfo<AddPwdUserUploadReturn> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<AddPwdUserUploadReturn>>(){}.getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData() != null) {
                        loadingView.cancel();
                        finish();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 发送pwd给seal
                                String pwd = responseInfo.getData().getPassword();
                                String sealCount = etUseTimes.getText().toString().trim();


                            }
                        });
                        Looper.prepare();
                        showToast("添加成功");
                        Looper.loop();
                    } else {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(responseInfo.getMessage());
                        Looper.loop();
                    }
                }else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }

            }
        });

    }


    /**
     * 时间选择器
     */
    @SuppressLint("SimpleDateFormat")
    private void selectTime(){
        TimePickerView timePicker = new TimePickerBuilder(AddPwdUserActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
                format = simpleDateFormat.format(date);  //选择的时间
                //获取当前时间
                Date nowTime = new Date();
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
                String nowT = dateFormat.format(nowTime);
                //判断选择的时间是否过期
                Boolean compare = DateUtils.isDateOneBigger(format,nowT);
                if (compare) {
                    tvExpiredTime.setText(format);
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
        if (requestCode == 123 && resultCode == 10) {
            userId = data.getStringExtra("id");
            userName = data.getStringExtra("name");
            tvUser.setText(userName);
        }
    }
}
