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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.AddSealActivity;
import cn.fengwoo.sealsteward.activity.AddUserActivity;
import cn.fengwoo.sealsteward.activity.FileActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.adapter.TopRightPopuAdapter;
import cn.fengwoo.sealsteward.bean.MessageEvent;

public class MessagePopuwindow extends PopupWindow implements View.OnClickListener{
    private ListView listView;
    public View mview;
    private Intent intent;

    public MessagePopuwindow(Activity context,int code){
        final Activity activity = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //判断是首页还是记录详情进入
        if (code == 1){
            mview = inflater.inflate(R.layout.message_popuwindowlayout,null);   //添加人员，添加印章
            initView(1);
        }else if (code == 3){
            mview = inflater.inflate(R.layout.see_file_popuwindow,null);   //查看申请文件,查看盖章文件,查看记录文件
            initView(3);
        }
        else {
            mview = inflater.inflate(R.layout.see_record_popuwindow,null);   //查看申请文件，上传照片
            initView(2);
        }
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
 /*       listView = mview.findViewById(R.id.list);
        final TopRightPopuAdapter topRightPopuAdapter = new TopRightPopuAdapter(listData,context);
        listView.setAdapter(topRightPopuAdapter);*/
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

    }

    private void initView(int i){
        if (i == 1){
            TextView add_user_tv = mview.findViewById(R.id.add_user_tv);
            TextView add_seal_tv = mview.findViewById(R.id.add_seal_tv);
            add_user_tv.setOnClickListener(this);
            add_seal_tv.setOnClickListener(this);
        }else if (i == 3){
            TextView see_apply_file_tv = mview.findViewById(R.id.see_apply_file_tv);
            TextView see_seal_file_tv = mview.findViewById(R.id.see_seal_file_tv);
            TextView see_record_file_tv = mview.findViewById(R.id.see_record_file_tv);
            see_apply_file_tv.setOnClickListener(this);
            see_seal_file_tv.setOnClickListener(this);
            see_record_file_tv.setOnClickListener(this);
        }else {
            TextView apply_file_tv = mview.findViewById(R.id.apply_file_tv);
            TextView upload_photo_tv = mview.findViewById(R.id.upload_photo_tv);
            apply_file_tv.setOnClickListener(this);
            upload_photo_tv.setOnClickListener(this);
        }


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
            case R.id.see_apply_file_tv:  //查看申请文件
                EventBus.getDefault().post(new MessageEvent("申请文件","申请文件"));
                this.dismiss();
                break;
            case R.id.see_seal_file_tv:   //查看盖章文件
                EventBus.getDefault().post(new MessageEvent("盖章文件","盖章文件"));
                this.dismiss();
                break;
            case R.id.see_record_file_tv:   //查看记录文件
                EventBus.getDefault().post(new MessageEvent("记录文件","记录文件"));
                this.dismiss();
                break;
            case R.id.apply_file_tv:
                EventBus.getDefault().post(new MessageEvent("申请文件","申请文件"));
                this.dismiss();
                break;
            case R.id.upload_photo_tv:
                EventBus.getDefault().post(new MessageEvent("上传照片","上传照片"));
                this.dismiss();
                break;

        }
    }

}
