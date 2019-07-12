package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longsh.optionframelibrary.OptionBottomDialog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.ForgetPasswordActivity;
import cn.fengwoo.sealsteward.bean.ApproveProgress;
import cn.fengwoo.sealsteward.utils.DateUtils;

/**
 * 审批进度adapter
 */
public class ApproveProgressAdapter extends BaseAdapter {

    private List<ApproveProgress> list;
    private Context context;
    private SpannableString spanString;
    private String applyTime, cTime;

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
            viewHolder.month_day = view.findViewById(R.id.month_day_tv);
            viewHolder.hour_min = view.findViewById(R.id.hour_min_tv);
            viewHolder.oval = view.findViewById(R.id.oval_iv);
            viewHolder.line = view.findViewById(R.id.line_view);
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
        if (approveTime != null) {
            applyTime = DateUtils.getDateString(Long.parseLong(approveTime));
        }
        cTime = DateUtils.getDateString(Long.parseLong(createTime));

        if (suggestion == null) {
            suggestion = "无";
        }
        int status = list.get(position).getApproveStatus();
        String statusStr = null;
        if (status == 0) {
            statusStr = "未审批!";
            String content = "审批人-" + userName + ",  部门-" + org + ",  " + statusStr;
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
            getUseTime(viewHolder);

        } else if (status == 3) {
            statusStr = "驳回";
            getUseTime(viewHolder);
        }
//        viewHolder.result.setText(statusStr);
        if (status != 0) {
            spanString = new SpannableString("审批人-" + userName + ",  部门-" + org + ",  审批结果-" + statusStr + ",  审批意见-" + suggestion);
        }

        viewHolder.result.setText(spanString);

        /***
         * 弹出催审提示框
         */
        String phone = list.get(position).getMobilePhone();
        viewHolder.hurry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> stringList = new ArrayList<String>();
                stringList.add("打电话");
                stringList.add("发短信");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(context, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            //Intent intent = new Intent(Intent.ACTION_CALL);  //直接拨打
                            Uri data = Uri.parse("tel:" + phone);
                            intent.setData(data);
                            context.startActivity(intent);
                            optionBottomDialog.dismiss();

                        } else {
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                            context.startActivity(intent);
                            optionBottomDialog.dismiss();
                        }
                    }
                });
            }
        });
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
        TextView month_day;
        TextView hour_min;
        ImageView oval;
        View line;
    }

    @SuppressLint("SetTextI18n")
    private void getUseTime(ViewHolder viewHolder) {
        viewHolder.month_day.setVisibility(View.VISIBLE);
        viewHolder.hour_min.setVisibility(View.VISIBLE);
        viewHolder.oval.setImageResource(R.drawable.oval);
        viewHolder.line.setBackgroundResource(R.color.style);
        // 2019-06-25 11:05:53分割之后拼接
        String time1 = applyTime.split(" ")[0];
        String time2 = applyTime.split(" ")[1];
        String[] month_day = time1.split("-");
        String[] hours_min = time2.split(":");
        viewHolder.month_day.setText(month_day[1] + "-" + month_day[2]);
        viewHolder.hour_min.setText(hours_min[0] + ":" + hours_min[1]);
        try {
            String difTime = DateUtils.getTimeExpend(applyTime, cTime);
            String[] useT = difTime.split(":");    //day , hour , min
            viewHolder.time.setText(useT[0] + "天" + useT[1] + "小时" + useT[2] + "分钟");

            if (useT[0].equals("0")) {
                viewHolder.time.setText(useT[1] + "小时" + useT[2] + "分钟");
            }

            if (useT[0].equals("0") && useT[1].equals("0")) {
                int min = Integer.parseInt(useT[2]);
                if (min < 1) {
                    viewHolder.time.setText("不到一分钟");
                } else if (min > 1 && min < 60) {
                    viewHolder.time.setText(min + "分钟");
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
