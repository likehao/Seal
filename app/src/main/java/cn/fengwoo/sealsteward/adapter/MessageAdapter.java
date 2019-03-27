package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageDeatileBean;

public class MessageAdapter extends BaseAdapter {

    private List<MessageDeatileBean> list;
    private Context context;

    public MessageAdapter(List<MessageDeatileBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.msglist,null);
            viewHolder.msgTime = convertView.findViewById(R.id.msg_list_time);
            viewHolder.title = convertView.findViewById(R.id.msg_title_tv);
            viewHolder.msgContent = convertView.findViewById(R.id.msg_list_content);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
            viewHolder.msgTime.setText(list.get(position).getCreateTime());
            viewHolder.title.setText(list.get(position).getTitle());
            viewHolder.msgContent.setText(list.get(position).getContent());
        return convertView;
    }

    class ViewHolder{
        private TextView msgTime;
        private TextView title;
        private TextView msgContent;
    }
}
