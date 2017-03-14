package com.pixelall.mylibrary;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/2.
 */
public class MachineUtils {

    private static final String TAG = "MachineUtils";

    /**
     * 获取手机的imei
     * IMEI(International Mobile Equipment Identity)是国际移动设备身份码的缩写，国际移动装备辨识码，是由15位数字组成的"电子串号"，
     * 它与每台手机一一对应，而且该码是全世界唯一的。
     * @return
     */
    public static String getImeiInfo(Context context) {
        try {
            TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return mTm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取手机的imsi
     * 国际移动用户识别码（IMSI：International Mobile SubscriberIdentification Number）是区别移动用户的标志，储存在SIM卡中，
     * 可用于区别移动用户的有效信息。
     * @return
     */
    public static String getImsiInfo(Context context) {
        try {
            String imsi = "";
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imsi = telephonyManager.getSubscriberId();
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = "UNKNOWN";
            }
            return imsi;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取手机型号  那些什么Hol U-10
     * @return
     */
    public static String getTypeMODEL() {
        return android.os.Build.MODEL; // 手机型号
    }

    /**
     * 获取手机品牌  那些什么华为 小米
     * @return
     */
    public static String getTypeBRAND() {
        return android.os.Build.BRAND; // 手机品牌
    }

    /**
     * 获取手机系统版本
     * @return
     */
    public static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取路由器的SSID
     * @return
     */
    public static String getRouteSSID(Context mContext) {
        try {
            WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            if(info.getSSID().contains("<")){
                return "";
            }else{
                return info.getSSID().replace("\"", "") + "";
            }
        } catch (Exception e) {
            Log.e(TAG,"异常:" + e.getMessage() + ",获取SSID失败!");
            return "";
        }
    }

    /**
     * 获取路由器的Mac地址
     * @return
     */
    public static String getRouteMac(Context mContext) {
        try {
            WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            if(info.getBSSID() == null){
                return "";
            }else{
                return info.getBSSID() + "";
            }
        } catch (Exception e) {
            Log.e(TAG, "异常:" + e.getMessage() + ",获取Mac地址失败!");
            return "";
        }
    }

    /**
     * 获取本机的ip
     * @return
     */
    public static String getLocalHostIp(){
        String ipaddress = "";
        try{
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()){
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()){
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(ip.getHostAddress())){
                        return ip.getHostAddress();
                    }
                    if (!ip.isLoopbackAddress()&& InetAddressUtils.isIPv6Address(ip.getHostAddress())){
                        return ip.getHostAddress();
                    }
                }
            }
        }catch (SocketException e){
            Log.e(TAG, "获取本地ip地址失败");
        }
        return ipaddress;
    }

    /**
     * 检查端口port是否被占用了
     * @param port
     * @return
     */
    public static boolean checkPort(int port){
        try{
            InetAddress theAddress=InetAddress.getByName("127.0.0.1");
            Socket theSocket = new Socket(theAddress,port);
            theSocket.close();
            theSocket = null;
            theAddress = null;
            return false;
        }catch(Exception e) {
            Log.e(TAG,"异常:"+e.getMessage()+"检查端口号是否被占用");
        }
        return true;
    }

    /**
     * 解析uri参数
     * @param uri
     * @return
     */
    public static Map<String,String> getParam(String uri){
        Map<String,String> params = new HashMap<String,String>();
        try{
            if(!TextUtils.isEmpty(uri)){
                String subStr = uri.substring(uri.indexOf("?")+1);
                String[] ary = subStr.split("&");
                for(int i=0;i<ary.length;i++){
                    String[] temp = ary[i].split("=");
                    if(temp.length<2){
                        params.put(temp[0], "");
                    }else{
                        params.put(temp[0], temp[1]);
                    }
                }
                return params;
            }else{
                return null;
            }
        }catch(Exception e){
            return null;
        }
    }


    public static Map<String,String> collectDeviceInfo(Context mContext){
        Map<String,String> deviceInfos = new HashMap<String,String>();
        String MODEL = getTypeMODEL();
        deviceInfos.put("MODEL",MODEL);
        String BRAND = getTypeBRAND();
        deviceInfos.put("BRAND",BRAND);
        String osVersion = getOsVersion();
        deviceInfos.put("osVersion",osVersion);
        String imei = getImeiInfo(mContext);
        deviceInfos.put("IMEI",imei);
        String ims = getImsiInfo(mContext);
        deviceInfos.put("IMS",ims);
        String IP = getLocalHostIp();
        deviceInfos.put("IP",IP);
        String MAC = getRouteMac(mContext);
        deviceInfos.put("MAC",MAC);
        return deviceInfos;
    }


}
