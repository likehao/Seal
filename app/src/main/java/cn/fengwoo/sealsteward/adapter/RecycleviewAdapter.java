package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.List;
import cn.fengwoo.sealsteward.R;

/**
 * 意见反馈上传图片使用的适配
 */
public class RecycleviewAdapter extends RecyclerView.Adapter<RecycleviewAdapter.ViewHolder>{
    private List<Uri> uriList;
    private Context context;
    public void setData(List<Uri> list,Context context){
        this.uriList = list;
        this.context = context;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.uri_item,parent,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i) {
        Glide.with(context).load(uriList.get(i)).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return uriList == null ? 0 : uriList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);

        }
    }
}
