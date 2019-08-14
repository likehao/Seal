package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.SealStatisticsData;
import cn.fengwoo.sealsteward.bean.UserStatisticsData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用户统计
 */
public class UserStatisticActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.org_RealName_tv)
    TextView org_RealName_tv;
    @BindView(R.id.user_statistics_lv)
    ListView listView;
    @BindView(R.id.userSta_ll)
    LinearLayout userSta_ll;
    @BindView(R.id.userSta_tv)
    TextView userSta_tv;
    @BindView(R.id.userSearchSta_et)
    EditText userSearchSta_et;
    private String sealId, orgName,sealName;
    private ArrayList<SealStatisticsData> arrayList;
    private LoadingView loadingView;
    private int year,month;
    private CommonAdapter commonAdapter;
    private  ResponseInfo<List<SealStatisticsData>> responseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistic);
        ButterKnife.bind(this);
        initView();

        getSealStatistic(year,month);
    }

    private void initView() {
        loadingView = new LoadingView(this);
        arrayList = new ArrayList<>();
        back.setVisibility(View.VISIBLE);
        title.setText("用印统计");
        Intent intent = getIntent();
        orgName = intent.getStringExtra("orgName");
        sealId = intent.getStringExtra("sealId");
        sealName = intent.getStringExtra("sealName");
        org_RealName_tv.setText(orgName+"-"+sealName);  //显示部门及印章名称
        userSta_ll.setOnClickListener(this);
        back.setOnClickListener(this);
        //获取当前年月
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //系统时间
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        userSearchSta_et.setOnClickListener(this);
        userSearchSta_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String searchStr = userSearchSta_et.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchStr)){
                        //清空数据
                        arrayList.clear();
                        if (commonAdapter != null) {
                            commonAdapter.notifyDataSetChanged();
                        }
                        getSearchData(searchStr);
                        return true;   //返回false表示点击后，隐藏软键盘。返回true表示保留软键盘
                    }

                }
                return false;
            }
        });
    }

    /**
     * 获取印章统计数据
     */
    private void getSealStatistic(int YY,int MM) {
        loadingView.show();
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setSealId(sealId);
        userStatisticsData.setYear(YY);
        userStatisticsData.setMonth(MM);
        userStatisticsData.setSearchType(0);
        HttpUtil.sendDataAsync(this, HttpUrl.SEAL_STATISTIC, 2, null, userStatisticsData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", e + "用户统计错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<SealStatisticsData>>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (SealStatisticsData sealStatisticsData : responseInfo.getData()) {
                        arrayList.add(new SealStatisticsData(sealStatisticsData.getId(), sealStatisticsData.getName(),
                                sealStatisticsData.getSealPrint(), sealStatisticsData.getStampCount()));

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData(){
        commonAdapter = new CommonAdapter<SealStatisticsData>(this, arrayList, R.layout.seal_statistics_item) {
            @Override
            public void convert(ViewHolder viewHolder, SealStatisticsData sealStatisticsData, int position) {
//                                viewHolder.setText(R.id.user_statistics_iv,userStatisticsData.getSealPrint());
                viewHolder.setText(R.id.seal_statistics_name_tv, sealStatisticsData.getName());
                viewHolder.setText(R.id.seal_statistics_count_tv, sealStatisticsData.getStampCount());

            }

        };
        commonAdapter.notifyDataSetChanged();
        listView.setAdapter(commonAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.userSta_ll:
                selectTime();
                break;
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.userSearchSta_et:
                userSearchSta_et.setCursorVisible(true);
                break;
        }
    }

    private void selectTime() {
        Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //系统时间
        startDate.set(2016, 0, 1);   //设置起始年月
        Calendar endDate = Calendar.getInstance();
        int y = endDate.get(Calendar.YEAR);
        int m = endDate.get(Calendar.MONTH);
        endDate.set(y, m, 1);
        //选择时间
        TimePickerView timePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
                String format = simpleDateFormat.format(date);  //选择的时间
                SimpleDateFormat yearSim = new SimpleDateFormat("yyyy");
                int Y = Integer.parseInt(yearSim.format(date));
                SimpleDateFormat monthSim = new SimpleDateFormat("MM");
                int M = Integer.parseInt(monthSim.format(date));
                //清空数据
                arrayList.clear();
                if (commonAdapter != null) {
                    commonAdapter.notifyDataSetChanged();
                }
                userSta_tv.setText(format);
                getSealStatistic(Y,M);
            }
        })
                .setType(new boolean[]{true, true, false, false, false, false})  //年月日时分秒 的显示与否，不设置则默认全部显示
                .setLabel("", "", "", "", "", "")  //设置是否需要年月日时分秒文字
                .setRangDate(startDate, endDate)  //起始终止年月日设定
                .setLineSpacingMultiplier(2f)  //设置行间距宽度
                .build();
        //   timePicker.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        timePicker.show();
    }

    /**
     * 搜索结果
     */
    private void getSearchData(String search){
        List<SealStatisticsData> list = new ArrayList<>();
        if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
            for (SealStatisticsData sealStatisticsData : responseInfo.getData()) {
                if (sealStatisticsData.getName().contains(search)) {
                    if (!list.contains(sealStatisticsData)) {
                        list.add(sealStatisticsData);
                    }
                }
            }
        }
        for (SealStatisticsData data : list){
            arrayList.add(new SealStatisticsData(data.getId(),data.getName(),data.getSealPrint(),data.getStampCount()));
        }

        listView.setAdapter(commonAdapter);
        if (commonAdapter != null) {
            commonAdapter.notifyDataSetChanged();
        }
        if (list.size() == 0) {
            showToast("未查询到结果");
        }
    }
}
