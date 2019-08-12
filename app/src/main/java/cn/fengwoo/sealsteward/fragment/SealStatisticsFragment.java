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
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
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
public class SealStatisticsFragment extends Fragment {
    private View view;
    @BindView(R.id.sealOrgName_tv)
    TextView sealOrgName_tv;
    @BindView(R.id.sealStatistic_lv)
    ListView listView;
    private ArrayList<SealStatisticsData> detailData;
    private String id, orgName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.seal_statistics_layout,container,false);
        ButterKnife.bind(this, view);
        initView();
        getSealStatistics();
        initData();

        return view;
    }

    private void initView(){
        detailData = new ArrayList<>();
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            id = intent.getStringExtra("orgId");
            orgName = intent.getStringExtra("orgName");
        }
        sealOrgName_tv.setText(orgName);
    }

    private void getSealStatistics(){
        //获取当前年月
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH + 1);
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setOrgStructureId(id);
        userStatisticsData.setYear(year);
        userStatisticsData.setMonth(month);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.SEAL_STATISTIC, 2, null, userStatisticsData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "用户统计错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<SealStatisticsData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<SealStatisticsData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (SealStatisticsData data : responseInfo.getData()) {
                        detailData.add(new SealStatisticsData(data.getName(), data.getSealPrint(), data.getStampCount()));
                    }
                }
            }
        });
    }

    private void initData(){
        CommonAdapter commonAdapter = new CommonAdapter<SealStatisticsData>(getActivity(), detailData, R.layout.seal_statistics_item) {
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
}
