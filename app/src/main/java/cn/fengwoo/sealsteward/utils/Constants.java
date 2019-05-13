package cn.fengwoo.sealsteward.utils;

import java.util.UUID;

public class Constants {

    // EasySP 保存的数据
    // dataProtocolVersion   String // 是二期还是三期   2 为二，3为三
    // mac  String  // mac地址
    //currentSealId  // 当前seal id
    //currentApplyId  // 当前 申请 id
    // permission // permission json
    // isAdmin   boolean // 权限是不是admin

    // scope   String // radius
    // latitude   String // 纬度
    // longitude   String // 经度

    // DFU
    // dfu_version String
    // dfu_file_name String
    // dfu_content String

    // enableEnclosure boolean // 是否开启电子围栏

    // take_pic_mode    String // 拍照模式： 0：手动   1：自动

    // finger_print   String // 指纹： 0：关    1：开
    // hasNewDfuVersion   String // 指纹： 0：无    1：有

    // dfu_current_version   String   // 当前固件版本

    public static final int TO_NEARBY_DEVICE = 20;
    public static final int TO_WANT_SEAL = 21;

    public static final String permission1 = "266b076d34e511e998a22cfda1ba8f68";//添加印章"
    public static final String permission2 = "7680b087421611e9af5a2cfda1ba8f68";//"编辑印章信息"
    public static final String permission3 = "267381ce34e511e998a22cfda1ba8f68";// "删除印章"
    public static final String permission4 = "267692e734e511e998a22cfda1ba8f68";//"设置印章长按时间"
    public static final String permission5 = "f25f49df421511e9af5a2cfda1ba8f68";//"设置盖章延时时间"
    public static final String permission6 = "2679db9934e511e998a22cfda1ba8f68";//"设置印章启动密码"
    public static final String permission7 = "267ceff634e511e998a22cfda1ba8f68";//"设置印章按键密码"
    public static final String permission8 = "f26461e3421511e9af5a2cfda1ba8f68";//"设置印章语音开关"
    public static final String permission9 = "2681798f34e511e998a22cfda1ba8f68";//"重置印章"
    public static final String permission10 = "268490a434e511e998a22cfda1ba8f68";//"添加脱机用户"
    public static final String permission11 = "39ae3e97421511e9af5a2cfda1ba8f68";//"设置脱机用户权限"
    public static final String permission12 = "39b4d216421511e9af5a2cfda1ba8f68";//"删除脱机用户"
    public static final String permission13 = "2687d67534e511e998a22cfda1ba8f68";//"设置电子围栏"
    public static final String permission14 = "268b020f34e511e998a22cfda1ba8f68";//"设置审批流"
    public static final String permission15 = "268e645734e511e998a22cfda1ba8f68";//"添加用户"
    public static final String permission16 = "26916fff34e511e998a22cfda1ba8f68";//"删除用户"
    public static final String permission17 = "78bdfb99421811e9af5a2cfda1ba8f68";//"添加用户权限"
    public static final String permission18 = "2697435e34e511e998a22cfda1ba8f68";//"设置用户权限"
    public static final String permission19 = "269a5ee034e511e998a22cfda1ba8f68";//"查询公司人员盖章记录"
    public static final String permission20 = "f3371243418811e9af5a2cfda1ba8f68";//"添加组织架构" //
    public static final String permission21 = "b9f8b134421611e9af5a2cfda1ba8f68";//"编辑组织架构"
    public static final String permission22 = "f33d24bc418811e9af5a2cfda1ba8f68";//"删除组织架构"
    public static final String permission23 = "b4a8e501626611e9802700163e004197";//"移动用户或印章到其他部门权限"
    public static final String permission24 = "afeb1d90626611e9802700163e004197";//"固件升级权限"

    public static final String permission25 = "256d4bbe65b011e9802700163e004197";//"更换印章"
    public static final String permission26 = "299f9afe65b011e9802700163e004197";//"查看公司组织架构"


    public static final UUID SERVICE_UUID = UUID.fromString("000018F0-0000-1000-8000-00805F9B34FB");
    public static final UUID WRITE_UUID = UUID.fromString("00002AF1-0000-1000-8000-00805F9B34FB");
    public static final UUID NOTIFY_UUID = UUID.fromString("00002AF0-0000-1000-8000-00805F9B34FB");

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wxf3669f6ea87d71d4";

}
