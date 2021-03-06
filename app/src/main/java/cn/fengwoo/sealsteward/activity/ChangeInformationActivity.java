package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
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
import cn.fengwoo.sealsteward.entity.AddCompanyInfo;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
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
    String companyId,userId;
    private static final int CODE = 1;

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
        String realName = intent.getStringExtra("realName");             //姓名
        String mobilePhone = intent.getStringExtra("mobilePhone");    //电话
        String userEmail = intent.getStringExtra("userEmail");       //邮箱
        String companyName = intent.getStringExtra("companyName");   //公司名字
        String socialCode = intent.getStringExtra("socialCode");    //社会信用代码
        String legalPerson = intent.getStringExtra("legalPerson");   //法人
        companyId = intent.getStringExtra("companyId");   //法人
        String job = intent.getStringExtra("job");   //职位
        userId = intent.getStringExtra("userId");    //组织架构用户ID
        tag = intent.getIntExtra("TAG", 0);
        //判断点击的是哪个
        switch (tag) {
            case 1:
                information_et.setText(realName);
                information_et.setSelection(realName.length());  //将光标移至文字末尾
                break;
            case 2:
                information_et.setText(mobilePhone);
                information_et.setSelection(mobilePhone.length());
                break;
            case 3:
                information_et.setText(userEmail);
                information_et.setSelection(userEmail.length());
                break;
            case 4:
                information_et.setText(companyName);
                information_et.setSelection(companyName.length());
                break;
            case 5:
                information_et.setText(socialCode);
                information_et.setSelection(socialCode.length());
                break;
            case 6:
                information_et.setText(legalPerson);
                information_et.setSelection(legalPerson.length());
                break;
            case 7:
                information_et.setText(job);
                information_et.setSelection(job.length());
                break;
            case 8:
                information_et.setText(realName);
                information_et.setSelection(realName.length());
                break;
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
    private void typeChangeInfomation() {
        if (information_et.getText().toString().trim().length() != 0) {
            loadingView.show();
            if (tag == 1) {  //修改姓名
                changeNameInfomation();
            } else if (tag == 2) {  //修改电话
                changePhoneInfomation();
            } else if (tag == 3) {
                changeEmailInfomation();   //修改邮箱
            } else if (tag == 7){
                changeJob();   //修改职位
            }else if (tag == 8){
                changeUserName();  //修改组织架构用户名
            }else {
                Intent intent = new Intent();
                intent.putExtra("companyText",information_et.getText().toString());
                setResult(CODE,intent);
                finish();
//                changeCompanyName();   //修改公司
            }
        } else {
            showToast("信息不能为空");
        }
    }

    /**
     * 修改组织架构用户名
     */
    private void changeUserName(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("realName", information_et.getText().toString());
        hashMap.put("userId",userId);
        HttpUtil.sendDataAsync(this, HttpUrl.UPDATEUSERNAME, 3, hashMap, null, new Callback() {
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
                        //更新存储姓名
                        LoginData data = CommonUtil.getUserData(ChangeInformationActivity.this);
                        if (data != null) {
                            data.setRealName(newName);
                            CommonUtil.setUserData(ChangeInformationActivity.this, data);
                        }
                        setResult(12);
                        finish();
                        showMsg("修改成功");
                    } else {
                        loadingView.cancel();
                        showMsg(responseInfo.getMessage());
                    }
                } else {
                    loadingView.cancel();
                    showMsg(responseInfo.getMessage());
                }
            }
        });
    }
    /**
     * 更改姓名
     */
    private void changeNameInfomation() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("realName", information_et.getText().toString());
        HttpUtil.sendDataAsync(this, HttpUrl.UPDATEREALNAME, 3, hashMap, null, new Callback() {
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
                        //更新存储姓名
                        LoginData data = CommonUtil.getUserData(ChangeInformationActivity.this);
                        if (data != null) {
                            data.setRealName(newName);
                            CommonUtil.setUserData(ChangeInformationActivity.this, data);
                        }
                        Intent intent = new Intent(ChangeInformationActivity.this, PersonCenterActivity.class);
                        //       intent.putExtra("changeRealName", newName);
                        startActivity(intent);
                        finish();
                        showMsg("修改成功");
                    } else {
                        loadingView.cancel();
                        showMsg(responseInfo.getMessage());
                    }
                } else {
                    loadingView.cancel();
                    showMsg(responseInfo.getMessage());
                }
            }
        });
    }

    /**
     * 更改电话
     */
    private void changePhoneInfomation() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mobilePhone", information_et.getText().toString());
        HttpUtil.sendDataAsync(this, HttpUrl.UPDATEMOBILEPHONE, 3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                showMsg(e + "");
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
                        String newPhone = information_et.getText().toString().trim();
                        //更新存储电话
                        LoginData data = CommonUtil.getUserData(ChangeInformationActivity.this);
                        if (data != null) {
                            data.setMobilePhone(newPhone);
                            CommonUtil.setUserData(ChangeInformationActivity.this, data);
                        }
                        Intent intent = new Intent(ChangeInformationActivity.this, PersonCenterActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        loadingView.cancel();
                        showMsg(responseInfo.getMessage());
                    }
                } else {
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
        HttpUtil.sendDataAsync(this, HttpUrl.UPDATEUSEREMAIL, 3, hashMap, null, new Callback() {
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
                    } else {
                        loadingView.cancel();
                        showMsg(responseInfo.getMessage());
                    }
                } else {
                    loadingView.cancel();
                    showMsg(responseInfo.getMessage());
                }
            }
        });
    }

    /**
     * 更改公司名称
     */
    private void changeCompanyName() {
        AddCompanyInfo companyInfo = new AddCompanyInfo();
        companyInfo.setCompanyName(information_et.getText().toString().trim());
        companyInfo.setId(companyId);
        HttpUtil.sendDataAsync(this, HttpUrl.UPDATECOMPANY, 5, null, companyInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG",e+"更改公司名称错误错误错误!!!!!!!!!!!!!!!!");
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
                        //更新存储公司名
                        String newCompanyName = information_et.getText().toString().trim();
                        LoginData data = CommonUtil.getUserData(ChangeInformationActivity.this);
                        if (data != null) {
                            data.setCompanyName(newCompanyName);
                            CommonUtil.setUserData(ChangeInformationActivity.this, data);
                        }
                        finish();
                        showMsg("修改成功");
                    }
                }else {
                    loadingView.cancel();
                    showMsg(responseInfo.getMessage());
                }
            }
        });

    }

    /**
     * 更改职位
     */
    private void changeJob(){
        HashMap<String , String> hashMap = new HashMap<>();
        if (userId != null){
            hashMap.put("userId", userId);
        }else {
            hashMap.put("userId", CommonUtil.getUserData(this).getId());
        }
        hashMap.put("job",information_et.getText().toString());
        HttpUtil.sendDataAsync(this, HttpUrl.CHANGE_JOB, 3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG","更改职位错误错误错误错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}.getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()){
                        loadingView.cancel();
                        //更新存储职位
                        String newJob = information_et.getText().toString().trim();
                        LoginData data = CommonUtil.getUserData(ChangeInformationActivity.this);
                        if (data != null) {
                            data.setJob(newJob);
                            CommonUtil.setUserData(ChangeInformationActivity.this, data);
                        }
                        if (userId != null){
                            //组织架构用户修改
                            setResult(12);
                        }
                        finish();
                        showMsg("修改成功");
                    }
                }else {
                    loadingView.cancel();
                    showMsg(responseInfo.getMessage());
                }
            }
        });
    }
    private void showMsg(String str) {
        Looper.prepare();
        showToast(str);
        Looper.loop();
    }
}
