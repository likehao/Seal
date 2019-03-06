package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.FromToJson;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import cn.fengwoo.sealsteward.utils.RequestHeaderUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 修改个人信息
 */
public class ChangeInformationActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.cancel_ll)
    LinearLayout cancel_ll;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.delete_ll)
    LinearLayout delete_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.information_et)
    EditText information_et;
    LoadingView loadingView;
    private int tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        cancel_ll.setVisibility(View.VISIBLE);
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("完成");
        title_tv.setText("修改信息");
        cancel_ll.setOnClickListener(this);
        delete_ll.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
   //     information_et.setCursorVisible(false);  //隐藏光标
        loadingView = new LoadingView(this);

    }

    /**
     * 初始化个人资料跳转传递过来的数据
     */
    private void initData() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("sealName");
        String mobilePhone = intent.getStringExtra("mobilePhone");
        String userEmail = intent.getStringExtra("userEmail");
        tag = intent.getIntExtra("TAG", 0);
        //判断点击的是哪个
        if (tag == 1) {
            information_et.setText(name);
            information_et.setSelection(name.length());  //将光标移至文字末尾
        } else if(tag == 2){
            information_et.setText(mobilePhone);
            information_et.setSelection(mobilePhone.length());
        }else {
            information_et.setText(userEmail);
            information_et.setSelection(userEmail.length());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_ll:
                finish();
                break;
            case R.id.delete_ll:
                information_et.getText().clear(); //清空内容
                break;
            case R.id.edit_tv:
                typeChangeInfomation();
                break;
        }
    }

    /**
     * 判断修改类型
     */
    private void typeChangeInfomation(){
        if (information_et.getText().length() != 0){
            loadingView.show();
            if (tag == 1){  //修改姓名
                changeNameInfomation();
            }else if (tag == 2){
                changePhoneInfomation();
            }else {
                changeEmailInfomation();
            }
        }else {
            showToast("信息不能为空");
        }
    }
    /**
     * 更改姓名
     */
    private void changeNameInfomation() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("realName", information_et.getText().toString());
        HttpUtil.sendDataAsync(HttpUrl.UPDATEREALNAME,3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                showMsg(e + "");
                Log.e("TAG", "更改名字失败........");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        loadingView.cancel();
                        String newName = information_et.getText().toString().trim();
                        Intent intent = new Intent(ChangeInformationActivity.this, PersonCenterActivity.class);
                 //       intent.putExtra("changeRealName", newName);
                        startActivity(intent);
                        finish();
                        showMsg("修改成功");
                    }else {
                        loadingView.cancel();
                        showMsg(responseInfo.getMessage());
                    }
                }else {
                    loadingView.cancel();
                    showMsg(responseInfo.getMessage());
                }
            }
        });
    }

    /**
     * 更改电话
     */
    private void changePhoneInfomation(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mobilePhone", information_et.getText().toString());
        HttpUtil.sendDataAsync(HttpUrl.UPDATEMOBILEPHONE,3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                showMsg(e +"");
                Log.e("TAG", "更改电话失败........");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        loadingView.cancel();
                        Intent intent = new Intent(ChangeInformationActivity.this, PersonCenterActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        loadingView.cancel();
                        showMsg(responseInfo.getMessage());
                    }
                }
                else {
                    loadingView.cancel();
                    showMsg(responseInfo.getMessage());
                }
            }
        });
    }
    /**
     * 更改邮箱
     */
    private void changeEmailInfomation() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userEmail", information_et.getText().toString());
        HttpUtil.sendDataAsync(HttpUrl.UPDATEUSEREMAIL,3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                showMsg(e + "");
                Log.e("TAG", "更改邮箱失败........");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        loadingView.cancel();
                        Intent intent = new Intent(ChangeInformationActivity.this, PersonCenterActivity.class);
                        startActivity(intent);
                        finish();
                        showMsg("修改成功");
                    }else {
                        loadingView.cancel();
                        showMsg(responseInfo.getMessage());
                    }
                }else {
                    loadingView.cancel();
                    showMsg(responseInfo.getMessage());
                }
            }
        });
    }

    private void showMsg(String str){
        Looper.prepare();
        showToast(str);
        Looper.loop();
    }
}
