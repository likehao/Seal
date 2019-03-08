package cn.fengwoo.sealsteward.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.LoginActivity;
import cn.fengwoo.sealsteward.database.AccountDao;
import cn.fengwoo.sealsteward.entity.HistoryInfo;

public class OptionsAdapter extends BaseAdapter {

    private ArrayList<HistoryInfo> list = new ArrayList<>();
    private LoginActivity activity = null;
    AccountDao dao;

    public OptionsAdapter( Activity activity,ArrayList<HistoryInfo> list, AccountDao dao) {
        this.list = list;
        this.activity =  (LoginActivity) activity;
        this.dao = dao;
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null){
            holder = new ViewHolder();
            //下拉项布局
            view = LayoutInflater.from(activity).inflate(R.layout.account_list_item,null);
            holder.textView = view.findViewById(R.id.item_text); //账号
     //       holder.down_civ = view.findViewById(R.id.down_civ);
            holder.imageView = view.findViewById(R.id.delete_iv);
            holder.rl_item = view.findViewById(R.id.rl_item);
            holder.nickName = view.findViewById(R.id.nickName);  //账户名
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        final HistoryInfo historyInfo = list.get(position);
        holder.textView.setText(historyInfo.getPhone());
        holder.nickName.setText(historyInfo.getName());

        //为下拉框选项删除图标部分设置事件，最终效果是点击将该选项删除
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(historyInfo);
                dao.delete(historyInfo.getPhone());
                activity.setView();
                notifyDataSetChanged();
            }
        });
        notifyDataSetChanged();
        return view;
    }

    static class ViewHolder{
        TextView textView;
   //     ImageView down_civ;
        ImageView imageView;
        RelativeLayout rl_item;
        TextView nickName;
    }
}
