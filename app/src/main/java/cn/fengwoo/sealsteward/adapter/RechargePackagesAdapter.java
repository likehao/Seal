package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.PayRechargePackages;

public class RechargePackagesAdapter extends BaseAdapter {

    private List<PayRechargePackages> list;
    private Context context;
    int mSelect = 0;   //选中项

    public RechargePackagesAdapter(List<PayRechargePackages> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.pay_recharge_packages_item,null);
            viewHolder.payMoney = convertView.findViewById(R.id.pay_money_tv);
            viewHolder.select_package = convertView.findViewById(R.id.select_money_iv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.payMoney.setText(String.valueOf(list.get(position).getContent()));

        //选中状态
        if (mSelect == position){
            viewHolder.select_package.setVisibility(View.VISIBLE);
        }else {
            viewHolder.select_package.setVisibility(View.GONE);
        }

        return convertView;
    }
    class ViewHolder{
        private TextView payMoney;
        private ImageView select_package;
    }
    /**
     * 监听选中
     * @param positon
     */
    public int changeSelected(int positon){
        if(positon != mSelect){
            mSelect = positon;
            notifyDataSetChanged();
        }
        return positon;
    }
}