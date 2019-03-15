package cn.fengwoo.sealsteward.utils;

public class HttpUrl {
    public static final String URL = "http://192.168.0.117/sealappservice/";  //测试环境
//    public static final String URL = "http://192.168.0.117:8800/";  //调试环境
    /**
     * 登录
     */
    public static final String LOGIN = "user/login";
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
    /**
     * 更改姓名
     */
    public static final String UPDATEREALNAME = "/user/updaterealname";
    /**
     * 更改电话
     */
    public static final String UPDATEMOBILEPHONE = "/user/updatemobilephone";
    /**
     * 更改邮箱
     */
    public static final String UPDATEUSEREMAIL = "/user/updateuseremail";
    /**
     * 修改头像
     */
    public static final String UPDATEHEADPORTRAIT = "/user/updateheadportrait";
    /**
     * 上传头像
     */
    public static final String UPLOADIMAGE = "/file/uploadimage";
    /**
     * 添加公司
     */
    public static final String ADDCOMPANY = "/company/addcompany";
    /**
     * 用户公司列表
     */
    public static final String USERCOMPANY = "/company/usercompany";
    /**
     * 删除公司
     */
    public static final String DELETECOMPANY = "/company/deletecompany";
    /**
     * 公司详情
     */
    public static final String COMPANYINFO = "/company/companyinfo";
    /**
     * 签名
     */
    public static final String AUTOGRAPH = "/user/updateautograph";
    /**
     * 广告
     */
    public static final String BANNER = "/user/banner";
    /**
     * 下载图片
     */
    public static final String DOWNLOADIMG = "/file/scanimage";
    /**
     * 组织架构
     */
    public static final String  ORGANIZATIONAL_STRUCTURE = "/company/companyorgstr";

    /**
     * 用户详情
     */
    public static final String  USER_INFO = "/company/companyorgstr";
    /**
     * 添加用户
     */
    public static final String  ADD_USER = "/user/adduser";
}