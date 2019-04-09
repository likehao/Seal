package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.bean.StampRecordList;
import cn.fengwoo.sealsteward.entity.RecordData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.CommonDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 盖章记录adapter
 */
public class RecordAdapter extends BaseAdapter {
    private List<RecordData> recordData;
    private LayoutInflater inflater;
    private Context context;
    ViewHolder viewHolder;

    public RecordAdapter(List<RecordData> recordData, Context context) {
        this.recordData = recordData;
        this.context = context;
        //inflater = LayoutInflater.from(context);
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
            view = LayoutInflater.from(context).inflate(R.layout.record_list_item, null);
            viewHolder.couse = view.findViewById(R.id.record_couse_tv);
            viewHolder.sealName = view.findViewById(R.id.record_seal_name_tv);
            viewHolder.sealPeople = view.findViewById(R.id.record_seal_people_tv);
            viewHolder.sealCount = view.findViewById(R.id.record_seal_count);
            viewHolder.restCount = view.findViewById(R.id.rest_count_tv);
            viewHolder.uploadPhotoNum = view.findViewById(R.id.upload_photoNum_tv);
            viewHolder.failTime = view.findViewById(R.id.record_failTime_tv);
            viewHolder.sealTime = view.findViewById(R.id.record_seal_time_tv);
            viewHolder.sealAddress = view.findViewById(R.id.record_seal_address_tv);
            viewHolder.close = view.findViewById(R.id.close_documents_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.couse.setText(recordData.get(position).getCause());
        viewHolder.sealName.setText(recordData.get(position).getSealName());
        viewHolder.sealPeople.setText(recordData.get(position).getSealPeople());
        viewHolder.sealCount.setText(recordData.get(position).getSealCount() + "");
        viewHolder.restCount.setText(recordData.get(position).getRestCount() + "");
        viewHolder.uploadPhotoNum.setText(recordData.get(position).getUploadPhotoNum() + "");
        viewHolder.failTime.setText(recordData.get(position).getFailTime());
        viewHolder.sealTime.setText(recordData.get(position).getSealTime());
        viewHolder.sealAddress.setText(recordData.get(position).getSealAddress());
        int i = recordData.get(position).getApproveStatus();
        if (i == 5) {
            viewHolder.close.setText("已关闭");
            viewHolder.close.setEnabled(false);
            viewHolder.close.setTextColor(context.getResources().getColor(R.color.gray_text));
            viewHolder.close.setBackgroundResource(R.drawable.record_off);
        } else {
            viewHolder.close.setText("关闭单据");
            viewHolder.close.setEnabled(true);
            viewHolder.close.setTextColor(context.getResources().getColor(R.color.black));
            viewHolder.close.setBackgroundResource(R.drawable.suggestion_gray);
        }

        //关闭单据
        viewHolder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordData.get(position).getUploadPhotoNum() == 0){
                    CommonDialog commonDialog = new CommonDialog(context,"提示",
                            "此单据还未上传盖章后牌照,将无法在记录详情查看到盖章文件,是否继续关闭？","关闭");
                    commonDialog.showDialog();
                    commonDialog.setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applyClose(position);
                            commonDialog.dialog.dismiss();
                        }
                    });
                }else {
                    applyClose(position);
                }

            }
        });
        return view;
    }

    /**
     * 关闭单据
     */
    private void applyClose(int position){
        String applyId = recordData.get(position).getId();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", applyId);
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

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewHolder.close.setText("已关闭");
                                Log.e("TAG","111111111111111111111111111111111111111");
                                viewHolder.close.setEnabled(false);
                                viewHolder.close.setTextColor(context.getResources().getColor(R.color.gray_text));
                                recordData.get(position).setApproveStatus(5);
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }

    class ViewHolder {
        private TextView couse;
        private TextView sealName;
        private TextView sealPeople;
        private TextView sealCount;
        private TextView restCount;
        private TextView uploadPhotoNum;
        private TextView failTime;
        private TextView sealTime;
        private TextView sealAddress;
        private TextView close;
    }

}
