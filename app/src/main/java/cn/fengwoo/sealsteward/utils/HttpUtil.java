package cn.fengwoo.sealsteward.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.white.easysp.EasySP;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.fengwoo.sealsteward.view.MyApp;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * http二次封装
 */
public class HttpUtil {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final String TAG = HttpUtil.class.getSimpleName();
    private static OkHttpClient okHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;
    private static String addStr;
    private static String URL2,URL1;
    public static String BASE_URL;    //拼接地址

    /**
     * 初始化RequestManager
     */
    public HttpUtil(Context context) {
        //初始化OkHttpClient
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .build();
        //初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    /**
     * 发送请求
     * <p>
     * type 1 get
     * type 2 post
     * type 3 patch
     * type 4 delete
     * type 5 put
     *
     * @param url      地址
     * @param type     1,get 2,post 3,patch ,4delete,5put
     * @param params   字典
     * @param data     发送的JSON对象
     * @param callback 回调
     * @param <T>
     */
    public static <T> void sendDataAsync(Activity activity, String url, Integer type, Map<String, String> params, T data, Callback callback) {
        addStr = EasySP.init(MyApp.getAppContext()).getString("addStr").trim();  //接收正式服务器地址
        URL1 = "http://www.baiheyz.com:8080";
        URL2 = "/bhsealappservice/";

        if (addStr.length() == 0){
            BASE_URL = String.format("%s%s", URL1, URL2);
        }else {
            BASE_URL = String.format("%s%s", addStr, URL2);    //拼接地址
        }

        //初始化OkHttpClient
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .build();
        String requestUrl = null;
        Request request = null;
        RequestBody formBody = null;
        RequestBody body;
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            //判断params是否为空拼接params到url
            if (params != null) {
                //判断type拼接数据（get,post）
                for (String key : params.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(params.get(key), "utf-8")));
                    pos++;
                }
                if (type == 3) {  //拼接patch数据
                    FormBody.Builder builder = new FormBody.Builder();
                    for (String key : params.keySet()) {
                        builder.add(key, params.get(key));
                    }
                    formBody = builder.build();
                }
            }
            //判断数据对象是否为空，不为空则将对象转换成JSON对象并写入请求体中
            if (data != null) {
                Gson gson = new Gson();
                String json = gson.toJson(data);
                body = RequestBody.create(MEDIA_TYPE_JSON, json);
                requestUrl = String.format("%s/%s", BASE_URL, url);
                if (type == 5) {
                    request = addHeaders(activity).url(requestUrl).put(body).build();
                } else {
                    //放入头信息
                    request = addHeaders(activity).url(requestUrl).post(body).build();
                }
            } else {
                if (type == 1) {
                    requestUrl = String.format("%s/%s?%s", BASE_URL, url, tempParams.toString());
                    request = addHeaders(activity).url(requestUrl).get().build();
                } else if (type == 3) {
                    requestUrl = String.format("%s/%s", BASE_URL, url);
                    if (formBody != null) {
                        request = addHeaders(activity).url(requestUrl).patch(formBody).build();
                    }
                } else if (type == 5) {
                    Gson gson = new Gson();
                    String json = gson.toJson(data);
                    body = RequestBody.create(MEDIA_TYPE_JSON, json);
                    requestUrl = String.format("%s/%s?%s", BASE_URL, url, tempParams.toString());
                    request = addHeaders(activity).url(requestUrl).put(body).build();
                } else {
                    requestUrl = String.format("%s/%s?%s", BASE_URL, url, tempParams.toString());
                    request = addHeaders(activity).url(requestUrl).delete().build();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        //发送请求
        if (request != null) {
            final Call call = client.newCall(request);
            call.enqueue(callback);

//            if (NetCheckUtil.checkNet(activity)){
//            }else {
//                Toast.makeText(activity,"网络不可用",Toast.LENGTH_SHORT).show();
//            }
        }
    }

    /**
     * 上传文件
     *
     * @param actionUrl 接口地址
     * @param paramsMap 参数
     * @param callBack  回调
     * @param <T>
     */
    public <T> void upLoadFile(Activity activity, String actionUrl, HashMap<String, Object> paramsMap, final ReqCallBack<T> callBack) {
        try {
            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            for (String key : paramsMap.keySet()) {
                Object object = paramsMap.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                }
            }
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = addHeaders(activity).url(requestUrl).post(body).build();
            //单独设置参数 比如读取超时时间
            final Call call = okHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.toString());
                    failedCallBack("上传失败", callBack);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                     /*   Gson gson = new Gson();
                        ResponseInfo<LoadImageData> responseInfo = gson.fromJson(string, new TypeToken<ResponseInfo<LoadImageData>>() {
                        }.getType());*/
                        Log.e(TAG, "response ----->" + string);
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack("上传失败", callBack);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 添加请求头
     *
     * @return
     */
    public static Request.Builder addHeaders(Activity activity) {
        String version = getVersion(activity);
        assert version != null;   //断言
        Request.Builder builder = new Request.Builder()
                .addHeader("X-Token", CommonUtil.getUserData(activity).getToken())     //token
                .addHeader("X-User-Id", CommonUtil.getUserData(activity).getId())   //用户Id
                .addHeader("X-Company-Id", CommonUtil.getUserData(activity).getCompanyId())   //公司Id
                .addHeader("X-System-Version", Build.VERSION.RELEASE)   // 系统版本
                .addHeader("X-App-Version", version)  //app版本
                .addHeader("X-Phone-Type", Build.MODEL);  //手机型号
        return builder;
    }

    /**
     * 成功
     */
    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 失败
     */
    private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }

    /**
     * 获取版本号
     *
     * @param activity
     * @return
     */
    private static String getVersion(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);
            String appVersion = pi.versionName;
            return appVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}

