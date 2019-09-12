package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longsh.optionframelibrary.OptionBottomDialog;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.squareup.picasso.Picasso;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.TabFragmentAdapter;
import cn.fengwoo.sealsteward.entity.PplAddEntity;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.fragment.EmptyFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 新员工加入
 */
public class PplAddActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.tv_left)
    TextView tv_left;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.list1)
    ListView list1;
    @BindView(R.id.list2)
    ListView list2;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;

    @BindView(R.id.approval_tabLayout)
    TabLayout approval_tabLayout;
    @BindView(R.id.approval_viewPager)
    ViewPager approval_viewPager;

    //tab文字集合
    private List<String> titleList;
    //主页面集合
    private List<Fragment> fragmentList;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private CommonAdapter commonAdapter1;
    private CommonAdapter commonAdapter2;

    private List<PplAddEntity> items; // all
    private List<PplAddEntity> items1; // left
    private List<PplAddEntity> items2; // right

    private List<String> stringList;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppl_add);
        ButterKnife.bind(this);
        initData();
        initView();
        setListAdapter1();
        setListAdapter2();
        getNetDate();
    }

    private void initData() {
        items = new ArrayList<>();
        items1 = new ArrayList<>();
        items2 = new ArrayList<>();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("新员工加入");
        set_back_ll.setOnClickListener(this);
        tv_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);

        titleList = new ArrayList<String>();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new EmptyFragment());
        fragmentList.add(new EmptyFragment());
        titleList.add("待我处理");
        titleList.add("我的申请");
        approval_viewPager.setAdapter(new TabFragmentAdapter(fragmentManager, PplAddActivity.this, fragmentList, titleList));
        approval_tabLayout.setupWithViewPager(approval_viewPager);//此方法就是让tablayout和ViewPager联动

        approval_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("待我处理")) {
                    no_record_ll.setVisibility(View.GONE);
                    list1.setVisibility(View.VISIBLE);
                    list2.setVisibility(View.GONE);
                    if (items1.size() == 0) {
                        list1.setVisibility(View.GONE);
                        no_record_ll.setVisibility(View.VISIBLE);
                    }
                } else {
                    no_record_ll.setVisibility(View.GONE);
                    list1.setVisibility(View.GONE);
                    list2.setVisibility(View.VISIBLE);
                    if (items2.size() == 0) {
                        list2.setVisibility(View.GONE);
                        no_record_ll.setVisibility(View.VISIBLE);
                    }
                }
//                Toast.makeText(ApprovalRecordActivity.this, "选中的"+tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

//                Toast.makeText(ApprovalRecordActivity.this, "未选中的"+tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

//                Toast.makeText(ApprovalRecordActivity.this, "复选的"+tab.getText(), Toast.LENGTH_SHORT).show();

            }
        });


        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(items1.get(position));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    /**
     * 获取人员加入列表数据
     */
    private void getNetDate() {
        loadingView.show();
        Utils.log("getDate");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealId", EasySP.init(this).getString("currentSealId"));
        hashMap.put("userType", "1");
        HttpUtil.sendDataAsync(this, HttpUrl.APPLY_JOIN_USER_LIST, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                ResponseInfo<List<PplAddEntity>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<PplAddEntity>>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    loadingView.cancel();
                    items = responseInfo.getData();

                    // items 处理
                    String userId = CommonUtil.getUserData(PplAddActivity.this).getId();
                    for (PplAddEntity pplAddEntity : items) {
                        if (!pplAddEntity.getUserId().equals(userId)) {
                            items1.add(pplAddEntity);
                        } else {
                            items2.add(pplAddEntity);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            updateState();

                            // left
//                            setListAdapter1();
                            if (commonAdapter1 != null) {
                                commonAdapter1.notifyDataSetChanged();
                                no_record_ll.setVisibility(View.GONE);
                            }

                            // right
//                            setListAdapter2();
                            if (commonAdapter2 != null) {
                                commonAdapter2.notifyDataSetChanged();
                                no_record_ll.setVisibility(View.GONE);
                            }

                        }
                    });

                } else {
                    loadingView.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            updateState();

                            // left
//                            setListAdapter1();
                            if (commonAdapter1 != null) {
                                commonAdapter1.notifyDataSetChanged();
                                no_record_ll.setVisibility(View.VISIBLE);
                            }

                            // right
//                            setListAdapter2();
                            if (commonAdapter2 != null) {
                                commonAdapter2.notifyDataSetChanged();
                                no_record_ll.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }


    private void updateState() {
        HttpUtil.sendDataAsync(this, HttpUrl.UPDATE_JOIN_READ_STATUS, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("updateState" + result);
                Gson gson = new Gson();
            }
        });
    }

    /**
     * 待我处理
     */
    private void setListAdapter1() {
        commonAdapter1 = new CommonAdapter<PplAddEntity>(this, items1, R.layout.ppl_add_item_left) {
            @Override
            public void convert(ViewHolder viewHolder, PplAddEntity pplAddEntity, int i) {
                // 文字
                viewHolder.setText(R.id.tv_userName_tel, pplAddEntity.getUserName() + " " + pplAddEntity.getMobilePhone());
                viewHolder.setText(R.id.tv_content, pplAddEntity.getContent() + "");

                String headPortrait = pplAddEntity.getHeadPortrait();
                if (headPortrait != null && !headPortrait.isEmpty()) {
                    //先从本地读取，没有则下载
                    Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(headPortrait);
                    if (bitmap == null) {
                        HttpDownloader.downloadImage(PplAddActivity.this, 1, headPortrait, new DownloadImageCallback() {
                            @Override
                            public void onResult(final String fileName) {
                                if (fileName != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String headPortraitPath = "file://" + HttpDownloader.path + fileName;
                                            Picasso.with(PplAddActivity.this).load(headPortraitPath).into((CircleImageView) viewHolder.getView(R.id.iv));
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        String headPortraitPath = "file://" + HttpDownloader.path + headPortrait;
                        Picasso.with(PplAddActivity.this).load(headPortraitPath).into((CircleImageView) viewHolder.getView(R.id.iv));
                    }
                }
            }
        };
        list1.setAdapter(commonAdapter1);
    }

    /**
     * 我的申请
     */
    private void setListAdapter2() {
        commonAdapter2 = new CommonAdapter<PplAddEntity>(this, items2, R.layout.ppl_add_item_right) {
            @Override
            public void convert(ViewHolder viewHolder, PplAddEntity pplAddEntity, int i) {
                // 文字
                String nameString = "";
                if (pplAddEntity.getUserName().length() > 6) {
                    nameString = pplAddEntity.getUserName().substring(0, 6) + "...";
                } else {
                    nameString = pplAddEntity.getUserName();
                }

                viewHolder.setText(R.id.tv_userName_tel, nameString + " " + pplAddEntity.getMobilePhone());
                viewHolder.setText(R.id.tv_content, pplAddEntity.getContent() + "");

                switch (pplAddEntity.getStatus()) {
                    case 1:
                        viewHolder.setText(R.id.tv_status, "已同意");
                        break;
                    case 2:
                        viewHolder.setText(R.id.tv_status, "已拒绝");
                        break;
                    default:
                        viewHolder.setText(R.id.tv_status, "待处理");
                }

                // delete
                viewHolder.setOnClickListener(R.id.btn_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refuseOrDelete(pplAddEntity, 3);
                    }
                });

                String headPortrait = pplAddEntity.getHeadPortrait();
                if (headPortrait != null && !headPortrait.isEmpty()) {
                    //先从本地读取，没有则下载
                    Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(headPortrait);
                    if (bitmap == null) {
                        HttpDownloader.downloadImage(PplAddActivity.this, 1, headPortrait, new DownloadImageCallback() {
                            @Override
                            public void onResult(final String fileName) {
                                if (fileName != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String headPortraitPath = "file://" + HttpDownloader.path + fileName;
                                            Picasso.with(PplAddActivity.this).load(headPortraitPath).into((CircleImageView) viewHolder.getView(R.id.iv));
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        String headPortraitPath = "file://" + HttpDownloader.path + headPortrait;
                        Picasso.with(PplAddActivity.this).load(headPortraitPath).into((CircleImageView) viewHolder.getView(R.id.iv));
                    }
                }
            }
        };
        list2.setAdapter(commonAdapter2);
    }


    /***
     * 弹出提示框
     */
    private void showDialog(PplAddEntity pplAddEntity) {
        stringList = new ArrayList<String>();
        stringList.add("同意");
        stringList.add("拒绝");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(PplAddActivity.this, stringList);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    intent = new Intent(PplAddActivity.this, PplAddAgreeActivity.class);
                    intent.putExtra("entity", pplAddEntity);
                    startActivityForResult(intent, 123);
                    optionBottomDialog.dismiss();
                } else {
                    refuseOrDelete(pplAddEntity, 2);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == 123) {
            // refresh
            items1.clear();
            items2.clear();
            getNetDate();
        }
    }

    private void refuseOrDelete(PplAddEntity pplAddEntity, int status) {
        pplAddEntity.setStatus(status);
        HttpUtil.sendDataAsync(PplAddActivity.this, HttpUrl.HANDLE_JOIN_COMPANY, 2, null, pplAddEntity, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("result:" + result);
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    if (responseInfo.getData()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                items1.clear();
                                items2.clear();
                                getNetDate();
                            }
                        });
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(responseInfo.getMessage());
                            items1.clear();
                            items2.clear();
                            getNetDate();
                        }
                    });
                }
            }
        });
    }
}