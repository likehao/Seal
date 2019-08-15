package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.UseSealDetailData;

public class BarAdapter extends RecyclerView.Adapter<BarAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<UseSealDetailData> list;
    private Context context;

    public BarAdapter(List<UseSealDetailData> list, Context context) {
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.progressBar.setProgress(list.get(position).getStampCount());
        viewHolder.name.setText(list.get(position).getOrgStructureName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            progressBar = itemView.findViewById(R.id.progress);
        }
    }
}
