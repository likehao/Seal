package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.SeeRecordBean;

public class PwdRecordAdapter extends BaseAdapter {

    private List<SeeRecordBean> recordData;
    private Context context;
    ViewHolder viewHolder;

    public PwdRecordAdapter(List<SeeRecordBean> recordData, Context context) {
        this.recordData = recordData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return recordData.size();
    }

    @Override
    public Object getItem(int position) {
        return recordData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.pwd_record_item, null);
            viewHolder.serial_number = view.findViewById(R.id.pwdRecord_serial_number_tv);
            viewHolder.sealName = view.findViewById(R.id.pwdRecord_sealName_tv);
            viewHolder.sealPeople = view.findViewById(R.id.pwdRecord_sealPerson_tv);
            viewHolder.sealTime = view.findViewById(R.id.pwdRecord_sealTime_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.serial_number.setText(recordData.get(position).getSerialNumber());
        viewHolder.sealName.setText(recordData.get(position).getSealName());
        viewHolder.sealPeople.setText(recordData.get(position).getSealPerson());
        viewHolder.sealTime.setText(recordData.get(position).getSealTime());

        return view;

    }

    class ViewHolder {
        private TextView serial_number;   //流水号
        private TextView sealName;        //印章名字
        private TextView sealPeople;      //印章人
        private TextView sealTime;        //盖章时间
    }
}
