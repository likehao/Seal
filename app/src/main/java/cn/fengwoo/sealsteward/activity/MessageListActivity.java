package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.MessageAdapter;
import cn.fengwoo.sealsteward.adapter.WaitApplyAdapter;
import cn.fengwoo.sealsteward.bean.ApplyListData;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.bean.MessageDeatileBean;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.bean.SeeRecordDetailBean;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.WaitApplyData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 消息列表
 */
public class MessageListActivity extends BaseActivity{

    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.message_list_smt)
    SmartRefreshLayout message_list_smt;
    @BindView(R.id.message_list_lv)
    ListView message_list_lv;
    List<MessageDeatileBean> list;
    private MessageAdapter messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        ButterKnife.bind(this);
        initView();
        getMsgDetail();
        message_list_smt.autoRefresh(); // 自动刷新
    }

    private void initView() {
        title_tv.setText("消息列表");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = new ArrayList<>();

    }

    private void getMsgDetail(){
        message_list_smt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list.clear();
                SeeRecordDetailBean msgData = new SeeRecordDetailBean();
                msgData.setCurPage(1);
                msgData.setHasPage(true);
                msgData.setPageSize(10);
                Intent intent = getIntent();
                String param = intent.getStringExtra("msgId");
                msgData.setParam(param);
                HttpUtil.sendDataAsync(MessageListActivity.this, HttpUrl.MESSAGEDETAIL, 2, null, msgData, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG",e+"错误错误错误错误错误错误!!!!!!!!!!!!!!!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<List<MessageDeatileBean>> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<List<MessageDeatileBean>>>(){}
                                .getType());
                        if (responseInfo.getData() != null && responseInfo.getCode() == 0){
                            for(MessageDeatileBean app : responseInfo.getData()) {
                                //时间戳转为时间
                                String expireTime = DateUtils.getDateString(Long.parseLong(app.getCreateTime())); //失效时间
                                list.add(new MessageDeatileBean(expireTime,app.getTitle(),app.getContent()));
                            }
                            //请求数据
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter = new MessageAdapter(list,MessageListActivity.this);
                                    message_list_lv.setAdapter(messageAdapter);
                                    messageAdapter.notifyDataSetChanged(); //刷新数据
                                    refreshLayout.finishRefresh(); //刷新完成
                                    Log.e("TAG","获取消息列表成功!!!!!!!!!!!!!!!!!!!!");
                                    updateReadMsg(param);
                                }
                            });

                        }else {
                            refreshLayout.finishRefresh(); //刷新完成
                            Looper.prepare();
                            showToast(responseInfo.getMessage());
                            Looper.loop();
                        }

                    }
                });

            }
        });
        message_list_smt.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();  //加载完成
                refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
            }
        });
    }

    /**
     * 更新已读消息
     */
    private void updateReadMsg(String msgId){
        HashMap<String ,String> hashMap = new HashMap<>();
        hashMap.put("messageId",msgId);
        HttpUtil.sendDataAsync(MessageListActivity.this, HttpUrl.UPDATEREADMSG, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}
                .getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()){
                        Log.e("TAG","消息已被阅读成功!!!!!!!!!");
                    }
                }else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

}
