package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.security.rp.utils.RP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.WaitMeAgreeData;
import cn.fengwoo.sealsteward.utils.DateUtils;

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
        //获取当前时间
        Date nowTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowT = dateFormat.format(nowTime);
        //获取失效时间
        String failTime = waitMeAgreeData.get(position).getFailTime();
        //时间戳转为时间
        String failT = DateUtils.getDateString(Long.parseLong(failTime));
        Boolean selectB = DateUtils.isDateOneBigger(nowT,failT);
        if (selectB){
            viewHolder.fail_iv.setVisibility(View.VISIBLE);   //已失效之后审批单据不可点击
            viewHolder.item.setClickable(true);
        }else {
            viewHolder.fail_iv.setVisibility(View.GONE);
            viewHolder.item.setClickable(false);
        }
        return view;
    }

    class ViewHolder {
        private TextView wait_agree_cause_tv;
        private TextView wait_agree_person_tv;
        private TextView wait_agree_time_tv;
        private TextView wait_agree_department_tv;
        private TextView wait_agree_sealName_tv;
        private ImageView fail_iv;
        private LinearLayout item;

        public ViewHolder(View view) {
            wait_agree_cause_tv = view.findViewById(R.id.wait_agree_cause_tv);
            wait_agree_person_tv = view.findViewById(R.id.wait_agree_person_tv);
            wait_agree_time_tv = view.findViewById(R.id.wait_agree_time_tv);
            wait_agree_department_tv = view.findViewById(R.id.wait_agree_department_tv);
            wait_agree_sealName_tv = view.findViewById(R.id.wait_agree_sealName_tv);
            fail_iv = view.findViewById(R.id.fail_iv);
            item = view.findViewById(R.id.item_fail_ll);
        }
    }
}
