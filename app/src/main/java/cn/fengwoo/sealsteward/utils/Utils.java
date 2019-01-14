package cn.fengwoo.sealsteward.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

import java.lang.reflect.Field;

public class Utils {

    public static IntentFilter intentFilter(){
       final IntentFilter intentFilter = new IntentFilter();
       intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
       intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
       intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
       return intentFilter;
    }
}
