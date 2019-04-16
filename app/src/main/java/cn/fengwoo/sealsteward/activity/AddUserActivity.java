package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.weiwangcn.betterspinner.library.BetterSpinner;
import com.white.easysp.EasySP;

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
import cn.fengwoo.sealsteward.utils.Constants;
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
    @BindView(R.id.spinner_job)
    BetterSpinner spinner_job;
    @BindView(R.id.et_job)
    EditText et_job;
    @BindView(R.id.add_user_next_Bt)
    Button add_user_next_Bt;

    private String departmentId;
    private String departmentName;
    private LoadingView loadingView;

    private static final String[] COUNTRIES = new String[]{
            "经理", "普通员工"
    };

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
//        edit_tv.setVisibility(View.VISIBLE);
//        edit_tv.setText("扫一扫");
        edit_tv.setOnClickListener(this);
        set_back_ll.setOnClickListener(this);
        mail_list_rl.setOnClickListener(this);
        select_organizational_rl.setOnClickListener(this);
        add_user_next_Bt.setOnClickListener(this);
        loadingView = new LoadingView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        spinner_job.setAdapter(adapter);
        spinner_job.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_job.setText(COUNTRIES[position]);
                spinner_job.setText("");
            }
        });
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
                startActivityForResult(intent, 123);
                break;
            case R.id.edit_tv:
                intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.add_user_next_Bt:
                addUser();
                // 判断是不是有 添加用户权限
                String permissionJson = EasySP.init(this).getString("permission");
                if (permissionJson.contains(Constants.permission17)) {
                } else {
                    add_user_next_Bt.setText("完成");
                }

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
        addUserInfo.setMobilePhone(phone_number_et.getText().toString().replace(" ", ""));
        addUserInfo.setJob(et_job.getText().toString());
        addUserInfo.setCode(code_et.getText().toString());

        HttpUtil.sendDataAsync(AddUserActivity.this, HttpUrl.ADD_USER, 2, null, addUserInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("addUser:" + result);

                JSONObject jsonObject = null;
                JSONObject jsonObject2 = null;
                try {
                    jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("message");
                    if (state.equals("成功")) {
                        loadingView.cancel();
                        String dataString = jsonObject.getString("data");

                        jsonObject2 = new JSONObject(dataString);
                        String userId = jsonObject2.getString("id");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Utils.hasThePermission(AddUserActivity.this, Constants.permission17)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("userId", userId);
                                    intent.setClass(AddUserActivity.this, SetPowerActivity.class);
                                    intent.putExtra("last_activity", AddUserActivity.class.getSimpleName());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    finish();
                                }
                            }
                        });
                    } else {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(state);
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

        if (resultCode == 123) {
            departmentId = data.getExtras().getString("id");
            departmentName = data.getExtras().getString("name");
            Utils.log("888***id:" + departmentId + "  ***name:" + departmentName);
            tv_department.setText(departmentName);
        }
      /*  switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().query(contactData, null, null, null,
                            null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    String num = this.getContactPhone(cursor);
                    phone_number_et.setText(num);
                }
                break;
        }*/
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

    /**
     * 获取联系人电话
     *
     * @param cursor
     * @return
     */
    private String getContactPhone(Cursor cursor) {
        // TODO Auto-generated method stub
        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String result = "";
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人电话的cursor
            Cursor phone = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            assert phone != null;
            if (phone.moveToFirst()) {
                // 遍历所有的电话号码
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int index = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int phone_type = phone.getInt(typeindex);
                    String phoneNumber = phone.getString(index);
                    switch (phone_type) {
                        case 2:
                            result = phoneNumber;
                            break;
                    }

                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        return result;
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
                        .url(HttpUrl.URL + HttpUrl.SENDVERIFICATIONCODE + "?mobilePhone=" + phone_number_et.getText().toString().trim().replace(" ", "") + "&type=" + 5)
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
                        ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                        }.getType());
                        //           ResponseInfo<Boolean> responseInfo = fromToJson.fromToJson(result);
                        if (responseInfo.getCode() == 0) {
                            if (responseInfo.getData()) {
                                timer.start();
                                Looper.prepare();
                                showToast("验证码已发送");
                                Looper.loop();
                                Log.e("TAG", "获取验证码成功!!!!!!!!!!!!!!!!");
                            }
                        } else {
                            Looper.prepare();
                            showToast(responseInfo.getMessage());
                            Looper.loop();
                        }
                    }
                });

                break;
        }
    }
    /**
     * 验证码倒计时
     */
    CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            sendSecurityCode.setEnabled(false);
            sendSecurityCode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            sendSecurityCode.setEnabled(true);
            sendSecurityCode.setText("重新发送");
        }
    };
}
