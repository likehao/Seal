package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.ApprovalActivity;
import cn.fengwoo.sealsteward.entity.WaitApplyData;

/**
 * 我的申请adapter
 */
public class WaitApplyAdapter extends BaseAdapter {

    private Context context;
    private List<WaitApplyData> waitApplyData;
    private Integer code;  //判断相对应的fragment来改变相应的view
    private Intent intent;
    public WaitApplyAdapter(Context context, List<WaitApplyData> waitApplyData,int code) {
        this.context = context;
        this.waitApplyData = waitApplyData;
        this.code = code;
    }
    @Override
    public int getCount() {
        return waitApplyData.size();
    }

    @Override
    public Object getItem(int position) {
        return waitApplyData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.wait_apply,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.tv_cause.setText(waitApplyData.get(position).getCause());
            viewHolder.sealName_tv.setText(waitApplyData.get(position).getSealName());
            viewHolder.failTime_tv.setText(waitApplyData.get(position).getFailTime());
            viewHolder.apply_count_tv.setText(waitApplyData.get(position).getApplyCount()+"");
            viewHolder.applyTime_tv.setText(waitApplyData.get(position).getApplyTime());
            if (code == 2){ //审批中
                viewHolder.item2_tv.setText("审批进度");
                viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(context, ApprovalActivity.class);
                        context.startActivity(intent);
                    }
                });
            }else if (code == 3){  //已审批
                viewHolder.item1_tv.setVisibility(View.VISIBLE);
                viewHolder.item2_tv.setText("关闭单据");
                viewHolder.item1_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(context, ApprovalActivity.class);
                        context.startActivity(intent);
                    }
                });
            }else if (code == 4){  //已驳回
                viewHolder.item1_tv.setVisibility(View.VISIBLE);
                viewHolder.item2_tv.setText("重提");
                viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(context, ApprovalActivity.class);
                        context.startActivity(intent);
                    }
                });
            }
        return view;

    }

    private static class ViewHolder{
        private TextView tv_cause;
        private TextView sealName_tv;
        private TextView failTime_tv;
        private TextView apply_count_tv;
        private TextView applyTime_tv;
        private TextView item1_tv;
        private TextView item2_tv;

        public ViewHolder(View view){
            tv_cause = view.findViewById(R.id.tv_cause);
            sealName_tv = view.findViewById(R.id.sealName_tv);
            failTime_tv = view.findViewById(R.id.failTime_tv);
            apply_count_tv = view.findViewById(R.id.apply_count_tv);
            applyTime_tv = view.findViewById(R.id.applyTime_tv);
            item1_tv = view.findViewById(R.id.item1_tv);
            item2_tv = view.findViewById(R.id.item2_tv);
        }
    }
}
