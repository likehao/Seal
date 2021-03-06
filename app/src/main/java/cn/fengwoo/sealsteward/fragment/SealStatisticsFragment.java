package cn.fengwoo.sealsteward.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.SealStatisticActivity;
import cn.fengwoo.sealsteward.activity.SelectTimeActivity;
import cn.fengwoo.sealsteward.activity.UserStatisticActivity;
import cn.fengwoo.sealsteward.bean.SealStatisticsData;
import cn.fengwoo.sealsteward.bean.UserStatisticsData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ??????????????????
 */
public class SealStatisticsFragment extends Fragment implements View.OnClickListener {
    private View view;
    @BindView(R.id.sealOrgName_tv)
    TextView sealOrgName_tv;
    @BindView(R.id.sealStatistic_lv)
    ListView listView;
    @BindView(R.id.seal_statistics_ll)
    LinearLayout seal_statistics_ll;
    @BindView(R.id.seal_time_tv)
    TextView time;
    @BindView(R.id.seal_time_tv2)
    TextView time2;
    @BindView(R.id.sealStatistics_search)
    EditText sealSearch;
    private ArrayList<SealStatisticsData> detailData;
    private String id, orgName;
    private CommonAdapter commonAdapter;
    private int year, month;
    private ResponseInfo<List<SealStatisticsData>> responseInfo;
    private static final int SEAL_TIME_CODE = 123;
    private String bigTime, small;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.seal_statistics_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        getSealStatistics(year, month, 0);

        return view;
    }

    private void initView() {
        detailData = new ArrayList<>();
        seal_statistics_ll.setOnClickListener(this);
        Intent intent = getActivity().getIntent();
        id = intent.getStringExtra("orgId");
        orgName = intent.getStringExtra("orgName");
        sealOrgName_tv.setText(orgName);
        //??????????????????
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //????????????
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        sealSearch.setOnClickListener(this);
        sealSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*??????????????????????????????*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchStr = sealSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchStr)) {
                        //????????????
                        detailData.clear();
                        if (commonAdapter != null) {
                            commonAdapter.notifyDataSetChanged();
                        }
                        getSearchData(searchStr);
                        return true;   //??????false??????????????????????????????????????????true?????????????????????
                    }

                }
                return false;
            }
        });
    }

    private void getSealStatistics(int YY, int MM, int type) {
        UserStatisticsData userStatisticsData = new UserStatisticsData();
        userStatisticsData.setOrgStructureId(id);
        if (type == 0) {
            userStatisticsData.setYear(YY);
            userStatisticsData.setMonth(MM);
        } else if (type == 1) {
            userStatisticsData.setStartTime(small);
            userStatisticsData.setEndTime(bigTime);
        }
        userStatisticsData.setSearchType(type);
        HttpUtil.sendDataAsync(getActivity(), HttpUrl.SEAL_STATISTIC, 2, null, userStatisticsData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "????????????????????????????????????????????????!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<SealStatisticsData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (SealStatisticsData data : responseInfo.getData()) {
                        detailData.add(new SealStatisticsData(data.getId(), data.getName(), data.getSealPrint(), data.getStampCount()));
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initData();
                            }
                        });
                    }
                }
            }
        });
    }

    private void initData() {
        commonAdapter = new CommonAdapter<SealStatisticsData>(getActivity(), detailData, R.layout.seal_statistics_item) {
            @Override
            public void convert(ViewHolder viewHolder, SealStatisticsData sealStatisticsData, int position) {
                //????????????
                ImageView view = viewHolder.getView(R.id.seal_statistics_iv);
                String sealPrint = sealStatisticsData.getSealPrint();
                if (sealPrint != null && !sealPrint.isEmpty()) {
                    Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(sealPrint);
                    if (bitmap == null) {
                        HttpDownloader.downloadImage(getActivity(), 1, sealPrint, new DownloadImageCallback() {
                            @Override
                            public void onResult(final String fileName) {
                                if (fileName != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String path = "file://" + HttpDownloader.path + fileName;
                                            Picasso.with(getActivity()).load(path).into(view);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        String path = "file://" + HttpDownloader.path + sealPrint;
                        Picasso.with(getActivity()).load(path).into(view);
                    }
                }
                //??????????????????
                if (position == 0) {
                    viewHolder.setTextColorRes(R.id.seal_statistics_count_tv, R.color.red);
                } else if (position == 1) {
                    viewHolder.setTextColorRes(R.id.seal_statistics_count_tv, R.color.number_tv);
                } else if (position == 2) {
                    viewHolder.setTextColorRes(R.id.seal_statistics_count_tv, R.color.style);
                } else if (position > 2) {
                    viewHolder.setTextColorRes(R.id.seal_statistics_count_tv, R.color.dark_gray);
                }

                viewHolder.setText(R.id.seal_statistics_name_tv, sealStatisticsData.getName());
                viewHolder.setText(R.id.seal_statistics_count_tv, sealStatisticsData.getStampCount() + "???");
                viewHolder.setOnClickListener(R.id.sealStatistics_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserStatisticActivity.class);
                        intent.putExtra("sealId", detailData.get(position).getId());
                        intent.putExtra("orgName", orgName);
                        intent.putExtra("sealName", detailData.get(position).getName());
                        startActivity(intent);
                    }
                });
            }

        };
        commonAdapter.notifyDataSetChanged();
        listView.setAdapter(commonAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seal_statistics_ll:
                Intent intent = new Intent(getActivity(), SelectTimeActivity.class);
                startActivityForResult(intent, SEAL_TIME_CODE);
//                selectTime();
                break;
            case R.id.sealStatistics_search:
                sealSearch.setCursorVisible(true);
                break;
        }
    }

    /**
     * ????????????
     */
    @SuppressLint("WrongConstant")
    private void selectTime() {
        Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //????????????
        startDate.set(2016, 0, 1);   //??????????????????
        Calendar endDate = Calendar.getInstance();
        int y = endDate.get(Calendar.YEAR);
        int m = endDate.get(Calendar.MONTH);
        endDate.set(y, m, 1);
        //????????????
        TimePickerView timePicker = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy???MM???");
                String format = simpleDateFormat.format(date);  //???????????????
                SimpleDateFormat yearSim = new SimpleDateFormat("yyyy");
                int Y = Integer.parseInt(yearSim.format(date));
                SimpleDateFormat monthSim = new SimpleDateFormat("MM");
                int M = Integer.parseInt(monthSim.format(date));
                //????????????
                detailData.clear();
                if (commonAdapter != null) {
                    commonAdapter.notifyDataSetChanged();
                }
                time.setText(format);
                getSealStatistics(Y, M, 0);
            }
        })
                .setType(new boolean[]{true, true, false, false, false, false})  //?????????????????? ????????????????????????????????????????????????
                .setLabel("", "", "", "", "", "")  //??????????????????????????????????????????
                .setRangDate(startDate, endDate)  //???????????????????????????
                .setLineSpacingMultiplier(2f)  //?????????????????????
                .build();
        //   timePicker.setDate(Calendar.getInstance());//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        timePicker.show();
    }

    /**
     * ????????????
     */
    private void getSearchData(String search) {
        List<SealStatisticsData> list = new ArrayList<>();
        if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
            for (SealStatisticsData sealStatisticsData : responseInfo.getData()) {
                if (sealStatisticsData.getName().contains(search)) {
                    if (!list.contains(sealStatisticsData)) {
                        list.add(sealStatisticsData);
                    }
                }
            }
        }
        for (SealStatisticsData data : list) {
            detailData.add(new SealStatisticsData(data.getId(), data.getName(), data.getSealPrint(), data.getStampCount()));
        }

        listView.setAdapter(commonAdapter);
        if (commonAdapter != null) {
            commonAdapter.notifyDataSetChanged();
        }
        if (list.size() == 0) {
            Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int yTime = 0, mTime = 0;
        if (requestCode == SEAL_TIME_CODE && resultCode == SEAL_TIME_CODE) {
            if (data != null) {
                String getTime = data.getStringExtra("time");
                String startTime = data.getStringExtra("startTime");
                String endTime = data.getStringExtra("endTime");
                if (startTime != null && endTime != null) {
                    Boolean selectB = DateUtils.compare(startTime, endTime);  //?????????????????????????????????
                    if (selectB) {
                        bigTime = startTime;
                        small = endTime;
                    } else {
                        bigTime = endTime;
                        small = startTime;
                    }
                }
                int type = data.getIntExtra("type", 2);
                //????????????
                if (getTime != null) {
                    yTime = Integer.parseInt(getTime.split("-")[0]);
                    mTime = Integer.parseInt(getTime.split("-")[1]);
                    time.setText(yTime + "???" + mTime + "???");
                    time2.setVisibility(View.GONE);
                }
                if (type == 1) {
                    time.setText(small);
                    time2.setVisibility(View.VISIBLE);
                    time2.setText(bigTime);
                }
                //????????????
                detailData.clear();
                if (commonAdapter != null) {
                    commonAdapter.notifyDataSetChanged();
                }
                getSealStatistics(yTime, mTime, type);
            }
        }
    }
}
