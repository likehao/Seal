package cn.fengwoo.sealsteward.fragment;

import android.annotation.SuppressLint;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.BarAdapter;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.bean.UserStatisticsData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UseSealDetailData;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用印统计
 */
public class StatisticsFragment extends Fragment implements View.OnClickListener {
    private View view;
    @BindView(R.id.user_ll)
    LinearLayout user_ll;
    @BindView(R.id.seal_ll)
    LinearLayout seal_ll;
    @BindView(R.id.user_tv)
    TextView user_tv;
    @BindView(R.id.seal_tv)
    TextView seal_tv;
    @BindView(R.id.useSeal_num_tv)
    TextView sealNum;
    @BindView(R.id.useSeal_people_tv)
    TextView peopleNum;
    List<String> list = new ArrayList<>();
    private ArrayList<String> xValues = new ArrayList<>();
    private ArrayList<Integer> stampCount = new ArrayList<>();
    int sum;
    private LoadingView loadingView;
    @BindView(R.id.rec_statistics)
    RecyclerView recyclerView;
    BarAdapter barAdapter;
    List<UseSealDetailData.orgStructureStatisticVoList> mDatas;
    private LinearLayout[] linearLayouts = new LinearLayout[2];
    private TextView[] textViews = new TextView[2];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.statistics_fragment, container, false);
        EventBus.getDefault().register(this);   //注册Eventbus
        ButterKnife.bind(this, view);
        initView();
        getStatistic();
        return view;

    }

    private void initView() {
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
        changeView(0);
    }

    private void initAdapter() {
        barAdapter = new BarAdapter(mDatas, getActivity());
        recyclerView.setAdapter(barAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    private void changeView(int code) {
        textViews[0].setTextColor(code == 0 ? ContextCompat.getColor(getActivity(), R.color.white) :
                ContextCompat.getColor(getActivity(), R.color.style));
        linearLayouts[0].setBackground(code == 0 ? ContextCompat.getDrawable(getActivity(), R.drawable.user_statistics_select) :
                ContextCompat.getDrawable(getActivity(), R.drawable.user_statistics));

        textViews[1].setTextColor(code == 1 ? ContextCompat.getColor(getActivity(), R.color.white) :
                ContextCompat.getColor(getActivity(), R.color.style));
        linearLayouts[1].setBackground(code == 1 ? ContextCompat.getDrawable(getActivity(), R.drawable.seal_statistics_select) :
                ContextCompat.getDrawable(getActivity(), R.drawable.seal_statistics));
    }

    /**
     * 获取统计数据
     */

    @SuppressLint("SetTextI18n")
    private void getStatistic() {
        loadingView.show();
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setSearchType(3);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.ORG_STATISTIC, 2, null, userStatisticsData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", e + "统计错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<UseSealDetailData> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<UseSealDetailData>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                        for (UseSealDetailData.orgStructureStatisticVoList list : responseInfo.getData().getOrgStructureStatisticVoList()) {
                            mDatas.add(new UseSealDetailData.orgStructureStatisticVoList(list.getId(), list.getOrgStructureName(), list.getStampCount()));
                        }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initAdapter();
                            sealNum.setText(responseInfo.getData().getSealTotalCount()+"");
                            peopleNum.setText(responseInfo.getData().getUserTotalCount()+"");
                        }
                    });
                }
            }
        });

    }

    /**
     * 处理注册事件
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        switch (s) {
            case "切换公司":
                mDatas.clear();
                barAdapter.notifyDataSetChanged();
                getStatistic();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
