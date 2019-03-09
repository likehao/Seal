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
        String userEmail = sharedPreferences.getString("userEmail","");
        String headPortrait = sharedPreferences.getString("headPortrait","");
        String autoGraph = sharedPreferences.getString("autoGraph","");
        String address = sharedPreferences.getString("address","");
        boolean authStatus = sharedPreferences.getBoolean("authStatus",false);
        String companyName = sharedPreferences.getString("companyName","");
        String orgStructureName = sharedPreferences.getString("orgStructureName","");
        String job = sharedPreferences.getString("job","");
        boolean needSync = sharedPreferences.getBoolean("needSync",false);


        LoginData user = new LoginData();
        user.setId(userId);
        user.setRealName(realName);
        user.setMobilePhone(mobilePhone);
        user.setCompanyId(companyId);
        user.setToken(token);
        user.setUserEmail(userEmail);
        user.setHeadPortrait(headPortrait);
        user.setAutoGraph(autoGraph);
        user.setAddress(address);
        user.setAuthStatus(authStatus);
        user.setCompanyName(companyName);
        user.setOrgStructureName(orgStructureName);
        user.setJob(job);
        user.setNeedSync(needSync);

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
        editor.putString("userEmail",user.getUserEmail());
        editor.putString("headPortrait",user.getHeadPortrait());
        editor.putString("autoGraph",user.getAutoGraph());
        editor.putString("address",user.getAddress());
        editor.putBoolean("authStatus",user.getAuthStatus());
        editor.putString("companyName",user.getCompanyName());
        editor.putString("orgStructureName",user.getOrgStructureName());
        editor.putString("job",user.getJob());
        editor.putBoolean("needSync",user.getNeedSync());
     //   editor.putString("systemFuncList", String.valueOf(user.getSystemFuncList()));

        //提交
        editor.commit();
    }

}
