package cn.fengwoo.sealsteward.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.hjq.toast.ToastUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tianma.netdetector.lib.NetStateChangeReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.Utils;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * SmartRefreshLayout使用指定Header 和 Footer
 */
public class MyApp extends MultiDexApplication {
    private static final String TAG = "MyApp";
    private Observable<RxBleConnection> connectionObservable;
    public Disposable connectDisposable;
    public List<Disposable> disposableList;
    public RxBleDevice rxBleDevice;


    public RxBleDevice getRxBleDevice() {
        return rxBleDevice;
    }

    public void setRxBleDevice(RxBleDevice rxBleDevice) {
        this.rxBleDevice = rxBleDevice;
    }


    public List<Disposable> getDisposableList() {
        return disposableList;
    }

    public void setDisposableList(List<Disposable> disposableList) {
        this.disposableList = disposableList;
    }

    public Disposable getConnectDisposable() {
        return connectDisposable;
    }

    public void setConnectDisposable(Disposable connectDisposable) {
        this.connectDisposable = connectDisposable;
    }

    public Observable<RxBleConnection> getConnectionObservable() {
        return connectionObservable;
    }

    public void setConnectionObservable(Observable<RxBleConnection> connectionObservable) {
        this.connectionObservable = connectionObservable;
    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }
    private static List<Activity> activityLinkedList;
    @Override
    public void onCreate() {
        super.onCreate();
        NetStateChangeReceiver.registerReceiver(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        ToastUtils.init(this);
        disposableList = new ArrayList<>();
//        createNoMedia();


        activityLinkedList = new LinkedList<>();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d(TAG, "onActivityCreated: " + activity.getLocalClassName());
//                Log.d(TAG, "Pid: " +  + Process.myPid());
                activityLinkedList.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(TAG, "onActivityStarted: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(TAG, "onActivityStopped: " + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(TAG, "onActivityDestroyed: " + activity.getLocalClassName());
                activityLinkedList.remove(activity);
            }
        });

    }

    private void createNoMedia() {
        String filePath = "file://" + HttpDownloader.path;
        File nomedia = new File(filePath + ".nomedia" );
        if (! nomedia.exists())
            try {
                nomedia.mkdirs();
                nomedia.createNewFile();
            } catch (Exception e) {
                Utils.log(e.toString());
                e.printStackTrace();
            }
    }

    public void removeAllDisposable() {
        for (Disposable disposable : disposableList) {
            disposable.dispose();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 取消BroadcastReceiver注册
        NetStateChangeReceiver.unregisterReceiver(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    public static void showList() {
        for (Activity activity : activityLinkedList) {
            Log.d(TAG, "showList: " + activity.getLocalClassName());
        }
    }

    public static void exitAppList() {
        for (Activity activity : activityLinkedList) {
            activity.finish();
        }
    }
}