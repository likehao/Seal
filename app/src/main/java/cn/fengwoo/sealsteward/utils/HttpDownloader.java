package cn.fengwoo.sealsteward.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 下载工具类
 */
public class HttpDownloader {

    public static final String path = "/sdcard/SealDownImage/";// sd路径

    /**
     * 下载图片
     *
     * @param activity
     * @param category
     * @param fileName
     */
    public static void downLoadImg(Activity activity, Integer category, final String fileName) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("category", category + "");
        hashMap.put("fileName", fileName);
        HttpUtil.sendDataAsync(activity, HttpUrl.DOWNLOADIMG, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] imgData = response.body().bytes();
                if(imgData!= null && imgData.length > 0){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                    //保存到本地
                    down(bitmap, fileName);
                }
            }
        });
    }

    /**
     * 保存到SD卡
     *
     * @param bitmap
     */
    public static void down(Bitmap bitmap, String fileName) {
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {  // 检测sd是否可用
            return;
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String picName = path + fileName;   //图片名字
            FileOutputStream out = new FileOutputStream(picName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  // 把数据写入文件
            // 关闭流
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取本地文件
     */
    public static Bitmap readFile(String fileName) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        else{
            String filePath = path + fileName;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            return bitmap;
        }
    }

    /**
     * 从SD卡中读取图片
     * @param imgName
     * @return
     */
    public static Bitmap getBitmapFromSDCard(String imgName) {
        // 判断图片文件是否存在
        String filePath = path + imgName;
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try{
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 设置为ture只获取图片大小
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeFile(filePath, opts);
            int width = opts.outWidth;
            int height = opts.outHeight;
            WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(filePath, opts));
            Bitmap bitmap = Bitmap.createScaledBitmap(weak.get(), width, height, true);

            return bitmap;
        }catch (Exception ex){

        }

        return null;
    }

    /**
     * 保存bitmap到SD卡中
     * @param bitmap
     * @param fileName
     */
    public static void saveBitmapToSDCard(Bitmap bitmap, String fileName){
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {  // 检测sd是否可用
            return;
        }
        if(bitmap == null || fileName == null){
            return;
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String picName = path + fileName;   //图片名字
            FileOutputStream out = new FileOutputStream(picName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  // 把数据写入文件
            // 关闭流
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
