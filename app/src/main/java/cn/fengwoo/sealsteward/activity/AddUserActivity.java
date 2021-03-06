package cn.fengwoo.sealsteward.activity;

import android.Manifest;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.white.easysp.EasySP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddUserInfo;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.widget.WheelView;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
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
    //    @BindView(R.id.spinner_job)
//    Spinner spinner_job;
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
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @BindView(R.id.down_ll)
    LinearLayout down_ll;

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
        down_ll.setOnClickListener(this);
        loadingView = new LoadingView(this);
/*        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        spinner_job.setAdapter(adapter);
        spinner_job.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_job.setText(COUNTRIES[position]);
//                spinner_job.setText("");
            }
        });*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.mail_list_rl:      //被隐藏了
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
                // 判断是不是有 添加用户权限
                String permissionJson = EasySP.init(this).getString("permission");
                if (permissionJson.contains(Constants.permission17)) {
                    addUser();
                } else {
                    add_user_next_Bt.setText("完成");
                }

//                intent = new Intent(this, SetPowerActivity.class);
//                startActivity(intent);
                break;
            case R.id.down_ll:
                selectJob();
                break;

        }
    }

    /**
     * 选择职位
     */
    private void selectJob() {
        List<String> list = new ArrayList<>();
        list.add("经理");
        list.add("普通员工");
        SinglePicker<String> picker;
        picker = new SinglePicker<String>(this, list);
        picker.setCanceledOnTouchOutside(true);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setTextColor(0xFF000000);
//        picker.setSubmitTextColor(0xFFFB2C3C);
//        picker.setCancelTextColor(0xFFFB2C3C);
        picker.setTextSize(15);
        picker.setLineSpaceMultiplier(2);   //设置每项的高度，范围为2-4
        picker.setContentPadding(0, 10);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                et_job.setText(COUNTRIES[index]);
            }
        });
        picker.show();
    }

    private void addUser() {

        if (TextUtils.isEmpty(et_job.getText().toString())) {
            Toast.makeText(this, "请输入职位", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(code_et.getText().toString())) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(departmentName)) {
            Toast.makeText(this, "请选择部门", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone_number_et.getText().toString())) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

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
            if (data != null) {
                departmentId = data.getStringExtra("id");
                departmentName = data.getStringExtra("name");
                Utils.log("888***id:" + departmentId + "  ***name:" + departmentName);
                tv_department.setText(departmentName);
            }
        }
/*
        switch (requestCode) {
            case 1:
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

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //获取手机通讯录联系人
                ContentResolver cr = AddUserActivity.this.getContentResolver();
                assert data != null;
                Uri contactData = data.getData();
                //获取所有联系人
                assert contactData != null;
                Cursor cursor = cr.query(contactData, null, null, null, null);
                if (cursor != null) {
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
                readPermissions();
                break;
            case R.id.sendSecurityCode:
                sendCode();
                break;
        }
    }
    /**
     * 获取验证码
     */
    private void sendCode() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HashMap<String , String> hashMap = new HashMap<>();
                hashMap.put("mobilePhone",phone_number_et.getText().toString().trim().replace(" ", ""));
                hashMap.put("type",5+"");
                HttpUtil.sendDataAsync(AddUserActivity.this, HttpUrl.SENDVERIFICATIONCODE, 1, hashMap, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        showToast(e + "");
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Utils.log(result);
                        Gson gson = new Gson();
                        ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                        }.getType());
                        if (responseInfo.getCode() == 0) {
                            if (responseInfo.getData()) {
                                timer.start();
                                Log.e("TAG", "获取验证码成功!!!!!!!!!!!!!!!!");
                                Looper.prepare();
                                showToast("验证码已发送");
                                Looper.loop();
                            }
                        } else {
                            Looper.prepare();
                            showToast(responseInfo.getMessage());
                            Looper.loop();
                        }
                    }
                });
            }
        });
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

    /**
     * 申请权限
     */
    @SuppressLint("CheckResult")
    private void readPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //添加需要的权限
        rxPermissions.requestEachCombined(Manifest.permission.READ_CONTACTS)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Utils.log("****************  666 ***********************");
//                            createNoMedia();
                            intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            startActivityForResult(intent, 1);
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });
    }
}
