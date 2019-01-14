package cn.fengwoo.sealsteward.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 点击空白区域关闭软键盘
 */
public class HideKeyBroadUtils {
    public static void hide(Activity activity, MotionEvent ev){
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            View v = activity.getCurrentFocus();
            if (isShouldHideKeyboard(v,ev)){
                hideKeyboard(activity,v.getWindowToken());
            }
        }
    }
    //判断是否需要隐藏
    private static boolean isShouldHideKeyboard(View v,MotionEvent event){
        if (v != null && (v instanceof EditText)){
            int[] i = {0,0};
            v.getLocationInWindow(i);
            int left = i[0],
                    top = i[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom){
                return false;
            }else {
                return true;
            }
        }
        return false;
    }
    //隐藏软键盘
    private static void hideKeyboard(Activity activity,IBinder token){
        if (token != null){
            InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
