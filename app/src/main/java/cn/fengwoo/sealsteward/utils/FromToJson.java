package cn.fengwoo.sealsteward.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import cn.jiguang.net.HttpResponse;

/**
 * 根据泛型返回解析制定的类型
 */
public class FromToJson{

    public <T> T fromToJson(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<T>(){}.getType();
        return gson.fromJson(json, type);
    }
}
