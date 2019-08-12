package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.UserStatisticsData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
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
    @BindView(R.id.userStatistic_lv)
    ListView userStatistic_lv;
    @BindView(R.id.UserOrgName_tv)
    TextView UserOrgName_tv;
    private ArrayList<UserStatisticsData> detailData;
    private String id, orgName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.user_statistics_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        getUseStatistics();
        initData();
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
    }

    private void initData() {
        CommonAdapter commonAdapter = new CommonAdapter<UserStatisticsData>(getActivity(), detailData, R.layout.user_statistics_item) {
            @Override
            public void convert(ViewHolder viewHolder, UserStatisticsData userStatisticsData, int position) {
//                                viewHolder.setText(R.id.user_statistics_iv,userStatisticsData.getHeadPortrait());
                viewHolder.setText(R.id.user_statistics_name_tv, userStatisticsData.getRealName());
                viewHolder.setText(R.id.user_statistics_count_tv, userStatisticsData.getStampCount());
            }

        };
        commonAdapter.notifyDataSetChanged();
        userStatistic_lv.setAdapter(commonAdapter);

    }

    /**
     * 获取人员统计数据
     */
    private void getUseStatistics() {
        //获取当前年月
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH + 1);
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setOrgStructureId(id);
        userStatisticsData.setYear(year);
        userStatisticsData.setMonth(month);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.USER_STATISTIC, 2, null, userStatisticsData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "用户统计错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<UserStatisticsData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<UserStatisticsData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (UserStatisticsData data : responseInfo.getData()) {
                        detailData.add(new UserStatisticsData(data.getRealName(), data.getHeadPortrait(), data.getStampCount()));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_statistics_ll:
                selectTime();
                break;
        }
    }

    /**
     * 选择时间
     */
    private void selectTime() {
        TimePickerView timePicker = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
                String format = simpleDateFormat.format(date);  //选择的时间
                time.setText(format);
            }
        })
                .setType(new boolean[]{true, true, false, false, false, false})  //年月日时分秒 的显示与否，不设置则默认全部显示
                .build();
        //   timePicker.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        timePicker.show();
    }
}
