package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import com.imuxuan.floatingview.FloatingView;
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
import cn.fengwoo.sealsteward.entity.CompanyInfo;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.fragment.ApplicationFragment;
import cn.fengwoo.sealsteward.fragment.MainFragment;
import cn.fengwoo.sealsteward.fragment.MessageFragment;
import cn.fengwoo.sealsteward.fragment.MineFragment;
import cn.fengwoo.sealsteward.fragment.RecordFragment;
import cn.fengwoo.sealsteward.fragment.StatisticsFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
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
    private TextView title_tv;  //?????????
    private LinearLayout scan_ll;  //?????????

    @BindView(R.id.add_ll)
    LinearLayout add_ll;  //??????
    @BindView(R.id.msg_rl)
    RelativeLayout msg_ll;
    @BindView(R.id.msg_iv)
    ImageView msg_iv;
    @BindView(R.id.main_msg_tv)
    TextView main_msg_tv;    //??????????????????
    @BindView(R.id.ll_record_title)
    LinearLayout ll_record;
    @BindView(R.id.tv_left)
    TextView tv_left;
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.welcome_tv)
    TextView welcome;
    private ImageView[] imageViews = new ImageView[8];  //?????????????????????
    private TextView[] textViews = new TextView[8];   //????????????????????????
    private LinearLayout[] linearLayouts = new LinearLayout[7];
    private ImageView record_more_iv;  //????????????????????????
    private TextView message_more_iv;
    AddPopuwindow popuwindow;
    MessagePopuwindow messagePopuwindow;
    //???????????????
    List<Fragment> fragmentList;
    MainFragment mainFragment;
    MineFragment mineFragment;
    RecordFragment recordFragment;
    MessageFragment messageFragment;
    ApplicationFragment applicationFragment;
    StatisticsFragment statisticsFragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private List<String> dialogList;
    /*    @BindView(R.id.circle_useSeal_apply_ll)
        LinearLayout circle_useSeal_apply_ll;*/
    /**
     * ??????????????????????????????
     */
    private long lastBackPressed;
    /**
     * ???????????????????????????
     */
    private static final int QUIT_INTERVAL = 2000;
    @BindView(R.id.msgSum_tv)
    TextView msgSum_tv;  //????????????
    private Timer timer;
    int sum, addSum;
    private boolean firstTag = false; // ????????????????????????true
    private LeftOrRightListener leftOrRightListener;
    int msgFourSix = 0;
    @BindView(R.id.title_ll)
    LinearLayout title_ll;
    @BindView(R.id.phone_seal_ll)
    LinearLayout phoneSeal;  //????????????
    @BindView(R.id.pwd_seal_ll)
    LinearLayout pwdSeal;   //????????????
    @BindView(R.id.statistics)
    LinearLayout statistics;
    @BindView(R.id.statistics_iv)
    ImageView statistics_iv;
    @BindView(R.id.statistics_tv)
    TextView statistics_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;

    @BindView(R.id.none_company_ll)
    LinearLayout none_company;
    @BindView(R.id.scan_addCompany_tv)
    TextView scanAddCompany;
    @BindView(R.id.add_Company_tv)
    TextView addCompany;
    @BindView(R.id.cancel_iv)
    ImageView cancel_iv;
    @BindView(R.id.none_bg_ll)
    LinearLayout none_bg_ll;
    private ArrayList<CompanyInfo> arrayList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSwipeBackEnable(false);//??????????????????
        Utils.log("onCreateonCreate");
        ButterKnife.bind(this);
        String state = EasySP.init(this).getString("finger_print");
        String isFP = "";
        if (getIntent() != null) {
            isFP = getIntent().getStringExtra("isFP");
        }
        if (state.equals("1")) {
            Utils.log("isFP:" + isFP);
            // ?????????????????????
            if (TextUtils.isEmpty(isFP)) {
                // ?????????????????????
                Intent intent = new Intent();
                intent.setClass(this, FingerPrintActivity.class);
                startActivity(intent);
                finish();
            }
            Utils.log("888isFP:" + isFP);

            // ???????????????????????????????????????????????????????????????
            if (isFP != null && !isFP.equals("1") && !isFP.equals("0")) {
                Intent intent = new Intent();
                intent.setClass(this, FingerPrintActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
        }

        initView();

        //???????????????
        ZXingLibrary.initDisplayOpinion(this);
        changeView(0);  //???????????????????????????
        changeTitleView(5);
        setListener();

        timer = new Timer();
        timer.schedule(timerTask, 1000, 3000); //??????1s?????????3???????????????run??????

        Utils.log(Utils.isLocationEnabled(this) + "7788");
        Utils.isLocationEnabled(this);
    }

    /**
     * ???????????????
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.e("TAG????????????????????????",Utils.isBackground(MainActivity.this)+"");
                //??????????????????????????????????????????
                if (!Utils.isBackground(MainActivity.this)){
                    getMessageNum();
                }
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
        textViews[7] = statistics_tv;

        imageViews[0] = (ImageView) findViewById(R.id.seal_iv);
        imageViews[1] = (ImageView) findViewById(R.id.record_iv);
        imageViews[2] = (ImageView) findViewById(R.id.matter_iv);
        imageViews[3] = (ImageView) findViewById(R.id.mine_iv);
        imageViews[4] = (ImageView) findViewById(R.id.application_iv);
        imageViews[7] = statistics_iv;

        textViews[5] = tv_left;
        textViews[6] = tv_right;
        linearLayouts[5] = phoneSeal;
        linearLayouts[6] = pwdSeal;

        record_more_iv = (ImageView) findViewById(R.id.record_more_iv);
        message_more_iv = (TextView) findViewById(R.id.message_more_iv);
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
        statistics.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
        scanAddCompany.setOnClickListener(this);
        addCompany.setOnClickListener(this);
        cancel_iv.setOnClickListener(this);

        //????????????????????????????????????
        if (!Utils.isHaveCompanyId(this)) {
            //            bt.performClick();
            none_bg_ll.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            none_bg_ll.getBackground().mutate().setAlpha(80);
            none_company.setVisibility(View.VISIBLE);
            none_bg_ll.setClickable(true);
        }
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
                edit_tv.setVisibility(View.GONE);
                changeView(0);
//                mainFragment.openFloatingView(2);
                break;
            case R.id.record_page:
//                title_tv.setText("??????");

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
                edit_tv.setVisibility(View.GONE);
                changeView(1);
//                FloatingView.get().remove();   //???????????????
                break;
            case R.id.message_page:
                title_tv.setText("??????");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                msg_ll.setVisibility(View.GONE);
                title_ll.setVisibility(View.VISIBLE);
                welcome.setVisibility(View.GONE);
                edit_tv.setVisibility(View.GONE);
                changeView(2);
                break;
            case R.id.mine:
                ll_record.setVisibility(View.GONE);
                title_tv.setVisibility(View.VISIBLE);
                title_tv.setText("??????");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                msg_ll.setVisibility(View.GONE);
                title_ll.setVisibility(View.GONE);
                welcome.setVisibility(View.GONE);
                edit_tv.setVisibility(View.GONE);
                changeView(3);
//                FloatingView.get().remove();
                break;
            case R.id.application_page:
                ll_record.setVisibility(View.GONE);
                title_tv.setVisibility(View.VISIBLE);
                title_tv.setText("??????");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                msg_ll.setVisibility(View.GONE);
                title_ll.setVisibility(View.VISIBLE);
                welcome.setVisibility(View.GONE);
                edit_tv.setVisibility(View.GONE);
                changeView(4);
//                FloatingView.get().remove();
                break;
            case R.id.statistics:
                ll_record.setVisibility(View.GONE);
                title_tv.setVisibility(View.VISIBLE);
                title_tv.setText("????????????");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                msg_ll.setVisibility(View.GONE);
                title_ll.setVisibility(View.VISIBLE);
                welcome.setVisibility(View.GONE);
                //?????????????????????????????????????????????
                if (Utils.isHaveCompanyId(this)) {
//                    if (companyId != null && !companyId.equals("")) {
                    if (!Utils.hasThePermission(this, Constants.permission27)) {
                        edit_tv.setVisibility(View.GONE);
                    } else {
                        edit_tv.setVisibility(View.VISIBLE);
                        edit_tv.setText("??????");
                    }
//                    }
                }
                changeView(7);
//                FloatingView.get().remove();
                break;
            case R.id.record_more_iv:
                //??????????????????popuwindow
                popuwindow = new AddPopuwindow(MainActivity.this);  //????????????initview??????????????????????????????????????????????????????????????????1
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
                //????????????
                checkPm();
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.add_ll:
                //??????popuwindow
                messagePopuwindow = new MessagePopuwindow(MainActivity.this, 1);
                messagePopuwindow.showPopuwindow(view);
                break;
            case R.id.msg_rl:
                intent = new Intent(this, MsgActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_left:
                Utils.log("left");
                //?????????????????????
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
            case R.id.edit_tv:
                intent = new Intent(this, SealDetailedActivity.class);
                startActivity(intent);
                break;
            case R.id.scan_addCompany_tv:      //??????????????????
                intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
                none_company.setVisibility(View.GONE);
                none_bg_ll.getBackground().mutate().setAlpha(0);
                none_bg_ll.setClickable(false);
                break;
            case R.id.add_Company_tv:   //????????????
                intent = new Intent(this, MyCompanyActivity.class);
                startActivity(intent);
                none_company.setVisibility(View.GONE);
                none_bg_ll.getBackground().mutate().setAlpha(0);
                none_bg_ll.setClickable(false);
                break;
            case R.id.cancel_iv:
                none_company.setVisibility(View.GONE);
                none_bg_ll.getBackground().mutate().setAlpha(0);
                none_bg_ll.setClickable(false);
                break;
        }
    }

    private void changeView(int index) {
        imageMethod(index);  //??????????????????????????????
        //????????????
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);// ?????????????????????Fragment?????????????????????Fragment???????????????????????????
        switch (index) {
            case 0:
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    transaction.add(R.id.home_fragment, mainFragment);
                } else {
                    transaction.show(mainFragment); //?????????????????????fragment
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
                break;
            case 7:
                if (statisticsFragment == null) {
                    statisticsFragment = new StatisticsFragment();
                    transaction.add(R.id.home_fragment, statisticsFragment);
                } else {
                    transaction.show(statisticsFragment);
                }
                break;

        }
        transaction.commit();

    }

    /**
     * ????????????????????????????????????????????????
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
        textViews[4].setTextColor(index == 4 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        imageViews[4].setImageResource(index == 4 ? R.drawable.application_select : R.drawable.application);
        textViews[7].setTextColor(index == 7 ? ContextCompat.getColor(this, R.color.style) : ContextCompat.getColor(this, R.color.gray_text));
        imageViews[7].setImageResource(index == 7 ? R.drawable.icon_printed_statistics_click : R.drawable.icon_printed_statistics_default);
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     *
     * @param code
     */
    public void changeTitleView(int code) {
        textViews[5].setTextColor(code == 5 ? ContextCompat.getColor(this, R.color.style) : ContextCompat.getColor(this, R.color.white));
        linearLayouts[5].setBackground(code == 5 ? ContextCompat.getDrawable(this, R.drawable.select_phone_seal1) : ContextCompat.getDrawable(this, R.drawable.select_phone_seal));
        textViews[6].setTextColor(code == 6 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.white));
        linearLayouts[6].setBackground(code == 6 ? getResources().getDrawable(R.drawable.select_pwd_seal1) : getResources().getDrawable(R.drawable.select_pwd_seal));

    }

    /***
     * ???????????????fragment
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
        if (statisticsFragment != null) {
            transaction.hide(statisticsFragment);
        }
    }

    private void initFragment() {
        mainFragment = (MainFragment) fragmentManager.findFragmentByTag("main");
        recordFragment = (RecordFragment) fragmentManager.findFragmentByTag("record");
        messageFragment = (MessageFragment) fragmentManager.findFragmentByTag("message");
        mineFragment = (MineFragment) fragmentManager.findFragmentByTag("mine");
        applicationFragment = (ApplicationFragment) fragmentManager.findFragmentByTag("application");
        statisticsFragment = (StatisticsFragment) fragmentManager.findFragmentByTag("statistics");
    }

    //????????????
    private void checkPm() {
        String[] permissions = PermissionUtils.checkPermissions(this);
        if (permissions.length == 0) {
            //??????????????????
        } else {
            //????????????
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
/*
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},0);
        }*/
    }

    /***
     * ??????????????????????????????
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //??????
                //?????????????????????????????????????????????????????????????????????????????????
                String sdCard = Environment.getExternalStorageState();
                if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                    //          showToast("????????????");
                    Log.e("TAG", "????????????");
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //   showToast("??????");
                        Log.e("TAG", "????????????");
                    }
                });
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * ????????????
     */

    @SuppressLint("SetTextI18n")
    private void getMessageNum() {
        HttpUtil.sendDataAsync(MainActivity.this, HttpUrl.MESSAGE, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????????????????????????????!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
//                Utils.log("000 000:getMessageNum result:" + result);
                Gson gson = new Gson();
                ResponseInfo<List<MessageData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<MessageData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() != null) {
                    if (responseInfo.getCode() == 1002 || responseInfo.getCode() == 1003 || responseInfo.getCode() == 1004) {
                        int i = responseInfo.getCode();
                        Log.e("TAG", "??????????????????" + i);
                        //???????????????
                        stopTimer();
                        //??????????????????
                        LoginData.logout(MainActivity.this);
                        //????????????
                        ((MyApp) getApplication()).removeAllDisposable();
                        ((MyApp) getApplication()).setConnectionObservable(null);
                        //??????????????????????????????
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        String ip = EasySP.init(MainActivity.this).getString("ip");  //?????????ip
                        String port_num = EasySP.init(MainActivity.this).getString("port_num");   //??????????????????
                        String agreement = EasySP.init(MainActivity.this).getString("agreement");  //????????????
                        intent.putExtra("ip", ip);
                        intent.putExtra("port_num", port_num);
                        intent.putExtra("agreement", agreement);
                        intent.putExtra("loginstatus", "timeout");
                        intent.putExtra("info", responseInfo.getMessage());
                        startActivity(intent);
                        System.exit(0);
                        //   onBackPressed();// ????????????activity???????????????
                        Log.e("ATG", "????????????!!!!!!!!!!!!!!!");
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                            for (MessageData messageData : responseInfo.getData()) {

                                int msgNum = messageData.getUnreadCount();

                                //???????????????
                                int type = messageData.getType();
                                if (type != 5 && type != 4 && type != 6) {
                                    sum += msgNum;
                                }
                                if (type == 4 || type == 6) {
                                    msgFourSix += msgNum;
                                }
//                                if (type == 4) { //???????????????????????????????????????
//                                    if (msgNum != 0) {
//                                        main_msg_tv.setVisibility(View.VISIBLE);
//                                        main_msg_tv.setText(msgFourSix + "");
//                                        EventBus.getDefault().post(new MessageEvent("??????????????????", "??????????????????"));
//                                        msgFourSix = 0;
//                                    } else {
//                                        main_msg_tv.setVisibility(View.GONE);
//                                    }
//                                } else if (type == 6) { // ppl add
//                                    if (msgNum != 0) {
//                                        Utils.log("000 000:" + msgNum + "");
//                                        main_msg_tv.setVisibility(View.VISIBLE);
//                                        main_msg_tv.setText(msgFourSix + "");
//                                        EventBus.getDefault().post(new MessageEvent("??????????????????", "??????????????????"));
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
                                //???????????????????????????????????????
                                if (type == 6) {
                                    if (msgNum != 0 && !Utils.isHaveCompanyId(MainActivity.this)) {
                                        //????????????????????????
                                        getCompanyList();
                                    }
                                }
                            }

//                            if (type == 4) { //???????????????????????????????????????
                            if (msgFourSix != 0) {
                                main_msg_tv.setVisibility(View.VISIBLE);
                                main_msg_tv.setText(msgFourSix + "");
                                EventBus.getDefault().post(new MessageEvent("??????????????????", "??????????????????"));
                                msgFourSix = 0;
                            } else {
                                main_msg_tv.setVisibility(View.GONE);
                            }
//                            } else if (type == 6) { // ppl add
//                                if (msgFourSix != 0) {
//                                    Utils.log("000 000:" + msgFourSix + "");
//                                    main_msg_tv.setVisibility(View.VISIBLE);
//                                    main_msg_tv.setText(msgFourSix + "");
//                                    EventBus.getDefault().post(new MessageEvent("??????????????????", "??????????????????"));
//                                    msgFourSix = 0;
//                                } else {
//                                    main_msg_tv.setVisibility(View.GONE);
//                                }
//                            } else

                            //?????????????????????
                            if (sum == 0) {
                                msg_iv.setVisibility(View.GONE);
                            } else {
                                msg_iv.setVisibility(View.VISIBLE);
                                sum = 0;
                            }
                  /*  runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //??????????????????
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
                            Log.e("TAG", "??????????????????!!!!!!!!!!!!!!!!");

                        } else {
                            Log.e("TAG", responseInfo.getMessage());
                        }

                    }
                });

            }
        });

    }

    /**
     * ??????????????????????????????
     */
    private void getCompanyList() {
        loadingView.show();
        HttpUtil.sendDataAsync(MainActivity.this, HttpUrl.USERCOMPANY, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<CompanyInfo>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<CompanyInfo>>>() {
                }.getType());
                arrayList = new ArrayList<>();
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    for (int i = 0; i < responseInfo.getData().size(); i++) {
                        arrayList.add(new CompanyInfo(responseInfo.getData().get(i).getCompanyName(),
                                responseInfo.getData().get(i).getId(), responseInfo.getData().get(i).getBelongUser(),
                                responseInfo.getData().get(i).getTrade()));
                    }
                    //????????????????????????,ID
                    LoginData data = CommonUtil.getUserData(MainActivity.this);
                    if (data != null) {
                        data.setCompanyName(arrayList.get(0).getCompanyName());
                        data.setCompanyId(arrayList.get(0).getId());
                        CommonUtil.setUserData(MainActivity.this, data);
                    }
                }
            }
        });

    }

    //?????????????????????,popuwindow??????
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        ApplicationFragment applicationFragment = new ApplicationFragment();
        applicationFragment.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ???????????????????????????????????????
     */
    @Override
    public void onBackPressed() {
        long backPressed = System.currentTimeMillis();
        if (backPressed - lastBackPressed > QUIT_INTERVAL) {
            lastBackPressed = backPressed;
            showToast("??????????????????");
        } else {
//            ((MyApp) this.getApplication()).removeAllDisposable();
//            ((MyApp) getApplication()).setConnectionObservable(null);
            finish();
            System.exit(0);  //??????????????????
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
        final CommonDialog commonDialog = new CommonDialog(this, "??????", "?????????????????????", "??????");
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
     * ????????????????????????????????????
     *
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
     * ??????????????????
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        if (s.equals("title_ui")) {   //?????????????????????UI
            changeTitleView(5);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);   //??????Eventbus
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);  //????????????
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
