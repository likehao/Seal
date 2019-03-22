package cn.fengwoo.sealsteward.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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


    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static void setUneditable(EditText editText) {
        editText.setFocusable(false);

        editText.setFocusableInTouchMode(false);
        editText.setClickable(false);

    }

    public static void setEditable(EditText editText) {
        editText.setFocusableInTouchMode(true);

        editText.setFocusable(true);

//        editText.requestFocus();
        editText.setClickable(true);
    }

    public static int getNumberOfPeople(Map map, Object value){
        Set set = map.entrySet(); //通过entrySet()方法把map中的每个键值对变成对应成Set集合中的一个对象
        Iterator<Map.Entry<Object, Object>> iterator = set.iterator();
        ArrayList<Object> arrayList = new ArrayList();
        while(iterator.hasNext()){
            //Map.Entry是一种类型，指向map中的一个键值对组成的对象
            Map.Entry<Object, Object> entry = iterator.next();
            if(entry.getValue().equals(value)){
                arrayList.add(entry.getKey());
            }
        }
        return arrayList.size();
    }


    public static byte[] createShakeHandsData() {
        byte[] targetBytes = new byte[10];
        Calendar now = Calendar.getInstance();
        targetBytes[0] = (byte) 0xFF;
        targetBytes[1] = (byte) 6;
        targetBytes[2] = (byte) 0xA0;
        targetBytes[3] = (byte)(now.get(Calendar.YEAR)-2000);
        targetBytes[4] = (byte) (now.get(Calendar.MONTH) + 1);
        targetBytes[5] = (byte) (now.get(Calendar.DAY_OF_MONTH));
        targetBytes[6] = (byte) (now.get(Calendar.HOUR_OF_DAY));
        targetBytes[7] = (byte) (now.get(Calendar.MINUTE));
        targetBytes[8] = (byte) (now.get(Calendar.SECOND));
        targetBytes[9] = (byte) (targetBytes[0] + targetBytes[1]  + targetBytes[2] + targetBytes[3] + targetBytes[4] + targetBytes[5] + targetBytes[6] + targetBytes[7] + targetBytes[8]);
        return targetBytes;
    }



    /**
     * byte转字符串
     */
    public static String bytesToHexString(byte[] bytes) {
        String str = "";
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            str += hex.toUpperCase()+" ";
        }

        return (str);
    }
}
