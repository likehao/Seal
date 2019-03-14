package cn.fengwoo.sealsteward.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;

public class Utils {

    public static final boolean logTag = true;
    public static IntentFilter intentFilter(){
       final IntentFilter intentFilter = new IntentFilter();
       intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
       intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
       intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
       return intentFilter;
    }
    public static void log(String str){
        if (logTag == true) {
            Logger.d(str);
        }
    }


    public static int hexStringToInt(String hexString){
        return Integer.valueOf(hexString,16);
    }
}
