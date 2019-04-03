package cn.fengwoo.sealsteward.view;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.polidea.rxandroidble2.RxBleConnection;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tianma.netdetector.lib.NetStateChangeReceiver;

import java.util.ArrayList;
import java.util.List;

import cn.fengwoo.sealsteward.R;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * SmartRefreshLayout使用指定Header 和 Footer
 */
public class MyApp extends MultiDexApplication {
    private Observable<RxBleConnection> connectionObservable;
    public Disposable connectDisposable;
    public List<Disposable> disposableList;


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

    @Override
    public void onCreate() {
        super.onCreate();
        NetStateChangeReceiver.registerReceiver(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        disposableList = new ArrayList<>();
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
}