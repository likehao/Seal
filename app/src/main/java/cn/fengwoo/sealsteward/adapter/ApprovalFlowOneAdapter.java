package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.ApproveProgress;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ApprovalFlowOneAdapter extends BaseAdapter {

    private List<SealInfoData.SealApproveFlowListBean> list;
    private Context context;

    public ApprovalFlowOneAdapter(List<SealInfoData.SealApproveFlowListBean> list, Context context) {
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
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.approval_flow_one_item,null);
            viewHolder.name = view.findViewById(R.id.approvalFlow_name_tv);
            viewHolder.department = view.findViewById(R.id.approvalFlow_department_tv);
            viewHolder.level = view.findViewById(R.id.approvalFlow_level_tv);
            viewHolder.oneDelete = view.findViewById(R.id.oneDelete);

            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(list.get(position).getApproveUserName());
        viewHolder.department.setText(list.get(position).getOrgStructureName());
        viewHolder.level.setText(list.get(position).getApproveLevel()+"级审批");

        viewHolder.oneDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String ,String > hashMap = new HashMap<>();
                hashMap.put("id",list.get(position).getId());
                HttpUtil.sendDataAsync((Activity) context, HttpUrl.DELETEAPPROVALFLOW, 4, hashMap, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG",e+"删除审批流错误错误!!!!!!!!!!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}
                        .getType());
                        if (responseInfo.getCode() == 0){
                            if (responseInfo.getData()){

                            }
                        }
                    }
                });
            }
        });
        return view;
    }

    class ViewHolder{
        TextView name;
        TextView department;
        TextView level;
        Button oneDelete;
    }
}
