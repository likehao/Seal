package cn.fengwoo.sealsteward.utils;

public class HttpUrl {
    //    public static final String URL = "http://192.168.0.117/sealappservice/";  //测试环境
//    public static final String URL = "http://192.168.1.117:8800/";  //调试环境
//    public static final String URL = "http://www.baiheyz.com:8080/sealappservicetest/";  //测试
//    public static final String URL = "http://192.168.1.101:8800";  //测试
    public static final String URL = "http://www.baiheyz.com:8080/bhsealappservice/";    //线上


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
     * 数据同步
     */
    public static final String SYNC = "user/sync";
    /**
     * 获取验证码
     */
    public static final String SENDVERIFICATIONCODE = "user/sendverificationcode";
    /**
     * 验证验证码
     */
    public static final String CHECKVERIFICATIONCODE = "user/checkverificationcode";
    /**
     * 忘记密码
     */
    public static final String FORGETPASSWORD = "user/forgetpassword";
    /**
     * 用户信息
     */
    public static final String USERINFO = "user/userinfo";
    /**
     * 更改姓名
     */
    public static final String UPDATEREALNAME = "user/updaterealname";
    /**
     * 更改电话
     */
    public static final String UPDATEMOBILEPHONE = "user/updatemobilephone";
    /**
     * 更改邮箱
     */
    public static final String UPDATEUSEREMAIL = "user/updateuseremail";
    /**
     * 修改头像
     */
    public static final String UPDATEHEADPORTRAIT = "user/updateheadportrait";
    /**
     * 更新地址
     */
    public static final String UPDATEADDRESS = "user/updateaddress";
    /**
     * 上传头像
     */
    public static final String UPLOADIMAGE = "file/uploadimage";
    /**
     * 添加公司
     */
    public static final String ADDCOMPANY = "company/addcompany";
    /**
     * 用户公司列表
     */
    public static final String USERCOMPANY = "company/usercompany";
    /**
     * 删除公司
     */
    public static final String DELETECOMPANY = "company/deletecompany";
    /**
     * 公司详情
     */
    public static final String COMPANYINFO = "company/companyinfo";
    /**
     * 签名
     */
    public static final String AUTOGRAPH = "user/updateautograph";
    /**
     * 广告
     */
    public static final String BANNER = "user/banner";
    /**
     * 下载图片
     */
    public static final String DOWNLOADIMG = "file/scanimage";
    /**
     * 组织架构
     */
    public static final String ORGANIZATIONAL_STRUCTURE = "company/companyorgstr";

    /**
     * 组织架构
     */
    public static final String ORGANIZATIONAL_STRUCTURE_PLUS = "company/companyorgstr";

    /**
     * 建议
     */
    public static final String SUGGESTION = "user/feedback";
    /**
     * 修改密码
     */
    public static final String UPDATEPASSWORD = "user/updatepassword";

    /**
     * 用户详情
     */
    public static final String USER_INFO = "company/companyorgstr";
    /**
     * 添加用户
     */
    public static final String ADD_USER = "user/adduser";
    /**
     * delete用户
     */
    public static final String DELETE_USER = "company/deleteuser";
    /**
     * 添加用户权限
     */
    public static final String ADD_USER_PERMISSION = "user/adduserpermission";
    /**
     * 核对添加seal
     */
    public static final String SEAL_CHECK_ADD = "seal/checkadd";
    /**
     * 添加seal
     */
    public static final String ADD_SEAL = "seal/addseal";
    /**
     * delete seal
     */
    public static final String DELETE_SEAL = "seal/deleteseal";
    /**
     * 添加用印申请
     */
    public static final String ADDUSESEAL = "apply/adduseseal";
    /**
     * 检验用印申请（用印申请）
     */
    public static final String CHECKUSESEAL = "apply/checkuseseal";
    /**
     * 重提用印申请
     */
    public static final String APPLY_REMENTION = "apply/applyremention";
    /**
     * 审批列表
     */
    public static final String APPLYLIST = "apply/applylist";

    /**
     * seal 信息
     */
    public static final String SEAL_INFO = "seal/sealinfo";

    /**
     * seal 更新信息
     */
    public static final String SEAL_UPDATE_INFO = "seal/updateseal";
    /**
     * 申请详情
     */
    public static final String APPLYDETAIL = "apply/applydetail";
    /**
     * 盖章记录详情
     */
    public static final String SEALRECORDLIST = "stamp/applystamprecordlist";
    /**
     * 盖章记录
     */
    public static final String STAMPRECORDAPPLYLIST = "apply/stamprecordapplylist";

    /**
     * seal 更新地理围栏信息
     */
    public static final String SEAL_UPDATE_ENCLOSURE = "seal/updateenclosure";

    /**
     * 更新审批流
     */
    public static final String UPDATE_EXAMINE = "approvalflow/updatesealapprovalflow";

    /**
     * 申请列表（--我要盖章--）
     */
    public static final String USE_SEAL_APPLYLIST = "apply/usesealapplylist";

    /**
     * seal列表
     */
    public static final String SEAL_LIST = "seal/seallist";
    /**
     * 充值记录
     */
    public static final String RECHARGERECORD = "cost/rechargerecord";
    /**
     * 费用套餐
     */
    public static final String RECHARGE_PACKAGES = "cost/rechargepackages";
    /**
     * 支付下单
     */
    public static final String ADDORDER = "cost/addorder";

    /**
     * delete 组织
     */
    public static final String DELETE_ORG = "company/deleteorgstr";

    /**
     * edit 组织名字
     */
    public static final String EDIT_ORG = "company/updateorgstrname";
    /**
     * 消息数
     */
    public static final String MESSAGE = "user/message";
    /**
     * 消息列表
     */
    public static final String MESSAGEDETAIL = "user/messagedetail";
    /**
     * 已读消息
     */
    public static final String UPDATEREADMSG = "user/updatereadmessage";

    /**
     * stamp upload record
     */
    public static final String STAMP_UPLOAD_RECORD = "stamp/uploadrecord";

    /**
     * add 密码用户
     */
    public static final String ADD_PWD_USER = "seal/addofflineuser";
    /**
     * 关闭单据
     */
    public static final String APPLICLOSE = "apply/applyclose";
    /**
     * 下载PDF
     */
    public static final String DOWNLOADPDF = "file/downloadpdf";

    /**
     * update 密码用户
     */
    public static final String UPDATE_PWD_USER = "seal/updateofflineuser";

    /**
     * 获取密码用户列表
     */
    public static final String PWD_USER_LIST = "seal/offlineuserlist";

    /**
     * delete 密码用户
     */
    public static final String DELETE_PWD_USER = "seal/deleteofflineuser";

    /**
     * reset seal
     */
    public static final String RESET_SEAL = "seal/resetofflineuser";
    /**
     * 使用说明书
     */
    public static final String USEINSTRUCTIONS = "http://www.baiheyz.com/directions/index.html";

    /**
     * 添加部门
     */
    public static final String ADD_ORG = "company/addorgstr";
    /**
     * 上传记录图片
     */
    public static final String UPDATESTAMPIMAGE = "stamp/updatestampimage";
    /**
     * 切换公司
     */
    public static final String SWITCHCOMPANY = "company/switch";
    /**
     * 更新公司
     */
    public static final String UPDATECOMPANY = "company/updatecompany";

    /**
     * 上传历史记录
     */
    public static final String UPLOAD_HISTORY_RECORD = "stamp/uploadhistoryrecord";

    /**
     * 获取系统时间
     */
    public static final String SYSTEM_TIME = "user/systemtime";

    /**
     * 用印审批
     */
    public static final String APPROVE = "apply/approve";
    /**
     * 删除签名
     */
    public static final String DELETEAUTOGRAPH = "user/deleteautograph";

    /**
     * illegal
     */
    public static final String ILLEGAL = "seal/illegalstamp";


    /**
     * TRIGGERED_ENCLOSURE 超出围栏报警
     */
    public static final String TRIGGERED_ENCLOSURE = "seal/triggeredenclosure";
    /**
     * 离线记录
     */
    public static final String OFFLINERECORD = "stamp/offlinestamprecord";

    /**
     * 离线记录查询
     */
    public static final String OFFLINERECORD_search = "stamp/flownumberofflinestamprecord";

    /**
     * 审批进度
     */
    public static final String APPROVEPROGRESS = "apply/approveprogress";

    /**
     * 改变部门
     */
    public static final String CHANGE_ORG = "company/moveuserorseal";
    /**
     * 添加审批流
     */
    public static final String ADDAPPROVALFLOW = "approvalflow/addsealapprovalflow";
    /**
     * 删除审批流
     */
    public static final String DELETEAPPROVALFLOW = "approvalflow/deletesealapprovalflow";

    /**
     * 改变seal
     */
    public static final String REPLACE_SEAL = "seal/replaceseal";

    /**
     * dfu version check
     */
    public static final String DFU_UPGRADE_CHECK = "seal/lastsealfirmware";
    /**
     * dfu 升级包下载
     */
    public static final String DOWNLOAD_DFU_UPGRADE = "file/downloadupgrade";

    /**
     * 个人认证
     */
    public static final String PERSONAL_AUTH = "user/userauthtoken";

    /**
     * 个人认证结果上传服务器
     */
    public static final String UPLOAD_SUCCESSFUL_AUTH = "user/updateuserauthstatus";

    /**
     * 个人认证结果上传服务器
     */
    public static final String USER_OFFLINE_STAMP_RECORD = "stamp/userofflinestamprecord";

    /**
     * qrcode login
     */
    public static final String SCAN_QRCODE_LOGIN = "user/scanqrcodelogin";

    /**
     * 添加人员列表
     */
    public static final String APPLY_JOIN_USER_LIST = "company/applyjoinuserlist";

    /**
     * 申请加入公司
     */
    public static final String APPLY_JOIN_COMPANY = "company/applyjoincompany";

    /**
     * 处理申请
     */
    public static final String HANDLE_JOIN_COMPANY = "company/handlejoincompany";

    /**
     * 更新读取状态
     */
    public static final String UPDATE_JOIN_READ_STATUS = "company/updatejoinreadstatus";

    /**
     * 撤销单据
     */
    public static final String CANCEL_APPLY = "apply/applyrevoke";
    /**
     * 修改手机号码
     */
    public static final String RESETACCOUNT = "user/resetaccount";
    /**
     * 转让公司
     */
    public static final String CHANGEBELONG = "company/changebusinessadmin";

    /**
     * 修改职位
     */
    public static final String CHANGE_JOB = "user/updatejob";

    /**
     * 统计
     */
    public static final String ORG_STATISTIC = "statistic/orgstructurestatistic";
    /**
     * 印章统计
     */
    public static final String SEAL_STATISTIC = "statistic/sealstatistic";
    /**
     * 用户统计
     */
    public static final String USER_STATISTIC = "statistic/userstatistic";


    public static final String GET_ACC = "sdk/getsdkaccount";
    public static final String DEVICE_ACCESS = "sdk/sdkdeviceaccess";


}