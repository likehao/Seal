package cn.fengwoo.sealsteward.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import cn.fengwoo.sealsteward.entity.LoginData;

import static android.content.Context.MODE_PRIVATE;

/**
 * 登录存储信息
 */
public class CommonUtil {

    public static LoginData getUserData(Activity activity){
        //获取SharedPreferences对象
        SharedPreferences sharedPreferences = activity.getSharedPreferences("userdata",MODE_PRIVATE);
        //获取Editor对象
        String userId = sharedPreferences.getString("id","");
        String realName = sharedPreferences.getString("realName","");
        String mobilePhone = sharedPreferences.getString("mobilePhone","");
        String token = sharedPreferences.getString("token","");
        String companyId = sharedPreferences.getString("companyId","");
        //TODO

        LoginData user = new LoginData();
        user.setId(userId);
        user.setRealName(realName);
        user.setMobilePhone(mobilePhone);
        user.setCompanyId(companyId);

        return user;
    }

    public static void setUserData(Activity activity,LoginData user){
        //获取SharedPreferences对象
        SharedPreferences sharedPreferences = activity.getSharedPreferences("userdata",MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //设置参数
        editor.putString("id",user.getId());
        editor.putString("realName",user.getRealName());
        editor.putString("mobilePhone",user.getMobilePhone());
        editor.putString("token",user.getToken());
        editor.putString("companyId",user.getCompanyId());
        //TODO

        //提交
        editor.commit();
    }

}
