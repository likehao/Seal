package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.ApplyUseSealActivity;
import cn.fengwoo.sealsteward.activity.ApprovalActivity;
import cn.fengwoo.sealsteward.activity.SeeRecordActivity;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.WaitApplyData;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 我的申请adapter
 */
public class WaitApplyAdapter extends BaseAdapter {

    private Context context;
    private List<WaitApplyData> waitApplyData;
    private Integer code;  //判断相对应的fragment来改变相应的view
    private Intent intent;
    ViewHolder viewHolder;

    public WaitApplyAdapter(Context context, List<WaitApplyData> waitApplyData, int code) {
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
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.wait_apply, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_cause.setText(waitApplyData.get(position).getCause());
        viewHolder.sealName_tv.setText(waitApplyData.get(position).getSealName());
        viewHolder.failTime_tv.setText(waitApplyData.get(position).getFailTime());
        viewHolder.apply_count_tv.setText(waitApplyData.get(position).getApplyCount() + "");
        viewHolder.applyTime_tv.setText(waitApplyData.get(position).getApplyTime());
        viewHolder.apply_person_tv.setText(waitApplyData.get(position).getApplyUserName());
        viewHolder.apply_department_tv.setText(waitApplyData.get(position).getOrgStructureName());
        int status = waitApplyData.get(position).getApproveStatus();
        String id = waitApplyData.get(position).getId();
        String applyUser = waitApplyData.get(position).getApplyUser();
        String userId = CommonUtil.getUserData((Activity)context).getId();

        if (code == 1){    //待审批
            statusView(status);
            //关闭单据
            viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    revokeDialog(id, 2, position);

                   /* if (waitApplyData.get(position).getUploadPhotoNum() == 0){
                        CommonDialog commonDialog = new CommonDialog(context,"提示",
                                "此单据还未上传盖章后拍照,将无法在记录详情查看到盖章文件,是否继续关闭？","关闭");
                        commonDialog.showDialog();
                        commonDialog.setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commonDialog.dialog.dismiss();
                                commonPostRequest2(id, 2, position);
                            }
                        });
                    }else {
                        commonPostRequest2(id, 2, position);
                    }*/
                }
            });
        }else if (code == 2) { //审批中
            viewHolder.item2_tv.setText("审批进度");
            viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.black));
            viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(context, ApprovalActivity.class);
                    intent.putExtra("applyId",waitApplyData.get(position).getId());
                    context.startActivity(intent);
                }
            });
        } else if (code == 3) {  //已审批(我的申请)
            seeRecordCloseBill(status, position, id);

        } else if (code == 4) {  //已驳回（我的申请）
            if (applyUser.equals(userId))  {   //判断此单据是否是登陆者本人来显示是否可以重提

                viewHolder.item2_tv.setVisibility(View.VISIBLE);
                viewHolder.item2_tv.setText("重提");
                viewHolder.item2_tv.setBackgroundResource(R.drawable.suggestion_gray);
                viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        putValue(position);
                    }
                });
            } else {
                viewHolder.item2_tv.setVisibility(View.GONE);
            }
        } else if (code == 5) {  //已审批(审批记录)
            seeRecordCloseBill(status, position, id);

        } else {    //code == 6     已驳回（审批记录）
            viewHolder.item2_tv.setVisibility(View.GONE);
        }

        return view;

    }

    /**
     *  待审批撤销
     */
    private void revokeDialog(String id,int code,int position){
        final CommonDialog commonDialog = new CommonDialog(context, "提示", "您是否确认撤销此单据？", "撤销");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dialog.dismiss();
                commonPostRequest2(id, code, position);
            }
        });
    }

    /**
     * 重提传值
     * @param position
     */
    private void putValue(int position){
        intent = new Intent(context, ApplyUseSealActivity.class);
        intent.putExtra("重提","重提");
        intent.putExtra("sealName",waitApplyData.get(position).getSealName());
        intent.putExtra("applyCount",waitApplyData.get(position).getApplyCount());
        intent.putExtra("failTime",waitApplyData.get(position).getFailTime());
        intent.putExtra("cause",waitApplyData.get(position).getCause());
        intent.putExtra("sign",waitApplyData.get(position).getAutoGraph());
        intent.putExtra("sealId",waitApplyData.get(position).getSealId());
        intent.putExtra("applyId",waitApplyData.get(position).getId());
//        intent.putStringArrayListExtra("imgList", (ArrayList<String>) waitApplyData.get(position).getStampRecordImgList());
        context.startActivity(intent);
    }
    /**
     * 查看记录关闭单据（已审批,已驳回）
     */
    private void seeRecordCloseBill(int status, int position, String id) {

        viewHolder.item1_tv.setVisibility(View.VISIBLE);
        viewHolder.item2_tv.setText("关闭单据");
        viewHolder.item2_tv.setEnabled(true);
        viewHolder.item2_tv.setBackgroundResource(R.drawable.suggestion_gray);
        viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.black));

        statusView(status);

        //查看记录
        viewHolder.item1_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer status = waitApplyData.get(position).getApproveStatus();
                intent = new Intent(context, SeeRecordActivity.class);
                intent.putExtra("status", status);    //传递状态值弹出不同的popuwindow
                intent.putExtra("id", id);
                intent.putExtra("count", waitApplyData.get(position).getSealCount());
                intent.putExtra("restCount", waitApplyData.get(position).getApplyCount());
                intent.putExtra("photoNum", waitApplyData.get(position).getUploadPhotoNum());
                intent.putExtra("headPortrait", waitApplyData.get(position).getHeadPortrait());
                intent.putExtra("sealName", waitApplyData.get(position).getSealName());
                intent.putExtra("orgStructureName", waitApplyData.get(position).getOrgStructureName());
                intent.putExtra("sealPerson", waitApplyData.get(position).getApplyUserName());
                intent.putExtra("applyPdf", waitApplyData.get(position).getApplyPdf());
                intent.putExtra("stampPdf", waitApplyData.get(position).getStampPdf());
                intent.putExtra("stampRecordPdf", waitApplyData.get(position).getStampRecordPdf());
                intent.putExtra("photoList", (Serializable) waitApplyData.get(position).getStampRecordImgList());
                intent.putExtra("type", 321);
                context.startActivity(intent);
            }
        });

        //关闭单据
        viewHolder.item2_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waitApplyData.get(position).getUploadPhotoNum() == 0){
                    CommonDialog commonDialog = new CommonDialog(context,"提示",
                            "此单据还未上传盖章后拍照,将无法在记录详情查看到盖章文件,是否继续关闭？","关闭");
                    commonDialog.showDialog();
                    commonDialog.setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commonDialog.dialog.dismiss();
                            commonPostRequest(id, code == 1 ? 2 : 1, position);
                        }
                    });
                }else {
                    commonPostRequest(id, code == 1 ? 2 : 1, position);
                }
            }
        });

    }

    /**
     * 根据状态值显示每条数据的状态
     *
     * @param status
     */
    @SuppressLint("ResourceAsColor")
    private void statusView(int status) {
        switch (status) {
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
            default:   //状态为0的
                viewHolder.item2_tv.setText("撤销");
                viewHolder.item2_tv.setEnabled(true);
                viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.item2_tv.setBackgroundResource(R.drawable.suggestion_gray);
                break;
        }
    }

    /**
     * 共同方法（Param）
     * 关闭单据（5）,已撤销（4）
     */
    private void commonPostRequest(String id, int code, int position) {
        //关闭单据
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
                Utils.log("result:" + result);
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (code == 1) {
                                    viewHolder.item2_tv.setText("已关闭");
                                    viewHolder.item2_tv.setEnabled(false);
                                    viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                                    viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                                    waitApplyData.get(position).setApproveStatus(5);
                                    notifyDataSetChanged();
                                    EventBus.getDefault().post(new MessageEvent("关闭刷新","关闭刷新"));
                                } else {
                                    viewHolder.item2_tv.setText("已撤销");
                                    viewHolder.item2_tv.setEnabled(false);
                                    viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                                    viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                                    waitApplyData.get(position).setApproveStatus(4);
                                    notifyDataSetChanged();
                                    EventBus.getDefault().post(new MessageEvent("撤销刷新","撤销刷新"));
                                }
                            }
                        });
                    }
                }
            }
        });

    }


    private void commonPostRequest2(String id, int code, int position) {
        //关闭单据
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", id);
        HttpUtil.sendDataAsync((Activity) context, HttpUrl.CANCEL_APPLY, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = response.body().string();
                Utils.log("result:" + result);
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (code == 1) {
                                    viewHolder.item2_tv.setText("已关闭");
                                    viewHolder.item2_tv.setEnabled(false);
                                    viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                                    viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                                    waitApplyData.get(position).setApproveStatus(5);
                                    notifyDataSetChanged();
                                    EventBus.getDefault().post(new MessageEvent("关闭刷新","关闭刷新"));
                                } else {
                                    viewHolder.item2_tv.setText("已撤销");
                                    viewHolder.item2_tv.setEnabled(false);
                                    viewHolder.item2_tv.setTextColor(context.getResources().getColor(R.color.gray_text));
                                    viewHolder.item2_tv.setBackgroundResource(R.drawable.record_off);
                                    waitApplyData.get(position).setApproveStatus(4);
                                    notifyDataSetChanged();
                                    EventBus.getDefault().post(new MessageEvent("撤销刷新","撤销刷新"));
                                }
                            }
                        });
                    }
                }
            }
        });

    }


    public static class ViewHolder {
        private TextView tv_cause;
        private TextView sealName_tv;
        private TextView failTime_tv;
        private TextView apply_count_tv;
        private TextView applyTime_tv;
        private TextView item1_tv;
        private TextView item2_tv;
        private LinearLayout apply_person_ll;
        private LinearLayout apply_department_ll;
        private TextView apply_person_tv;
        private TextView apply_department_tv;

        public ViewHolder(View view) {
            tv_cause = view.findViewById(R.id.tv_cause);
            sealName_tv = view.findViewById(R.id.sealName_tv);
            failTime_tv = view.findViewById(R.id.failTime_tv);
            apply_count_tv = view.findViewById(R.id.apply_count_tv);
            applyTime_tv = view.findViewById(R.id.applyTime_tv);
            item1_tv = view.findViewById(R.id.item1_tv);
            item2_tv = view.findViewById(R.id.item2_tv);
            apply_person_ll = view.findViewById(R.id.apply_person_ll);
            apply_department_ll = view.findViewById(R.id.apply_department_ll);
            apply_person_tv = view.findViewById(R.id.apply_person_tv);
            apply_department_tv = view.findViewById(R.id.apply_department_tv);
        }
    }

}