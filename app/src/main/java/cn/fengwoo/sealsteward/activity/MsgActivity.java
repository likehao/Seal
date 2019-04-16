package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 消息
 */
public class MsgActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.system_msg_tv)
    TextView systemMsgTv;
    @BindView(R.id.apply_msg_tv)
    TextView applyMsgTv;
    @BindView(R.id.alarm_msg_tv)
    TextView alarmMsgTv;
    @BindView(R.id.system_msg_ll)
    LinearLayout system_msg_ll;
    @BindView(R.id.apply_msg_ll)
    LinearLayout apply_msg_ll;
    @BindView(R.id.alarm_msg_ll)
    LinearLayout alarm_msg_ll;
    @BindView(R.id.message_smt)
    SmartRefreshLayout message_smt;
    private Intent intent;
    String sysId, applyId, alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        ButterKnife.bind(this);
        setListener();
        getMessage();
        message_smt.setEnableLoadMore(false);  //是否启用上拉加载功能
        message_smt.autoRefresh();//自动刷新
    }

    private void setListener() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("消息");
        set_back_ll.setOnClickListener(this);
        system_msg_ll.setOnClickListener(this);
        apply_msg_ll.setOnClickListener(this);
        alarm_msg_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.system_msg_ll:
                setMsgId(sysId);
                break;
            case R.id.apply_msg_ll:
                setMsgId(applyId);
                break;
            case R.id.alarm_msg_ll:
                setMsgId(alarmId);
                break;
        }
    }


    private void setMsgId(String msgId) {
        if (msgId != null) {
            intent = new Intent(this, MessageListActivity.class);
            intent.putExtra("msgId", msgId);
            startActivity(intent);
        }else {
            Toast.makeText(this,"暂无消息",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取消息
     */

    @SuppressLint("SetTextI18n")
    private void getMessage() {
        message_smt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                HttpUtil.sendDataAsync(MsgActivity.this, HttpUrl.MESSAGE, 1, null, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<List<MessageData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<MessageData>>>() {
                        }
                                .getType());
                        assert responseInfo != null;
                        if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                            for (MessageData messageData : responseInfo.getData()) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //显示消息数
                                            int type = messageData.getType();
                                            int msgNum = messageData.getUnreadCount();
                                            String id = messageData.getId();
                                            if (messageData.getType() == 1) {
                                                sysId = id;    //传递ID到消息列表
                                                if (msgNum != 0) {
                                                    systemMsgTv.setVisibility(View.VISIBLE);   //系统消息
                                                    systemMsgTv.setText(msgNum + "");
                                                }else {
                                                    systemMsgTv.setVisibility(View.GONE);
                                                }
                                            } else if (messageData.getType() == 2) {
                                                applyId = id;
                                                if (msgNum != 0) {
                                                    applyMsgTv.setVisibility(View.VISIBLE);   //审批通知
                                                    applyMsgTv.setText(msgNum + "");
                                                }else {
                                                    applyMsgTv.setVisibility(View.GONE);
                                                }
                                            } else if (messageData.getType() == 3) {
                                                alarmId = id;
                                                if (msgNum != 0) {
                                                    alarmMsgTv.setVisibility(View.VISIBLE);   //告警消息
                                                    alarmMsgTv.setText(msgNum + "");
                                                }else {
                                                    alarmMsgTv.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                    });

                            }

                            refreshLayout.finishRefresh(); //刷新完成
                            Log.e("TAG","获取消息成功!!!!!!!!!!!!!!!!");

                        } else {
                            refreshLayout.finishRefresh(); //刷新完成
                            Log.e("TAG",responseInfo.getMessage());
                        }

                    }
                });

            }
        });
        message_smt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();  //加载完成
                refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        message_smt.autoRefresh(); //自动刷新
    }
}
