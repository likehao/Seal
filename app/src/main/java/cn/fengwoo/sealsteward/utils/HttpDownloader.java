package cn.fengwoo.sealsteward.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 下载工具类
 */
public class HttpDownloader {

    public static String path = "/sdcard/headImage/";// sd路径

    /**
     * 保存到SD卡
     * @param bitmap
     */
    public static void down(Bitmap bitmap){
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {  // 检测sd是否可用
            return;
        }
        //时间命名
        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        String fileName = simpleDate.format(now.getTime());
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String picName = path + fileName + ".jpg";   //图片名字
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
    public static Bitmap readFile(String url){
        //获取
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(path + url + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(fs);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
