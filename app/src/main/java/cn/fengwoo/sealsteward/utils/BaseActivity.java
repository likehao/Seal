package cn.fengwoo.sealsteward.utils;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.fengwoo.sealsteward.view.MyApp;


/**
 * 沉浸式状态栏
 */

public class BaseActivity extends AppCompatActivity{
    public ImmersionBar immersionBar;
    protected LoadingView loadingView;
    protected MyApp application;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (MyApp) getApplication();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //保持竖屏
        immersionBar = ImmersionBar.with(this)
        .statusBarDarkFont(false,0.2f)   //状态栏字体是深色，不写默认为亮色,如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
        .fitsSystemWindows(true, R.color.style);  //解决状态栏和布局重叠问题,指定颜色修改状态栏与标题栏之间存在的白色间隙
        immersionBar.init();
        loadingView = new LoadingView(this);

        Utils.log(this.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null){
            //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
            immersionBar.destroy();
        }
    }

    /**
     *隐藏软键盘
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        HideKeyBroadUtils.hide(this,ev);
        return super.dispatchTouchEvent(ev);
    }

    public void showToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    protected void showLoadingView() {
        loadingView.show();
    }
    protected void cancelLoadingView() {
        loadingView.cancel();
    }
}
