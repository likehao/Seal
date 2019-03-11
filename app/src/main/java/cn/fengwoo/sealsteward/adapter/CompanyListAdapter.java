package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.CompanyInfo;

public class CompanyListAdapter extends BaseAdapter {

    private List<CompanyInfo> list;
    private Context context;
    int mSelect = 0;   //选中项

    public CompanyListAdapter(List<CompanyInfo> list, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.company_list_item,null);
            viewHolder.company_name_tv = convertView.findViewById(R.id.company_name_tv);
            viewHolder.select_company_iv = convertView.findViewById(R.id.select_company_iv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
            viewHolder.company_name_tv.setText(list.get(position).getCompanyName());
        //选中状态
        if (mSelect == position){
            viewHolder.select_company_iv.setVisibility(View.VISIBLE);
        }else {
            viewHolder.select_company_iv.setVisibility(View.GONE);
        }
        return convertView;
    }
    class ViewHolder{
        private TextView company_name_tv;
        private ImageView select_company_iv;
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
