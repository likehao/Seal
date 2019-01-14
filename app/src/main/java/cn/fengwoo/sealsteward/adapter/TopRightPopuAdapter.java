package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.fengwoo.sealsteward.R;

public class TopRightPopuAdapter extends BaseAdapter {

    private List<String> lstItem;
    private Context mContext;

    public TopRightPopuAdapter(List<String> lstItem, Context mContext) {
        this.lstItem = lstItem;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return lstItem.size();
    }

    @Override
    public Object getItem(int position) {
        return lstItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_popu,null);
            viewHolder.addText = view.findViewById(R.id.addtext);
            viewHolder.addLayout = view.findViewById(R.id.addlayout);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String str = lstItem.get(position);
        viewHolder.addText.setText(str);
        return view;
    }
    class ViewHolder{
        TextView addText;
        LinearLayout addLayout;
    }
}
