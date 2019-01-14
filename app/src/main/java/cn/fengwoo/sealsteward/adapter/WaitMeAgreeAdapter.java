package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.WaitMeAgreeData;

public class WaitMeAgreeAdapter extends BaseAdapter {

    private Context context;
    private List<WaitMeAgreeData> waitMeAgreeData;

    public WaitMeAgreeAdapter(Context context, List<WaitMeAgreeData> waitMeAgreeData) {
        this.context = context;
        this.waitMeAgreeData = waitMeAgreeData;
    }

    @Override
    public int getCount() {
        return waitMeAgreeData.size();
    }

    @Override
    public Object getItem(int position) {
        return waitMeAgreeData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.wait_agree_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.wait_agree_cause_tv.setText(waitMeAgreeData.get(position).getCause());
        viewHolder.wait_agree_person_tv.setText(waitMeAgreeData.get(position).getApplyPerson());
        viewHolder.wait_agree_time_tv.setText(waitMeAgreeData.get(position).getApplyTime());
        viewHolder.wait_agree_department_tv.setText(waitMeAgreeData.get(position).getDepartment());
        viewHolder.wait_agree_sealName_tv.setText(waitMeAgreeData.get(position).getSealName());
        return view;
    }

    class ViewHolder {
        private TextView wait_agree_cause_tv;
        private TextView wait_agree_person_tv;
        private TextView wait_agree_time_tv;
        private TextView wait_agree_department_tv;
        private TextView wait_agree_sealName_tv;

        public ViewHolder(View view) {
            wait_agree_cause_tv = view.findViewById(R.id.wait_agree_cause_tv);
            wait_agree_person_tv = view.findViewById(R.id.wait_agree_person_tv);
            wait_agree_time_tv = view.findViewById(R.id.wait_agree_time_tv);
            wait_agree_department_tv = view.findViewById(R.id.wait_agree_department_tv);
            wait_agree_sealName_tv = view.findViewById(R.id.wait_agree_sealName_tv);
        }
    }
}
