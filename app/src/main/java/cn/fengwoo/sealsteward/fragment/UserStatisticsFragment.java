package cn.fengwoo.sealsteward.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import cn.fengwoo.sealsteward.activity.SealStatisticActivity;
import cn.fengwoo.sealsteward.activity.SelectTimeActivity;
import cn.fengwoo.sealsteward.bean.UserStatisticsData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用户用印统计
 */
public class UserStatisticsFragment extends Fragment implements View.OnClickListener {
    private View view;
    @BindView(R.id.user_statistics_ll)
    LinearLayout user_statistics_ll;
    @BindView(R.id.statistics_time_tv)
    TextView time;
    @BindView(R.id.statistics_time_tv2)
    TextView time2;
    @BindView(R.id.userStatistic_lv)
    ListView userStatistic_lv;
    @BindView(R.id.UserOrgName_tv)
    TextView UserOrgName_tv;
    @BindView(R.id.userStatistics_search)
    EditText userSearch;
    private ArrayList<UserStatisticsData> detailData;
    private String id, orgName;
    private CommonAdapter commonAdapter;
    private int year,month;
    private ResponseInfo<List<UserStatisticsData>> responseInfo;
    private static final int USER_TIME_CODE = 123;
    private String bigTime,small;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.user_statistics_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        getUseStatistics(year,month,0);
        return view;
    }

    private void initView() {
        detailData = new ArrayList<>();
        user_statistics_ll.setOnClickListener(this);
        //获取当前时间
        Date nowTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月");
        String nowT = dateFormat.format(nowTime);
        //显示当前时间
        time.setText(nowT);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            id = intent.getStringExtra("orgId");
            orgName = intent.getStringExtra("orgName");
        }
        UserOrgName_tv.setText(orgName);
        //获取当前年月
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));   //获取东八区时间
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        userSearch.setOnClickListener(this);
        userSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String searchStr = userSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchStr)){
                        //清空数据
                        detailData.clear();
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

    private void initData() {
        commonAdapter = new CommonAdapter<UserStatisticsData>(getActivity(), detailData, R.layout.user_statistics_item) {
            @Override
            public void convert(ViewHolder viewHolder, UserStatisticsData userStatisticsData, int position) {
                ImageView view = viewHolder.getView(R.id.user_statistics_iv);
                String headPortrait = userStatisticsData.getHeadPortrait();
                if (headPortrait != null && !headPortrait.isEmpty()) {
                    Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(headPortrait);
                    if (bitmap == null) {
                        HttpDownloader.downloadImage(getActivity(), 1, headPortrait, new DownloadImageCallback() {
                            @Override
                            public void onResult(final String fileName) {
                                if (fileName != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String path = "file://" + HttpDownloader.path + fileName;
                                            Picasso.with(getActivity()).load(path).into(view);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        String path = "file://" + HttpDownloader.path + headPortrait;
                        Picasso.with(getActivity()).load(path).into(view);
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

                viewHolder.setText(R.id.user_statistics_name_tv, userStatisticsData.getRealName());
                viewHolder.setText(R.id.user_statistics_count_tv, userStatisticsData.getStampCount() + "次");
                viewHolder.setOnClickListener(R.id.userStatistics_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SealStatisticActivity.class);
                        intent.putExtra("userId",detailData.get(position).getId());
                        intent.putExtra("orgName",orgName);
                        intent.putExtra("realName",detailData.get(position).getRealName());
                        startActivity(intent);
                    }
                });
            }

        };
        commonAdapter.notifyDataSetChanged();
        userStatistic_lv.setAdapter(commonAdapter);
    }

    /**
     * 获取人员统计数据
     */
    private void getUseStatistics(int yy,int mm,int type) {
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setOrgStructureId(id);
        if (type == 0){
            userStatisticsData.setYear(yy);
            userStatisticsData.setMonth(mm);

        }else if (type == 1){
            userStatisticsData.setStartTime(small);
            userStatisticsData.setEndTime(bigTime);
        }
        userStatisticsData.setSearchType(type);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.USER_STATISTIC, 2, null, userStatisticsData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "用户统计错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("TAGTTTTTTTTTTTTTTTTT",result);
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<UserStatisticsData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (UserStatisticsData data : responseInfo.getData()) {
                        detailData.add(new UserStatisticsData(data.getId(),data.getRealName(), data.getHeadPortrait(), data.getStampCount()));
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initData();
                            }
                        });
                    }
                }else {
                    Looper.prepare();
                    Toast.makeText(getActivity(),responseInfo.getMessage(),Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_statistics_ll:
                Intent intent = new Intent(getActivity(), SelectTimeActivity.class);
                startActivityForResult(intent,USER_TIME_CODE);
//                selectTime();
                break;
            case R.id.userStatistics_search:
                userSearch.setCursorVisible(true);  //显示光标
                break;
        }
    }

    /**
     * 选择时间
     */
    @SuppressLint("WrongConstant")
    private void selectTime() {
        Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //系统时间
        startDate.set(2016,0,1);   //设置起始年月
        Calendar endDate = Calendar.getInstance();
        int y = endDate.get(Calendar.YEAR);
        int m = endDate.get(Calendar.MONTH);
        endDate.set(y, m,1);
        //选择时间
        TimePickerView timePicker = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
                String format = simpleDateFormat.format(date);  //选择的时间
                SimpleDateFormat yearSim = new SimpleDateFormat("yyyy");  //获取年份
                int Y = Integer.parseInt(yearSim.format(date));
                SimpleDateFormat monthSim = new SimpleDateFormat("MM");   //获取月份
                int M = Integer.parseInt(monthSim.format(date));
                //清空数据
                detailData.clear();
                if (commonAdapter != null) {
                    commonAdapter.notifyDataSetChanged();
                }
                time.setText(format);
                getUseStatistics(Y,M,0);
            }
        })
                .setType(new boolean[]{true, true, false, false, false, false})  //年月日时分秒 的显示与否，不设置则默认全部显示
                .setLabel( "", "", "" ,"", "", "")  //设置是否需要年月日时分秒文字
                .setRangDate(startDate,endDate)  //起始终止年月日设定
                .setLineSpacingMultiplier(2f)  //设置行间距宽度
                .build();
        //   timePicker.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        timePicker.show();
    }

    /**
     * 获取搜索的数据
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
            detailData.add(new UserStatisticsData(data.getId(),data.getRealName(),data.getHeadPortrait(),data.getStampCount()));
        }

        userStatistic_lv.setAdapter(commonAdapter);
        if (commonAdapter != null) {
            commonAdapter.notifyDataSetChanged();
        }
        if (list.size() == 0) {
            Toast.makeText(getActivity(),"未查询到结果",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int yTime = 0,mTime = 0;
        if (requestCode == USER_TIME_CODE && resultCode == USER_TIME_CODE){
            if (data != null){
                String getTime = data.getStringExtra("time");
                String startTime = data.getStringExtra("startTime");
                String endTime = data.getStringExtra("endTime");
                if (startTime != null && endTime != null){
                    Boolean selectB = DateUtils.compare(startTime, endTime);  //比较开始时间和结束时间
                    if (selectB){
                        bigTime = startTime;
                        small = endTime;
                    }else {
                        bigTime = endTime;
                        small = startTime;
                    }
                }
                int type = data.getIntExtra("type",2);
                //分割年月
                if (getTime != null) {
                    yTime = Integer.parseInt(getTime.split("-")[0]);
                    mTime = Integer.parseInt(getTime.split("-")[1]);
                    time.setText(yTime + "年" + mTime + "月");
                    time2.setVisibility(View.GONE);
                }
                if (type == 1){
                    time.setText(small);
                    time2.setVisibility(View.VISIBLE);
                    time2.setText(bigTime);
                }
                //清空数据
                detailData.clear();
                if (commonAdapter != null) {
                    commonAdapter.notifyDataSetChanged();
                }
                getUseStatistics(yTime,mTime,type);
            }
        }
    }
}
