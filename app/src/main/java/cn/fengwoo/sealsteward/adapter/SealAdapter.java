package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.database.SealItemBean;

public class SealAdapter extends BaseAdapter {

    private List<SealItemBean> Datas;
    private Context context;

    public SealAdapter(List<SealItemBean> datas, Context context) {
        Datas = datas;
        this.context = context;
    }

    @Override
    public int getCount(    ) {
        return Datas.size();
    }

    @Override
    public Object getItem(int i) {
        return Datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.seal_item,null);
            viewHolder.imageView = view.findViewById(R.id.seal_imageView);
            viewHolder.textView = view.findViewById(R.id.seal_textView);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.imageView.setImageResource(Datas.get(i).itemImageResId);
            viewHolder.textView.setText(Datas.get(i).itemContent);
            return view;
    }
    class ViewHolder{
        private ImageView imageView;
        private TextView textView;
    }
}
