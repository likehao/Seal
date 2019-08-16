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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.UserStatisticsData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UseSealDetailData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用印明细
 */
public class SealDetailedActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.useSeal_detail_lv)
    ListView listView;
    @BindView(R.id.search)
    EditText search;
    private ArrayList<UseSealDetailData.orgStructureStatisticVoList> arrayList;
    private LoadingView loadingView;
    private CommonAdapter detailAdapter;
    private ResponseInfo<UseSealDetailData> responseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_detailed);

        ButterKnife.bind(this);
        initView();
        getData();
    }

    private void initView() {
        arrayList = new ArrayList<>();
        back.setVisibility(View.VISIBLE);
        title_tv.setText("用印明细");
        back.setOnClickListener(this);
        loadingView = new LoadingView(this);
        search.setOnClickListener(this);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String searchStr = search.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchStr)){
                        //清空数据
                        arrayList.clear();
                        detailAdapter.notifyDataSetChanged();
                        getSearchData(searchStr);
                        return true;   //返回false表示点击后，隐藏软键盘。返回true表示保留软键盘
                    }

                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.search:
                search.setCursorVisible(true);  //显示光标
                break;
        }
    }

    private void getData(){
        loadingView.show();
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setSearchType(3);
        HttpUtil.sendDataAsync(this, HttpUrl.ORG_STATISTIC, 2, null, userStatisticsData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", e + "统计错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<UseSealDetailData>>(){}
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null){
                    loadingView.cancel();
                    for (UseSealDetailData.orgStructureStatisticVoList list : responseInfo.getData().getOrgStructureStatisticVoList()) {
                        arrayList.add(new UseSealDetailData.orgStructureStatisticVoList(list.getId(), list.getOrgStructureName(), list.getStampCount()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setData();
                        }
                    });
                }
            }
        });

    }

    private void setData(){
        detailAdapter = new CommonAdapter<UseSealDetailData.orgStructureStatisticVoList>(SealDetailedActivity.this, arrayList, R.layout.detail_item) {
            @Override
            public void convert(ViewHolder viewHolder, UseSealDetailData.orgStructureStatisticVoList useSealDetailData, int position) {
                viewHolder.setText(R.id.detail_department_tv,useSealDetailData.getOrgStructureName());
                viewHolder.setText(R.id.detail_time_tv,useSealDetailData.getStampCount()+"");
                viewHolder.setText(R.id.number_tv,position+1+"");  //初始是红，从1开始
                if (position == 1){
                    viewHolder.setBackgroundRes(R.id.number_tv,R.drawable.circle_textview_yellow);
                }else if (position == 2){
                    viewHolder.setBackgroundRes(R.id.number_tv,R.drawable.circle_textview_blue);
                }else if (position > 2){
                    viewHolder.setBackgroundRes(R.id.number_tv,R.drawable.circle_textview_gray);
                }
                viewHolder.setOnClickListener(R.id.detail_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SealDetailedActivity.this,UserAndSealStatisticsActivity.class);
                        intent.putExtra("orgId",arrayList.get(position).getId());
                        intent.putExtra("orgName",arrayList.get(position).getOrgStructureName());
                        startActivity(intent);
                    }
                });
            }
        } ;
        detailAdapter.notifyDataSetChanged();
        listView.setAdapter(detailAdapter);
    }

    /**
     *  获取搜索内容
     * @param search
     */
    private void getSearchData(String search){
        ArrayList<UseSealDetailData.orgStructureStatisticVoList> searchList = new ArrayList<>();
        if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
            for (UseSealDetailData.orgStructureStatisticVoList useSealDetailData : responseInfo.getData().getOrgStructureStatisticVoList()) {
                if (useSealDetailData.getOrgStructureName().contains(search)) {
                    if (!searchList.contains(useSealDetailData)) {
                        searchList.add(useSealDetailData);
                    }
                }
            }
        }
        for (UseSealDetailData.orgStructureStatisticVoList data : searchList){
            arrayList.add(new UseSealDetailData.orgStructureStatisticVoList(data.getId(),data.getOrgStructureName(),data.getStampCount()));
        }
        listView.setAdapter(detailAdapter);
        if (detailAdapter != null) {
            detailAdapter.notifyDataSetChanged();
        }
        if (searchList.size() == 0) {
            showToast("未查询到结果");
        }
    }
}
