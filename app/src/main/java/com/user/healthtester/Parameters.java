package com.user.healthtester;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.NetworkInterface;

public class Parameters {

    private static Resources resources;
    private static String routeName;
    private static String macIP;
    public static Context context;

    public static void init(Context context) {
        routeName = context.getPackageName();
        resources = context.getResources();
        Parameters.context = context;
    }

    public static String getString(String strName) {
        int strId = getStringID(strName);
        return resources.getString(strId);
    }

    public static int getStringID(String strName) {
        return resources.getIdentifier(strName, "string", routeName);
    }

    public static String getMacIP() {
        if (macIP == null) {
            try {
                NetworkInterface networkInterface = NetworkInterface.getByName("eth1");
                if (networkInterface == null) {
                    networkInterface = NetworkInterface.getByName("wlan0");
                }
                byte[] addrByte = networkInterface.getHardwareAddress();
                StringBuilder sb = new StringBuilder();
                for (byte b : addrByte) {
                    sb.append(String.format("%02X:", b));
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                macIP = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                WifiManager wifiM = (WifiManager) context
                        .getApplicationContext().getSystemService(
                                Context.WIFI_SERVICE);
                try {
                    WifiInfo wifiI = wifiM.getConnectionInfo();
                    macIP = wifiI.getMacAddress();
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                    macIP = "02:00:00:00:00:00";
                }
            }
        }
        return macIP;
    }
}
