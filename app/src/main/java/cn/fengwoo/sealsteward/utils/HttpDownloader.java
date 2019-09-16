package cn.fengwoo.sealsteward.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 下载工具类
 */
public class HttpDownloader {

//    public static final String path = "/sdcard/SealDownImage/";// sd路径
    public static final String path = "/sdcard/Android/data/cn.fengwoo.sealsteward/cache/";// sd路径

    public static void downloadImage(Activity activity, Integer category, final String fileName,final DownloadImageCallback callback){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("category", category + "");
        hashMap.put("fileName", fileName);
        HttpUtil.sendDataAsync(activity, HttpUrl.DOWNLOADIMG, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onResult(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] imgData = response.body().bytes();
                if(imgData.length > 0){
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                    //保存到本地
                    Boolean result = saveBitmapToSDCard(imgData, fileName);
                    if(result){
                        callback.onResult(fileName);

                        return;
                    }
                }
                callback.onResult(null);
            }
        });
    }

    /**
     * 扫描记录二维码进入查看照片需要
     * @param activity
     * @param category
     * @param fileName
     * @param companyId
     * @param callback
     */
    public static void downloadImage(Activity activity, Integer category, final String fileName,String companyId,final DownloadImageCallback callback){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("category", category + "");
        hashMap.put("fileName", fileName);
        hashMap.put("companyId",companyId);
        HttpUtil.sendDataAsync(activity, HttpUrl.DOWNLOADIMAGE, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onResult(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] imgData = response.body().bytes();
                if(imgData.length > 0){
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                    //保存到本地
                    Boolean result = saveBitmapToSDCard(imgData, fileName);
                    if(result){
                        callback.onResult(fileName);

                        return;
                    }
                }
                callback.onResult(null);
            }
        });
    }

    public static void downloadDfuZip(Activity activity, final String fileName,final DownloadImageCallback callback){
        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("category", category + "");
        hashMap.put("fileName", fileName);
        HttpUtil.sendDataAsync(activity, HttpUrl.DOWNLOAD_DFU_UPGRADE, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onResult(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] imgData = response.body().bytes();
                if(imgData.length > 0){
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                    //保存到本地
                    Boolean result = saveBitmapToSDCard(imgData, fileName);
                    if(result){
                        callback.onResult(fileName);
                        return;
                    }
                }
                callback.onResult(null);
            }
        });
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
     * @param imgData
     * @param fileName
     * @return
     */
    public static Boolean saveBitmapToSDCard(byte[] imgData, String fileName){
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {  // 检测sd是否可用
            return false;
        }
        if(imgData == null || fileName == null){
            return false;
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String picName = path + fileName;   //图片名字
            FileOutputStream out = new FileOutputStream(picName);
            //写入图片数据
            out.write(imgData);
            // 关闭流
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
