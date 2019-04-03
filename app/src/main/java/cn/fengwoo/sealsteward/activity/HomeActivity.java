package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.utils.Base2Activity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import io.reactivex.functions.Consumer;

public class HomeActivity extends Base2Activity {

    private ImageView home_iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        home_iv = findViewById(R.id.home_iv);
        //获取屏幕宽度
        WindowManager wm = this.getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Picasso.with(this).load(R.drawable.startpage).resize(width,height).into(home_iv);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!LoginData.isLogin(HomeActivity.this)){
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                }else {
                    startActivity(new Intent(HomeActivity.this,MainActivity.class));
                }
                HomeActivity.this.finish();
                overridePendingTransition(R.anim.welcome_anim1,R.anim.welcome_anim2); //淡出淡入动画效果
            }
        },2500);

    }

}
