package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.UseSealApplyActivity;
import cn.fengwoo.sealsteward.adapter.WaitApplyAdapter;
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
 * 待审批fragment
 */
public class FirstMyApplyFragment extends Fragment implements AdapterView.OnItemClickListener{
    private View view;
    @BindView(R.id.wait_apply_lv)
    ListView wait_apply_lv;
    private WaitApplyAdapter waitApplyAdapter;
    private List<WaitApplyData> waitApplyDataList;
    @BindView(R.id.wait_apply_smartRL)
    SmartRefreshLayout wait_apply_smartRL;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.first_my_apply_fragment,container,false);
        ButterKnife.bind(this,view);
        initData();
        setSmartRefreshLayout();
        return view;
    }

    private void initData() {
        waitApplyDataList = new ArrayList<>();
        wait_apply_lv.setOnItemClickListener(this);
    }

    /**
     * 刷新加载
     */
    public void setSmartRefreshLayout(){
        wait_apply_smartRL.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                ApplyListData applyListData = new ApplyListData();
                applyListData.setCurPage(0);
                applyListData.setHasExportPdf(false);
                applyListData.setHasPage(true);
                applyListData.setPageSize(5);
                applyListData.setParam(0);
                HttpUtil.sendDataAsync(getActivity(), HttpUrl.APPLYLIST, 2, null, applyListData, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG",e+"错误错误错误错误错误错误!!!!!!!!!!!!!!!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<List<GetApplyListBean>> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<List<GetApplyListBean>>>(){}
                        .getType());
                        if (responseInfo.getData() != null && responseInfo.getCode() == 0){
                            for(GetApplyListBean app : responseInfo.getData()) {
                                //时间戳转为时间
                                String expireTime = DateUtils.getDateString(Long.parseLong(app.getExpireTime())); //失效时间
                                String applyTime = DateUtils.getDateString(Long.parseLong(app.getApplyTime()));  //申请时间
                                waitApplyDataList.add(new WaitApplyData(app.getApplyCause(),app.getSealName()
                                        ,expireTime,app.getApplyCount(),applyTime));
                            }
                            //请求数据
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    waitApplyAdapter = new WaitApplyAdapter(getActivity(),waitApplyDataList,1);
                                    wait_apply_lv.setAdapter(waitApplyAdapter);
                                    waitApplyAdapter.notifyDataSetChanged(); //刷新数据
                                    refreshLayout.finishRefresh(); //刷新完成
                                }
                            });

                        }else {
                            refreshLayout.finishRefresh(); //刷新完成
                            Looper.prepare();
                            Toast.makeText(getActivity(),responseInfo.getMessage(),Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                });

            }
        });
        wait_apply_smartRL.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();  //加载完成
                refreshLayout.finishLoadMoreWithNoMoreData();  //全部加载完成,没有数据了调用此方法
            }
        });

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), UseSealApplyActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        wait_apply_smartRL.autoRefresh();   //自动刷新
    }
}
