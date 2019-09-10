package cn.fengwoo.sealsteward.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import cn.fengwoo.sealsteward.R;

/**
 * 暂无公司
 */
public class NoCompanyView extends PopupWindow {
    public View view;
    public ImageView cancel_iv;
    public NoCompanyView(Activity activity){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.no_company_view,null);
        //设置选择的popuwindow的View
        this.setContentView(view);
        //获取屏幕宽高度
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //设置弹出的popuwindow的宽
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        //设置弹出的popuwindow的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //设置弹窗是否可以点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //刷新状态
        this.update();
        //实例化一个colorDrawable颜色为半透明
        ColorDrawable colorDrawable = new ColorDrawable(0000000000);
        // 点击返回键和其他地方使其消失，设置了这个才能触发OnDismissListener,设置其他控件变化等操作
        this.setBackgroundDrawable(colorDrawable);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        //设置popuwindow以外的背景透明度
        backgroundAlpha(0.65f,activity);
        this.setOnDismissListener(new PopupWindow.OnDismissListener(){
            @Override
            public void onDismiss() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backgroundAlpha(1.0f,activity);
                    }
                },300);
            }
        });

        cancel_iv = view.findViewById(R.id.cancel_iv);
        cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    //显示
    public void showPopuwindow(View view){
        if (!this.isShowing()){
            this.showAsDropDown(view,view.getLayoutParams().width / 2,18);
        }else {
            this.dismiss();
        }
    }
    //设置透明度
    public void backgroundAlpha(float bgAlpha,Activity activity){
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }
}
