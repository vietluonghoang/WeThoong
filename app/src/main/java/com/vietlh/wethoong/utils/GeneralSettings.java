package com.vietlh.wethoong.utils;

import java.util.HashMap;

/**
 * Created by vietlh on 2/23/18.
 */

public class GeneralSettings {
    public static String[] mucphatRange = {"50.000", "60.000", "80.000", "100.000", "120.000", "200.000", "300.000", "400.000", "500.000", "600.000", "800.000", "1.000.000", "1.200.000", "500.000", "1.600.000", "2.000.000", "2.500.000", "3.000.000", "4.000.000", "5.000.000", "6.000.000", "7.000.000", "8.000.000", "10.000.000", "12.000.000", "14.000.000", "15.000.000", "16.000.000", "18.000.000", "20.000.000", "25.000.000", "28.000.000", "30.000.000", "32.000.000", "36.000.000", "37.500.000", "40.000.000", "50.000.000", "52.500.000", "56.000.000", "64.000.000", "70.000.000", "75.000.000", "80.000.000", "150.000.000"};
    public static String[] danhsachvanban = {"nd46", "qc41", "tt01", "lgt", "lxlvphc"};
    public static final String SEARCH_TYPE_VANBAN = "vanban";
    public static final String SEARCH_TYPE_MUCPHAT = "mucphat";
    public static final String SEARCH_TYPE_BIENBAO = "bienbao";
    public static final String SEARCH_TYPE_VACHKEDUONG = "vachkeduong";
    public static final String PHUONGTIEN_OTO = "Ô tô";
    public static final String PHUONGTIEN_XEMAY = "Xe máy";
    public static final String PHUONGTIEN_XEDAP = "Xe đạp";
    public static final String PHUONGTIEN_XECHUYENDUNG = "Xe chuyên dùng";
    public static final String PHUONGTIEN_TAUHOA = "Tàu hoả";
    public static final String PHUONGTIEN_DIBO = "Đi bộ";
    public static String tamgiuPhuongtienDieukhoanID = "2820";
    public static final String APP_CONFIG_KEY_ADSOPTOUT ="adsOptout";

    public static boolean isAdsOptout = false; //false will allow ads to display
    public static boolean isDevMode = false;
    public static int dbVersion = 5;

    public static String MINIMUM_APP_VERSION_REQUIRED = "1.0";
    public static boolean ENABLE_INAPP_NOTIF = false;
    public static boolean ENABLE_BANNER_ADS = false;
    public static boolean ENABLE_INTERSTITIAL_ADS = false;
    public static int MINIMUM_ADS_INTERVAL = 300; //in seconds
    public static int INTERSTITIAL_ADS_OPEN_TIME = 0;
    public static long LAST_APP_OPEN_TIMESTAMP = 0;
    public static long LAST_INTERSTITIAL_ADS_OPEN_TIMESTAMP = 0;

    public static int resultCountLimit = 100;
    public static boolean wasConnectedToInternet = false;
    public static long lastApplicationStateCheckTimestamp = 0;
    public static int defaultHttpRequestConnectionTimeout = 60000; //in miliseconds
    public static int defaultApplicationStateCheckInterval = 60;
    public static boolean isAppClosed = true;

    private static String nd46Id = "2";
    private static String qc41Id = "1";
    private static String tt01Id = "3";
    private static String lgtId = "4";
    private static String lxlvphcId = "5";
    private static HashMap<String, HashMap<String, String>> vanbanInfo = new HashMap<>();

    public static int getRecordCapByRam() {
        float ram = getFreePhysicalMemorySize();

        if (ram <= 0.5) {
            return 100;
        }
        if (ram <= 1) {
            return 150;
        }
        if (ram <= 1.5) {
            return 200;
        }
        if (ram <= 2) {
            return 300;
        }
        if (ram <= 2.5) {
            return 500;
        }
        if (ram <= 3) {
            return 700;
        }
        if (ram <= 3.5) {
            return 900;
        }
        if (ram <= 4) {
            return 1000;
        }
        return 0;
    }

    private static float getFreePhysicalMemorySize() {
        long freeSize = 0L;
//        long totalSize = 0L;
//        long usedSize = -1L;
        try {
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();
//            totalSize = info.totalMemory();
//            usedSize = totalSize - freeSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (float) (freeSize / 1048576L);
    }

    public static String getVanbanInfo(String name, String info) {
        HashMap<String, String> value = vanbanInfo.get(name);

        if (value == null) {
            HashMap<String, String> vbInfo = new HashMap<>();
            switch (name) {
                case "nd46":
                    vbInfo.put("id", nd46Id);
                    vbInfo.put("fullName", "Nghị định 46 - 2016");
                    break;
                case "qc41":
                    vbInfo.put("id", qc41Id);
                    vbInfo.put("fullName", "Quy chuẩn 41 - 2016");
                    break;
                case "tt01":
                    vbInfo.put("id", tt01Id);
                    vbInfo.put("fullName", "Thông tư 01 - 2016");
                    break;
                case "lgt":
                    vbInfo.put("id", lgtId);
                    vbInfo.put("fullName", "Luật giao thông 2008");
                    break;
                case "lxlvphc":
                    vbInfo.put("id", lxlvphcId);
                    vbInfo.put("fullName", "Luật xử lý vi phạm hành chính 2012");
                    break;
                default:
                    break;
            }
            vanbanInfo.put(name, vbInfo);
            return vanbanInfo.get(name).get(info);
        } else {
            return value.get(info);
        }
    }


}
