package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RecordAdapter;
import cn.fengwoo.sealsteward.bean.StampRecordData;
import cn.fengwoo.sealsteward.bean.StampRecordList;
import cn.fengwoo.sealsteward.entity.NearTime;
import cn.fengwoo.sealsteward.entity.RecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 查询盖章记录
 */
public class SelectSealRecodeActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.set_back_ll)
    LinearLayout setBackLl;
    @BindView(R.id.select_person_tv)
    TextView selectPersonTv;
    @BindView(R.id.select_person_rl)
    RelativeLayout selectPersonRl;
    @BindView(R.id.select_seal_tv)
    TextView selectSealTv;
    @BindView(R.id.select_seal_rl)
    RelativeLayout selectSealRl;
    @BindView(R.id.select_nearTime_tv)
    TextView selectNearTimeTv;
    @BindView(R.id.select_nearTime_rl)
    RelativeLayout selectNearTimeRl;
    @BindView(R.id.select_beginTime_tv)
    TextView selectBeginTimeTv;
    @BindView(R.id.select_beginTime_rl)
    RelativeLayout selectBeginTimeRl;
    @BindView(R.id.select_endTime_tv)
    TextView selectEndTimeTv;
    @BindView(R.id.select_endTime_rl)
    RelativeLayout selectEndTimeRl;
    @BindView(R.id.select_bt)
    Button selectBt;
    Intent intent;
    private String person,personId,sealId;
    private final static int SELECTPERSONREQUESTCODE = 123;
    private final static int SELECTSEALREQUESTCODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_seal_recode);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        titleTv.setText("查询盖章记录");
        setBackLl.setVisibility(View.VISIBLE);
        setBackLl.setOnClickListener(this);
        selectPersonRl.setOnClickListener(this);
        selectSealRl.setOnClickListener(this);
        selectNearTimeRl.setOnClickListener(this);
        selectBeginTimeRl.setOnClickListener(this);
        selectEndTimeRl.setOnClickListener(this);
        selectBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.select_person_rl:
                if (!Utils.hasThePermission(this, Constants.permission19)) {
                    return;
                }
                intent = new Intent(this, SelectSinglePeopleActivity.class);
                intent.putExtra("code", 1);
                startActivityForResult(intent, SELECTPERSONREQUESTCODE);
                break;
            case R.id.select_seal_rl:
                intent = new Intent(this, SelectSealActivity.class);
                intent.putExtra("code", 1);
                startActivityForResult(intent, SELECTSEALREQUESTCODE);
                break;
            case R.id.select_nearTime_rl:
                nearTime();
                break;
            case R.id.select_beginTime_rl:
                selectBeginTime(1);
                break;
            case R.id.select_endTime_rl:
                selectBeginTime(2);
                break;
            case R.id.select_bt:
                selectRecord();
                break;

        }
    }

    /**
     * 查询盖章记录请求
     */
    private void selectRecord(){
        StampRecordData stampRecordData = new StampRecordData();
        StampRecordData.Parem parem = new StampRecordData.Parem();
        stampRecordData.setCurPage(1);
        stampRecordData.setHasExportPdf(false);
        stampRecordData.setHasPage(true);
        stampRecordData.setPageSize(10);
        try {
            //时间转为时间戳
            if (end != null && begin != null){
                String endTime = DateUtils.dateToStamp2(end);
                String startTime = DateUtils.dateToStamp2(begin);
                parem.Parem(personId,endTime,sealId,startTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        stampRecordData.setParam(parem);

        HttpUtil.sendDataAsync(SelectSealRecodeActivity.this, HttpUrl.STAMPRECORDAPPLYLIST, 2, null, stampRecordData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<StampRecordList>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<StampRecordList>>>() {
                }.getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {

                } else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });

    }
    /**
     * 获取最近时间选择器
     */
    private void nearTime(){
        List<String> list = new ArrayList<>();
     //   List<NearTime> list = new ArrayList<>();
        list.add("一天");
        list.add("三天");
        list.add("一周");
        list.add("两周");
        list.add("一个月");
        SinglePicker<String> picker = new SinglePicker<String>(this,list);
        picker.setCanceledOnTouchOutside(true);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setTextColor(0xFF000000);
//        picker.setSubmitTextColor(0xFFFB2C3C);
//        picker.setCancelTextColor(0xFFFB2C3C);
        picker.setTextSize(15);
        picker.setSelectedIndex(1);
        picker.setLineSpaceMultiplier(2);   //设置每项的高度，范围为2-4
        picker.setContentPadding(0,10);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                selectNearTimeTv.setText(item);
            }

        });
        picker.show();
    }
    /**
     * 时间选择器
     */
    String begin,end;
    @SuppressLint("SimpleDateFormat")
    private void selectBeginTime(int code) {
        TimePickerView timePicker = new TimePickerBuilder(SelectSealRecodeActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (code == 1) {
                    begin = simpleDateFormat.format(date);  //选择的开始时间
                    if (end != null){
                        Boolean selectB = DateUtils.compare(begin, end);  //比较开始时间和结束时间
                        if (selectB){
                            showToast("开始时间必须小于结束时间");
                        }else {
                            selectBeginTimeTv.setText(begin);
                        }
                    }else {
                        selectBeginTimeTv.setText(begin);
                    }
                }
                if (code == 2){
                    end = simpleDateFormat.format(date);  //选择的结束时间
                    if (begin != null){
                        Boolean selectB = DateUtils.compare(begin, end);
                        if (selectB){
                            showToast("结束时间必须大于开始时间");
                        }else {
                            selectEndTimeTv.setText(end);
                        }
                    }else {
                        selectEndTimeTv.setText(end);
                    }
                }
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})  //年月日时分秒 的显示与否，不设置则默认全部显示
                .build();
        timePicker.show();
    }

    /**
     * 回调结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTPERSONREQUESTCODE) {
            if (data != null) {
                person = Objects.requireNonNull(data.getExtras()).getString("name");
                personId = data.getExtras().getString("id");
                selectPersonTv.setText(person);

            }
        }
        if (requestCode == SELECTSEALREQUESTCODE){
            if (data != null) {
                person = data.getExtras().getString("name");
                sealId = data.getExtras().getString("id");
                selectSealTv.setText(person);
            }
        }
    }


}
