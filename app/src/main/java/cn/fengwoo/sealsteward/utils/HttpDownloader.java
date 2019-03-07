package cn.fengwoo.sealsteward.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载工具类
 */
public class HttpDownloader {
    private URL url = null;
    private final String TAG = "TAG";
    private static String path = "/sdcard/myHead/";// sd路径

    /**
     * 读取文本文件
     *
     * @param urlStr url路径
     * @return 文本信息
     * 根据url下载文件，前提是这个文件中的内容是文本，
     * 1.创建一个URL对象
     * 2.通过URL对象，创建一个Http连接
     * 3.得到InputStream
     * 4.从InputStream中得到数据
     */
    public String download(String urlStr) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader bufferedReader = null;

        try {
            url = new URL(urlStr);
            //创建http连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //使用IO流读取数据
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("TAG", "下载txt文件");
        Log.e("TAG", sb.toString());
        return sb.toString();
    }
/*
    public static Uri getImage2(String path, File cacheDir){
        File localFile = new File(cacheDir,MD5.getMD5(path)+path.substring(path.lastIndexOf(".")));
        if(localFile.exists()){
            return Uri.fromFile(localFile);
        }else
        {
            HttpURLConnection conn;
            try {
                conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if(conn.getResponseCode() == 200){
                    System.out.println("tdw");
                    FileOutputStream outputStream = new FileOutputStream(localFile);
                    InputStream inputStream = conn.getInputStream();
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    while((length=inputStream.read(buffer))!=-1){
                        outputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    outputStream.close();
                    return Uri.fromFile(localFile);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }*/

    public static void setPicToView(Bitmap bitmap){
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)){  //检测SD卡是否可用
            return;
        }
        FileOutputStream fileOutputStream = null;
        File file = new File(path);
        file.mkdirs();  //创建文件夹
        String fileName = path + "head.jpg";  //图片名字
        try {
            fileOutputStream = new FileOutputStream(fileName);
            if(bitmap != null){
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream); //把数据写入文件
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
