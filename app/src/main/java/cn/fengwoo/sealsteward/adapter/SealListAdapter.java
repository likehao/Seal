package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.MySealListData;

public class SealListAdapter extends BaseAdapter {
    private List<MySealListData> lists;
    private Context context;

    public SealListAdapter(List<MySealListData> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.seal_list_item, null);
            viewHolder.sealName = view.findViewById(R.id.sealName_item_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.sealName.setText(lists.get(position).getSealName());
        return view;
    }

    class ViewHolder {
        private TextView sealName;
    }
}
