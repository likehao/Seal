package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.StartViewPagerAdapter;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.PrefUtils;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{

    private ViewPager viewpager;
    private Button welcome_bt;
    private List<View> views;
    private StartViewPagerAdapter startViewPagerAdapter;
    //引导图片资源
    private static final int[] imgs = {R.drawable.ic_launcher_background,
            R.drawable.alipay,R.drawable.startpage,};

    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //只启动一次引导页
        //getSharedPreferences参数（文件名,默认的模式为0或MODE_PRIVATE）为MODE_PRIVATE，则该配置文件只能被自己的应用程序访问
        boolean isfirst = PrefUtils.getBoolean(WelcomeActivity.this,"ISFIRST",true);
      //  sp = getSharedPreferences("Seal", Context.MODE_PRIVATE);
      //  boolean isfirst = sp.getBoolean("ISFIRST",false);
        if(isfirst){
            startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
            overridePendingTransition(R.anim.welcome_anim2,R.anim.welcome_anim1);// 淡出淡入动画效果
            finish();
        }else {
            initView();
        }
    }

    private void initView(){
        views = new ArrayList<View>();

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // 初始化引导图片列表
        for (int i = 0; i < imgs.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(imgs[i]);
            views.add(iv);
        }
        viewpager = findViewById(R.id.viewpager);
        //初始化adapter
        startViewPagerAdapter = new StartViewPagerAdapter(views);
        viewpager.setAdapter(startViewPagerAdapter);
        viewpager.addOnPageChangeListener(this);
       /* viewpager.setAdapter(new StartAdapter());
        viewpager.addOnPageChangeListener(this);*/
        //欢迎按钮
        welcome_bt = findViewById(R.id.welcome_bt);
        welcome_bt.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.welcome_bt) {
            PrefUtils.setBoolean(WelcomeActivity.this,"ISFIRST",true);
            /*SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("ISFIRST",true);
            editor.commit();*/
            startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
            overridePendingTransition(R.anim.welcome_anim1,R.anim.welcome_anim2);// 淡出淡入动画效果
            finish();
        } else {
            int position = (Integer) view.getTag();
            setCurView(position);
        }
    }
    /**
     * 设置当前的引导页
     */
    private void setCurView(int position) {
        if (position < 0 || position >= imgs.length) {
            return;
        }
        viewpager.setCurrentItem(position);

    }
    //当前页面被滑动调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // arg0 :当前页面，及你点击滑动的页面
        // arg1:当前页面偏移的百分比
        // arg2:当前页面偏移的像素位置
    }
    //当新的页面被选中时调用,某个页面完全进入视野执行
    @Override
    public void onPageSelected(int position) {
        if (position == imgs.length-1){
            welcome_bt.setVisibility(View.VISIBLE);
        }else {
            welcome_bt.setVisibility(View.GONE);
        }
    }
    //当滑动状态改变调用
    @Override
    public void onPageScrollStateChanged(int position) {
        // arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。
    }
}
