package cn.fengwoo.sealsteward.utils;

public class HttpUrl {
//    public static final String URL = "http://192.168.0.117/sealappservice/";  //测试环境
//    public static final String URL = "http://192.168.0.117:8800/";  //调试环境
    public static final String URL = "http://www.baiheyz.com:8080/sealappservicetest/";  //线上

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
     * 更新地址
     */
    public static final String UPDATEADDRESS = "/user/updateaddress";
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
    public static final String ORGANIZATIONAL_STRUCTURE = "/company/companyorgstr";
    /**
     * 建议
     */
    public static final String SUGGESTION = "/user/feedback";
    /**
     * 修改密码
     */
    public static final String UPDATEPASSWORD = "/user/updatepassword";

    /**
     * 用户详情
     */
    public static final String USER_INFO = "/company/companyorgstr";
    /**
     * 添加用户
     */
    public static final String ADD_USER = "/user/adduser";
    /**
     * delete用户
     */
    public static final String DELETE_USER = "/company/deleteuser";
    /**
     * 添加用户权限
     */
    public static final String ADD_USER_PERMISSION = "/user/adduserpermission";
    /**
     * 核对添加seal
     */
    public static final String SEAL_CHECK_ADD = "/seal/checkadd";
    /**
     * 添加seal
     */
    public static final String ADD_SEAL = "/seal/addseal";
    /**
     * delete seal
     */
    public static final String DELETE_SEAL = "/seal/deleteseal";
    /**
     * 添加用印申请
     */
    public static final String ADDUSESEAL = "/apply/adduseseal";
    /**
     * 检验用印申请
     */
    public static final String CHECKUSESEAL = "/apply/checkuseseal";
    /**
     * 审批列表
     */
    public static final String APPLYLIST = "/apply/applylist";

    /**
     * seal 信息
     */
    public static final String SEAL_INFO = "/seal/sealinfo";

    /**
     * seal 更新信息
     */
    public static final String SEAL_UPDATE_INFO = "/seal/updateseal";
    /**
     * 申请详情
     */
    public static final String APPLYDETAIL = "/apply/applydetail";
    /**
     * 盖章记录详情
     */
    public static final String SEALRECORDLIST = "/stamp/applystamprecordlist";
    /**
     * 盖章记录
     */
    public static final String STAMPRECORDAPPLYLIST = "/apply/stamprecordapplylist";

    /**
     * seal 更新地理围栏信息
     */
    public static final String SEAL_UPDATE_ENCLOSURE = "/seal/updateenclosure";

    /**
     * 更新审批流
     */
    public static final String UPDATE_EXAMINE = "/approvalflow/updatesealapprovalflow";

    /**
     * 申请列表（--我要盖章--）
     */
    public static final String USE_SEAL_APPLYLIST = "/apply/usesealapplylist";

    /**
     * seal列表
     */
    public static final String SEAL_LIST = "/seal/seallist";
    /**
     * 充值记录
     */
    public static final String RECHARGERECORD = "/cost/rechargerecord";
    /**
     * 费用套餐
     */
    public static final String RECHARGE_PACKAGES = "/cost/rechargepackages";
    /**
     * 支付下单
     */
    public static final String ADDORDER = "/cost/addorder";

    /**
     * delete 组织
     */
    public static final String DELETE_ORG = "/company/deleteorgstr";

    /**
     * edit 组织名字
     */
    public static final String EDIT_ORG = "/company/updateorgstrname";

}