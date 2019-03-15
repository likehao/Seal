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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddCompanyInfo;
import cn.fengwoo.sealsteward.entity.AddUserInfo;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加用户
 */
public class AddUserActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.tv_department)
    TextView tv_department;
    @BindView(R.id.mail_list_rl)
    RelativeLayout mail_list_rl;
    @BindView(R.id.code_et)
    EditText code_et;
    @BindView(R.id.phone_number_et)
    EditText phone_number_et;
    @BindView(R.id.select_organizational_rl)
    RelativeLayout select_organizational_rl;
    @BindView(R.id.jumpToAdrrList)
    TextView jumpToAdrrList;
    @BindView(R.id.sendSecurityCode)
    TextView sendSecurityCode;
    private Intent intent;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.tv_job)
    TextView tv_job;
    @BindView(R.id.add_user_next_Bt)
    Button add_user_next_Bt;

    private String departmentId;
    private String departmentName;
    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加用户");
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("扫一扫");
        edit_tv.setOnClickListener(this);
        set_back_ll.setOnClickListener(this);
        mail_list_rl.setOnClickListener(this);
        select_organizational_rl.setOnClickListener(this);
        add_user_next_Bt.setOnClickListener(this);
        loadingView = new LoadingView(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.mail_list_rl:
                intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 0);
                break;
            case R.id.select_organizational_rl:
                intent = new Intent(this, OrganizationalManagementActivity.class);
                startActivityForResult(intent,123);
                break;
            case R.id.edit_tv:
                intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.add_user_next_Bt:

                addUser();

//                intent = new Intent(this, SetPowerActivity.class);
//                startActivity(intent);
                break;
        }
    }

    private void addUser() {
        loadingView.show();
        AddUserInfo addUserInfo = new AddUserInfo();

        addUserInfo.setOrgStructureId(departmentId);
        addUserInfo.setOrgStructureName(departmentName);
        addUserInfo.setMobilePhone(phone_number_et.getText().toString().replace(" ",""));
        addUserInfo.setJob(tv_job.getText().toString());
        addUserInfo.setCode(code_et.getText().toString());

        HttpUtil.sendDataAsync(AddUserActivity.this, HttpUrl.ADD_USER, 2, null, addUserInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                showToast(e+"");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("***:" + result);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("message");
                    if (state.equals("成功")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                Gson gson = new Gson();
//                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}.getType());
//                if (responseInfo.getCode() == 0){
//                    if (responseInfo.getData()){
//                        loadingView.cancel();
//                        finish();
//                        Looper.prepare();
//                        showToast("添加成功");
//                        Looper.loop();
//                    }else {
//                        loadingView.cancel();
//                        Looper.prepare();
//                        showToast(responseInfo.getMessage());
//                        Looper.loop();
//                    }
//                }else {
//                    loadingView.cancel();
//                    Looper.prepare();
//                    showToast(responseInfo.getMessage());
//                    Looper.loop();
//                }

            }
        });
    }


    /**
     * 获取通信录信息结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 123){
            departmentId = data.getExtras().getString("id");
            departmentName = data.getExtras().getString("name");
            Utils.log("888***id:" + departmentId + "  ***name:" + departmentName);
            tv_department.setText(departmentName);
        }

        if (resultCode == Activity.RESULT_OK) {
            //获取手机通讯录联系人
            ContentResolver cr = AddUserActivity.this.getContentResolver();
            Uri contactData = data.getData();
            //获取所有联系人
            Cursor cursor = cr.query(contactData, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //获取用户名和电话
                    String userName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null,
                            null);
                    while (phone.moveToNext()) {
                        String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        userName_et.setText(userName);
                        phone_number_et.setText(phoneNumber);
                    }
                    phone.close();
                }
                cursor.close();
            }
        }
    }

    @OnClick({R.id.jumpToAdrrList, R.id.sendSecurityCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.jumpToAdrrList:
                intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 0);
                break;
            case R.id.sendSecurityCode:
                //创建okHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建请求
                Request request = new Request.Builder()
                        .url(HttpUrl.URL + HttpUrl.SENDVERIFICATIONCODE + "?mobilePhone=" + phone_number_et.getText().toString().trim().replace(" ","") + "&type=" + 5)
                        .get()
                        .build();
                //设置回调
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        showToast(e + "");
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){
                        }.getType());
                        //           ResponseInfo<Boolean> responseInfo = fromToJson.fromToJson(result);
                        if (responseInfo.getCode() == 0){
                            if (responseInfo.getData()){
                                Looper.prepare();
                                showToast("验证码已发送");
                                Looper.loop();
                                Log.e("TAG","获取验证码成功!!!!!!!!!!!!!!!!");
                            }
                        }else {
                            Looper.prepare();
                            showToast(responseInfo.getMessage());
                            Looper.loop();
                        }
                    }
                });

                break;
        }
    }
}
