package cn.fengwoo.sealsteward.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

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



    /**
     * 握手帧类型
     */
    public static final byte HANDSHAKE = (byte) 0xA0;
    /**
     * 启动
     */
    public static final byte START = (byte) 0xA1;
    /**
     * 盖章记录上传
     */
    public static final byte SEALHISTORYUPLOAD = (byte) 0xA2;
    /**
     * 通知印章上传盖章历史记录
     */
    public static final byte NOTIFYHISTORYUPLOAD = (byte) 0xA3;
    /**
     * 修改按键密码
     */
    public static final byte UPDATEKEPWD = (byte) 0xB0;
    /**
     * 修改按键密码权限
     */
    public static final byte CHANGEPWDPOWER = (byte) 0xB1;
    /**
     * 删除按键密码
     */
    public static final byte DELETEPRESSPWD = (byte) 0xB2;
    /**
     * 查询盖章延时时间
     */
    public static final byte SELECTSEALDELAY = (byte) 0xB3;
    /**
     * 设置盖章延时时间
     */
    public static final byte SETSEALDELAY = (byte) 0xB4;
    /**
     * 添加按键密码和权限
     */
    public static final byte ADDPRESSPWD = (byte) 0xA4;
    /**
     * 重置
     */
    public static final byte RESET = (byte) 0xA5;
    /**
     *设置长按时间
     */
    public static final byte PRESSTIME = (byte) 0xA6;
    /**
     * 查询长按时间
     */
    public static final byte SELECTPRESSTIME = (byte) 0xA7;
    /**
     * 电量查询
     */
    public static final byte ElECTRIC = (byte) 0xAF;
    /**
     * 锁定
     */
    public static final byte LOCK = (byte) 0xA9;
    /**
     * 录制指纹
     */
    public static final byte RECORDING = (byte) 0xAA;
    /**
     * 删除指纹
     */
    public static final byte DELETE = (byte) 0xAB;
    /**
     * 设置指纹权限
     */
    public static final byte SET = (byte) 0xAC;
    /**
     * 查询指纹
     */
    public static final byte SELECT = (byte) 0xAD;
    /**
     * 盖章历史记录上传
     */
    public static final byte UPLOAD = (byte) 0xAE;

    /**
     * 获取时间字节数据（见协议定义）
     *
     * @param date
     * @return
     */
    public static byte[] getDateTime(Date date) {
        byte[] bytes = new byte[]{};
        if (date != null) {
            //TODO
            //获取时间年月日
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
//            byte[] yearBytes = DataTrans.shortToByteArray((short) year, true);
            int y = year % 2000;
            byte yearB = (byte) y;
            bytes = new byte[]{yearB, (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) second};

        }
        return bytes;
    }

    public static byte[] getDateTime() {
        return getDateTime(new Date());
    }

    /**
     * 启动数据
     */
    public static byte[] getStartData() {
        int startInt = Integer.parseInt("80");
        byte[] time = DataTrans.intToByteArray(3, true);
        byte[] startYear = DataTrans.shortToByteArray((short) 2019, true);
        byte[] startByte = new byte[]{(byte) startInt,
                1, 2, 3, 4, 5, 6,
                0, 0, 0, 26, 27, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 38,
                time[0], time[1], time[2], time[3],
                startYear[0], startYear[1], 9, 9, 19, 19, 19};
        return startByte;
    }

    /**
     * 启动
     */
    public static byte[] startData(){
        byte[] time = DataTrans.shortToByteArray((short) 5,false);  //可盖章次数
        int year = 2019 % 2000;
        byte failYear = (byte) year;
        byte[] startByte = new byte[]{ time[0], time[1],failYear,9, 9, 19, 19, 19};
        return startByte;
    }
    /**
     * 指纹权限
     * @return
     */
    public static byte[] setFingerprint() {
        long setLong = DataTrans.parseLong("EB");
        int setInt = DataTrans.integer("25");
        byte[] setBytes = DataTrans.intToByteArray(3, true);
        byte[] failYear = DataTrans.shortToByteArray((short) 2019, true);
        byte[] setFingerprint = new byte[]{(byte) setLong, (byte) setInt,
                setBytes[0], setBytes[1], setBytes[2], setBytes[3],
                failYear[0], failYear[1], 9, 9, 19, 19, 19};
        return setFingerprint;
    }

    /**
     * 添加按键密码和权限
     * @return
     */
    public static byte[] addPressPwd(){
        byte[] time = DataTrans.shortToByteArray((short) 10,false);
        int year = 2019 % 2000;
        byte failYear = (byte) year;
        byte[] addPressPwd = new byte[]{ 1,1,1,1,1,1,time[0], time[1], failYear ,9, 9, 19, 19, 19};
        return addPressPwd;
    }

    /**
     * 修改按键密码权限
     * @return
     */
    public static byte[] changePwdPower(byte[] bytes){
        //     byte[] changePwdCode = DataTrans.intToByteArray(1,false);
        byte[] time = DataTrans.shortToByteArray((short) 10,false);
        int year = 2019 % 2000;
        byte failYear = (byte) year;
        byte[] changePrePow = new byte[]{bytes[0],bytes[1],bytes[2],bytes[3],time[0],time[1],failYear ,9, 9, 19, 19, 19};
        return changePrePow;
    }
    /**
     * 修改按键密码
     */
    public static byte[] changePwd(byte[] bytes){
        byte[] keyPwd = new byte[]{bytes[0],bytes[1],bytes[2],bytes[3], 1,1, 1, 1, 1, 1, 6, 5, 4, 3, 2, 1};
        return keyPwd;
    }
    /**
     * 删除按键密码
     */
    public static byte[] deletePressPwd(byte[] bytes){
       /* byte[] deletePwdCode = DataTrans.intToByteArray(1,false);
        byte[] deletePrePwd = new byte[]{deletePwdCode[0], deletePwdCode[1], deletePwdCode[2], deletePwdCode[3]};*/
        byte[] deletePrePwd = new byte[]{bytes[0],bytes[1],bytes[2],bytes[3]};
        return deletePrePwd;
    }


}
