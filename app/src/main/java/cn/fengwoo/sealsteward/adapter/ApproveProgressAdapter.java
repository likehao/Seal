package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.ApproveProgress;
import cn.fengwoo.sealsteward.utils.DateUtils;

/**
 * 审批进度adapter
 */
public class ApproveProgressAdapter extends BaseAdapter {

    private List<ApproveProgress> list;
    private Context context;
    private SpannableString spanString;

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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.approve_progress_item, null);
//            viewHolder.person = view.findViewById(R.id.progress_person_tv);
//            viewHolder.ogr = view.findViewById(R.id.progress_org_tv);
//            viewHolder.result = view.findViewById(R.id.progress_result_tv);
            viewHolder.result = view.findViewById(R.id.approve_progress_tv);
            viewHolder.hurry = view.findViewById(R.id.hurry_bt);
            viewHolder.useTime = view.findViewById(R.id.useTime_ll);
            viewHolder.time = view.findViewById(R.id.time_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
//        viewHolder.person.setText(list.get(position).getApproveUserName());
//        viewHolder.ogr.setText(list.get(position).getOrgStructureName());
        String userName = list.get(position).getApproveUserName();
        String org = list.get(position).getOrgStructureName();
        String suggestion = list.get(position).getApproveOpinion(); //审批意见
        String approveTime = list.get(position).getApproveTime(); // 审批时间
        String createTime = list.get(position).getCreateTime(); // 创建时间
        //时间戳转为时间
     /*   String applyTime = null;
        if (approveTime != null){
            applyTime = DateUtils.getDateString(Long.parseLong(approveTime));
        }
        String cTime = DateUtils.getDateString(Long.parseLong(createTime));

        try {
            String difTime = DateUtils.getTimeExpend(applyTime, cTime);
            String day = difTime.split(":")[0];
            String hour = difTime.split(":")[1];
            String mi = difTime.split(":")[2];
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/
        if (suggestion == null) {
            suggestion = "无";
        }
        int status = list.get(position).getApproveStatus();
        String statusStr = null;
        if (status == 0) {
            statusStr = "未审批!";
            String content = "审批人-" + userName + ",  部门-" + org +",  "+ statusStr;
            spanString = new SpannableString(content);
            //再构造一个改变字体颜色的Span
            ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
            //将这个Span应用于指定范围的字体
            spanString.setSpan(span, content.length() - 4, content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            //设置给EditText显示出来
            viewHolder.hurry.setVisibility(View.GONE);
            viewHolder.useTime.setVisibility(View.GONE);
        } else if (status == 1) {
            statusStr = "正在审批...";
            viewHolder.hurry.setVisibility(View.VISIBLE);
            viewHolder.useTime.setVisibility(View.GONE);
        } else if (status == 2) {
            statusStr = "同意";
        } else if (status == 3) {
            statusStr = "驳回";
        }
//        viewHolder.result.setText(statusStr);
        if (status != 0) {
            spanString = new SpannableString("审批人-" + userName + ",  部门-" + org + ",  审批结果-" + statusStr + ",  审批意见-" + suggestion);
        }

        viewHolder.result.setText(spanString);
        return view;
    }

    class ViewHolder {
        //        TextView person;
//        TextView ogr;
//        TextView result;
        TextView result;
        Button hurry;    //催促审批按钮
        LinearLayout useTime;
        TextView time;
    }

}
