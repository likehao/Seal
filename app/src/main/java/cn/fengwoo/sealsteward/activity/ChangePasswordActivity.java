package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 修改密码
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.oldPwd_et)
    EditText oldPwd_et;
    @BindView(R.id.newPwd_et)
    EditText newPwd_et;
    @BindView(R.id.sure_newPwd_et)
    EditText sure_newPwd_et;
    private String oldPwd,newPwd,surePwd;
    private LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        title_tv.setText("修改密码");
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("完成");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
        loadingView = new LoadingView(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.edit_tv:
                changePwd();
                break;
        }
    }
    /**
     * 提交修改密码
     */
    private void changePwd(){
        if (checkData()){
            loadingView.show();
            patchChangePwd();
        }
    }

    /**
     * 检验密码数据
     */
    private boolean checkData(){
        oldPwd = oldPwd_et.getText().toString().trim();
        newPwd = newPwd_et.getText().toString().trim();
        surePwd = sure_newPwd_et.getText().toString().trim();
        if (oldPwd.length()== 0) {
            showToast("请输入6位以上18位以下密码和数字组合");
            return false;
        }
        if (oldPwd.length()< 6) {
            showToast("密码必须大于6位数");
            return false;
        }
        if (!newPwd.equals(surePwd)){
            showToast("两次密码不一致");
            return false;
        }
        return true;
    }

    private void patchChangePwd(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("oldPassword",oldPwd);
        hashMap.put("newPassword",newPwd);
        HttpUtil.sendDataAsync(ChangePasswordActivity.this, HttpUrl.UPDATEPASSWORD, 3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}
                .getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()){
                        loadingView.cancel();
                        finish();
                        Log.e("TAG","密码修改成功");
                        Looper.prepare();
                        showToast("修改成功,请重新登录");
                        Looper.loop();
                    }
                }else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

}
