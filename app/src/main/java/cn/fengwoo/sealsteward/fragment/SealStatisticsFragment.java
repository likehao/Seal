package cn.fengwoo.sealsteward.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import cn.fengwoo.sealsteward.activity.SealStatisticActivity;
import cn.fengwoo.sealsteward.activity.SelectTimeActivity;
import cn.fengwoo.sealsteward.activity.UserStatisticActivity;
import cn.fengwoo.sealsteward.bean.SealStatisticsData;
import cn.fengwoo.sealsteward.bean.UserStatisticsData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 印章统计详情
 */
public class SealStatisticsFragment extends Fragment implements View.OnClickListener {
    private View view;
    @BindView(R.id.sealOrgName_tv)
    TextView sealOrgName_tv;
    @BindView(R.id.sealStatistic_lv)
    ListView listView;
    @BindView(R.id.seal_statistics_ll)
    LinearLayout seal_statistics_ll;
    @BindView(R.id.seal_time_tv)
    TextView time;
    @BindView(R.id.sealStatistics_search)
    EditText sealSearch;
    private ArrayList<SealStatisticsData> detailData;
    private String id, orgName;
    private  CommonAdapter commonAdapter;
    private int year,month;
    private ResponseInfo<List<SealStatisticsData>> responseInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.seal_statistics_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        getSealStatistics(year,month);

        return view;
    }

    private void initView() {
        detailData = new ArrayList<>();
        seal_statistics_ll.setOnClickListener(this);
        Intent intent = getActivity().getIntent();
        id = intent.getStringExtra("orgId");
        orgName = intent.getStringExtra("orgName");
        sealOrgName_tv.setText(orgName);
        //获取当前年月
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //系统时间
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        sealSearch.setOnClickListener(this);
        sealSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String searchStr = sealSearch.getText().toString().trim();
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

    private void getSealStatistics(int YY,int MM) {
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setOrgStructureId(id);
        userStatisticsData.setYear(YY);
        userStatisticsData.setMonth(MM);
        userStatisticsData.setSearchType(0);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.SEAL_STATISTIC, 2, null, userStatisticsData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "用户统计错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<SealStatisticsData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (SealStatisticsData data : responseInfo.getData()) {
                        detailData.add(new SealStatisticsData(data.getId(), data.getName(), data.getSealPrint(), data.getStampCount()));
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initData();
                            }
                        });
                    }
                }
            }
        });
    }

    private void initData() {
        commonAdapter = new CommonAdapter<SealStatisticsData>(getActivity(), detailData, R.layout.seal_statistics_item) {
            @Override
            public void convert(ViewHolder viewHolder, SealStatisticsData sealStatisticsData, int position) {
//                                viewHolder.setText(R.id.user_statistics_iv,userStatisticsData.getSealPrint());
                if (position == 0){
                    viewHolder.setTextColorRes(R.id.seal_statistics_count_tv,R.color.red);
                }else if (position == 1){
                    viewHolder.setTextColorRes(R.id.seal_statistics_count_tv,R.color.number_tv);
                }else if (position == 2){
                    viewHolder.setTextColorRes(R.id.seal_statistics_count_tv,R.color.style);
                }else if (position > 2){
                    viewHolder.setTextColorRes(R.id.seal_statistics_count_tv,R.color.dark_gray);
                }

                viewHolder.setText(R.id.seal_statistics_name_tv, sealStatisticsData.getName());
                viewHolder.setText(R.id.seal_statistics_count_tv, sealStatisticsData.getStampCount() + "次");
                viewHolder.setOnClickListener(R.id.sealStatistics_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserStatisticActivity.class);
                        intent.putExtra("sealId", detailData.get(position).getId());
                        intent.putExtra("orgName", orgName);
                        intent.putExtra("sealName",detailData.get(position).getName());
                        startActivity(intent);
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
            case R.id.seal_statistics_ll:
                Intent intent = new Intent(getActivity(), SelectTimeActivity.class);
                startActivity(intent);
//                selectTime();
                break;
            case R.id.sealStatistics_search:
                sealSearch.setCursorVisible(true);
                break;
        }
    }

    /**
     * 选择时间
     */
    @SuppressLint("WrongConstant")
    private void selectTime() {
        Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //系统时间
        startDate.set(2016, 0, 1);   //设置起始年月
        Calendar endDate = Calendar.getInstance();
        int y = endDate.get(Calendar.YEAR);
        int m = endDate.get(Calendar.MONTH);
        endDate.set(y, m, 1);
        //选择时间
        TimePickerView timePicker = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
                String format = simpleDateFormat.format(date);  //选择的时间
                SimpleDateFormat yearSim = new SimpleDateFormat("yyyy");
                int Y = Integer.parseInt(yearSim.format(date));
                SimpleDateFormat monthSim = new SimpleDateFormat("MM");
                int M = Integer.parseInt(monthSim.format(date));
                //清空数据
                detailData.clear();
                if (commonAdapter != null) {
                    commonAdapter.notifyDataSetChanged();
                }
                time.setText(format);
                getSealStatistics(Y,M);
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
            detailData.add(new SealStatisticsData(data.getId(),data.getName(),data.getSealPrint(),data.getStampCount()));
        }

        listView.setAdapter(commonAdapter);
        if (commonAdapter != null) {
            commonAdapter.notifyDataSetChanged();
        }
        if (list.size() == 0) {
            Toast.makeText(getActivity(),"未查询到结果",Toast.LENGTH_SHORT).show();
        }
    }
}
