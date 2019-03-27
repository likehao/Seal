package cn.fengwoo.sealsteward.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.MessageListActivity;
import cn.fengwoo.sealsteward.activity.WaitMeAgreeActivity;
import cn.fengwoo.sealsteward.bean.MessageData;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 消息
 */
public class MessageFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.system_msg_tv)
    TextView systemMsgTv;
    @BindView(R.id.apply_msg_tv)
    TextView applyMsgTv;
    @BindView(R.id.alarm_msg_tv)
    TextView alarmMsgTv;
    @BindView(R.id.waitApply_msg_tv)
    TextView waitApplyMsgTv;
    private View view;
    @BindView(R.id.system_msg_ll)
    LinearLayout system_msg_ll;
    @BindView(R.id.apply_msg_ll)
    LinearLayout apply_msg_ll;
    @BindView(R.id.alarm_msg_ll)
    LinearLayout alarm_msg_ll;
    @BindView(R.id.waitApply_msg_ll)
    LinearLayout waitApply_msg_ll;
    @BindView(R.id.message_smt)
    SmartRefreshLayout message_smt;
    private Intent intent;
    String sysId, applyId, alarmId, waitId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_fragment, container, false);

        ButterKnife.bind(this, view);
        setListener();
        getMessage();
        message_smt.setEnableLoadMore(false);  //是否启用上拉加载功能
        message_smt.autoRefresh();//自动刷新

        return view;
    }

    private void setListener() {
        system_msg_ll.setOnClickListener(this);
        apply_msg_ll.setOnClickListener(this);
        alarm_msg_ll.setOnClickListener(this);
        waitApply_msg_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.system_msg_ll:
                setMsgId(sysId);
                break;
            case R.id.apply_msg_ll:
                setMsgId(applyId);
                break;
            case R.id.alarm_msg_ll:
                setMsgId(alarmId);
                break;
            case R.id.waitApply_msg_ll:
                setMsgId(waitId);
                break;
        }
    }

    private void setMsgId(String msgId) {
        if (msgId != null) {
            intent = new Intent(getActivity(), MessageListActivity.class);
            intent.putExtra("msgId", msgId);
            startActivity(intent);
        }else {
            Toast.makeText(getActivity(),"暂无消息",Toast.LENGTH_SHORT).show();
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

                HttpUtil.sendDataAsync(getActivity(), HttpUrl.MESSAGE, 1, null, null, new Callback() {
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
                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //显示消息数
                                        int type = messageData.getType();
                                        int msgNum = messageData.getUnreadCount();
                                        String id = messageData.getId();
                                        if (messageData.getType() == 1) {
                                            sysId = id;    //传递ID到消息列表
                                            if (msgNum != 0) {
                                                systemMsgTv.setVisibility(View.VISIBLE);
                                                systemMsgTv.setText(msgNum + "");
                                            }
                                        } else if (messageData.getType() == 2) {
                                            applyId = id;
                                            if (msgNum != 0) {
                                                applyMsgTv.setVisibility(View.VISIBLE);
                                                applyMsgTv.setText(msgNum + "");
                                            }
                                        } else if (messageData.getType() == 3) {
                                            alarmId = id;
                                            if (msgNum != 0) {
                                                alarmMsgTv.setVisibility(View.VISIBLE);
                                                alarmMsgTv.setText(msgNum + "");
                                            }
                                        } else if (messageData.getType() == 4) {
                                            waitId = id;
                                            if (msgNum != 0) {
                                                applyMsgTv.setVisibility(View.VISIBLE);
                                                applyMsgTv.setText(msgNum + "");
                                            }
                                        }
                                    }
                                });
                            }

                            refreshLayout.finishRefresh(); //刷新完成
                            Log.e("TAG","获取消息成功!!!!!!!!!!!!!!!!");

                        } else {
                            refreshLayout.finishRefresh(); //刷新完成
                            Looper.prepare();
                            Toast.makeText(getActivity(), responseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
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

    /**
     * 处理注册事件
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        if (s.equals("10")){
            message_smt.autoRefresh();  //自动刷新
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);   //注册Eventbus
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);  //解除注册
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        message_smt.autoRefresh(); //自动刷新
    }
}
