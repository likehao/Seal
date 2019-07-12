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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.ApproveProgressAdapter;
import cn.fengwoo.sealsteward.bean.ApproveProgress;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 审批进度
 */
public class ApprovalActivity extends BaseActivity {
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.progress_lv)
    ListView progress_lv;
    private ApproveProgressAdapter progressAdapter;
    private List<ApproveProgress> list;
    LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        ButterKnife.bind(this);
        initView();
        getApproveProgress();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        loadingView = new LoadingView(this);
        list = new ArrayList<>();
        progressAdapter = new ApproveProgressAdapter(list, this);
        progress_lv.setAdapter(progressAdapter);
        title_tv.setText("审批进度");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 审批进度
     */
    private void getApproveProgress() {
        loadingView.show();
        Intent intent = getIntent();
        String applyId = intent.getStringExtra("applyId");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", applyId);
        HttpUtil.sendDataAsync(this, HttpUrl.APPROVEPROGRESS, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "审批进度错误错误");
                loadingView.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<ApproveProgress>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<ApproveProgress>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    loadingView.cancel();
                    for (ApproveProgress approveProgress : responseInfo.getData()) {
                        list.add(new ApproveProgress(approveProgress.getApproveStatus(), approveProgress.getApproveUserName(), approveProgress.getOrgStructureName()
                        ,approveProgress.getApproveOpinion(),approveProgress.getApproveTime(),approveProgress.getCreateTime(),approveProgress.getMobilePhone()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressAdapter.notifyDataSetChanged(); //刷新数据
                        }
                    });

                } else {
                    loadingView.cancel();
                    Log.e("TAG", responseInfo.getMessage() + "查看审批进度错误错误!!!!!!!!!!!!!");
                }

            }
        });
    }
}
