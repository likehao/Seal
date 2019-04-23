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

/**
 * 审批进度adapter
 */
public class ApproveProgressAdapter extends BaseAdapter {

    private List<ApproveProgress> list;
    private Context context;

    public ApproveProgressAdapter(List<ApproveProgress> list, Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.approve_progress_item, null);
            viewHolder.person = view.findViewById(R.id.progress_person_tv);
            viewHolder.ogr = view.findViewById(R.id.progress_org_tv);
            viewHolder.result = view.findViewById(R.id.progress_result_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.person.setText(list.get(position).getApproveUserName());
        viewHolder.ogr.setText(list.get(position).getOrgStructureName());
        int status = list.get(position).getApproveStatus();
        String statusStr = null;
        if (status == 0){
            statusStr = "暂未审批";
        }else if (status == 1){
            statusStr = "正在审批...";
        }else if (status == 2){
            statusStr = "同意";
        }else {
            statusStr = "驳回";
        }
        viewHolder.result.setText(statusStr);
        return view;
    }

    class ViewHolder {
        TextView person;
        TextView ogr;
        TextView result;
    }

}
