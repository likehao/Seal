package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.RecordDetailActivity;
import cn.fengwoo.sealsteward.activity.SeeRecordActivity;
import cn.fengwoo.sealsteward.adapter.RecordAdapter;
import cn.fengwoo.sealsteward.entity.RecordData;
import cn.fengwoo.sealsteward.view.HeaderAndFooter;

/**
 * 记录
 */
public class RecordFragment extends Fragment implements AdapterView.OnItemClickListener{
    private View view;
    private RecordAdapter recordAdapter;
    private List<RecordData>list;
    private Intent intent;
    @BindView(R.id.record_refreshLayout)
    RefreshLayout record_refreshLayout;
    @BindView(R.id.record_lv)
    ListView record_lv;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.record_fragment,container,false);

        ButterKnife.bind(this,view);
        initData();
        setListener();
        setSmartRefreshLayout();
        return view;
    }

    private void initData() {
        list = new ArrayList<>();
        list.add(new RecordData("公章","习大大","11/09 12:00","深圳龙岗华南城"));
        list.add(new RecordData("公章","习大大","11/09 12:00","深圳龙岗华南城"));
        list.add(new RecordData("公章","习大大","11/09 12:00","深圳龙岗华南城"));
        list.add(new RecordData("公章","习大大","11/09 12:00","深圳龙岗华南城"));
    }

    /**
     * 刷新加载
     */
    public void setSmartRefreshLayout(){
        recordAdapter = new RecordAdapter(list,getActivity());
        record_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //请求数据
                record_lv.setAdapter(recordAdapter);
                refreshLayout.autoRefresh(); //自动刷新
                refreshLayout.finishRefresh(); //刷新完成
            }
        });
        record_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore();  //加载完成
            }
        });
      /*  //设置 Header 为 贝塞尔雷达 样式
        record_refreshLayout.setRefreshHeader(new BezierRadarHeader(getActivity()).setEnableHorizontalDrag(true));
        //设置 Footer 为 球脉冲 样式
        record_refreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));*/
    }
    private void setListener() {
        record_lv.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
     //   intent = new Intent(getActivity(), RecordDetailActivity.class);
        intent = new Intent(getActivity(), SeeRecordActivity.class);
        startActivity(intent);
    }
}
