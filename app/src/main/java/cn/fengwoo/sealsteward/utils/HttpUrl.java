package cn.fengwoo.sealsteward.utils;

public class HttpUrl {
    public static final String URL = "http://192.168.0.117/sealappservice/";  //测试环境
//    public static final String URL = "http://192.168.0.117:8800/";  //调试环境
    /**
     * 登录
     */
    public static final String  LOGIN = "user/login";
    /**
     * 退出
     */
    public static final String LOGOUT = "user/logout";
    /**
     * 注册
     */
    public static final String REGISTER = "user/register";
    /**
     * 获取验证码
     */
    public static final String SENDVERIFICATIONCODE = "user/sendverificationcode";
    /**
     * 验证验证码
     */
    public static final String CHECKVERIFICATIONCODE = "/user/checkverificationcode";
    /**
     * 忘记密码
     */
    public static final String FORGETPASSWORD = "/user/forgetpassword";
    /**
     * 用户信息
     */
    public static final String USERINFO = "/user/userinfo";
}