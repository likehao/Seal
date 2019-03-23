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
import cn.fengwoo.sealsteward.entity.RecordData;

/**
 * 盖章记录adapter
 */
public class RecordAdapter extends BaseAdapter {
    private List<RecordData> recordData;
    private LayoutInflater inflater;

    public RecordAdapter(List<RecordData> recordData,Context context){
        this.recordData = recordData;
        inflater = LayoutInflater.from(context);
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.record_list_item,null);
            viewHolder.couse = view.findViewById(R.id.record_couse_tv);
            viewHolder.sealName = view.findViewById(R.id.record_seal_name_tv);
            viewHolder.sealPeople = view.findViewById(R.id.record_seal_people_tv);
            viewHolder.sealCount = view.findViewById(R.id.record_seal_count);
            viewHolder.restCount = view.findViewById(R.id.rest_count_tv);
            viewHolder.uploadPhotoNum = view.findViewById(R.id.upload_photoNum_tv);
            viewHolder.failTime = view.findViewById(R.id.record_failTime_tv);
            viewHolder.sealTime = view.findViewById(R.id.record_seal_time_tv);
            viewHolder.sealAddress = view.findViewById(R.id.record_seal_address_tv);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.couse.setText(recordData.get(position).getCouse());
            viewHolder.sealName.setText(recordData.get(position).getSealName());
            viewHolder.sealPeople.setText(recordData.get(position).getSealPeople());
            viewHolder.sealCount.setText(recordData.get(position).getSealCount()+"");
            viewHolder.restCount.setText(recordData.get(position).getRestCount()+"");
            viewHolder.uploadPhotoNum.setText(recordData.get(position).getUploadPhotoNum()+"");
            viewHolder.failTime.setText(recordData.get(position).getFailTime());
            viewHolder.sealTime.setText(recordData.get(position).getSealTime());
            viewHolder.sealAddress.setText(recordData.get(position).getSealAddress());
        return view;
    }

    class ViewHolder{
        private TextView couse;
        private TextView sealName;
        private TextView sealPeople;
        private TextView sealCount;
        private TextView restCount;
        private TextView uploadPhotoNum;
        private TextView failTime;
        private TextView sealTime;
        private TextView sealAddress;
    }
}
