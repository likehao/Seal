package cn.fengwoo.sealsteward.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 添加印章
 */
public class AddSealActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.et_seal_name)
    EditText etSealName;
    @BindView(R.id.et_seal_number)
    EditText etSealNumber;
    @BindView(R.id.tv_mac)
    TextView tvMac;
    @BindView(R.id.et_range)
    EditText etRange;
    @BindView(R.id.tv_department)
    TextView tvDepartment;
    @BindView(R.id.rl_choose_department)
    RelativeLayout rlChooseDepartment;
    @BindView(R.id.btn_next)
    Button btnNext;
    private String macString = "";

    private String departmentId;
    private String departmentName;
    private String sealName;
    private String sealNumber;
    private String useRange;


    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_add_seal);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        scan_ll.setVisibility(View.GONE);
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加印章");
        set_back_ll.setOnClickListener(this);
        loadingView = new LoadingView(this);
    }

    private void initData() {
        macString = getIntent().getStringExtra("mac");
        Utils.log(macString);
        tvMac.setText(macString);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    @OnClick({R.id.rl_choose_department, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_choose_department:
                Intent intent = new Intent(this, OrganizationalManagementActivity.class);
                startActivityForResult(intent,123);
                break;
            case R.id.btn_next:
                sealName = etSealName.getText().toString().trim();
                sealNumber = etSealNumber.getText().toString().trim();
                useRange = etRange.getText().toString().trim();
                checkSeal();

//                Intent intent2 = new Intent();
//                intent2.setClass(AddSealActivity.this, AddSealSecStepActivity.class);
//                finish();
//                startActivity(intent2);
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 123){
            departmentId = data.getExtras().getString("id");
            departmentName = data.getExtras().getString("name");
            Utils.log("888***id:" + departmentId + "  ***name:" + departmentName);
            tvDepartment.setText(departmentName);
        }
    }



    /**
     * 发送请求刷新个人信息
     */
    private void checkSeal() {
        loadingView.show();
        //添加用户ID为参数
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mac", macString);
        HttpUtil.sendDataAsync(this, HttpUrl.SEAL_CHECK_ADD, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
                loadingView.cancel();
                Looper.prepare();
                Toast.makeText(AddSealActivity.this, e + "", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Utils.log(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String codeString = jsonObject.getString("code");
                    if (codeString.equals("0")) {
                        Utils.log("success");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setClass(AddSealActivity.this, AddSealSecStepActivity.class);
                                intent.putExtra("name", sealName);
                                intent.putExtra("mac", macString);
                                intent.putExtra("sealNo", sealNumber);
                                intent.putExtra("scope", useRange);
                                intent.putExtra("orgStructrueId", departmentId);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


}
