package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longsh.optionframelibrary.OptionBottomDialog;
import com.nestia.biometriclib.BiometricPromptManager;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.white.easysp.EasySP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.OptionsAdapter;
import cn.fengwoo.sealsteward.bean.ServiceConfigData;
import cn.fengwoo.sealsteward.database.AccountDao;
import cn.fengwoo.sealsteward.entity.HistoryInfo;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HideKeyBroadUtils;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.fengwoo.sealsteward.view.RealmNameDialog;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.widget.WheelView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ??????
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.more_tv)
    TextView more_tv;
    @BindView(R.id.phone_et)
    EditText phone_et;
    @BindView(R.id.psd_et)
    EditText psd_et;
    @BindView(R.id.login_bt)
    Button login_bt;
    @BindView(R.id.head_civ)
    CircleImageView headImageView;

    @BindView(R.id.tv_finger_print_login)
    TextView tv_finger_print_login;
    @BindView(R.id.register_tv)
    TextView register;
    @BindView(R.id.forget_pwd_tv)
    TextView forgetPwd;
    @BindView(R.id.register_forgetPwd_ll)
    LinearLayout register_forgetPwd;
    @BindView(R.id.set_realmName_ll)
    LinearLayout setRealmName;    //????????????
    private List<String> stringList;
    private CheckBox check_down_up;  //??????????????????????????????
    private AccountDao accountDao;
    private RelativeLayout password_rl; //??????????????????
    //????????????????????????
    ArrayList<HistoryInfo> datas = new ArrayList<HistoryInfo>();
    private List<HistoryInfo> historyList;
    private PopupWindow selectPopuwindow;
    private ListView listView;
    private OptionsAdapter optionsAdapter;
    private Intent intent;
    private String phone, password;
    private LoadingView loadingView;
    private LoginData user;
    private BiometricPromptManager mManager;
    private String addStr, addressUrl, ip, agreement, port_num,serviceConfig;
    private RealmNameDialog realmNameDialog;
    private String saveAddStr, saveIp, savePOrt, saveAgreement;
    private static final int JPUSHTAG = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSwipeBackEnable(false);//??????????????????
        //???????????????????????????????????????
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //??????
        ButterKnife.bind(this);
        mManager = BiometricPromptManager.from(this);
        initView();
        readPermissions();
        initData();
        dialogOut();
    }

    private void createNoMedia() {
        //????????????File????????????????????????
        File file = new File("/mnt/sdcard/SealDownImage");
        File file2 = new File("/mnt/sdcard/SealDownImage/.nomedia");
        //????????????????????????????????????????????????????????????????????????
//        if (!file.exists()) {
        //??????file???mkdirs()???????????????????????????????????????????????????
        file.mkdirs();
        try {
            file2.createNewFile();
        } catch (IOException e) {
//            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
//        }
    }

    private void initView() {
        realmNameDialog = new RealmNameDialog(this);
        check_down_up = (CheckBox) findViewById(R.id.check_down_up);
        password_rl = (RelativeLayout) findViewById(R.id.password_rl);
        login_bt.setOnClickListener(this);
        more_tv.setOnClickListener(this);
        phone_et.setOnClickListener(this);
        accountDao = new AccountDao(this);
        loadingView = new LoadingView(this);
        tv_finger_print_login.setOnClickListener(this);
        register.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
        setRealmName.setOnClickListener(this);
        realmNameDialog.agreement_rl.setOnClickListener(this);
        realmNameDialog.sure.setOnClickListener(this);
        String state = EasySP.init(this).getString("finger_print");
        if (state.equals("1")) {
        } else {
            // ????????????????????????
//            showToast("????????????????????????");
            tv_finger_print_login.setVisibility(View.GONE);
        }
        saveAddStr = getIntent().getStringExtra("addStr");   //??????????????????????????????
        saveIp = getIntent().getStringExtra("ip");
        savePOrt = getIntent().getStringExtra("port_num");
        saveAgreement = getIntent().getStringExtra("agreement");

    }

    private void initData() {
        //????????????????????????
        phone_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int a = 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int a = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    loadUserHeadPortrait(s.toString());
                }
            }
        });

        //??????????????????????????????
        check_down_up.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    initPopuwindow(); //??????????????????
                    hintKbTwo();
                    if (historyList.size() == 0) {
                        password_rl.setVisibility(View.VISIBLE);
                        register_forgetPwd.setVisibility(View.VISIBLE);
                    } else {
                        password_rl.setVisibility(View.GONE);
                        register_forgetPwd.setVisibility(View.GONE);
                    }
                } else {
                    selectPopuwindow.dismiss(); //????????????
                    password_rl.setVisibility(View.VISIBLE);
                    register_forgetPwd.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * ???????????????
     */
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * ??????????????????
     * ???????????????adapter??????list??????
     */
    private void initAddNum() {
        datas.clear();
        historyList = accountDao.quaryAll();
        //?????????
        Comparator<HistoryInfo> comparator = new Comparator<HistoryInfo>() {
            @Override
            public int compare(HistoryInfo t1, HistoryInfo t2) {
                if (Long.parseLong(t1.getTime() + "") < Long.parseLong(t2.getTime() + "")) {
                    return 1;
                }
                if (Long.parseLong(t1.getTime() + "") > Long.parseLong(t2.getTime() + "")) {
                    return -1;
                }
                return 1;
            }
        };
        //?????????????????????
        Collections.sort(historyList, comparator);
        if (historyList.size() > 5) {
            historyList = historyList.subList(0, 5);  //0,1,2,3,4
        }
        datas.addAll(historyList);

    }

    /**
     * ?????????Popuwindow
     */
    private void initPopuwindow() {
        initAddNum();
        //Popuwindow?????????????????????
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.account_list, null);
        selectPopuwindow = new PopupWindow(view);
        selectPopuwindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        selectPopuwindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        listView = view.findViewById(R.id.account_list);       //???????????????listview
        optionsAdapter = new OptionsAdapter(this, datas, accountDao);
        listView.setAdapter(optionsAdapter);
        selectPopuwindow.showAsDropDown(check_down_up, 0, 0);  //????????????,0??????xy????????????
        selectPopuwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //????????????????????????
        //??????popuwindow????????????????????????????????????????????????
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = datas.get(position).getPhone();
                phone_et.setText(phone);
                selectPopuwindow.dismiss();
                check_down_up.setChecked(false);
                password_rl.setVisibility(View.VISIBLE);
                register_forgetPwd.setVisibility(View.VISIBLE);
                optionsAdapter.notifyDataSetChanged();
                psd_et.getText().clear(); //????????????
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more_tv:
                moreDialog();
                break;
            case R.id.login_bt:
                getLogin();
                break;
            case R.id.phone_et:
                if (selectPopuwindow != null) {
                    selectPopuwindow.dismiss();
                }
                check_down_up.setChecked(false);
                break;
            case R.id.tv_finger_print_login:
                String fpPwd = EasySP.init(LoginActivity.this).getString("l_pwd");
                if (TextUtils.isEmpty(fpPwd)) {
                    showToast("???????????????????????????");
                    return;
                }

                String state = EasySP.init(this).getString("finger_print");
                if (state.equals("1")) {
                } else {
                    // ????????????????????????
                    showToast("????????????????????????");
                }
                identify();
                break;
            case R.id.register_tv:
                getResgister();
                break;
            case R.id.forget_pwd_tv:
                getForgetPwd();
                break;
            case R.id.set_realmName_ll:     //?????????????????????
                showRealmNameDialog();
                break;
            case R.id.realName_agreement_rl:    //????????????
                showPickView();
                break;
            case R.id.realmName_sure_tv:     //???????????????????????????
                getServiceConfig();
                break;
        }
    }


    /**
     * ???????????????????????????
     */
    private void showPickView() {
        List<String> list = new ArrayList<>();
        list.add("http");
        list.add("https");
        SinglePicker<String> picker = new SinglePicker<String>(this, list);
        picker.setCanceledOnTouchOutside(true);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setTextColor(0xFF000000);
//        picker.setSubmitTextColor(0xFFFB2C3C);
//        picker.setCancelTextColor(0xFFFB2C3C);
        picker.setTextSize(15);
        picker.setLineSpaceMultiplier(2);   //?????????????????????????????????2-4
        picker.setContentPadding(0, 10);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                realmNameDialog.agreement.setText(item);
            }
        });
        picker.show();
    }

    /**
     * ?????????????????????????????????dialog
     */
    private void showDia(String str) {
        final CommonDialog commonDialog = new CommonDialog(this, "??????????????????????????????", "?????????????????????????????????????????????????????????????????????????????????,??????????????????????????????????????????", str);
        commonDialog.cancel.setText("??????");
        commonDialog.showDialog();
        //??????
        commonDialog.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dialog.dismiss();
                showRealmNameDialog();
            }
        });
        //??????
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dialog.dismiss();
                addressUrl = "http://www.baiheyz.com:8080";
                EasySP.init(LoginActivity.this).putString("addStr", addressUrl);
                if (str.equals("??????")) {
                    //??????get??????
                    loadingView.show();
                    loadingView.showView("?????????");
                    loginGet(phone, password, "0");
                } else if (str.equals("????????????")) {
                    intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                } else if (str.equals("????????????")) {
                    intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * ???????????????????????????dialog
     */
    @SuppressLint({"ApplySharedPref", "SetTextI18n"})
    private void showRealmNameDialog() {
        realmNameDialog.showDialog();
        if (saveIp != null) {
            realmNameDialog.service_ip.setText(saveIp);
        }
        if (savePOrt != null) {
            realmNameDialog.port_number.setText(savePOrt);
        }
        if (ip != null && ip.length() != 0) {
            realmNameDialog.service_ip.setText(ip);
        }
        if (port_num != null && port_num.length() != 0) {
            realmNameDialog.port_number.setText(port_num);
        }
        realmNameDialog.service_ip.setSelection(realmNameDialog.service_ip.getText().toString().trim().length());  //???????????????????????????
        realmNameDialog.port_number.setSelection(realmNameDialog.port_number.getText().toString().trim().length());

    }

    /**
     * ??????????????????????????????????????????
     */
    private void getServiceAddress() {
        agreement = realmNameDialog.agreement.getText().toString().trim();  //http/htpps
        saveAgreement = agreement;

        //?????????????????????,?????????????????????
        if (port_num.length() != 0) {
            addressUrl = String.format("%s%s%s%s", agreement, "://", ip, ":" + port_num);
        } else {
            addressUrl = String.format("%s%s%s", agreement, "://", ip);
        }
        Log.e("TAG", "???????????????addressUrl:" + addressUrl);

        EasySP.init(LoginActivity.this).putString("addStr", addressUrl);

        realmNameDialog.dialog.dismiss();

//        getServiceConfig();
    }

    /**
     * ??????????????????
     */
    private void getServiceConfig(){
        ip = realmNameDialog.service_ip.getText().toString().trim();     // ip
        saveIp = ip;
        if (ip.length() == 0) {
            saveIp = null;
        }
        port_num = realmNameDialog.port_number.getText().toString().trim();  //?????????
        savePOrt = port_num;
        if (port_num.length() == 0) {
            savePOrt = null;
        }

        //?????????????????????,?????????????????????
        if (port_num.length() != 0) {
            serviceConfig = String.format("%s%s", ip, ":" + port_num);
        } else {
            serviceConfig = ip;
        }
        EasySP.init(LoginActivity.this).putString("addStr", addressUrl).clear();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("serverAddress", serviceConfig);
        HttpUtil.sendDataAsync(this, HttpUrl.SERVICECONFIG, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"??????????????????????????????!!!!!!!!!!!!!");
                realmNameDialog.dialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                realmNameDialog.dialog.dismiss();
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<ServiceConfigData> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<ServiceConfigData>>(){}
                .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null){
                    String appKey = responseInfo.getData().getAppKey();
                    EasySP.init(LoginActivity.this).putString("appkey",appKey);
                    getServiceAddress();
                }else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }

            }
        });
    }

    /**
     * ??????
     */
    private void getLogin() {
        phone = phone_et.getText().toString().trim();
        password = psd_et.getText().toString().trim();
        if (checkLogin(phone, password)) {
            return;
        } else {
            if (saveIp == null || saveIp.equals("")) {
                //??????????????????http://?????????dialog
                showDia("??????");
            } else {
                if (savePOrt != null && !savePOrt.equals("")) {
                    addressUrl = String.format("%s%s%s%s", saveAgreement, "://", saveIp, ":" + savePOrt);
                } else {
                    addressUrl = String.format("%s%s%s", saveAgreement, "://", saveIp);
                }
                EasySP.init(LoginActivity.this).putString("addStr", addressUrl);  //????????????????????????
                //??????get??????
                loadingView.show();
                loadingView.showView("?????????");
                loginGet(phone, password, "0");
            }
        }
    }

    /**
     * ??????
     */
    private void getResgister() {
        if (realmNameDialog.service_ip.getText().toString().trim().length() == 0) {
            //??????????????????http://?????????dialog
            showDia("????????????");
        } else {
            EasySP.init(LoginActivity.this).putString("addStr", addressUrl);  //????????????????????????
            //??????get??????
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    /**
     * ????????????
     */
    private void getForgetPwd() {
        if (realmNameDialog.service_ip.getText().toString().trim().length() == 0) {
            //??????????????????http://?????????dialog
            showDia("????????????");
        } else {
            EasySP.init(LoginActivity.this).putString("addStr", addressUrl);  //????????????????????????
            //??????get??????
            intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        }
    }

    /**
     * ??????????????????
     *
     * @return
     */
    private boolean checkLogin(String phone, String password) {
        boolean isOk = false;
        if (phone.length() < 11) {
            isOk = true;
            Toast.makeText(LoginActivity.this, "?????????11??????????????????", Toast.LENGTH_SHORT).show();
            return isOk;
        }
        if (password.length() < 6 || password.length() > 18) {
            isOk = true;
            Toast.makeText(LoginActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
            return isOk;
        }
        return isOk;
    }


    /**
     * ??????????????????
     *
     * @param phoneNumber
     */
    private void loadUserHeadPortrait(String phoneNumber) {
        //??????????????????
        List<HistoryInfo> historyInfoList = accountDao.quaryAll();
        if (historyInfoList != null) {
            for (HistoryInfo historyInfo : historyInfoList) {
                if (phoneNumber.equals(historyInfo.getPhone())) {
                    String head = historyInfo.getHeadPortrait();
                    if (head != null && !head.isEmpty()) {
                        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(head);
                        if (bitmap != null) {
                            headImageView.setImageBitmap(bitmap);
                            return;
                        }
                    }
                }
            }
        }
        //????????????????????????
        headImageView.setImageResource(R.drawable.default_head_portrait);
    }


    /**
     * ??????????????????
     */
    private void loginGet(final String phone, final String password, String isFP) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mobilePhone", phone);
        hashMap.put("password", password);
        HttpUtil.sendDataAsync(this, HttpUrl.LOGIN, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", "?????????????????????????????????");
                Looper.prepare();
                showToast("error:" + e);
                Looper.loop();
            }

            @SuppressLint("ApplySharedPref")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("*** login ***:" + result);
                Gson gson = new Gson();
                ResponseInfo<LoginData> loginResponseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<LoginData>>() {
                }.getType());

                if (loginResponseInfo.getData() != null && loginResponseInfo.getCode() == 0) {
                    Log.e("TAG", "???????????????????????????????????????");

                    //??????jpush??????
                    JPushInterface.setAlias(LoginActivity.this,JPUSHTAG,phone);
                    // save data
                    EasySP.init(LoginActivity.this).putString("l_tel", phone);
                    EasySP.init(LoginActivity.this).putString("l_pwd", password);

                    //?????????????????????
                    EasySP.init(LoginActivity.this).putString("agreement", saveAgreement);
                    EasySP.init(LoginActivity.this).putString("ip", saveIp);
                    EasySP.init(LoginActivity.this).putString("port_num", savePOrt);
                    Log.e("TAG", "???????????????????????????:" + addressUrl);
                    loadingView.cancel();
                    //????????????????????????
                    loginResponseInfo.getData().login(LoginActivity.this);
                    //??????????????????
                    user = loginResponseInfo.getData();
                    CommonUtil.setUserData(LoginActivity.this, user);

                    // ??????????????????
                    String targetPermissionJson = "";

                    if (user.getAdmin() != null) {
                        if (user.getAdmin()) {
                            targetPermissionJson = new Gson().toJson(user.getSystemFuncList());
                        } else {
                            targetPermissionJson = new Gson().toJson(user.getFuncIdList());
                        }
                    }
                    EasySP.init(LoginActivity.this).putString("permission", targetPermissionJson);
                    EasySP.init(LoginActivity.this).putBoolean("isAdmin", loginResponseInfo.getData().getAdmin());

//                    List<SystemFuncListInfo> systemFuncListInfo = gson.fromJson(targetPermissionJson, new TypeToken<List<SystemFuncListInfo>>() {}.getType());
//                    Utils.log(targetPermissionJson);

                    //???????????????????????????????????????
                    HistoryInfo historyInfo = new HistoryInfo(phone, user.getRealName(), user.getHeadPortrait(), new Date().getTime());

                    accountDao.insert(historyInfo);
                    //??????????????????
                    if (user.getHeadPortrait() != null && !user.getHeadPortrait().isEmpty()) {
                        //????????????????????????????????????
                        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(user.getHeadPortrait());
                        if (bitmap == null) {
                            HttpDownloader.downloadImage(LoginActivity.this, 1, user.getHeadPortrait(), new DownloadImageCallback() {
                                @Override
                                public void onResult(String fileName) {
                                    super.onResult(fileName);
                                }
                            });
                        }
                    }
                    //????????????????????????????????????
                    if (user.getNeedSync()) {
                        dataSync();
                    } else {
                        //????????????
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("isFP", isFP);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(loginResponseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

    /***
     * ?????????????????????
     */
    private void moreDialog() {
        stringList = new ArrayList<String>();
        stringList.add("??????");
        stringList.add("????????????");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(LoginActivity.this, stringList);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    optionBottomDialog.dismiss();

                } else {
                    intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                    startActivity(intent);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }

    public void setView() {
        if (datas.size() == 0) {
            password_rl.setVisibility(View.VISIBLE);
            register_forgetPwd.setVisibility(View.VISIBLE);
            check_down_up.setChecked(false);
        }
    }

    /**
     * ????????????
     */
    public void dataSync() {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                loadingView.show();
                Looper.loop();
            }
        }.start();
        loadingView.showView("???????????????,?????????...");
        login_bt.setClickable(false);
        HttpUtil.sendDataAsync(this, HttpUrl.SYNC, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "??????????????????!!!!!!!!!!!!!!!!!!!!!");
                loadingView.cancel();
                //????????????
                intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        Log.e("TAG", "??????????????????!!!!!!!!!!!!!!!!!!!!!");
                        loadingView.cancel();
                        //????????????
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    loadingView.cancel();
                    Log.e("TAG", "??????????????????!!!!!!!!!!!!!!!!!!!!!");
                    //????????????
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * ???????????????????????????
     */
    private void dialogOut() {
        Intent intent = this.getIntent();
        String timeoutStr = intent.getStringExtra("loginstatus");
        String infoStr = intent.getStringExtra("info");
        if ("timeout".equals(timeoutStr)) {
            CommonDialog commonDialog = new CommonDialog(this, "????????????", infoStr, "??????");
            commonDialog.cancel.setVisibility(View.GONE);
            commonDialog.showDialog();
            commonDialog.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commonDialog.dialog.dismiss();
                }
            });
        }
    }

    /**
     * ????????????
     */
    @SuppressLint("CheckResult")
    private void readPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //?????????????????????
        rxPermissions.requestEachCombined(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Utils.log("****************  666 ***********************");
                            createNoMedia();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("????????????????????????");
                        } else {
                            showToast("??????????????????????????????????????????>????????????>????????????????????????");
                        }
                    }
                });
    }

    /**
     * ???????????????
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        HideKeyBroadUtils.hide(this, ev);
        if (selectPopuwindow != null) {
            selectPopuwindow.dismiss();
        }
        check_down_up.setChecked(false);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @TargetApi(28)
    private void identify() {
        if (mManager.isBiometricPromptEnable()) {
            mManager.authenticate(new BiometricPromptManager.OnBiometricIdentifyCallback() {
                @Override
                public void onUsePassword() {
//                    Toast.makeText(FingerPrintActivity.this, "onUsePassword", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSucceeded() {
                    Utils.log("onSucceededonSucceeded2");

                    //??????get??????
                    String fpTel = EasySP.init(LoginActivity.this).getString("l_tel");
                    String fpPwd = EasySP.init(LoginActivity.this).getString("l_pwd");
                    if (TextUtils.isEmpty(fpPwd)) {
                        return;
                    }
                    loadingView.show();
                    loadingView.showView("?????????");
                    loginGet(fpTel, fpPwd, "1");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 800);
//                    Toast.makeText(FingerPrintActivity.this, "onSucceeded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed() {

//                    Toast.makeText(FingerPrintActivity.this, "onFailed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int code, String reason) {

//                    Toast.makeText(FingerPrintActivity.this, "onError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {

//                    Toast.makeText(FingerPrintActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
