package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.MapViewActivity;
import cn.fengwoo.sealsteward.activity.RecordQrCodeActivity;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.bean.MessageEvent;
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
    private double latitude,longitude;

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
            viewHolder.restCount = view.findViewById(R.id.rest_count_tv); // 申请次数
            viewHolder.uploadPhotoNum = view.findViewById(R.id.upload_photoNum_tv);
            viewHolder.failTime = view.findViewById(R.id.record_failTime_tv);
            viewHolder.sealTime = view.findViewById(R.id.record_seal_time_tv);
            viewHolder.sealAddress = view.findViewById(R.id.record_seal_address_tv);
            viewHolder.close = view.findViewById(R.id.close_documents_tv);
            viewHolder.ygcs = view.findViewById(R.id.ygcs);
            viewHolder.qrcode = view.findViewById(R.id.record_qrcode_iv);  //记录二维码
            viewHolder.address_ll = view.findViewById(R.id.address_ll);  //地址
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.couse.setText(recordData.get(position).getCause());
        viewHolder.sealName.setText(recordData.get(position).getSealName());
        viewHolder.sealPeople.setText(recordData.get(position).getSealPeople());
        viewHolder.sealCount.setText(recordData.get(position).getSealCount() + "");
        viewHolder.restCount.setText(recordData.get(position).getApplyCount() + "");
        viewHolder.uploadPhotoNum.setText(recordData.get(position).getUploadPhotoNum() + "");
        viewHolder.failTime.setText(recordData.get(position).getFailTime());
        viewHolder.sealTime.setText(recordData.get(position).getSealTime());
        viewHolder.sealAddress.setText(recordData.get(position).getSealAddress());
        int i = recordData.get(position).getApproveStatus();
        if (i == 5) {
            viewHolder.close.setText("已关闭");
            viewHolder.close.setEnabled(false);
            viewHolder.close.setTextColor(context.getResources().getColor(R.color.gray_text));
            viewHolder.close.setBackgroundResource(R.drawable.record_bt_off);
        } else {
            viewHolder.close.setText("关闭单据");
            viewHolder.close.setEnabled(true);
            viewHolder.close.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.close.setBackgroundResource(R.drawable.login_circle_bg);
        }
        viewHolder.ygcs.setTextColor(context.getResources().getColor(R.color.black));
        viewHolder.sealCount.setTextColor(context.getResources().getColor(R.color.gray_text));
        if (recordData.get(position).getSealCount() > recordData.get(position).getApplyCount()) {
            viewHolder.ygcs.setTextColor(ContextCompat.getColor(context, com.nestia.biometriclib.R.color.text_red));
            viewHolder.sealCount.setTextColor(ContextCompat.getColor(context, com.nestia.biometriclib.R.color.text_red));
        }

        //关闭单据
        viewHolder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordData.get(position).getUploadPhotoNum() == 0){
                    CommonDialog commonDialog = new CommonDialog(context,"提示",
                            "此单据还未上传盖章后拍照,将无法在记录详情查看到盖章文件,是否继续关闭？","关闭");
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

        //记录二维码
        viewHolder.qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecordQrCodeActivity.class);
                intent.putExtra("applyId",recordData.get(position).getId());
                intent.putExtra("cause",recordData.get(position).getCause());
                context.startActivity(intent);
            }
        });
//
//        //查看地位地址地图
//        viewHolder.address_ll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                search(position);
//                Intent intent = new Intent(context, MapViewActivity.class);
//                context.startActivity(intent);
//            }
//        });
        return view;
    }

    private void search(int position){
        //创建地理编码检索
        GeoCoder coder = GeoCoder.newInstance();
        //创建地理编码检索监听
        @SuppressLint("DefaultLocale")
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR){
                    //没有检索到结果
                    Toast.makeText(context, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                //获取地理编码结果
                latitude = geoCodeResult.getLocation().latitude;
                longitude = geoCodeResult.getLocation().longitude;
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        };
        //设置监听
        coder.setOnGetGeoCodeResultListener(listener);
        //发起地理编码检索
        coder.geocode(new GeoCodeOption()
                .city("深圳")
                .address(recordData.get(position).getSealAddress()));
        //释放地理编码检索
        coder.destroy();
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
                                EventBus.getDefault().post(new MessageEvent("关闭刷新","关闭刷新"));
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
        private TextView ygcs;
        private ImageView qrcode;
        private LinearLayout address_ll;
    }

}
