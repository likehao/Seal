package cn.fengwoo.sealsteward.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 根据泛型返回解析制定的类型
 */
public class FromToJson{
    public <T> T fromToJson(String json){
        Type type = new TypeToken<T>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }
}
