package cc.seeed.iot.util;

/**
 * Created by tenwong on 15/10/20.
 */
public class CommonUrl {
    public static String Server_In_Prefix = "http://192.168.1.40/seeedcc-develop/api/index.php?";
    public static String Server_Out_Prefix = "http://bazaar.seeed.cc/api/index.php?";
    public static String Server_In_Ota_Url_Prefix = "https://192.168.4.99";
    public static String Server_In_Ota_Ip_Prefix = "192.168.4.99";
    public static String OTA_SERVER_URL = "https://wio.seeed.io";
    public static String OTA_SERVER_IP = "120.25.216.117";

    public static final String AP_IP = "192.168.4.1";

    public static String Hinge_User_Login = "r=common/user/login";
    public static String Hinge_User_Register = "r=common/user/register";
    public static String Hinge_User_ForgetPwd = "r=common/user/forget-password";
    public static String Hinge_User_ResetPwd = "r=common/user/rest-password";
    public static String Hinge_User_ChangePwd = "r=common/user/modify-password";
    public static String Hinge_Set_Token = "/v1/ext_users";


    public static SmartValue<String> Server_Prefix = new SmartValue<>(Server_Out_Prefix, Server_In_Prefix);
    public static SmartValue<String> Image_Prefix = new SmartValue<>("http://bazaar.seeed.cc/","http://192.168.1.40/seeedcc-develop/");
  /*  public static String OTA_SERVER_URL = new SmartValue<String>(Server_Out_Ota_Url_Prefix, Server_In_Ota_Url_Prefix).getVal();
    public static String OTA_SERVER_IP = new SmartValue<String>(Server_Out_Ota_Ip_Prefix, Server_In_Ota_Ip_Prefix).getVal();*/

}
