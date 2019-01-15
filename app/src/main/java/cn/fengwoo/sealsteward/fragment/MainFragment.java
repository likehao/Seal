package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.youth.banner.Banner;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.ApplyCauseActivity;
import cn.fengwoo.sealsteward.activity.ApprovalRecordActivity;
import cn.fengwoo.sealsteward.activity.MyApplyActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.utils.GlideImageLoader;

public class MainFragment extends Fragment implements View.OnClickListener{

    private View view;
    //设置图片资源:url或本地资源
    String[] images= new String[] {"url","url"};
    String[] titles=new String[]{"我是广告，全场2折起","全场2折起"};
    //设置图片集合
    private List<Integer> imageViews = new ArrayList<Integer>();
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.sealDevice_rl)
    RelativeLayout sealDevice_rl;
    @BindView(R.id.needSeal_rl)
    RelativeLayout needSeal_rl;
    @BindView(R.id.useSealApply_rl)
    RelativeLayout useSealApply_rl;
    @BindView(R.id.approval_record_rl)
    RelativeLayout approval_record_rl;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home_fragment,container,false);

        ButterKnife.bind(this,view);
        initBanner();
        setListener();
        return view;
    }
    private void setListener() {
        sealDevice_rl.setOnClickListener(this);
        needSeal_rl.setOnClickListener(this);
        useSealApply_rl.setOnClickListener(this);
        approval_record_rl.setOnClickListener(this);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 广告轮播图
     */
    private void initBanner(){
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        imageViews.add(R.drawable.photo_03);
        imageViews.add(R.drawable.ic_launcher);
        imageViews.add(R.drawable.photo_03);
        banner.setImages(imageViews);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sealDevice_rl:
                intent = new Intent(getActivity(), NearbyDeviceActivity.class);
                startActivity(intent);
                break;
            case R.id.needSeal_rl:
                intent = new Intent(getActivity(), ApplyCauseActivity.class);
                startActivity(intent);
                break;
            case R.id.useSealApply_rl:
                intent = new Intent(getActivity(), MyApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.approval_record_rl:
                intent = new Intent(getActivity(), ApprovalRecordActivity.class);
                startActivity(intent);
                break;
        }
    }
    /**
     * 增加体验
     */
    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

}
