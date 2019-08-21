package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
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
import com.squareup.picasso.Picasso;

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
import cn.fengwoo.sealsteward.fragment.RecordFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
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
    @BindView(R.id.sealSta_tv2)
    TextView sealSta_tv2;
    @BindView(R.id.sealSearchSta_et)
    EditText sealSearchSta_et;
    private String userId, orgName, realName;
    private ArrayList<SealStatisticsData> arrayList;
    private LoadingView loadingView;
    private CommonAdapter commonAdapter;
    private int year, month;
    private ResponseInfo<List<SealStatisticsData>> responseInfo;
    private static final int USER_TIME_CODE = 123;
    private String bigTime, small;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_statistic);

        ButterKnife.bind(this);
        initView();
        getUserStatistic(year, month,0);

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
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchStr = sealSearchSta_et.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchStr)) {
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
     *
     * @param YY
     * @param MM
     */
    private void getUserStatistic(int YY, int MM, int type) {
        loadingView.show();
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setOrgStructureId(userId);
        if (type == 0) {
            userStatisticsData.setYear(YY);
            userStatisticsData.setMonth(MM);
        } else if (type == 1) {
            userStatisticsData.setStartTime(small);
            userStatisticsData.setEndTime(bigTime);
        }
        userStatisticsData.setSearchType(type);
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
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (SealStatisticsData data : responseInfo.getData()) {
                        arrayList.add(new SealStatisticsData(data.getId(), data.getName(), data.getSealPrint(), data.getStampCount()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });

                } else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

    /**
     * 初始数据
     */
    private void initData() {
        commonAdapter = new CommonAdapter<SealStatisticsData>(this, arrayList, R.layout.user_statistics_item) {
            @Override
            public void convert(ViewHolder viewHolder, SealStatisticsData sealStatisticsData, int position) {
                ImageView view = viewHolder.getView(R.id.user_statistics_iv);
                String sealPrint = sealStatisticsData.getSealPrint();
                if (sealPrint != null && !sealPrint.isEmpty()) {
                    Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(sealPrint);
                    if (bitmap == null) {
                        HttpDownloader.downloadImage(SealStatisticActivity.this, 1, sealPrint, new DownloadImageCallback() {
                            @Override
                            public void onResult(final String fileName) {
                                if (fileName != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String path = "file://" + HttpDownloader.path + fileName;
                                            Picasso.with(SealStatisticActivity.this).load(path).into(view);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        String path = "file://" + HttpDownloader.path + sealPrint;
                        Picasso.with(SealStatisticActivity.this).load(path).into(view);
                    }
                }
                //设置前三名字体颜色
                if (position == 0){
                    viewHolder.setTextColorRes(R.id.user_statistics_count_tv,R.color.red);
                }else if (position == 1){
                    viewHolder.setTextColorRes(R.id.user_statistics_count_tv,R.color.number_tv);
                }else if (position == 2){
                    viewHolder.setTextColorRes(R.id.user_statistics_count_tv,R.color.style);
                }else if (position > 2){
                    viewHolder.setTextColorRes(R.id.user_statistics_count_tv,R.color.dark_gray);
                }
                viewHolder.setText(R.id.user_statistics_name_tv, sealStatisticsData.getName());
                viewHolder.setText(R.id.user_statistics_count_tv, sealStatisticsData.getStampCount());
                viewHolder.setOnClickListener(R.id.userStatistics_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

        };
        commonAdapter.notifyDataSetChanged();
        listView.setAdapter(commonAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sealSta_ll:
                Intent intent = new Intent(this, SelectTimeActivity.class);
                startActivityForResult(intent, USER_TIME_CODE);
//                selectTime();
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
                getUserStatistic(Y, M, 0);
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
    private void getSearchData(String search) {
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
        for (SealStatisticsData data : list) {
            arrayList.add(new SealStatisticsData(data.getId(), data.getName(), data.getSealPrint(), data.getStampCount()));
        }

        listView.setAdapter(commonAdapter);
        if (commonAdapter != null) {
            commonAdapter.notifyDataSetChanged();
        }
        if (list.size() == 0) {
            showToast("未查询到结果");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int yTime = 0, mTime = 0;
        if (requestCode == USER_TIME_CODE && resultCode == USER_TIME_CODE) {
            if (data != null) {
                String getTime = data.getStringExtra("time");
                String startTime = data.getStringExtra("startTime");
                String endTime = data.getStringExtra("endTime");
                if (startTime != null && endTime != null) {
                    Boolean selectB = DateUtils.compare(startTime, endTime);  //比较开始时间和结束时间
                    if (selectB) {
                        bigTime = startTime;
                        small = endTime;
                    } else {
                        bigTime = endTime;
                        small = startTime;
                    }
                }
                int type = data.getIntExtra("type", 2);
                //分割年月
                if (getTime != null) {
                    yTime = Integer.parseInt(getTime.split("-")[0]);
                    mTime = Integer.parseInt(getTime.split("-")[1]);
                    sealSta_tv.setText(yTime + "年" + mTime + "月");
                    sealSta_tv2.setVisibility(View.GONE);
                }
                if (type == 1) {
                    sealSta_tv.setText(small);
                    sealSta_tv2.setVisibility(View.VISIBLE);
                    sealSta_tv2.setText(bigTime);
                }
                //清空数据
                arrayList.clear();
                if (commonAdapter != null) {
                    commonAdapter.notifyDataSetChanged();
                }
                getUserStatistic(yTime, mTime, type);
            }
        }
    }
}
