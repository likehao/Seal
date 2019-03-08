package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.fragment.ApplicationFragment;
import cn.fengwoo.sealsteward.fragment.MainFragment;
import cn.fengwoo.sealsteward.fragment.MessageFragment;
import cn.fengwoo.sealsteward.fragment.MineFragment;
import cn.fengwoo.sealsteward.fragment.RecordFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.PermissionUtils;
import cn.fengwoo.sealsteward.view.AddPopuwindow;
import cn.fengwoo.sealsteward.view.MessagePopuwindow;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout home_page, record_page, application_page, message_page, mine;
    private TextView title_tv;  //头标题
    private LinearLayout scan_ll;  //扫一扫
    @BindView(R.id.add_ll)
    LinearLayout add_ll;  //添加
    private ImageView[] imageViews = new ImageView[5];  //底部导航图集合
    private TextView[] textViews = new TextView[5];   //底部导航文字集合
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
    private int REQUEST_CODE = 1;
    private int REQUEST_IMAGE = 2;
    /**
     * 上次点击返回键的时间
     */
    private long lastBackPressed;
    /**
     * 两次点击的间隔时间
     */
    private static final int QUIT_INTERVAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initView();
        //初始化扫描
        ZXingLibrary.initDisplayOpinion(this);
        changeView(0);  //启动默认显示主页面
        setListener();
    }

    private void initView() {
        title_tv = findViewById(R.id.title_tv);
        scan_ll = findViewById(R.id.scan_ll);
//        scan_ll.setVisibility(View.VISIBLE);
        add_ll.setVisibility(View.VISIBLE);
        home_page = findViewById(R.id.home_page);
        record_page = findViewById(R.id.record_page);
        application_page = findViewById(R.id.application_page);
        message_page = findViewById(R.id.message_page);
        mine = findViewById(R.id.mine);
        textViews[0] = findViewById(R.id.seal_tv);
        textViews[1] = findViewById(R.id.record_tv);
        textViews[2] = findViewById(R.id.matter_tv);
        textViews[3] = findViewById(R.id.mine_tv);
        textViews[4] = findViewById(R.id.application_tv);
        imageViews[0] = findViewById(R.id.seal_iv);
        imageViews[1] = findViewById(R.id.record_iv);
        imageViews[2] = findViewById(R.id.matter_iv);
        imageViews[3] = findViewById(R.id.mine_iv);
        imageViews[4] = findViewById(R.id.application_iv);
        record_more_iv = findViewById(R.id.record_more_iv);
        message_more_iv = findViewById(R.id.message_more_iv);
        fragmentList = new ArrayList<Fragment>();
        initFragment();
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_page:
                title_tv.setText("首页");
                //           scan_ll.setVisibility(View.VISIBLE);
                record_more_iv.setVisibility(View.GONE);
                add_ll.setVisibility(View.VISIBLE);
                changeView(0);
                break;
            case R.id.record_page:
                title_tv.setText("盖章记录");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.VISIBLE);
                changeView(1);
                break;
            case R.id.message_page:
                title_tv.setText("消息");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                changeView(2);
                break;
            case R.id.mine:
                title_tv.setText("我的");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                changeView(3);
                break;
            case R.id.application_page:
                title_tv.setText("应用");
                scan_ll.setVisibility(View.GONE);
                add_ll.setVisibility(View.GONE);
                record_more_iv.setVisibility(View.GONE);
                changeView(4);
                break;
            case R.id.record_more_iv:
                //盖章记录更多popuwindow
                popuwindow = new AddPopuwindow(MainActivity.this);
                popuwindow.showPopuwindow(view);
                //       popDialog();
                break;
 /*           case R.id.message_more_iv:
                messagePopuwindow = new MessagePopuwindow(MainActivity.this);
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
                messagePopuwindow = new MessagePopuwindow(MainActivity.this,1);
                messagePopuwindow.showPopuwindow(view);
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
                    showToast("允许授权");
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("拒绝");
                    }
                });
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /*
    @Override
    protected void onPause() {
        super.onPause();
        CameraManager.get().closeDriver();
    }*/
/*
    private void popDialog() {
        dialogList = new ArrayList<String>();
        dialogList.add("添加人员");
        dialogList.add("添加印章");
        final AddPopuwindow addPopuwindow = new AddPopuwindow(MainActivity.this,dialogList);
        addPopuwindow.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(MainActivity.this, "11111", Toast.LENGTH_LONG).show();
                }
            }
        });
    }*/

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
            finish();
            System.exit(0);  //正常结束程序
        }
    }
}
