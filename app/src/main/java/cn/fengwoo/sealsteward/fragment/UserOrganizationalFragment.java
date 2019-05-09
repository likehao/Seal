package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.ChangeInformationActivity;
import cn.fengwoo.sealsteward.activity.PersonCenterActivity;
import cn.fengwoo.sealsteward.activity.SelectPeopleMultiActivity;
import cn.fengwoo.sealsteward.activity.SelectSealMultiActivity;
import cn.fengwoo.sealsteward.activity.UserInfoActivity;
import cn.fengwoo.sealsteward.adapter.ExpandListViewAdapter;
import cn.fengwoo.sealsteward.adapter.NodeTreeAdapter;
import cn.fengwoo.sealsteward.entity.FirstModel;
import cn.fengwoo.sealsteward.entity.OrganizationalStructureData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SecondModel;
import cn.fengwoo.sealsteward.entity.ThirdModel;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.Dept;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Node;
import cn.fengwoo.sealsteward.utils.NodeHelper;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用户组织架构
 */
public class UserOrganizationalFragment extends Fragment {
    private static final String TAG = UserOrganizationalFragment.class.getSimpleName();
    private ListView mListView;
    private NodeTreeAdapter mAdapter;
    private LinkedList<Node> mLinkedList = new LinkedList<>();
    private View view;
    private List<Node> data;
    private int filterType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_organizational_fragment, container, false);
        ButterKnife.bind(this, view);
        mListView = (ListView) view.findViewById(R.id.id_tree);
        mAdapter = new NodeTreeAdapter(getActivity(), mListView, mLinkedList,0,0);
        mAdapter.setClickItemListener(new NodeTreeAdapter.ClickItemListener() {
            @Override
            public void clicked(String id,int type,String parentName) {
                Utils.log("id:" + id);
                if (type == 3) {
                    selectDialog(id);
                }
            }
        });
        mListView.setAdapter(mAdapter);
        initData();
        getDate();
        return view;
    }

    private void initData() {
        filterType = 4;  // 人
        data = new ArrayList<>();
        mLinkedList.addAll(NodeHelper.sortNodes(data));
        mAdapter.notifyDataSetChanged();
    }

    private void getDate() {
        Utils.log("good");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("loadType", "0");
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.ORGANIZATIONAL_STRUCTURE, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                OrganizationalStructureData organizationalStructureData = gson.fromJson(result, OrganizationalStructureData.class);
//                Utils.log(organizationalStructureData.getData().get(0).getName());
                for (OrganizationalStructureData.DataBean dataBean : organizationalStructureData.getData()) {
                    if (dataBean.getType() != filterType) {
                        data.add(new Dept(dataBean.getId(), (String) dataBean.getParentId(), dataBean.getName(),dataBean.getType(),2,false,dataBean.getPortrait()));
                    }
                }
                if(null != getActivity()){
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            mAdapter = new NodeTreeAdapter(getActivity(), mListView, mLinkedList,0,0);
                            mAdapter.setClickItemListener(new NodeTreeAdapter.ClickItemListener() {
                                @Override
                                public void clicked(String id,int type,String parentName) {
                                    Utils.log("id:" + id);
                                    if (type == 3) {
                                        selectDialog(id);
                                    }
                                }
                            });
                            mListView.setAdapter(mAdapter);

                            mLinkedList.addAll(NodeHelper.sortNodes(data));
                            mAdapter.notifyDataSetChanged();
                            Utils.log("mAdapter.notifyDataSetChanged();");
                        }
                    });
                }
            }
        });
    }

    private void selectDialog( String uid) {
        Utils.log("**********"+CommonUtil.getUserData(getActivity()).getId());

        ArrayList strings = new ArrayList<String>();
//        strings.add("切换");
        strings.add("删除");
        strings.add("查看详情");
        strings.add("移动到");

        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(getActivity(), strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.log("position"+position);
                if (position == 0) {
                    optionBottomDialog.dismiss();
                    CommonDialog commonDialog = new CommonDialog(getActivity(),"确定删除该用户？","删除后该用户将被移除此公司,请谨慎操作!","删除");
                    commonDialog.showDialog();
                    commonDialog.setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uID = CommonUtil.getUserData(getActivity()).getId();
                            // 判断自己在判断权限之前
                            if (uID.equals(uid)) {

                                Toast.makeText(getActivity(),"不能删除自己",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // 判断权限
                            if (!Utils.hasThePermission(getActivity(), Constants.permission16)) {
                                return;
                            }

                            // delete

                            if (uID.equals(uid)) {
                                Toast.makeText(getActivity(),"不能删除自己",Toast.LENGTH_SHORT).show();
                            }else {
                                // delete user
                                Utils.log("delete user");
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("userId", uid);
                                HttpUtil.sendDataAsync(getActivity(), HttpUrl.DELETE_USER, 4, hashMap, null, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Utils.log(e.toString());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String result = response.body().string();
                                        Utils.log(result);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
                                                commonDialog.dialog.dismiss();
                                            }
                                        });

                                        // init

                                        data .clear();

                                        mLinkedList.clear();

                                        if(null != getActivity()){
                                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
//                                        mLinkedList.addAll(NodeHelper.sortNodes(data));

                                                    optionBottomDialog.dismiss();
                                                    getDate();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });

                } else if (position == 1) {
//                    loadingView.show();
//                    deleteDialog(); //提示删除

                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                    if(null != getActivity()){
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                optionBottomDialog.dismiss();
                            }
                        });
                    }
                }
                else if (position == 2) {
//                    loadingView.show();
//                    deleteDialog(); //提示删除

                    if (!Utils.hasThePermission(getActivity(), Constants.permission23)) {
                        return;
                    }

                    Intent intent = new Intent(getActivity(), SelectPeopleMultiActivity.class);
                    Utils.log("sealID:" + uid);
//                    intent.putExtra("departmentName", departmentName);
//                    intent.putExtra("sealID", uid);
                    startActivityForResult(intent,123);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.log("" + requestCode);
        mLinkedList =  new LinkedList<>();
        initData();
        getDate();
    }

}