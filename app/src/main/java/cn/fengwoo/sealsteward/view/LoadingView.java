package cn.fengwoo.sealsteward.view;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cn.fengwoo.sealsteward.R;

public class LoadingView {
    private Activity context;
    private AlertDialog dialog;
    private TextView loadview_tv;
    public LoadingView(Activity context){
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_Transparent));
        View view = View.inflate(context, R.layout.loadingview,null);
        dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view,0,0,0,0);
        loadview_tv = view.findViewById(R.id.loadingview_tv);
    }

    public void show(){
        if (!dialog.isShowing()){
            if (!context.isFinishing()){
                dialog.show();
                dialog.getWindow().setLayout(300,300);
            }
        }
    }

    public void cancel(){
        if (dialog.isShowing()){
            dialog.cancel();
        }
    }

    public boolean isShow(){
        return dialog.isShowing();
    }

    public void showView(String string){
        loadview_tv.setText(string +"...");
    }
}
