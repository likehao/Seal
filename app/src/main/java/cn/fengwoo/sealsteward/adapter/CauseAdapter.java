package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ApplyCauseData;

public class CauseAdapter extends BaseAdapter {

    private List<ApplyCauseData> list;
    private Context context;

    public CauseAdapter(List<ApplyCauseData> list, Context context) {
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.cause_item, null);
            viewHolder.cause = view.findViewById(R.id.cause_tv);
            viewHolder.count = view.findViewById(R.id.count_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.cause.setText(list.get(position).getCause());
        viewHolder.count.setText(list.get(position).getCount()+"次");
        return view;
    }

    class ViewHolder {
        private TextView cause;
        private TextView count;
    }
}
