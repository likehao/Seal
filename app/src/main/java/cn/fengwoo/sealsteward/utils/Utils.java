package cn.fengwoo.sealsteward.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.white.easysp.EasySP;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.fengwoo.sealsteward.view.MyApp;

import static com.mob.tools.utils.DeviceHelper.getApplication;

public class Utils {
    private static final double EARTH_RADIUS = 6378.137;
    public static final boolean logTag = true;

    public static IntentFilter intentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        return intentFilter;
    }

    public static void log(String str) {
        if (logTag == true) {
            Logger.d(str);
        }
    }


    public static int hexStringToInt(String hexString) {
        return Integer.valueOf(hexString, 16);
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

    public static int getNumberOfPeople(Map map, Object value) {
        Set set = map.entrySet(); //??????entrySet()?????????map????????????????????????????????????Set????????????????????????
        Iterator<Map.Entry<Object, Object>> iterator = set.iterator();
        ArrayList<Object> arrayList = new ArrayList();
        while (iterator.hasNext()) {
            //Map.Entry????????????????????????map????????????????????????????????????
            Map.Entry<Object, Object> entry = iterator.next();
            if (entry.getValue().equals(value)) {
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
        targetBytes[3] = (byte) (now.get(Calendar.YEAR) - 2000);
        targetBytes[4] = (byte) (now.get(Calendar.MONTH) + 1);
        targetBytes[5] = (byte) (now.get(Calendar.DAY_OF_MONTH));
        targetBytes[6] = (byte) (now.get(Calendar.HOUR_OF_DAY));
        targetBytes[7] = (byte) (now.get(Calendar.MINUTE));
        targetBytes[8] = (byte) (now.get(Calendar.SECOND));
        targetBytes[9] = (byte) (targetBytes[0] + targetBytes[1] + targetBytes[2] + targetBytes[3] + targetBytes[4] + targetBytes[5] + targetBytes[6] + targetBytes[7] + targetBytes[8]);
        return targetBytes;
    }

    /**
     * byte????????????
     */
    public static String bytesToHexString(byte[] bytes) {
        String str = "";
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            str += hex.toUpperCase() + " ";
        }

        return (str);
    }

    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static boolean isConnect(Context context) {
        boolean connectState = false;
        if (((MyApp) getApplication()).getConnectionObservable() == null) {
            Utils.log("????????????ble??????");
            Utils.showToast(context, "???????????????");
            connectState = false;
        } else {
            connectState = true;
        }
        return connectState;
    }

    public static boolean isConnectWithouToast(Context context) {
        boolean connectState = false;
        if (((MyApp) getApplication()).getConnectionObservable() == null) {
            Utils.log("????????????ble??????");
//            Utils.showToast(context, "???????????????");
            connectState = false;
        } else {
            connectState = true;
        }
        return connectState;
    }

    public static boolean hasThePermission(Context context, String idString) {
        boolean hasTheOne = false;
        String allPermissionHaveGot = EasySP.init(context).getString("permission");
        if (allPermissionHaveGot.contains(idString)) {
            hasTheOne = true;
        } else {
            hasTheOne = false;
            //       showToast(context, "???????????????");
        }
        return hasTheOne;
    }


    private String getPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public final static Bitmap lessenUriImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); //???????????? bm ??????
        options.inJustDecodeBounds = false; //????????????????????????????????????????????????????????????????????????????????????????????????
        int be = (int) (options.outHeight / (float) 320);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be; //?????????????????????????????????????????? options.inJustDecodeBounds ?????? false ???
        bitmap = BitmapFactory.decodeFile(path, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println(w + " " + h); //after zoom
        return bitmap;
    }

    public static double distanceOfTwoPoints(double lat1, double lng1,
                                             double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        Log.i("??????", s + "");
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    public static String getExternalDCIM(String subDir) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (file == null) {
                return null;
            }
            String dir = "";
            if (!TextUtils.isEmpty(subDir)) {
                dir = subDir;
            }
            String result = file.getAbsolutePath() + dir;
//            BoxingLog.d("external DCIM is: " + result);
            return result;
        }
//        BoxingLog.d("external DCIM do not exist.");
        return null;
    }

    public static boolean isBluetoothOpen() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }


    /**
     * ????????????view?????????view????????????????????????
     */
    public static boolean isAllInvisible(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        boolean isAllInvisible = true;
        for (int i = 0; i < count; i++) {
            if (viewGroup.getChildAt(i).getVisibility() == View.VISIBLE) {
                isAllInvisible = false;
                break;
            }
        }
        return isAllInvisible;
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * ???????????????????????????
     */
    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
//            LogUtil.d("????????????????????????" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    //????????????????????????
    public static Bitmap turnTransparent(Bitmap mBitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        if (mBitmap != null) {
            int mWidth = mBitmap.getWidth();
            int mHeight = mBitmap.getHeight();
            for (int i = 0; i < mHeight; i++) {
                for (int j = 0; j < mWidth; j++) {
                    int color = mBitmap.getPixel(j, i);
                    int g = Color.green(color);
                    int r = Color.red(color);
                    int b = Color.blue(color);
                    int a = Color.alpha(color);

//                    if(g>=250&&r>=250&&b>=250){
//                        a = 0;
//                    }
//                    if(g<=100&&r>=200&&b<=100){
//                        a = 0;
//                    }

                    if (g >= 100 && r >= 100 && b >= 100) {
                        a = 0;
                    }
                    color = Color.argb(a, r, g, b);
                    createBitmap.setPixel(j, i, color);
                }
            }
        }
        return createBitmap;
    }
    /**
     * ??????????????????????????????
     *
     * @param
     * @return true ????????????
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * ?????????????????????ID
     * @param activity
     * @return
     */
    public static boolean isHaveCompanyId(Activity activity){
        String companyId = CommonUtil.getUserData(activity).getCompanyId();
        if (companyId == null || companyId.equals("")) {
            return false;
        }
        return true;
    }

    /**
     * ??????APP????????????????????????
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i("TAG", "????????????" + appProcess.processName);
                    return true;
                } else {
                    Log.i("TAG", "????????????" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
}
