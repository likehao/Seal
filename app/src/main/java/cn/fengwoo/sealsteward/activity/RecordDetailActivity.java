package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.youth.banner.Banner;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.GlideImageLoader;

/**
 * 记录详情
 */
public class RecordDetailActivity extends BaseActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.back_tv)
    TextView back_tv;
    @BindView(R.id.seal_photo_rl)
    RelativeLayout seal_photo_rl;
    @BindView(R.id.detail_checkBox)
    CheckBox detail_checkBox;
    @BindView(R.id.record_banner)
    Banner record_banner;
    private List<Integer> imageList;
    boolean check = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_details);

        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void initView() {
        scan_ll.setVisibility(View.GONE);
        title_tv.setText("详情");
        set_back_ll.setVisibility(View.VISIBLE);
        back_tv.setText("盖章记录");
        seal_photo_rl.setOnClickListener(this);
        detail_checkBox.setOnCheckedChangeListener(this);
        //设置图片加载器
        record_banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        imageList = new ArrayList<>();
        imageList.add((R.drawable.photo_03));
        imageList.add((R.drawable.photo_03));
        imageList.add((R.drawable.photo_03));
        record_banner.setImages(imageList);
        record_banner.start();
    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.seal_photo_rl:
                if (!check){
                    record_banner.setVisibility(View.VISIBLE);
                    check = true;
                }else {
                    record_banner.setVisibility(View.GONE);
                    check = false;
                }
                break;
        }
    }
    //判断checkBox是否被点击来展示banner
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked){
            record_banner.setVisibility(View.VISIBLE);
        }else {
            record_banner.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        record_banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        record_banner.stopAutoPlay();
    }
}
