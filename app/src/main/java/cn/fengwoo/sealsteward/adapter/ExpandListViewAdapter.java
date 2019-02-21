package cn.fengwoo.sealsteward.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.FirstModel;
import cn.fengwoo.sealsteward.entity.SecondModel;
import cn.fengwoo.sealsteward.utils.CustomExpandableListView;

/**
 * 树形列表适配器
 */
public class ExpandListViewAdapter extends BaseExpandableListAdapter {
    private List<FirstModel> mListData;
    private LayoutInflater mInflate;
    private Context context;

    public ExpandListViewAdapter(List<FirstModel> mListData, Context context) {
        this.mListData = mListData;
        this.context = context;
        this.mInflate = LayoutInflater.from(context);
    }

    //获得组数
    @Override
    public int getGroupCount() {
        return mListData.size();
    }

    //获得组的子项个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    //获得某组数据
    @Override
    public Object getGroup(int groupPosition) {
        return mListData.get(groupPosition);
    }

    // 获得指定子项
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListData.get(groupPosition).getListSecondModel().get(childPosition);
    }

    // 获取组ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 获取子项ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    @Override
    public boolean hasStableIds() {
        return false;
    }

    // 获取组视图
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final FirstHolder holder;
        if (convertView == null) {
            holder = new FirstHolder();
            convertView = mInflate.inflate(R.layout.item_expand_lv_first, parent, false);
            holder.tv = convertView.findViewById(R.id.tv);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            holder.swipeMenuLayout = convertView.findViewById(R.id.swipeMenuLayout);
            convertView.setTag(holder);
        } else {
            holder = (FirstHolder) convertView.getTag();
        }
        holder.tv.setText(mListData.get(groupPosition).getTitle());
        //删除点击事件
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除list中对应的数据
                mListData.remove(groupPosition);
                //重新绑定数据
                notifyDataSetChanged();
                //关闭侧滑菜单
                holder.swipeMenuLayout.quickClose();
            }
        });
        return convertView;
    }

    // 获取指定组的指定子项视图
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CustomExpandableListView lv = ((CustomExpandableListView) convertView);
        if (convertView == null) {
            lv = new CustomExpandableListView(context);
        }
        SecondAdapter secondAdapter = new SecondAdapter(context, mListData.get(groupPosition).getListSecondModel());
        lv.setAdapter(secondAdapter);
        return lv;
    }

    // 子项是否可选
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 第二层的适配器
     */
    class SecondAdapter extends BaseExpandableListAdapter {
        Context context;
        LayoutInflater inflater;
        List<SecondModel> listSecondModel;

        public SecondAdapter(Context context, List<SecondModel> listSecondModel) {
            this.context = context;
            this.listSecondModel = listSecondModel;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            int size = listSecondModel.size();
            Log.d("bigname", "getGroupCount: " + size);
            return size;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return listSecondModel.get(groupPosition).getListThirdModel().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return listSecondModel.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return listSecondModel.get(groupPosition).getListThirdModel().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            SecondHolder holder = null;
            if (convertView == null) {
                holder = new SecondHolder();
                convertView = mInflate.inflate(R.layout.item_expand_lv_second, parent, false);
                holder.tv = convertView.findViewById(R.id.tv);
                convertView.setTag(holder);
            } else {
                holder = (SecondHolder) convertView.getTag();
            }
            holder.tv.setText(listSecondModel.get(groupPosition).getTitle());
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ThirdHolder holder = null;
            if (convertView == null) {
                holder = new ThirdHolder();
                convertView = mInflate.inflate(R.layout.item_expand_lv_third, parent, false);
                holder.tv = convertView.findViewById(R.id.tv);
                convertView.setTag(holder);
            } else {
                holder = (ThirdHolder) convertView.getTag();
            }
            holder.tv.setText(listSecondModel.get(groupPosition).getListThirdModel().get(childPosition).getTitle());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


    class FirstHolder {
        SwipeMenuLayout swipeMenuLayout;
        TextView tv;
        Button btnDelete;
    }

    class SecondHolder {
        TextView tv;
    }

    class ThirdHolder {
        TextView tv;
    }
}
