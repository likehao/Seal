package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.CardTicketBean;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CardTicketDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 我的卡券
 */
public class MyCardTicketActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.card_ticket_lv)
    ListView listView;
    private CommonAdapter commonAdapter;
    private ArrayList<CardTicketBean> list;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record;
    private ResponseInfo<SealInfoData> responseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card_ticket);

        ButterKnife.bind(this);
        initView();
        getCardTicket();
    }

    private void initView() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title_tv.setText("我的卡券");
        list = new ArrayList<>();
    }

    /**
     * 获取卡券
     */
    private void getCardTicket() {
        showLoadingView();
        HttpUtil.sendDataAsync(this, HttpUrl.TICKET, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cancelLoadingView();
                Log.e("TAG", e + "获取卡券错误错误错误错误错误错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                cancelLoadingView();
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<CardTicketBean>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<CardTicketBean>>>() {
                }
                        .getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                            for (CardTicketBean data : responseInfo.getData()) {
                                list.add(new CardTicketBean(data.getAmountOfMoney(), data.getContent(),
                                        data.getReceiveStatus(), data.getId(), data.getRechargeType()));
                            }
                            no_record.setVisibility(View.GONE);
                            setData();
                        }

                    }
                });
            }
        });
    }

    /**
     * 设置数据
     */
    private void setData() {
        commonAdapter = new CommonAdapter<CardTicketBean>(MyCardTicketActivity.this, list, R.layout.card_ticket_item) {
            @Override
            public void convert(ViewHolder viewHolder, CardTicketBean cardTicketBean, int position) {
                LinearLayout item = viewHolder.getView(R.id.card_ticket_ll);

                viewHolder.setText(R.id.card_package_tv, cardTicketBean.getContent());
                if (cardTicketBean.getRechargeType() == 0) {   //判断是否是赠送
                    viewHolder.setVisible(R.id.gift_iv, true);
                    viewHolder.setVisible(R.id.card_money_tv, false);
                } else {
                    viewHolder.setText(R.id.card_money_tv, "￥" + cardTicketBean.getAmountOfMoney());
                    viewHolder.setVisible(R.id.gift_iv, false);
                }

                item.setEnabled(true);
                viewHolder.setText(R.id.card_use_tv, "未使用");
                viewHolder.setTextColorRes(R.id.card_use_tv, R.color.orange);
                viewHolder.setBackgroundRes(R.id.card_ticket_iv, R.drawable.card_money);
                viewHolder.setTextColorRes(R.id.card_package_tv, R.color.black_6);

                if (cardTicketBean.getReceiveStatus()) {
                    viewHolder.setText(R.id.card_use_tv, "已使用");
                    viewHolder.setTextColorRes(R.id.card_use_tv, R.color.card_gray);
                    viewHolder.setBackgroundRes(R.id.card_ticket_iv, R.drawable.card_money_use);
                    viewHolder.setTextColorRes(R.id.card_package_tv, R.color.card_gray);
                    item.setEnabled(false);
                }

                /**
                 * 选择卡券使用
                 */
                viewHolder.setOnClickListener(R.id.card_ticket_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyCardTicketActivity.this, SelectSealActivity.class);
                        intent.putExtra("cardId", list.get(position).getId());
                        startActivityForResult(intent, 123);
                    }
                });
            }

        };
        commonAdapter.notifyDataSetChanged();
        listView.setAdapter(commonAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    /**
     * 使用卡券充值
     *
     * @param sealId
     * @param cardId
     */
    private void rechargeTicket(String sealId, String cardId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", cardId);
        hashMap.put("sealId", sealId);
        HttpUtil.sendDataAsync(this, HttpUrl.RECHARGETICKET, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "使用卡券错误错误错误错误错误错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    list.clear();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getCardTicket();
                            getSealInfo(sealId);
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取充值的印章信息
     *
     * @param sealId
     */
    private void getSealInfo(String sealId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealIdOrMac", sealId);
        hashMap.put("type", "1");
        HttpUtil.sendDataAsync(MyCardTicketActivity.this, HttpUrl.SEAL_INFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"错误错误错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<SealInfoData>>() {
                }.getType());

                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showCardTicketDialog();
                        }
                    });
                }
            }

        });
    }

    /**
     * 显示充值完成的卡券dialog
     */
    private void showCardTicketDialog() {
        CardTicketDialog cardTicketDialog = new CardTicketDialog(this);
        cardTicketDialog.sealName.setText(responseInfo.getData().getName());
        cardTicketDialog.sealTime.setText("服务费过期时间：" + Utils.getDateToString(responseInfo.getData().getServiceTime(), "yyyy-MM-dd HH:mm:ss"));
        cardTicketDialog.showDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取选择的印章ID
        if (requestCode == 123) {
            if (data != null) {
                String sealId = data.getStringExtra("id");
                String cardId = data.getStringExtra("cardId");
                rechargeTicket(sealId, cardId);
            }
        }
    }
}
