package cn.fengwoo.sealsteward.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    public static String path = "/sdcard/SealDownImage/";// sd路径

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
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                //保存到本地
                down(bitmap, fileName);

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
        //时间命名
        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        //  String fileName = simpleDate.format(now.getTime());
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
    public static Bitmap readFile(String url) {
        //获取
        FileInputStream fs = null;
        try {
            String str = path + url;

            fs = new FileInputStream("file://"+ path +str);
            Bitmap bitmap = BitmapFactory.decodeStream(fs);
            String bitmap1 = String.valueOf(fs);
            fs.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
