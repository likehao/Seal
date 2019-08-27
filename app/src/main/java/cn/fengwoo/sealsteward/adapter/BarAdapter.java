package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.UseSealDetailData;

public class BarAdapter extends RecyclerView.Adapter<BarAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<UseSealDetailData.orgStructureStatisticVoList> list;
    private Context context;
    ArrayList<Integer> arrayList = new ArrayList<>();

    public BarAdapter(List<UseSealDetailData.orgStructureStatisticVoList> list, Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        ViewHolder viewHolder = new ViewHolder(layoutInflater.inflate(R.layout.bar_item, parent, false));
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //获取手机屏幕高度
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int high = height - 1000;
        //将所有次数加入到集合
        arrayList.add(list.get(position).getStampCount());
        //初始化最大值
        int maxValue = 1000;
        int firstValue = arrayList.get(0);
        if (firstValue == 0) {
            maxValue = 10;
        } else {
            if (firstValue < 100) {
                maxValue = (int) (firstValue * 1.5);
            } else {
                maxValue = (int) (firstValue * 1.1);
            }
        }

//        //获取值得最大值
//        Integer max = Collections.max(arrayList);
//        viewHolder.progressBar.setMax(max+10);
//        viewHolder.progressBar.setProgress(list.get(position).getStampCount());  //次数占比

        //设置值高度
        RelativeLayout.LayoutParams barLayoutParams = (RelativeLayout.LayoutParams) viewHolder.bar_ll.getLayoutParams();
        //设置view的最大高度
        barLayoutParams.height = maxValue;
        //获取每个柱状图的值
        int valueCount = list.get(position).getStampCount();
        //计算值所需要的高度
        int hei = valueCount * 400 / maxValue;

        viewHolder.name.setText(list.get(position).getOrgStructureName());    //组织架构名字
        viewHolder.count.setText(list.get(position).getStampCount() + "");      //次数

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) viewHolder.bar.getLayoutParams();
        //设置每条item的高度
        linearParams.height = hei;
        viewHolder.bar.setBackgroundResource(R.drawable.bar_statistics);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ProgressBar progressBar;
        private TextView count;
        private ImageView bar;
        private LinearLayout bar_ll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
//            progressBar = itemView.findViewById(R.id.progress);
            count = itemView.findViewById(R.id.stampCount_tv);
            bar = itemView.findViewById(R.id.barView);
            bar_ll = itemView.findViewById(R.id.bar_ll);
        }
    }
}
