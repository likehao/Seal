package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.RechargeRecordData;

/**
 * 充值记录adapter
 */
public class RechargeRecordAdapter extends BaseAdapter {

    List<RechargeRecordData> recordDataList;
    private Context context;

    public RechargeRecordAdapter(List<RechargeRecordData> recordDataList, Context context) {
        this.recordDataList = recordDataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return recordDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.recharge_record_item,null);
            viewHolder.recharge_time_tv = view.findViewById(R.id.recharge_time_tv);
            viewHolder.recharge_seal_name_tv = view.findViewById(R.id.recharge_seal_name_tv);
            viewHolder.recharge_department_tv = view.findViewById(R.id.recharge_department_tv);
            viewHolder.recharge_set_meal_tv = view.findViewById(R.id.recharge_set_meal_tv);
            viewHolder.recharge_end_time_tv = view.findViewById(R.id.recharge_end_time_tv);
            viewHolder.recharge_money_tv = view.findViewById(R.id.recharge_money_tv);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.recharge_time_tv.setText(recordDataList.get(position).getRechargeTime());
        viewHolder.recharge_seal_name_tv.setText(recordDataList.get(position).getSealName());
        viewHolder.recharge_department_tv.setText(recordDataList.get(position).getDepartment());
        viewHolder.recharge_set_meal_tv.setText(recordDataList.get(position).getSetMeal());
        viewHolder.recharge_end_time_tv.setText(recordDataList.get(position).getEndTime());
        viewHolder.recharge_money_tv.setText(recordDataList.get(position).getMoney());
        return view;
    }
    class ViewHolder{
        private TextView recharge_time_tv;  //充值时间
        private TextView recharge_seal_name_tv;  //充值印章名称
        private TextView recharge_department_tv; //充值部门
        private TextView recharge_set_meal_tv; //充值套餐
        private TextView recharge_end_time_tv; //充值服务费到期时间
        private TextView recharge_money_tv; //充值金额
    }
}
