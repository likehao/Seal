package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.SeeRecordBean;

/**
 * 查看记录
 */
public class SeeRecordAdapter extends BaseAdapter {

    private Context context;
    private List<SeeRecordBean> beanList;
    public SeeRecordAdapter(Context context, List<SeeRecordBean> beanList) {
        this.context = context;
        this.beanList = beanList;
    }

    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public Object getItem(int position) {
        return beanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.see_record_item,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.serial_number_tv.setText(beanList.get(position).getSerialNumber());
        viewHolder.sealTime_tv.setText(beanList.get(position).getSealTime());
        viewHolder.seal_address_tv.setText(beanList.get(position).getSealAddress());

        return view;
    }
    class ViewHolder{
        private TextView serial_number_tv;
        private TextView sealTime_tv;
        private TextView seal_address_tv;

        public ViewHolder(View view){
            serial_number_tv = view.findViewById(R.id.serial_number_tv);
            sealTime_tv = view.findViewById(R.id.sealTime_tv);
            seal_address_tv = view.findViewById(R.id.seal_address_tv);
        }
    }
}
