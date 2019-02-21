package cn.fengwoo.sealsteward.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.AddSealActivity;
import cn.fengwoo.sealsteward.activity.AddUserActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;

public class MessagePopuwindow extends PopupWindow implements View.OnClickListener{
    private ListView listView;
    public View mview;
    private TextView add_user_tv,add_seal_tv;
    private Intent intent;

    public MessagePopuwindow(Activity context){
        final Activity activity = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mview = inflater.inflate(R.layout.message_popuwindowlayout,null);
        //设置选择的popuwindow的View
        this.setContentView(mview);
        //获取屏幕宽高度
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //设置弹出的popuwindow的宽
        this.setWidth(width / 3);
        //设置弹出的popuwindow的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
/*
        listView = mview.findViewById(R.id.list);
        final TopRightPopuAdapter topRightPopuAdapter = new TopRightPopuAdapter(listData,context);
        listView.setAdapter(topRightPopuAdapter);
*/
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
        //       this.showAtLocation(activity.getWindow().getDecorView(), Gravity.TOP | Gravity.RIGHT, 0, 0);
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
        //初始化视图点击事件
        initView();
    }

    private void initView(){
        add_user_tv = mview.findViewById(R.id.add_user_tv);
        add_user_tv.setOnClickListener(this);
        add_seal_tv = mview.findViewById(R.id.add_seal_tv);
        add_seal_tv.setOnClickListener(this);
    }
    //显示
    public void showPopuwindow(View view){
        if (!this.isShowing()){
            this.showAsDropDown(view,view.getLayoutParams().width / 2,18);
        }else {
            this.dismiss();
        }
    }

    public void setItemClickListener(AdapterView.OnItemClickListener listener){
        listView.setOnItemClickListener(listener);
    }

    /**
     * 设置透明背景度
     * @param bgAlpha
     * @param activity
     */
    public void backgroundAlpha(float bgAlpha,Activity activity){
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_user_tv:
                intent = new Intent(mview.getContext(), AddUserActivity.class);
                mview.getContext().startActivity(intent);
                this.dismiss();
                break;
            case R.id.add_seal_tv:
                intent = new Intent(mview.getContext(), NearbyDeviceActivity.class);
                mview.getContext().startActivity(intent);
                this.dismiss();
                break;

        }
    }
}
