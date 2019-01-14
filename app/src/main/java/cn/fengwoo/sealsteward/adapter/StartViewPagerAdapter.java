package cn.fengwoo.sealsteward.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;



public class StartViewPagerAdapter extends PagerAdapter {

    //界面列表
    private List<View> views;
    public StartViewPagerAdapter(List<View> views) {
        this.views = views;
    }

    //获得当前界面数
    @Override
    public int getCount() {
        if (views != null){
            return views.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup arg0, int arg1) {
        (arg0).addView(views.get(arg1), 0);
        return views.get(arg1);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
