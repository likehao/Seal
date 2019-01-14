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
import cn.fengwoo.sealsteward.entity.UserData;

public class UserAdapter extends BaseAdapter {

    private List<UserData> userDatas;
    private Context context;

    public UserAdapter(List<UserData> userData,Context context){
        this.userDatas = userData;
        this.context = context;
    }
    @Override
    public int getCount() {
        return userDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return userDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.user_list_item,null);
            viewHolder.photo_iv = view.findViewById(R.id.userPhoto_civ);
            viewHolder.name_tv = view.findViewById(R.id.userName_tv);
            viewHolder.phone_tv = view.findViewById(R.id.userPhone_tv);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.photo_iv.setImageResource(userDatas.get(position).getPhoto());
            viewHolder.name_tv.setText(userDatas.get(position).getName());
            viewHolder.phone_tv.setText(userDatas.get(position).getPhone());
        return view;
    }

    class ViewHolder{
        private ImageView photo_iv;
        private TextView name_tv;
        private TextView phone_tv;
    }
}
