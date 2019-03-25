package cn.fengwoo.sealsteward.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 关于
 */
public class EditOrganizationNameActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.title_tv)TextView title_tv;
    @BindView(R.id.set_back_ll)LinearLayout set_back_ll;
    @BindView(R.id.et_name)EditText et_name;
    @BindView(R.id.btn_confirm)Button btn_confirm;
    String orgStrId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_organization_name);
        ButterKnife.bind(this);
        initView();
        orgStrId = getIntent().getStringExtra("orgStrId");
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("改名");
        set_back_ll.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.btn_confirm:
                String theNewName = et_name.getText().toString().trim();

                if (TextUtils.isEmpty(theNewName)) {
                    showToast("请输入组织名字");
                    return;
                }

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("orgStrId", orgStrId);
                hashMap.put("name", theNewName);
                HttpUtil.sendDataAsync(EditOrganizationNameActivity.this, HttpUrl.EDIT_ORG, 3, hashMap, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Utils.log(e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Utils.log(result);
                        EditOrganizationNameActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditOrganizationNameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                setResult(20);
                                finish();
                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                });
                break;
        }
    }


}
