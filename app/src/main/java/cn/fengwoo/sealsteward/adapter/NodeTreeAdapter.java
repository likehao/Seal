package cn.fengwoo.sealsteward.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.Node;
import cn.fengwoo.sealsteward.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQOCSHheqing on 2016/8/3.
 *
 * @description 适配器类，就是listview最常见的适配器写法
 */
public class NodeTreeAdapter extends BaseAdapter {

    //大家经常用ArrayList，但是这里为什么要使用LinkedList
    // ，后面大家会发现因为这个list会随着用户展开、收缩某一项而频繁的进行增加、删除元素操作，
    // 因为ArrayList是数组实现的，频繁的增删性能低下，而LinkedList是链表实现的，对于频繁的增删
    //操作性能要比ArrayList好。
    private LinkedList<Node> nodeLinkedList;
    private LayoutInflater inflater;
    private int retract;//缩进值
    private Context context;
    private ClickItemListener clickItemListener;
    private CheckBoxCheckedlistener checkBoxCheckedlistener;
    private int isSingleSelection; // 是否单选,0表示没有选择，1表示单选，2表示多选
    private int typeWillShowCB; // 要显示check box的类型，0代表都不要显示
    private SparseBooleanArray selectArray;


    public NodeTreeAdapter(Context context, ListView listView, LinkedList<Node> linkedList, int isSingleSelection, int typeWillShowCB) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        selectArray = new SparseBooleanArray();

        nodeLinkedList = linkedList;
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                expandOrCollapse(position);
//            }
//        });

        listView.setOnItemClickListener(null);

        //缩进值，大家可以将它配置在资源文件中，从而实现适配
        retract = (int) (context.getResources().getDisplayMetrics().density * 10 + 0.5f);

        this.isSingleSelection = isSingleSelection;
        this.typeWillShowCB = typeWillShowCB;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < linkedList.size(); i++) {
                    expandOrCollapse(i);
                }
            }
        }, 400);
    }

    public void expandAll() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < nodeLinkedList.size(); i++) {
                    expandOrCollapse(i);
                }
            }
        }, 400);
    }

    /**
     * 展开或收缩用户点击的条目
     *
     * @param position
     */
    private void expandOrCollapse(int position) {
        Node node = nodeLinkedList.get(position);
        if (node != null && !node.isLeaf()) {
            boolean old = node.isExpand();
            if (old) {
                List<Node> nodeList = node.get_childrenList();
                int size = nodeList.size();
                Node tmp = null;
                for (int i = 0; i < size; i++) {
                    tmp = nodeList.get(i);
                    if (tmp.isExpand()) {
                        collapse(tmp, position + 1);
                    }
                    nodeLinkedList.remove(position + 1);
                }
            } else {
                nodeLinkedList.addAll(position + 1, node.get_childrenList());
            }
            node.setIsExpand(!old);
            notifyDataSetChanged();
        } else {
            Utils.log("else");

        }
    }

    /**
     * 递归收缩用户点击的条目
     * 因为此中实现思路是：当用户展开某一条时，就将该条对应的所有子节点加入到nodeLinkedList
     * ，同时控制缩进，当用户收缩某一条时，就将该条所对应的子节点全部删除，而当用户跨级缩进时
     * ，就需要递归缩进其所有的孩子节点，这样才能保持整个nodeLinkedList的正确性，同时这种实
     * 现方式避免了每次对所有数据进行处理然后插入到一个list，最后显示出来，当数据量一大，就会卡顿，
     * 所以这种只改变局部数据的方式性能大大提高。
     *
     * @param position
     */
    private void collapse(Node node, int position) {
        node.setIsExpand(false);
        List<Node> nodes = node.get_childrenList();
        int size = nodes.size();
        Node tmp = null;
        for (int i = 0; i < size; i++) {
            tmp = nodes.get(i);
            if (tmp.isExpand()) {
                collapse(tmp, position + 1);
            }
            nodeLinkedList.remove(position + 1);
        }
    }

    @Override
    public int getCount() {
        return nodeLinkedList.size();
    }

    @Override
    public Object getItem(int position) {
        return nodeLinkedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        Node node = nodeLinkedList.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tree_listview_item, null);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.id_treenode_icon);
            holder.label = convertView.findViewById(R.id.id_treenode_label);
            holder.confirm = convertView.findViewById(R.id.id_confirm);
            holder.checkBox = convertView.findViewById(R.id.cb);
            holder.iv_right = convertView.findViewById(R.id.iv_right);

            holder.iv_mark = convertView.findViewById(R.id.iv_mark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrCollapse(position);
            }
        });


        holder.checkBox.setTag(position);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int tag = Integer.parseInt(buttonView.getTag().toString());
                if (isChecked) {
                    checkBoxCheckedlistener.checked(node.get_id(), node.get_label());
                    for (int i = 0; i < getCount(); i++) {
                        if (tag == i) {
                            selectArray.put(i, true);
                        } else {
                            selectArray.put(i, false);
                        }
                    }
                } else {
                    checkBoxCheckedlistener.unchecked(node.get_id(), node.get_label());
                    selectArray.put(tag, false);
                }
                if (isSingleSelection != 2) {  // 非多选状态时
                    notifyDataSetChanged();
                }
            }
        });


        // 如果node.get_type()为3或者4，显示出iv_mark
        if (node.get_type() == 3 || node.get_type() == 4) {
            holder.iv_mark.setVisibility(View.VISIBLE);

            int category = 0;
            if (node.get_type() == 3) {
                category = 1;
            } else {
                category = 3;
            }

            // 显示图片
            String pic = node.get_portrait();
            if (pic != null && !pic.isEmpty()) {
                //先从本地读取，没有则下载
                Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(pic);
                if (bitmap == null) {
                    HttpDownloader.downloadImage((Activity) context, category, pic, new DownloadImageCallback() {
                        @Override
                        public void onResult(String fileName) {
                            super.onResult(fileName);
                            if (fileName != null) {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String picPath = "file://" + HttpDownloader.path + fileName;
                                        Picasso.with(context).load(picPath).into(holder.iv_mark);
                                        holder.iv_mark.setBackgroundDrawable(context.getResources().getDrawable(R.color.white));
                                    }
                                });
                            }
                        }
                    });
                    if (node.get_type() == 3) {
                        holder.iv_mark.setBackgroundResource(R.drawable.human_pic);
                    } else if (node.get_type() == 4) {
                        holder.iv_mark.setBackgroundResource(R.drawable.seal_pic);
                    }
                } else {
                    String headPortraitPath = "file://" + HttpDownloader.path + pic;
                    Picasso.with(context).load(headPortraitPath).into(holder.iv_mark);
                    holder.iv_mark.setBackgroundDrawable(context.getResources().getDrawable(R.color.white));
                }
            }
        } else {
            holder.iv_mark.setVisibility(View.GONE);
        }

        // check box
        if (isSingleSelection == 0) {
            holder.checkBox.setVisibility(View.GONE);
        } else {
            if (node.get_type() == typeWillShowCB) {
                holder.checkBox.setVisibility(View.VISIBLE);
                if (node.is_check() == 1) {
                    holder.checkBox.setChecked(true);
                } else if (node.is_check() == 0) {
                    holder.checkBox.setChecked(false);
                }

                if (node.is_gray()) {
                    // 灰色时不能点，变成灰色
                    holder.checkBox.setEnabled(false);
                    holder.checkBox.setVisibility(View.GONE);
                    holder.iv_right.setVisibility(View.VISIBLE);
                } else {
                    holder.checkBox.setEnabled(true);
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.iv_right.setVisibility(View.GONE);
                }
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
        }

        if (isSingleSelection != 2) {  // 非多选状态时
            holder.checkBox.setChecked(selectArray.get(position));
        }


        holder.label.setText(node.get_label());
        if (node.get_icon() == -1) {
            holder.imageView.setVisibility(View.INVISIBLE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(node.get_icon());
        }
        holder.confirm.setTag(position);
//        holder.confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"选中:" + v.getTag(), Toast.LENGTH_SHORT).show();
//            }
//        });

        holder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expandOrCollapse(position);

//                if (node.get_parent() != null) {
//                    Utils.log("*************************************************************************" + node.get_parent().get_label());
//                    clickItemListener.clicked(node.get_id(),node.get_type(), node.get_parent().get_label());
//                }

                if (node.get_parent() != null) {
                    Utils.log("*************************************************************************" + node.get_parent().get_label());
                    clickItemListener.clicked(node.get_id(), node.get_type(), node.get_parent().get_label());
                } else {
                    clickItemListener.clicked(node.get_id(), node.get_type(), "10000");
                }


            }
        });

        // 展开
        if (node.get_type() == 1 || node.get_type() == 2) {
//            expandOrCollapse(position);
//            holder.imageView.performClick();
        }

        if (holder.iv_mark.getVisibility() == View.VISIBLE) {
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.label.getLayoutParams();
//            layoutParams.setMargins(123, 0, 0, 0);
//            holder.label.setLayoutParams(layoutParams);
        }


        convertView.setPadding(node.get_level() * retract, 5, 5, 5);
        return convertView;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView label;
        public LinearLayout confirm;
        public CheckBox checkBox;
        public ImageView iv_right;
        public CircleImageView iv_mark;
    }


    public interface ClickItemListener {
        void clicked(String id, int typeInt, String parentName);
    }

    public interface CheckBoxCheckedlistener {
        void checked(String id, String name);

        void unchecked(String id, String name);
    }

    public void setClickItemListener(ClickItemListener clickItemListener) {
        this.clickItemListener = clickItemListener;
    }

    public void setCheckBoxCheckedlistener(CheckBoxCheckedlistener checkBoxCheckedlistener) {
        this.checkBoxCheckedlistener = checkBoxCheckedlistener;
    }
}
