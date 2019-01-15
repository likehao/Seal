package cn.fengwoo.sealsteward.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import cn.fengwoo.sealsteward.R;

/**
 * 公共dialog
 */
public class CommonDialog {

    public AlertDialog dialog;
    private TextView title,msg,cancel,ok;
    public CommonDialog(Context context, String titleMsg, String message,String sure){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.dialog));
        View view = View.inflate(context,R.layout.common_dialog,null);
        title = view.findViewById(R.id.tv_dialog_title);
        msg = view.findViewById(R.id.tv_msg);
        cancel = view.findViewById(R.id.cancel);
        ok = view.findViewById(R.id.ok);
        title.setText(titleMsg);
        msg.setText(message);
        ok.setText(sure);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    public void showDialog(){
        dialog.show();
    }

    public void setClickListener(View.OnClickListener listener){
        ok.setOnClickListener(listener);
    }
}
