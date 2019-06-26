package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 添加审批流人员类型
 */
public class AddApprovalFlowTypeActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.approvalPeople_ll)
    LinearLayout approvalPeople_ll;
    @BindView(R.id.copyPeople_ll)
    LinearLayout copyPeople_ll;
    private Intent intent;
    private String sealId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_approval_flow_type);

        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("人员类型");
        set_back_ll.setOnClickListener(this);
        approvalPeople_ll.setOnClickListener(this);
        copyPeople_ll.setOnClickListener(this);
        intent = getIntent();
        sealId = intent.getStringExtra("sealId");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.approvalPeople_ll:
                intent = new Intent(this,AddApprovalFlowActivity.class);
                intent.putExtra("sealId",sealId);
                startActivityForResult(intent,20);
//                startActivity(intent);
                break;
            case R.id.copyPeople_ll:
                intent = new Intent(this, SelectSinglePeopleActivity.class);
                startActivityForResult(intent, 123);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == 10) {
            if (data != null) {
                String userId = data.getStringExtra("id");
                addSealApproveFlow(userId);
            }
        }
        if (resultCode == 20){
//            EventBus.getDefault().post(new MessageEvent("添加审批流刷新","添加审批流刷新"));
            finish();
        }
    }

    /**
     * 添加抄送人
     */
    private void addSealApproveFlow(String userId) {
        Intent intent = getIntent();
        String sealId = intent.getStringExtra("sealId");
        SealInfoData.SealApproveFlowListBean listBean = new SealInfoData.SealApproveFlowListBean();
        listBean.setApproveUser(userId);
        listBean.setApproveType(1);
        listBean.setApproveLevel(0);
        listBean.setSealId(sealId);
        HttpUtil.sendDataAsync(this, HttpUrl.ADDAPPROVALFLOW, 2, null, listBean, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + " 添加审批流错误错误错误!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        finish();
                    }
                } else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }
}
