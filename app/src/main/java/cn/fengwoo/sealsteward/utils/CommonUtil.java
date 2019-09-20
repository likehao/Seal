package cn.fengwoo.sealsteward.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.fengwoo.sealsteward.entity.LoginData;

import static android.content.Context.MODE_PRIVATE;

/**
 * 登录存储信息
 */
public class CommonUtil {

    public static LoginData getUserData(Activity activity) {
        //获取SharedPreferences对象
        SharedPreferences sharedPreferences = activity.getSharedPreferences("newuserdata", MODE_PRIVATE);
        //获取Editor对象
        String userId = sharedPreferences.getString("id", "");
        String realName = sharedPreferences.getString("realName", "");
        String mobilePhone = sharedPreferences.getString("mobilePhone", "");
        String token = sharedPreferences.getString("token", "");
        String companyId = sharedPreferences.getString("companyId", "");
        String userEmail = sharedPreferences.getString("userEmail", "");
        String headPortrait = sharedPreferences.getString("headPortrait", "");
        String autoGraph = sharedPreferences.getString("autoGraph", "");
        String address = sharedPreferences.getString("address", "");
        boolean authStatus = sharedPreferences.getBoolean("authStatus", false);
        String companyName = sharedPreferences.getString("companyName", "");
        String orgStructureName = sharedPreferences.getString("orgStructureName", "");
        String orgStructureId = sharedPreferences.getString("orgStructureId", "");
        String job = sharedPreferences.getString("job", "");
        boolean needSync = sharedPreferences.getBoolean("needSync", false);


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
        user.setOrgStructureId(orgStructureId);
        user.setJob(job);
        user.setNeedSync(needSync);

        return user;
    }

    @SuppressLint("ApplySharedPref")
    public static void setUserData(Activity activity, LoginData user) {
        //获取SharedPreferences对象
        SharedPreferences sharedPreferences = activity.getSharedPreferences("newuserdata", MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //设置参数
        editor.putString("id", user.getId());
        editor.putString("realName", user.getRealName());
        editor.putString("mobilePhone", user.getMobilePhone());
        editor.putString("token", user.getToken());
        editor.putString("companyId", user.getCompanyId());
        editor.putString("userEmail", user.getUserEmail());
        editor.putString("headPortrait", user.getHeadPortrait());
        editor.putString("autoGraph", user.getAutoGraph());
        editor.putString("address", user.getAddress());
        editor.putBoolean("authStatus", user.getAuthStatus());
        editor.putString("companyName", user.getCompanyName());
        editor.putString("orgStructureName", user.getOrgStructureName());
        editor.putString("orgStructureId", user.getOrgStructureId());
        editor.putString("job", user.getJob());
        editor.putBoolean("needSync", user.getNeedSync());
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


    public static final byte WRITE_VOICE = (byte) 0xB5;
    public static final byte READ_VOICE = (byte) 0xB6;



    /**
     * 添加按键密码和权限
     */
    public static final byte ADDPRESSPWD = (byte) 0xA4;
    /**
     * 重置
     */
    public static final byte RESET = (byte) 0xA5;
    /**
     * 设置长按时间
     */
    public static final byte PRESSTIME = (byte) 0xA6;
    /**
     * 查询长按时间
     */
    public static final byte SELECTPRESSTIME = (byte) 0xA7;
     /**
     * illegal
     */
    public static final byte ILLEGAL = (byte) 0xA8;
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
     * 擦除历史记录
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

    public static byte[] getDateTime(long timeStamp) {
        return getDateTime(new Date(timeStamp));
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
    public static byte[] startData(int sealTimes, String timeStamp) {
        byte[] time = DataTrans.shortToByteArray((short) sealTimes, false);  //可盖章次数

//        SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH mm ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date(Long.valueOf(timeStamp));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR)% 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        byte[] startByte = new byte[]{time[0], time[1], (byte) year, (byte) month, (byte) day, (byte) hour, (byte) min, (byte) sec};
        return startByte;
    }


    /**
     * 添加按键密码和权限
     */
    public static byte[] addPwd(String pwd,int sealTimes, String timeStamp) {
        byte[] time = DataTrans.shortToByteArray((short) sealTimes, false);

        Date date = new Date(Long.valueOf(timeStamp));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR)% 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        byte[] pwdBytes = new byte[6];
        for(int i=0;i<pwd.length();i++){
            String subStr = pwd.substring(i, i + 1);
            int a = Integer.valueOf(subStr);
            pwdBytes[i] = (byte)a;
        }

        byte[] startByte = new byte[]{pwdBytes[0],pwdBytes[1],pwdBytes[2],pwdBytes[3],pwdBytes[4],pwdBytes[5],time[0], time[1], (byte) year, (byte) month, (byte) day, (byte) hour, (byte) min, (byte) sec};
        return startByte;
    }


    /**
     * 修改次数
     */
    public static byte[] changeTimes(String pwdcode,int sealTimes, String timeStamp) {
        byte[] time = DataTrans.shortToByteArray((short) sealTimes, false);

        Date date = new Date(Long.valueOf(timeStamp));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR)% 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        byte[] pwdCodeBytes = DataTrans.intToBytesLittle(Integer.parseInt(pwdcode));

        byte[] startByte = new byte[]{pwdCodeBytes[0],pwdCodeBytes[1],pwdCodeBytes[2],pwdCodeBytes[3],time[0], time[1], (byte) year, (byte) month, (byte) day, (byte) hour, (byte) min, (byte) sec};
        return startByte;
    }


    /**
     * 指纹权限
     *
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
     *
     * @return
     */
    public static byte[] addPressPwd() {
        byte[] time = DataTrans.shortToByteArray((short) 10, false);
        int year = 2019 % 2000;
        byte failYear = (byte) year;
        byte[] addPressPwd = new byte[]{1, 1, 1, 1, 1, 1, time[0], time[1], failYear, 9, 9, 19, 19, 19};
        return addPressPwd;
    }

    /**
     * 修改按键密码权限
     *
     * @return
     */
    public static byte[] changePwdPower(byte[] bytes) {
        //     byte[] changePwdCode = DataTrans.intToByteArray(1,false);
        byte[] time = DataTrans.shortToByteArray((short) 10, false);
        int year = 2019 % 2000;
        byte failYear = (byte) year;
        byte[] changePrePow = new byte[]{bytes[0], bytes[1], bytes[2], bytes[3], time[0], time[1], failYear, 9, 9, 19, 19, 19};
        return changePrePow;
    }

    /**
     * 修改按键密码
     */
    public static byte[] changePwd(byte[] bytes) {
        byte[] keyPwd = new byte[]{bytes[0], bytes[1], bytes[2], bytes[3], 1, 1, 1, 1, 1, 1, 6, 5, 4, 3, 2, 1};
        return keyPwd;
    }

    /**
     * 删除按键密码
     */
    public static byte[] deletePressPwd(byte[] bytes) {
       /* byte[] deletePwdCode = DataTrans.intToByteArray(1,false);
        byte[] deletePrePwd = new byte[]{deletePwdCode[0], deletePwdCode[1], deletePwdCode[2], deletePwdCode[3]};*/
        byte[] deletePrePwd = new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]};
        return deletePrePwd;
    }

    /**
     * 录入指纹
     */
    public static byte[] recordFingerprint(int count,String failTime){
        byte[] time = DataTrans.shortToByteArray((short) count,false);  //可盖章次数

        Date date = new Date(Long.valueOf(failTime));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR)% 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        byte[] startFingerByte = new byte[]{time[0], time[1], (byte) year, (byte) month, (byte) day, (byte) hour, (byte) min, (byte) sec};

        return startFingerByte;
    }
}
