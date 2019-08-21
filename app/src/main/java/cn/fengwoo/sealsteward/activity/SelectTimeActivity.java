package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.carbs.android.gregorianlunarcalendar.library.view.GregorianLunarCalendarView;
import cn.carbs.android.indicatorview.library.IndicatorView;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 选择时间
 */
public class SelectTimeActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.cancel_tv)
    TextView cancel;
    @BindView(R.id.selectTime_ll)
    LinearLayout selectTime;
    @BindView(R.id.selectTime_tv)
    TextView selectTime_tv;
    @BindView(R.id.selectMonth_ll)
    LinearLayout selectMonth_ll;
    @BindView(R.id.calendar_view1)
    GregorianLunarCalendarView mGLCView1;
    @BindView(R.id.show_time_tv1)
    TextView time1;

    @BindView(R.id.selectDay_ll)
    LinearLayout selectDay_ll;
    @BindView(R.id.calendar_view2)
    GregorianLunarCalendarView mGLCView2;
    @BindView(R.id.startTime_tv)
    TextView startTime;
    @BindView(R.id.endTime_tv)
    TextView endTime;
    @BindView(R.id.picker_day)
    NumberPickerView mDay;
    @BindView(R.id.startTime_view)
    View startTime_view;
    @BindView(R.id.endTime_view)
    View endTime_view;
    private boolean status = false;
    int year,month,day;
    private static final int TIME_CODE = 123;
    private TextView[] textViews = new TextView[2];
    private View[] views = new View[2];
    private int type = 0;  //初始默认按月赋值0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        ButterKnife.bind(this);
        initView();

    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        cancel.setVisibility(View.VISIBLE);
        edit_tv.setVisibility(View.VISIBLE);
        title_tv.setText("选择时间");
        edit_tv.setText("完成");
        cancel.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
        selectTime.setOnClickListener(this);
        //初始化显示当前日期
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DATE);
        time1.setText(year +"-"+ month);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        textViews[0] = startTime;
        textViews[1] = endTime;
        views[0] = startTime_view;
        views[1] = endTime_view;

        mGLCView1.init();  //1.默认日期今天，默认模式公历

        mGLCView1.setOnDateChangedListener(new GregorianLunarCalendarView.OnDateChangedListener(){
            @Override
            public void onDateChanged(GregorianLunarCalendarView.CalendarData calendarData) {
                Calendar calendar = calendarData.getCalendar();
                @SuppressLint("WrongConstant")
                String showTime = calendar.get(Calendar.YEAR) + "-"
                        + (calendar.get(Calendar.MONTH) + 1);
//                        + calendar.get(Calendar.DAY_OF_MONTH);
                time1.setText(showTime);
            }
        });

        //隐藏天
        mDay.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_tv:
                finish();
                break;
            case R.id.selectTime_ll:
                if (!status){
                    selectTime_tv.setText("按日选择");
                    type = 1;
                    status = true;
                    selectDay();
                }else {
                    selectTime_tv.setText("按月选择");
                    type = 0;
                    status = false;
                    selectMonth();
                }
                break;
            case R.id.edit_tv:
                Intent intent = new Intent();
                if (type == 0){
                    intent.putExtra("time",time1.getText().toString());
                }else{
                    intent.putExtra("startTime",startTime.getText().toString());
                    intent.putExtra("endTime",endTime.getText().toString());
                }
                intent.putExtra("type",type);
                setResult(TIME_CODE,intent);
                finish();
                break;
            case R.id.endTime_tv:
                changeView(1);
                listener(endTime);
                break;
            case R.id.startTime_tv:
                changeView(0);
                listener(startTime);
                break;

        }
    }

    /**
     * 按月选择
     */
    private void selectMonth(){
        selectDay_ll.setVisibility(View.GONE);
        selectMonth_ll.setVisibility(View.VISIBLE);
    }

    /**
     * 按天选择
     */
    @SuppressLint("SetTextI18n")
    private void selectDay(){
        startTime.setText(year +"-"+ month +"-"+ day);
        endTime.setText(year +"-"+ month +"-"+ day);
        selectMonth_ll.setVisibility(View.GONE);
        selectDay_ll.setVisibility(View.VISIBLE);

        mGLCView2.init();
        listener(startTime);
    }

    /**
     * 判断点击的是哪个时间
     * @param view
     */
    private void listener(TextView view){
        mGLCView2.setOnDateChangedListener(new GregorianLunarCalendarView.OnDateChangedListener(){
            @SuppressLint("WrongConstant")
            @Override
            public void onDateChanged(GregorianLunarCalendarView.CalendarData calendarData) {
                Calendar calendar = calendarData.getCalendar();
                @SuppressLint("WrongConstant")
                String showTime = calendar.get(Calendar.YEAR) + "-"
                        + (calendar.get(Calendar.MONTH) + 1) + "-"
                        + calendar.get(Calendar.DAY_OF_MONTH);
                view.setText(showTime);
            }
        });
    }

    /**
     * 改变点击属性
     */
    private void changeView(int code){
        textViews[0].setTextColor(code == 0 ? ContextCompat.getColor(this,R.color.style) : ContextCompat.getColor(this,R.color.black_3));
        views[0].setBackground(code == 0 ? ContextCompat.getDrawable(this,R.color.style) : ContextCompat.getDrawable(this,R.color.black_6));
        textViews[1].setTextColor(code == 1 ? ContextCompat.getColor(this,R.color.style) : ContextCompat.getColor(this,R.color.black_3));
        views[1].setBackground(code == 1 ? ContextCompat.getDrawable(this,R.color.style) : ContextCompat.getDrawable(this,R.color.black_6));
    }

}
