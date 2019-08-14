package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * 印章统计
 */
public class SealStatisticActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.org_SealName_tv)
    TextView org_SealName_tv;
    @BindView(R.id.seal_statistics_lv)
    ListView listView;
    @BindView(R.id.sealSta_ll)
    LinearLayout sealSta_ll;
    @BindView(R.id.sealSta_tv)
    TextView sealSta_tv;
    @BindView(R.id.sealSearchSta_et)
    EditText sealSearchSta_et;
    private String userId, orgName, realName;
    private ArrayList<UserStatisticsData> arrayList;
    private LoadingView loadingView;
    private CommonAdapter commonAdapter;
    private int year, month;
    private ResponseInfo<List<UserStatisticsData>> responseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_statistic);

        ButterKnife.bind(this);
        initView();
        getUserStatistic(year, month);

    }

    private void initView() {
        loadingView = new LoadingView(this);
        arrayList = new ArrayList<>();
        back.setVisibility(View.VISIBLE);
        title.setText("用印统计");
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        orgName = intent.getStringExtra("orgName");
        realName = intent.getStringExtra("realName");
        org_SealName_tv.setText(orgName + "-" + realName);  //显示部门及用户名称
        sealSta_ll.setOnClickListener(this);
        back.setOnClickListener(this);
        //获取当前年月
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));   //获取东八区时间
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        sealSearchSta_et.setOnClickListener(this);
        sealSearchSta_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String searchStr = sealSearchSta_et.getText().toString().trim();
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
     * 获取数据统计
     * @param YY
     * @param MM
     */
    private void getUserStatistic(int YY, int MM) {
        loadingView.show();
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setOrgStructureId(userId);
        userStatisticsData.setYear(YY);
        userStatisticsData.setMonth(MM);
        userStatisticsData.setSearchType(0);
        HttpUtil.sendDataAsync(this, HttpUrl.USER_STATISTIC, 2, null, userStatisticsData, new Callback() {
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
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<UserStatisticsData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (UserStatisticsData data : responseInfo.getData()) {
                        arrayList.add(new UserStatisticsData(data.getId(), data.getRealName(), data.getHeadPortrait(), data.getStampCount()));
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
     * 初始数据
     */
    private void initData() {
        commonAdapter = new CommonAdapter<UserStatisticsData>(this, arrayList, R.layout.user_statistics_item) {
            @Override
            public void convert(ViewHolder viewHolder, UserStatisticsData userStatisticsData, int position) {
//                viewHolder.set(R.id.user_statistics_iv,userStatisticsData.getHeadPortrait());
                viewHolder.setText(R.id.user_statistics_name_tv, userStatisticsData.getRealName());
                viewHolder.setText(R.id.user_statistics_count_tv, userStatisticsData.getStampCount());
            }

        };
        commonAdapter.notifyDataSetChanged();
        listView.setAdapter(commonAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sealSta_ll:
                selectTime();
                break;
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.sealSearchSta_et:
                sealSearchSta_et.setCursorVisible(true);
                break;
        }
    }

    /**
     * 选择时间
     */
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
                sealSta_tv.setText(format);
                getUserStatistic(Y, M);
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
        List<UserStatisticsData> list = new ArrayList<>();
        if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
            for (UserStatisticsData userStatisticsData : responseInfo.getData()) {
                if (userStatisticsData.getRealName().contains(search)) {
                    if (!list.contains(userStatisticsData)) {
                        list.add(userStatisticsData);
                    }
                }
            }
        }
        for (UserStatisticsData data : list){
            arrayList.add(new UserStatisticsData(data.getId(),data.getRealName(),data.getHeadPortrait(),data.getStampCount()));
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
