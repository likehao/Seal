package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private ArrayList<UseSealDetailData> arrayList;
    private LoadingView loadingView;
    private ResponseInfo<List<UseSealDetailData>> responseInfo;
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    private void getData(){
        HttpUtil.sendDataAsync(this, HttpUrl.ORG_STATISTIC, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", e + "统计错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<List<UseSealDetailData>>>(){}
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null){
                    loadingView.cancel();
                    for (UseSealDetailData useSealDetailData : responseInfo.getData()){
                        arrayList.add(new UseSealDetailData(useSealDetailData.getOrgStructureName(),useSealDetailData.getStampCount()));
                    }
                }
            }
        });

        CommonAdapter detailAdapter = new CommonAdapter<UseSealDetailData>(SealDetailedActivity.this, arrayList, R.layout.detail_item) {
            @Override
            public void convert(ViewHolder viewHolder, UseSealDetailData useSealDetailData, int position) {
                viewHolder.setText(R.id.detail_department_tv,useSealDetailData.getOrgStructureName());
                viewHolder.setText(R.id.detail_time_tv,useSealDetailData.getStampCount()+"");
                viewHolder.setText(R.id.number_tv,position+1+"");
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
                        intent.putExtra("orgId",responseInfo.getData().get(position).getId());
                        intent.putExtra("orgName",responseInfo.getData().get(position).getOrgStructureName());
                        startActivity(intent);
                    }
                });
            }
        } ;
        detailAdapter.notifyDataSetChanged();
        listView.setAdapter(detailAdapter);
    }
}
