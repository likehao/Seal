package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageData;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.fragment.ApplicationFragment;
import cn.fengwoo.sealsteward.fragment.MainFragment;
import cn.fengwoo.sealsteward.fragment.MessageFragment;
import cn.fengwoo.sealsteward.fragment.MineFragment;
import cn.fengwoo.sealsteward.fragment.RecordFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.PermissionUtils;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.AddPopuwindow;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.MessagePopuwindow;
import cn.fengwoo.sealsteward.view.MyApp;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout home_page, record_page, application_page, message_page, mine;
    private TextView title_tv;  //头标题
    private LinearLayout scan_ll;  //扫一扫

    @BindView(R.id.add_ll)
    LinearLayout add_ll;  //添加
    @BindView(R.id.msg_rl)
    RelativeLayout msg_ll;
    @BindView(R.id.msg_iv)
    ImageView msg_iv;
    @BindView(R.id.main_msg_tv)
    TextView main_msg_tv;    //工作台消息数
    @BindView(R.id.ll_record_title)
    LinearLayout ll_record;
    @BindView(R.id.tv_left)
    TextView tv_left;
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.welcome_tv)
    TextView welcome;
    private ImageView[] imageViews = new ImageView[5];  //底部导航图集合
    private TextView[] textViews = new TextView[7];   //底部导航文字集合
    private LinearLayout[] linearLayouts = new LinearLayout[7];
    private ImageView record_more_iv, message_more_iv;  //右上角点点点更多
    AddPopuwindow popuwindow;
    MessagePopuwindow messagePopuwindow;
    //主页面集合
    List<Fragment> fragmentList;
    MainFragment mainFragment;
    MineFragment mineFragment;
    RecordFragment recordFragment;
    MessageFragment messageFragment;
    ApplicationFragment applicationFragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private List<String> dialogList;
    /*    @BindView(R.id.circle_useSeal_apply_ll)
        LinearLayout circle_useSeal_apply_ll;*/
    /**
     * 上次点击返回键的时间
     */
    private long lastBackPressed;
    /**
     * 两次点击的间隔时间
     */
    private static final int QUIT_INTERVAL = 2000;
    @BindView(R.id.msgSum_tv)
    TextView msgSum_tv;  //消息总数
    private Timer timer;
    int sum, addSum;

    private boolean firstTag = false; // 弹出过一次，变成true

    private LeftOrRightListener leftOrRightListener;

    int msgFourSix = 0;
    @BindView(R.id.title_ll)
    LinearLayout title_ll;
    @BindView(R.id.phone_seal_ll)
    LinearLayout phoneSeal;  //手机盖章
    @BindView(R.id.pwd_seal_ll)
    LinearLayout pwdSeal;   //密码盖章

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSwipeBackEnable(false);//禁止滑动退出
        Utils.log("onCreateonCreate");
        ButterKnife.bind(this);
        String state = EasySP.init(this).getString("finger_print");
        String isFP = "";
        if (getIntent() != null) {
            isFP = getIntent().getStringExtra("isFP");
        }
        if (state.equals("1")) {
            Utils.log("isFP:" + isFP);
            // 账号登录过来的
            if (TextUtils.isEmpty(isFP)) {
                // 第一次进入主页
                Intent intent = new Intent();
                intent.setClass(this, FingerPrintActivity.class);
                startActivity(intent);
                finish();
            }
            Utils.log("888isFP:" + isFP);

            // 忽略通过指纹登录来这和密码登录来这两种情形
            if (isFP != null && !isFP.equals("1") && !isFP.equals("0")) {
                Intent intent = new Intent();
                intent.setClass(this, FingerPrintActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
        }

        initView();

        //初始化扫描
        ZXingLibrary.initDisplayOpinion(this);
        changeView(0);  //启动默认显示主页面
        changeTitleView(5);
        setListener();

        timer = new Timer();
        timer.schedule(timerTask, 1000, 3000); //延时1s，每隔3秒执行一次run方法

        Utils.log(Utils.isLocationEnabled(this) + "7788");
        Utils.isLocationEnabled(this);
    }

    /**
     * 开启定时器
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                getMessageNum();
            }
            super.handleMessage(msg);
        }
    };
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    private void initView() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        scan_ll = (LinearLayout) findViewById(R.id.scan_ll);
        scan_ll.setVisibility(View.VISIBLE);
        add_ll.setVisibility(View.GONE);
        msg_ll.setVisibility(View.VISIBLE);
        home_page = (LinearLayout) findViewById(R.id.home_page);
        record_page = (LinearLayout) findViewById(R.id.record_page);
        application_page = (LinearLayout) findViewById(R.id.application_page);
        message_page = (LinearLayout) findViewById(R.id.message_page);
        mine = (LinearLayout) findViewById(R.id.mine);
        textViews[0] = (TextView) findViewById(R.id.seal_tv);
        textViews[1] = (TextView) findViewById(R.id.record_tv);
        textViews[2] = (TextView) findViewById(R.id.matter_tv);
        textViews[3] = (TextView) findViewById(R.id.mine_tv);
        textViews[4] = (TextView) findViewById(R.id.application_tv);

        imageViews[0] = (ImageView) findViewById(R.id.seal_iv);
        imageViews[1] = (ImageView) findViewById(R.id.record_iv);
        imageViews[2] = (ImageView) findViewById(R.id.matter_iv);
        imageViews[3] = (ImageView) findViewById(R.id.mine_iv);
        imageViews[4] = (ImageView) findViewById(R.id.application_iv);

        textViews[5] = tv_left;
        textViews[6] = tv_right;
        linearLayouts[5] = phoneSeal;
        linearLayouts[6] = pwdSeal;

        record_more_iv = (ImageView) findViewById(R.id.record_more_iv);
        message_more_iv = (ImageView) findViewById(R.id.message_more_iv);
        fragmentList = new ArrayList<Fragment>();
        initFragment();
//        title_tv.setText(CommonUtil.getUserData(this).getCompanyName());

//        tv_right.setTextColor(Color.argb(130, 255, 255, 255));
        welcome.setVisibility(View.VISIBLE);
        title_tv.setVisibility(View.GONE);
    }

    private void setListener() {
        home_page.setOnClickListener(this);
        mine.setOnClickListener(this);
        record_page.setOnClickListener(this);
        application_page.setOnClickListener(this);
        message_page.setOnClickListener(this);
        record_more_iv.setOnClickListener(this);
        message_more_iv.setOnClickListener(this);
        scan_ll.setOnClickListener(this);
        //       circle_useSeal_apply_ll.setOnClickListener(this);
        add_ll.setOnClickListener(this);
        msg_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_page:
                ll_record.setVisibility(View.GONE);
                title_tv.setVisibility(View.GONE);
//                title_tv.setText(CommonUtil.getUserData(this).getCompanyName());
                record_more_iv.setVisibility(View.GONE);
                //    add_ll.setVisibility(View.VISIBLE);
                msg_ll.setVisibility(View.VISIBLE);
                scan_ll.setVisibility(View.VISIBLE);
                title_ll.setVisibility(View.VISIBLE);
                welcome.setVisibility(View.VISIBLE);
                changeView(0);
                break;
            case R.id.record_page:
//                title_tv.setText("记录");

                tv_left.setOnClickListener(this);
                tv_right.setOnClickListener(this);
                ll_record.setVisibility(View.VISIBLE);
                title_tv.setVisibility(View.GONE);
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.VISIBLE);
                msg_ll.setVisibility(View.GONE);
                title_ll.setVisibility(View.VISIBLE);
                welcome.setVisibility(View.GONE);
                changeView(1);
                break;
            case R.id.message_page:
                title_tv.setText("消息");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                msg_ll.setVisibility(View.GONE);
                title_ll.setVisibility(View.VISIBLE);
                welcome.setVisibility(View.GONE);
                changeView(2);
                break;
            case R.id.mine:
                ll_record.setVisibility(View.GONE);
                title_tv.setVisibility(View.VISIBLE);
                title_tv.setText("我的");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                msg_ll.setVisibility(View.GONE);
                title_ll.setVisibility(View.GONE);
                welcome.setVisibility(View.GONE);
                changeView(3);
                break;
            case R.id.application_page:
                ll_record.setVisibility(View.GONE);
                title_tv.setVisibility(View.VISIBLE);
                title_tv.setText("应用");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                msg_ll.setVisibility(View.GONE);
                title_ll.setVisibility(View.VISIBLE);
                welcome.setVisibility(View.GONE);
                changeView(4);
                break;
            case R.id.record_more_iv:
                //盖章记录更多popuwindow
                popuwindow = new AddPopuwindow(MainActivity.this);  //不能放到initview里初始化，会导致一开始进入屏幕灰暗透明度不为1
                popuwindow.showPopuwindow(view);
                //       popDialog();
                break;
 /*           case R.id.message_more_iv:
                messagePopuwindow.showPopuwindow(view);
                break;*/
   /*         case R.id.circle_useSeal_apply_ll:
                Intent intent = new Intent(MainActivity.this,UseSealApplyActivity.class);
                startActivity(intent);
                break;*/
            case R.id.scan_ll:
                //检查权限
                checkPm();
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.add_ll:
                //添加popuwindow
                messagePopuwindow = new MessagePopuwindow(MainActivity.this, 1);
                messagePopuwindow.showPopuwindow(view);
                break;
            case R.id.msg_rl:
                intent = new Intent(this, MsgActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_left:
                Utils.log("left");
                //设置文字透明度
                changeTitleView(5);
//                tv_right.setTextColor(Color.argb(130, 255, 255, 255));
//                tv_left.setTextColor(Color.argb(255, 255, 255, 255));
                leftOrRightListener.whichSide("left");
                break;

            case R.id.tv_right:
                Utils.log("right");
                changeTitleView(6);

//                tv_left.setTextColor(Color.argb(130, 255, 255, 255));
//                tv_right.setTextColor(Color.argb(255, 255, 255, 255));
                leftOrRightListener.whichSide("right");
                break;
        }
    }

    private void changeView(int index) {
        imageMethod(index);  //底部选择图片文字状态
        //开启事物
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        switch (index) {
            case 0:
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    transaction.add(R.id.home_fragment, mainFragment);
                } else {
                    transaction.show(mainFragment); //显示需要显示的fragment
                }
                break;
            case 1:
                if (recordFragment == null) {
                    recordFragment = new RecordFragment();
                    transaction.add(R.id.home_fragment, recordFragment);
                } else {
                    transaction.show(recordFragment);
                }
                break;
            case 2:
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    transaction.add(R.id.home_fragment, messageFragment);
                } else {
                    transaction.show(messageFragment);
                }
                break;
            case 3:
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.home_fragment, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                break;
            case 4:
                if (applicationFragment == null) {
                    applicationFragment = new ApplicationFragment();
                    transaction.add(R.id.home_fragment, applicationFragment);
                } else {
                    transaction.show(applicationFragment);
                }

        }
        transaction.commit();

    }

    /**
     * 设备被选中的底部导航图标文字状态
     *
     * @param index
     */
    private void imageMethod(int index) {
        textViews[0].setTextColor(index == 0 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        imageViews[0].setImageResource(index == 0 ? R.drawable.seal_select : R.drawable.seal);
        textViews[1].setTextColor(index == 1 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        imageViews[1].setImageResource(index == 1 ? R.drawable.record_select : R.drawable.record);
        textViews[2].setTextColor(index == 2 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        imageViews[2].setImageResource(index == 2 ? R.drawable.matter_select : R.drawable.matter);
        textViews[3].setTextColor(index == 3 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        imageViews[3].setImageResource(index == 3 ? R.drawable.mian_select : R.drawable.mine);
        imageViews[4].setImageResource(index == 4 ? R.drawable.application_select : R.drawable.application);
        textViews[4].setTextColor(index == 4 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));

    }

    /**
     * 记录页面头部手机盖章和密码盖章按钮的选中状态
     * @param code
     */
    public void changeTitleView(int code){
        textViews[5].setTextColor(code == 5 ? ContextCompat.getColor(this,R.color.style) : ContextCompat.getColor(this,R.color.white));
        linearLayouts[5].setBackground(code == 5 ? ContextCompat.getDrawable(this,R.drawable.select_phone_seal1) : ContextCompat.getDrawable(this,R.drawable.select_phone_seal));
        textViews[6].setTextColor(code == 6 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.white));
        linearLayouts[6].setBackground(code == 6 ? getResources().getDrawable(R.drawable.select_pwd_seal1) : getResources().getDrawable(R.drawable.select_pwd_seal));

    }

    /***
     * 隐藏所有的fragment
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
        if (recordFragment != null) {
            transaction.hide(recordFragment);
        }
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
        if (applicationFragment != null) {
            transaction.hide(applicationFragment);
        }
    }

    private void initFragment() {
        mainFragment = (MainFragment) fragmentManager.findFragmentByTag("main");
        recordFragment = (RecordFragment) fragmentManager.findFragmentByTag("record");
        messageFragment = (MessageFragment) fragmentManager.findFragmentByTag("message");
        mineFragment = (MineFragment) fragmentManager.findFragmentByTag("mine");
        applicationFragment = (ApplicationFragment) fragmentManager.findFragmentByTag("application");
    }

    //检查权限
    private void checkPm() {
        String[] permissions = PermissionUtils.checkPermissions(this);
        if (permissions.length == 0) {
            //权限都申请了
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
/*
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},0);
        }*/
    }

    /***
     * 对获取权限处理的结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //允许
                //检验是否获取权限，如果获取权限，外部存储会处于开放状态
                String sdCard = Environment.getExternalStorageState();
                if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                    //          showToast("允许授权");
                    Log.e("TAG", "允许授权");
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //   showToast("拒绝");
                        Log.e("TAG", "拒绝授权");
                    }
                });
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 获取消息
     */

    @SuppressLint("SetTextI18n")
    private void getMessageNum() {
        HttpUtil.sendDataAsync(MainActivity.this, HttpUrl.MESSAGE, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
//                Utils.log("000 000:getMessageNum result:" + result);
                Gson gson = new Gson();
                ResponseInfo<List<MessageData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<MessageData>>>() {
                }
                        .getType());
                assert responseInfo != null;
                if (responseInfo.getCode() != null) {
                    if (responseInfo.getCode() == 1002 || responseInfo.getCode() == 1003 || responseInfo.getCode() == 1004) {
                        //关闭定时器
                        int i = responseInfo.getCode();
                        stopTimer();
                        //清除用户信息
                        LoginData.logout(MainActivity.this);
                        //断开蓝牙
                        ((MyApp) getApplication()).removeAllDisposable();
                        ((MyApp) getApplication()).setConnectionObservable(null);
                        //下线直接跳转登录界面
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra("loginstatus", "timeout");
                        intent.putExtra("info", responseInfo.getMessage());
                        startActivity(intent);
                        System.exit(0);
                        //   onBackPressed();// 销毁所有activity退出主程序
                        Log.e("ATG", "下线成功!!!!!!!!!!!!!!!");
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                            for (MessageData messageData : responseInfo.getData()) {

                                int msgNum = messageData.getUnreadCount();


                                //显示消息数
                                int type = messageData.getType();
                                if (type != 5 && type != 4 && type != 6) {
                                    sum += msgNum;
                                }
                                if (type == 4 || type == 6) {
                                    msgFourSix += msgNum;
                                }
//                                if (type == 4) { //显示工作台待我审批总消息数
//                                    if (msgNum != 0) {
//                                        main_msg_tv.setVisibility(View.VISIBLE);
//                                        main_msg_tv.setText(msgFourSix + "");
//                                        EventBus.getDefault().post(new MessageEvent("待我审批消息", "待我审批消息"));
//                                        msgFourSix = 0;
//                                    } else {
//                                        main_msg_tv.setVisibility(View.GONE);
//                                    }
//                                } else if (type == 6) { // ppl add
//                                    if (msgNum != 0) {
//                                        Utils.log("000 000:" + msgNum + "");
//                                        main_msg_tv.setVisibility(View.VISIBLE);
//                                        main_msg_tv.setText(msgFourSix + "");
//                                        EventBus.getDefault().post(new MessageEvent("待我审批消息", "待我审批消息"));
//                                        msgFourSix = 0;
//                                    } else {
//                                        main_msg_tv.setVisibility(View.GONE);
//                                    }
//                                } else
                                if (type == 5) {
                                    if (!firstTag) {
//                                        msgNum = 320;
//                                        float appVersionOnServer = (float)msgNum / 100;
//                                        Utils.log("appVersionOnServer:" + appVersionOnServer);
                                        Utils.log("getLocalVersion:" + Utils.getLocalVersion(MainActivity.this));

                                        if (msgNum > Utils.getLocalVersion(MainActivity.this)) {
                                            appUpdateDialog();
                                        }
                                        firstTag = true;
                                    }
                                }
                            }

//                            if (type == 4) { //显示工作台待我审批总消息数
                            if (msgFourSix != 0) {
                                main_msg_tv.setVisibility(View.VISIBLE);
                                main_msg_tv.setText(msgFourSix + "");
                                EventBus.getDefault().post(new MessageEvent("待我审批消息", "待我审批消息"));
                                msgFourSix = 0;
                            } else {
                                main_msg_tv.setVisibility(View.GONE);
                            }
//                            } else if (type == 6) { // ppl add
//                                if (msgFourSix != 0) {
//                                    Utils.log("000 000:" + msgFourSix + "");
//                                    main_msg_tv.setVisibility(View.VISIBLE);
//                                    main_msg_tv.setText(msgFourSix + "");
//                                    EventBus.getDefault().post(new MessageEvent("待我审批消息", "待我审批消息"));
//                                    msgFourSix = 0;
//                                } else {
//                                    main_msg_tv.setVisibility(View.GONE);
//                                }
//                            } else

                            //显示右上角消息
                            if (sum == 0) {
                                msg_iv.setVisibility(View.GONE);
                            } else {
                                msg_iv.setVisibility(View.VISIBLE);
                                sum = 0;
                            }
                  /*  runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //显示消息总数
                            if (sum != 0) {
                                msgSum_tv.setVisibility(View.VISIBLE);
                                msgSum_tv.setText(sum + "");
                                sum = 0;
                            } else {
                                msgSum_tv.setVisibility(View.GONE);
                                msgSum_tv.setText(sum + "");
                            }
                        }
                    });*/
                            Log.e("TAG", "获取消息成功!!!!!!!!!!!!!!!!");

                        } else {
                            Log.e("TAG", responseInfo.getMessage());
                        }

                    }
                });

            }
        });

    }

    //按下物理返回键,popuwindow消失
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        ApplicationFragment applicationFragment = new ApplicationFragment();
        applicationFragment.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 连续两次点击物理返回键退出
     */
    @Override
    public void onBackPressed() {
        long backPressed = System.currentTimeMillis();
        if (backPressed - lastBackPressed > QUIT_INTERVAL) {
            lastBackPressed = backPressed;
            showToast("再按一次退出");
        } else {
//            ((MyApp) this.getApplication()).removeAllDisposable();
//            ((MyApp) getApplication()).setConnectionObservable(null);
            finish();
            System.exit(0);  //正常结束程序
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // getMessageNum();

    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }


    private void appUpdateDialog() {
        final CommonDialog commonDialog = new CommonDialog(this, "提示", "发现有新的版本", "更新");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://a.app.qq.com/o/simple.jsp?pkgname=cn.fengwoo.sealsteward&fromcase=40002");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                commonDialog.dialog.dismiss();
            }
        });
    }


    private void createNoMedia() {
        String filePath = "file://" + HttpDownloader.path;
        File nomedia = new File(filePath + ".nomedia");
        if (!nomedia.exists())
            try {
//                nomedia.mkdirs();
                nomedia.createNewFile();
            } catch (Exception e) {
                Utils.log(e.toString());
                e.printStackTrace();
            }
    }


    public interface LeftOrRightListener {
        void whichSide(String side);
    }

    public void setLeftOrRightListener(LeftOrRightListener leftOrRightListener) {
        this.leftOrRightListener = leftOrRightListener;
    }

    /**
     * 防止点击过快出现两个页面
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 处理注册事件
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        if (s.equals("title_ui")){   //记录筛选改变头UI
            changeTitleView(5);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);   //注册Eventbus
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);  //解除注册
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
