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

import com.cjt2325.cameralibrary.util.LogUtil;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.OptionsAdapter;
import cn.fengwoo.sealsteward.database.AccountDao;
import cn.fengwoo.sealsteward.entity.HistoryInfo;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.Base2Activity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HideKeyBroadUtils;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.LoadingView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 登录
 */
public class LoginActivity extends Base2Activity implements View.OnClickListener {
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
    private List<String> stringList;
    private CheckBox check_down_up;  //弹出账号列表上下箭头
    private AccountDao accountDao;
    private RelativeLayout password_rl; //账号以下布局
    //下拉框选项数据源
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //注解
        ButterKnife.bind(this);
        mManager = BiometricPromptManager.from(this);
        initView();
        readPermissions();
        initData();
        dialogOut();
    }

    private void createNoMedia() {
        //新建一个File，传入文件夹目录
        File file = new File("/mnt/sdcard/SealDownImage");
        File file2 = new File("/mnt/sdcard/SealDownImage/.nomedia");
        //判断文件夹是否存在，如果不存在就创建，否则不创建
//        if (!file.exists()) {
        //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
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
        check_down_up = findViewById(R.id.check_down_up);
        password_rl = findViewById(R.id.password_rl);
        login_bt.setOnClickListener(this);
        more_tv.setOnClickListener(this);
        phone_et.setOnClickListener(this);
        accountDao = new AccountDao(this);
        loadingView = new LoadingView(this);
        tv_finger_print_login.setOnClickListener(this);
        register.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
        String state = EasySP.init(this).getString("finger_print");
        if (state.equals("1")) {
        } else {
            // 没有设置指纹登录
//            showToast("没有打开指纹登录");
            tv_finger_print_login.setVisibility(View.GONE);
        }
    }

    private void initData() {
        //电话号码编辑事件
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

        //是否显示历史登录列表
        check_down_up.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    initPopuwindow(); //显示历史列表
                    hintKbTwo();
                    if (historyList.size() == 0) {
                        password_rl.setVisibility(View.VISIBLE);
                        register_forgetPwd.setVisibility(View.VISIBLE);
                    } else {
                        password_rl.setVisibility(View.GONE);
                        register_forgetPwd.setVisibility(View.GONE);
                    }
                } else {
                    selectPopuwindow.dismiss(); //隐藏列表
                    password_rl.setVisibility(View.VISIBLE);
                    register_forgetPwd.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 关闭软键盘
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
     * 登录列表数据
     * 初始化填充adapter所用list数据
     */
    private void initAddNum() {
        datas.clear();
        historyList = accountDao.quaryAll();
        //比较器
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
        //排序，默认升序
        Collections.sort(historyList, comparator);
        if (historyList.size() > 5) {
            historyList = historyList.subList(0, 5);  //0,1,2,3,4
        }
        datas.addAll(historyList);

    }

    /**
     * 初始化Popuwindow
     */
    private void initPopuwindow() {
        initAddNum();
        //Popuwindow浮动下拉框布局
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.account_list, null);
        selectPopuwindow = new PopupWindow(view);
        selectPopuwindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        selectPopuwindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        listView = view.findViewById(R.id.account_list);       //显示账户的listview
        optionsAdapter = new OptionsAdapter(this, datas, accountDao);
        listView.setAdapter(optionsAdapter);
        selectPopuwindow.showAsDropDown(check_down_up, 0, 0);  //显示方式,0表示xy轴偏移量
        selectPopuwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //设置透明颜色背景
        //点击popuwindow下的账号使其显示到输入账号文本框
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
                psd_et.getText().clear(); //清空密码
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
                    showToast("请用密码先登录一次");
                    return;
                }

                String state = EasySP.init(this).getString("finger_print");
                if (state.equals("1")) {
                } else {
                    // 没有设置指纹登录
                    showToast("没有打开指纹登录");
                }
                identify();
                break;
            case R.id.register_tv:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.forget_pwd_tv:
                intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 登录
     */
    private void getLogin() {
        phone = phone_et.getText().toString().trim();
        password = psd_et.getText().toString().trim();
        if (checkLogin(phone, password)) {
            return;
        } else {
            //发送get请求
            loadingView.show();
            loadingView.showView("登录中");
            loginGet(phone, password,"0");
        }
    }

    /**
     * 检查登录条件
     *
     * @return
     */
    private boolean checkLogin(String phone, String password) {
        boolean isOk = false;
        if (phone.length() < 11) {
            isOk = true;
            Toast.makeText(LoginActivity.this, "请输入11位数手机号码", Toast.LENGTH_SHORT).show();
            return isOk;
        }
        if (password.length() < 6 || password.length() > 18) {
            isOk = true;
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return isOk;
        }
        return isOk;
    }


    /**
     * 加载用户头像
     *
     * @param phoneNumber
     */
    private void loadUserHeadPortrait(String phoneNumber) {
        //加载用户头像
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
        //否则加载默认头像
        headImageView.setImageResource(R.drawable.default_head_portrait);
    }


    /**
     * 发送登录请求
     */
    private void loginGet(final String phone, final String password,String isFP) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mobilePhone", phone);
        hashMap.put("password", password);
        HttpUtil.sendDataAsync(this, HttpUrl.LOGIN, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                showToast("error:" + e);
                Looper.loop();
                Log.e("TAG", "登录失败。。。。。。。");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("*** login ***:" + result);
                Gson gson = new Gson();
                ResponseInfo<LoginData> loginResponseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<LoginData>>() {
                }.getType());

                if (loginResponseInfo.getData() != null && loginResponseInfo.getCode() == 0) {
                    Log.e("TAG", "登录请求成功。。。。。。。");

                    // save data
                    EasySP.init(LoginActivity.this).putString("l_tel", phone);
                    EasySP.init(LoginActivity.this).putString("l_pwd", password);

                    loadingView.cancel();
                    //添加一个登陆标记
                    loginResponseInfo.getData().login(LoginActivity.this);
                    //登录存储信息
                    user = loginResponseInfo.getData();
                    CommonUtil.setUserData(LoginActivity.this, user);

                    // 本地存入权限
                    String targetPermissionJson = "";
                    if (user.getAdmin()) {
                        targetPermissionJson = new Gson().toJson(user.getSystemFuncList());
                    } else {
                        targetPermissionJson = new Gson().toJson(user.getFuncIdList());
                    }
//                    List<SystemFuncListInfo> systemFuncListInfo = gson.fromJson(targetPermissionJson, new TypeToken<List<SystemFuncListInfo>>() {}.getType());
//                    Utils.log(targetPermissionJson);

                    EasySP.init(LoginActivity.this).putString("permission", targetPermissionJson);
                    EasySP.init(LoginActivity.this).putBoolean("isAdmin", loginResponseInfo.getData().getAdmin());
                    //存储用户的姓名，电话，头像
                    HistoryInfo historyInfo = new HistoryInfo(phone, user.getRealName(), user.getHeadPortrait(), new Date().getTime());

                    accountDao.insert(historyInfo);
                    //下载用户头像
                    if (user.getHeadPortrait() != null && !user.getHeadPortrait().isEmpty()) {
                        //先从本地读取，没有则下载
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
                    //判断是否需要同步用户数据
                    if (user.getNeedSync()) {
                        dataSync();
                    } else {
                        //跳转首页
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
     * 弹出更多提示框
     */
    private void moreDialog() {
        stringList = new ArrayList<String>();
        stringList.add("注册");
        stringList.add("忘记密码");
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
     * 数据同步
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
        loadingView.showView("数据同步中,请稍后...");
        login_bt.setClickable(false);
        HttpUtil.sendDataAsync(this, HttpUrl.SYNC, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "数据同步错误!!!!!!!!!!!!!!!!!!!!!");
                loadingView.cancel();
                //跳转首页
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
                        Log.e("TAG", "数据同步成功!!!!!!!!!!!!!!!!!!!!!");
                        loadingView.cancel();
                        //跳转首页
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    loadingView.cancel();
                    Log.e("TAG", "数据同步失败!!!!!!!!!!!!!!!!!!!!!");
                    //跳转首页
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * 弹出下线通知提示框
     */
    private void dialogOut() {
        Intent intent = this.getIntent();
        String timeoutStr = intent.getStringExtra("loginstatus");
        String infoStr = intent.getStringExtra("info");
        if ("timeout".equals(timeoutStr)) {
            CommonDialog commonDialog = new CommonDialog(this, "下线通知", infoStr, "确定");
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
     * 申请权限
     */
    @SuppressLint("CheckResult")
    private void readPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //添加需要的权限
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
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });
    }

    /**
     * 隐藏软键盘
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

                    //发送get请求
                    String fpTel = EasySP.init(LoginActivity.this).getString("l_tel");
                    String fpPwd = EasySP.init(LoginActivity.this).getString("l_pwd");
                    if (TextUtils.isEmpty(fpPwd)) {
                        return;
                    }
                    loadingView.show();
                    loadingView.showView("登录中");
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
