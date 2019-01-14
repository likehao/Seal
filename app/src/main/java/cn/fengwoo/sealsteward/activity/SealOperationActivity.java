package cn.fengwoo.sealsteward.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

/**
 * 印章操作
 */
public class SealOperationActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.add_iv)
    ImageView add_iv;
    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.back_tv)
    TextView back_tv;
    @BindView(R.id.seal_viewPager)
    ViewPager seal_viewPager;
    @BindView(R.id.viewGroup)
    LinearLayout viewGroup;
    @BindView(R.id.useSeal_rl)
    RelativeLayout useSeal_rl;
    private List<View> viewPages = new ArrayList<>();
    //定义一个ImageVIew数组，来存放生成的小圆点
    private ImageView[] imageViews;
    private PagerAdapter adapter;
    private View page1,page2;
    //new一个圆点视图
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_operation);

        ButterKnife.bind(this);
        initView();
        setListener();
        initPageAdapter();
        initPointer();

    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        scan_ll.setVisibility(View.GONE);
        add_iv.setVisibility(View.GONE);
        title_tv.setText("操作");
        back_tv.setText("印章");
    }

    /**
     * 此处布局已设置为不可见
     */
    private void initPageAdapter(){
        LayoutInflater inflater = LayoutInflater.from(this);
        page1 = inflater.inflate(R.layout.seal_viewpager_one,null);
        page2 = inflater.inflate(R.layout.seal_viewpager_two,null);
        //添加到集合
        viewPages.add(page1);
        viewPages.add(page2);
        adapter = new PagerAdapter() {
            //获取当前界面个数
            @Override
            public int getCount() {
                return viewPages.size();
            }
            //判断是否由对象生成页面
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
            //移除一个view
            @Override
            public void destroyItem(ViewGroup container, int position,Object object) {
                container.removeView(viewPages.get(position));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = viewPages.get(position);
                container.addView(view);
                return view;
            }
        };
        seal_viewPager.setAdapter(adapter);
    }

    private void setListener(){
        set_back_ll.setOnClickListener(this);
        useSeal_rl.setOnClickListener(this);
        seal_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //页面滑行时执行
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            //页面滑行完成后执行
            @Override
            public void onPageSelected(int position) {
                //判断当前是在那个page，就把对应下标的ImageView原点设置为选中状态的图片
                for (int i = 0; i < imageViews.length; i++ ){
                    imageViews[position].setImageResource(R.drawable.viewpage_circle);
                    if (position != i){
                        imageViews[i].setImageResource(R.drawable.viewpage_circle2);
                    }
                }
            }
            //监听页面的状态，0--静止 1--滑动  2--滑动完成
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //根据页面个数添加圆点指示器
    private void initPointer(){
        //根据页面个数来new多少个
        imageViews = new ImageView[viewPages.size()];
        for (int i= 0; i < imageViews.length; i++){
            imageView = new ImageView(this);
       /*     LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15,15);
            params.setMargins(0,0,0,0);
            params.leftMargin = 20;*/
            //设置控件宽高
            imageView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //设置控件padding属性
            imageView.setPadding(20,0,20,0);
            imageViews[i] = imageView;
            //初始化第一个page页面的图片的圆点为选中状态
            if (i == 0){
                imageViews[0].setImageResource(R.drawable.viewpage_circle);
            }else {
                imageViews[i].setImageResource(R.drawable.viewpage_circle2);
            }
            viewGroup.addView(imageViews[i]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.useSeal_rl:
                Intent intent = new Intent(SealOperationActivity.this,ApplyCauseActivity.class);
                startActivity(intent);
                break;
        }
    }
}
