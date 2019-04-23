package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.ApproveProgress;

public class ApprovalFlowOneAdapter extends BaseAdapter {

    private List<ApproveProgress> list;
    private Context context;

    public ApprovalFlowOneAdapter(List<ApproveProgress> list, Context context) {
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
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.approval_flow_one_item,null);
            viewHolder.name = view.findViewById(R.id.approvalFlow_name_tv);
            viewHolder.department = view.findViewById(R.id.approvalFlow_department_tv);
            viewHolder.level = view.findViewById(R.id.approvalFlow_level_tv);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(list.get(position).getApproveUserName());
        viewHolder.department.setText(list.get(position).getOrgStructureName());
        viewHolder.level.setText(list.get(position).getApproveLevel());
        return view;
    }

    class ViewHolder{
        TextView name;
        TextView department;
        TextView level;
    }
}
