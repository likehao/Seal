package cn.fengwoo.sealsteward.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.BarAdapter;
import cn.fengwoo.sealsteward.bean.UserStatisticsData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UseSealDetailData;
import cn.fengwoo.sealsteward.utils.BarCharts;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用印统计
 */
public class StatisticsFragment extends Fragment implements View.OnClickListener{
    private View view;
    @BindView(R.id.bar_chart)
    BarChart barChart;
    @BindView(R.id.stampCountSum_tv)
    TextView stampCountSum_tv;  //盖章总次数
    @BindView(R.id.user_ll)
    LinearLayout user_ll;
    @BindView(R.id.seal_ll)
    LinearLayout seal_ll;
    @BindView(R.id.user_tv)
    TextView user_tv;
    @BindView(R.id.seal_tv)
    TextView seal_tv;
    List<String> list = new ArrayList<>();
    List<BarEntry> entries = new ArrayList<>();
    private BarCharts mBarChart;
    private ArrayList<String> xValues = new ArrayList<>();
    private ArrayList<Integer> stampCount = new ArrayList<>();
    int sum;
    private LoadingView loadingView;
    @BindView(R.id.rec_statistics)
    RecyclerView recyclerView;
    BarAdapter barAdapter;
    List<UseSealDetailData> mDatas;
    private LinearLayout[] linearLayouts = new LinearLayout[2];
    private TextView[] textViews = new TextView[2];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.statistics_fragment, container, false);
        ButterKnife.bind(this, view);
        initView();
        intData();
        initAdapter();
        return view;

    }

    private void initView() {
        mBarChart = new BarCharts();
        loadingView = new LoadingView(getActivity());
        mDatas = new ArrayList<>();
        linearLayouts[0] = user_ll;
        linearLayouts[1] = seal_ll;
        textViews[0] = user_tv;
        textViews[1] = seal_tv;
        user_ll.setOnClickListener(this);
        seal_ll.setOnClickListener(this);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//        getStatistic();
        changeView(0);
    }

    private void intData() {
        for (int i = 0; i < 20; i++) {
            UseSealDetailData useSealDetailData = new UseSealDetailData();
            useSealDetailData.setOrgStructureName(i + "个");
            useSealDetailData.setStampCount(i * 5);
            mDatas.add(useSealDetailData);
        }
    }

    private void initAdapter() {
        barAdapter = new BarAdapter(mDatas, getActivity());
        recyclerView.setAdapter(barAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_ll:
                changeView(0);
                break;
            case R.id.seal_ll:
                changeView(1);
                break;
        }
    }
    /**
     * 改变点击状态view
     */

    private void changeView(int code){
        textViews[0].setTextColor(code == 0 ? ContextCompat.getColor(getActivity(),R.color.white) :
                ContextCompat.getColor(getActivity(),R.color.style));
        linearLayouts[0].setBackground(code == 0 ? ContextCompat.getDrawable(getActivity(),R.drawable.user_statistics_select) :
                ContextCompat.getDrawable(getActivity(),R.drawable.user_statistics));

        textViews[1].setTextColor(code == 1 ? ContextCompat.getColor(getActivity(),R.color.white) :
                ContextCompat.getColor(getActivity(),R.color.style));
        linearLayouts[1].setBackground(code == 1 ? ContextCompat.getDrawable(getActivity(),R.drawable.seal_statistics_select) :
                ContextCompat.getDrawable(getActivity(),R.drawable.seal_statistics));
    }
    /**
     * 获取统计数据
     */
    private void getStatistic() {
        loadingView.show();
        UserStatisticsData userStatisticsData = new UserStatisticsData();

        HttpUtil.sendDataAsync(getActivity(), HttpUrl.ORG_STATISTIC, 2, null, userStatisticsData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", e + "统计错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<UseSealDetailData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<UseSealDetailData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (UseSealDetailData useSealDetailData : responseInfo.getData()) {
                        xValues.add(useSealDetailData.getOrgStructureName());
                        stampCount.add(useSealDetailData.getStampCount());
                        sum += useSealDetailData.getStampCount();
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingView.cancel();
                                stampCountSum_tv.setText(sum + "次");
                                mBarChart.showBarChart(barChart, getBarData(xValues.size()), true);
                            }
                        });
                    }
                }
            }
        });

    }

    /**
     * 这个方法是初始化数据的
     *
     * @param count X 轴的个数
     */
    public BarData getBarData(int count) {

        ArrayList<BarEntry> yValues = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            yValues.add(new BarEntry(stampCount.get(i), i));
        }
        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "统计图");
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            colors.add(Color.parseColor("#83CBFD"));
        }
        barDataSet.setColors(colors);
        // 设置栏阴影颜色
        barDataSet.setBarShadowColor(Color.parseColor("#01000000"));
        ArrayList<BarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet);
        barDataSet.setValueTextColor(Color.parseColor("#FF9933"));
        // 绘制值
        barDataSet.setDrawValues(true);
        BarData barData = new BarData(xValues, barDataSets);
        barData.setValueTextSize(15f);  //设置直方图上面文字的大小
        return barData;
    }

}
