package cn.fengwoo.sealsteward.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import cn.fengwoo.sealsteward.R;

/**
 * 卡券dialog
 */
public class CardTicketDialog {
    private View view;
    private AlertDialog dialog;
    public TextView sealName,sealTime,know;
    public CardTicketDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.dialog));
        view = View.inflate(context,R.layout.card_ticket,null);
        dialog = builder.create();
        dialog.setView(view,0,0,0,0);

        initView();
    }
    private void initView(){
        sealName = view.findViewById(R.id.pay_seal_name_tv);
        sealTime = view.findViewById(R.id.pay_seal_time_tv);
        know = view.findViewById(R.id.know_tv);
        know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showDialog(){
        dialog.show();
    }
}
