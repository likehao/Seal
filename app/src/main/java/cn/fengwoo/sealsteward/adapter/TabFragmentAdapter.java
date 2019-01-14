package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class TabFragmentAdapter extends FragmentStatePagerAdapter {

    private List<String> titileList;
    private List<Fragment> fmList;
    private Context context;
    public TabFragmentAdapter(FragmentManager fm,Context context,List<Fragment> fmList,List<String> titileList) {
        super(fm);
        this.context = context;
        this.titileList = titileList;
        this.fmList = fmList;
    }

    @Override
    public Fragment getItem(int i) {
        return fmList.get(i);
    }

    @Override
    public int getCount() {
        if(fmList==null){
            return 0;
        }else{
            return fmList.size();
        }
    }
    /**
     * 此方法用来显示tab上的名字
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return titileList.get(position);
    }

}
