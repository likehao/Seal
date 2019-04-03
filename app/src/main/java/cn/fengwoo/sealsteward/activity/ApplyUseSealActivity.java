package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.AddUseSealApplyBean;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    @BindView(R.id.apply_time_et)
    EditText apply_time_et;
    @BindView(R.id.failTime_rl)
    RelativeLayout failTime_rl;   //失效时间
    @BindView(R.id.apply_cause_et)
    EditText apply_cause_et;   //申请事由
    @BindView(R.id.sealName_TV)
    TextView sealName_TV;
    @BindView(R.id.apply_sign_ll)
    LinearLayout apply_sign_ll;
    @BindView(R.id.fail_time_tv)
    TextView fail_time_tv;
    @BindView(R.id.apply_sign_iv)
    ImageView apply_sign_iv;

    private final static int SELECTSEALREQUESTCODE = 123;  //选择印章结果码
    private String sealName,sealId;
    private String name,failTime,cause;
    private String applyCount;
    private Intent intent;

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

        String autoGraph = CommonUtil.getUserData(this).getAutoGraph();
        //读取签名
        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(autoGraph);
        if(bitmap == null){
            //下载签名
            HttpDownloader.downloadImage(ApplyUseSealActivity.this, 2, autoGraph, new DownloadImageCallback() {
                @Override
                public void onResult(final String fileName) {
                    if (fileName != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                Picasso.with(ApplyUseSealActivity.this).load(sealPrintPath).into(apply_sign_iv);
                            }
                        });
                    }
                }
            });

        } else{
            //不为空则直接显示
            String sealPrintPath = "file://" + HttpDownloader.path + autoGraph;
            Picasso.with(ApplyUseSealActivity.this).load(sealPrintPath).into(apply_sign_iv);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.nextBt:
                if (checkData()){
                    useSealApply();
                }
                break;
            case R.id.seal_name_rl:
                intent = new Intent(this,SelectSealActivity.class);
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
     * 校验用印申请
     */
    String time;
    private void useSealApply(){
        AddUseSealApplyBean useSealApplyBean = new AddUseSealApplyBean();
        useSealApplyBean.setApplyCause(cause);
        useSealApplyBean.setApplyCount(Integer.valueOf(applyCount));
        String userId = CommonUtil.getUserData(this).getId();
        useSealApplyBean.setApplyUser(userId);
        //时间转时间戳
        try {
            time = DateUtils.dateToStamp(failTime);
            useSealApplyBean.setExpireTime(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        useSealApplyBean.setSealId(sealId);
        HttpUtil.sendDataAsync(ApplyUseSealActivity.this, HttpUrl.CHECKUSESEAL, 2, null, useSealApplyBean, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"用印申请错误!!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}
                .getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()){
                        intent = new Intent(ApplyUseSealActivity.this, UploadFileActivity.class);
                        intent.putExtra("cause",cause);
                        intent.putExtra("applyCount",applyCount);
                        intent.putExtra("userId",userId);
                        intent.putExtra("failTime",time);
                        intent.putExtra("sealId",sealId);
                        startActivity(intent);
                        finish();
                        Log.e("ATG","用印申请check成功!!!!!!!");
                    }
                }else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }

            }
        });
    }

    private Boolean checkData(){
        name = sealName_TV.getText().toString().trim();
        applyCount = apply_time_et.getText().toString().trim();
        failTime = fail_time_tv.getText().toString().trim();
        cause = apply_cause_et.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            showToast("请选择印章名称");
            return false;
        }
        if (TextUtils.isEmpty(applyCount) && !applyCount.equals("0")){
            showToast("请输入申请次数");
            return false;
        }
        if (TextUtils.isEmpty(failTime)){
            showToast("请选择失效时间");
            return false;
        }
        if (TextUtils.isEmpty(cause)){
            showToast("请填写申请事由");
            return false;
        }
        return true;
    }
    /**
     * 时间选择器
     */
    @SuppressLint("SimpleDateFormat")
    private void selectTime(){
        TimePickerView timePicker = new TimePickerBuilder(ApplyUseSealActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = simpleDateFormat.format(date);  //选择的时间
                //获取当前时间
                Date nowTime = new Date();
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        if (requestCode == SELECTSEALREQUESTCODE){
            //获取选择的印章名称和印章ID
            if (data != null){
                sealId = data.getStringExtra("id");
                sealName = data.getStringExtra("name");
                Log.e("TAG","id------------:" + sealId + "  name------------:" + sealName);
                sealName_TV.setText(sealName);
            }
        }
    }
}
