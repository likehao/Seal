package cn.fengwoo.sealsteward.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 将file文件保存到map当中
 */
public class FileToHashMap {

    public static HashMap<String, String> fileToMap(String path){
        HashMap<String,String> map = new HashMap<>();
        File file = new File(path);
        if (!file.exists()){
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String temp = "";
            int line = 1;
            while ((temp = reader.readLine())!=null){
                String[] arr = temp.split(":");
                map.put(arr[0], arr[1]);
                line++;
            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}
