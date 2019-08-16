package cn.fengwoo.sealsteward.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
        ViewHolder viewHolder = new ViewHolder(layoutInflater.inflate(R.layout.bar_item,parent,false));
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        arrayList.add(list.get(position).getStampCount());
        Integer max = Collections.max(arrayList);
        viewHolder.progressBar.setMax(max+10);
        viewHolder.progressBar.setProgress(list.get(position).getStampCount());  //次数占比
        viewHolder.name.setText(list.get(position).getOrgStructureName());    //组织架构名字
        viewHolder.count.setText(list.get(position).getStampCount()+"");      //次数

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ProgressBar progressBar;
        private TextView count;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            progressBar = itemView.findViewById(R.id.progress);
            count = itemView.findViewById(R.id.stampCount_tv);
        }
    }
}
