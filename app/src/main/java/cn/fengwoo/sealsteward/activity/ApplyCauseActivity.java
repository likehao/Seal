package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.CauseAdapter;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.entity.ApplyCauseData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealApplyData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 申请事由（我要盖章进入）
 */
public class ApplyCauseActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    @BindView(R.id.cause_lv)
    ListView cause_lv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    private CauseAdapter causeAdapter;
    private List<ApplyCauseData> causeDataList;
    private ResponseInfo<List<SealApplyData>> responseInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_cause);

        ButterKnife.bind(this);
        initView();
        initData();
        getData();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
    }

    private void initData() {
        title_tv.setText("申请列表");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        causeDataList = new ArrayList<ApplyCauseData>();
        causeAdapter = new CauseAdapter(causeDataList,this);

        cause_lv.setAdapter(causeAdapter);
        cause_lv.setOnItemClickListener(this);
    }

    public void getData() {
        HashMap<String ,String> hashMap = new HashMap<>();
        hashMap.put("sealId", EasySP.init(this).getString("currentSealId"));
        HttpUtil.sendDataAsync(this, HttpUrl.USE_SEAL_APPLYLIST, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"查看详情错误错误!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<List<SealApplyData>>>(){}
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null){
                    for (SealApplyData sealApplyData : responseInfo.getData()) {
//                        causeDataList.add(new ApplyCauseData(sealApplyData.getApplyCause(),sealApplyData.getApplyCount()));
                        causeDataList.add(new ApplyCauseData(sealApplyData.getApplyCause(),sealApplyData.getAvailableCount()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            causeAdapter.notifyDataSetChanged();
                        }
                    });
                }else {
                    Looper.prepare();
                    Toast.makeText(ApplyCauseActivity.this,responseInfo.getMessage(),Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = new Intent(ApplyCauseActivity.this,SealDetailActivity.class);
//        startActivity(intent);
        EasySP.init(this).putString("currentApplyId", responseInfo.getData().get(i).getId());

        Intent intent = new Intent();
        intent.putExtra("expireTime", responseInfo.getData().get(i).getExpireTime()+"");
        intent.putExtra("availableCount", responseInfo.getData().get(i).getAvailableCount()+"");
        setResult(Constants.TO_WANT_SEAL,intent);
        finish();
    }
}
