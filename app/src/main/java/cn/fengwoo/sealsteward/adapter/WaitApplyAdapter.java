package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.ApprovalActivity;
import cn.fengwoo.sealsteward.activity.SeeRecordActivity;
import cn.fengwoo.sealsteward.bean.ApplyListData;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.WaitApplyData;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 我的申请adapter
 */
public class WaitApplyAdapter extends BaseAdapter{

    private Context context;
    private List<WaitApplyData> waitApplyData;
    private Integer code;  //判断相对应的fragment来改变相应的view
    private Intent intent;
    ViewHolder viewHolder;

    public WaitApplyAdapter(Context context, List<WaitApplyData> waitApplyData,int code) {
        this.context = context;
        this.waitApplyData = waitApplyData;
        this.code = code;
    }
    @Override
    public int getCount() {
        return waitApplyData.size();
    }

    @Override
    public Object getItem(int position) {
        return waitApplyData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.wait_apply,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.tv_cause.setText(waitApplyData.get(position).getCause());
            viewHolder.sealName_tv.setText(waitApplyData.get(position).getSealName());
            viewHolder.failTime_tv.setText(waitApplyData.get(position).getFailTime());
            viewHolder.apply_count_tv.setText(waitApplyData.get(position).getApplyCount()+"");
            viewHolder.applyTime_tv.setText(waitApplyData.get(position).getApplyTime());
            int status = waitApplyData.get(position).getApproveStatus();
            String id =  waitApplyData.get(position).getId();
            if (code == 2){ //审批中
                viewHolder.item2_tv.setText("审批进度");
                viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(context, ApprovalActivity.class);
                        context.startActivity(intent);
                    }
                });
            }else if (code == 3){  //已审批
                viewHolder.item1_tv.setVisibility(View.VISIBLE);
                viewHolder.item2_tv.setText("关闭单据");
                viewHolder.item2_tv.setBackgroundResource(R.drawable.suggestion_gray);
                viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.black));
                statusView(status);
                //查看记录
                viewHolder.item1_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer status = waitApplyData.get(position).getApproveStatus();
                        intent = new Intent(context, SeeRecordActivity.class);
                        intent.putExtra("status",status);    //传递状态值弹出不同的popuwindow
                        context.startActivity(intent);
                    }
                });
                //关闭单据
                viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonPostRequest(id,1);
                    }
                });
            }else if (code == 4){  //已驳回
                viewHolder.item2_tv.setText("重提");
                viewHolder.item2_tv.setBackgroundResource(R.drawable.suggestion_gray);
                viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        intent = new Intent(context, ApprovalActivity.class);
//                        context.startActivity(intent);
                    }
                });
            }else {
                statusView(status);
                viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonPostRequest(id,2);  //撤销
                    }
                });
            }
        return view;

    }

    /**
     * 根据状态值显示每条数据的状态
     * @param status
     */
    @SuppressLint("ResourceAsColor")
    private void statusView(int status){
        switch (status){
            case 5:
                viewHolder.item2_tv.setText("已关闭");
                viewHolder.item2_tv.setEnabled(false);
                viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                break;
            case 4:
                viewHolder.item2_tv.setText("已撤销");
                viewHolder.item2_tv.setEnabled(false);
                viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                break;
        }
    }
    /**
     * 共同方法（Param）
     * 关闭单据（5）,已撤销（4）
     */
    private void commonPostRequest(String id,int code){
                //关闭单据
        viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("applyId", id);
                HttpUtil.sendDataAsync((Activity) context, HttpUrl.APPLICLOSE, 1, hashMap, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                        }
                                .getType());
                        if (responseInfo.getCode() == 0) {
                            if (responseInfo.getData()) {
                                handler.sendEmptyMessage(code);
                          /*      ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (code == 1){
                                            viewHolder.item2_tv.setText("已关闭");
                                            viewHolder.item2_tv.setEnabled(false);
                                            viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                                            viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                                        }else {
                                            viewHolder.item2_tv.setText("已撤销");
                                            viewHolder.item2_tv.setEnabled(false);
                                            viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                                            viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                                        }
                                    }
                                });*/
                            }
                        }
                    }
                });
            }
        });

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    viewHolder.item2_tv.setText("已关闭");
                    viewHolder.item2_tv.setEnabled(false);
                    viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                    viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                    break;
                case 2:
                    viewHolder.item2_tv.setText("已撤销");
                    viewHolder.item2_tv.setEnabled(false);
                    viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                    viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                    break;
            }
            return false;
        }
    });

    public static class ViewHolder{
        private TextView tv_cause;
        private TextView sealName_tv;
        private TextView failTime_tv;
        private TextView apply_count_tv;
        private TextView applyTime_tv;
        private TextView item1_tv;
        private TextView item2_tv;

        public ViewHolder(View view){
            tv_cause = view.findViewById(R.id.tv_cause);
            sealName_tv = view.findViewById(R.id.sealName_tv);
            failTime_tv = view.findViewById(R.id.failTime_tv);
            apply_count_tv = view.findViewById(R.id.apply_count_tv);
            applyTime_tv = view.findViewById(R.id.applyTime_tv);
            item1_tv = view.findViewById(R.id.item1_tv);
            item2_tv = view.findViewById(R.id.item2_tv);
        }
    }
}
