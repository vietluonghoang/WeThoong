package com.vietlh.wethoong.utils;

import com.vietlh.wethoong.entities.Vanban;

import java.util.HashMap;

/**
 * Created by vietlh on 2/23/18.
 */

public class GeneralSettings {

    private static HashMap<Integer,String[]> mucphatRangePerVanban = new HashMap<Integer,String[]>(){{
        put(6, new String[]{"50.000", "60.000", "80.000", "100.000", "200.000", "250.000", "300.000", "400.000", "500.000", "600.000", "800.000", "1.000.000", "1.200.000", "1.500.000", "1.600.000", "2.000.000", "3.000.000", "4.000.000", "5.000.000", "6.000.000", "7.000.000", "7.500.000", "8.000.000", "10.000.000", "12.000.000", "14.000.000", "15.000.000", "16.000.000", "18.000.000", "20.000.000", "25.000.000", "28.000.000", "30.000.000", "32.000.000", "36.000.000", "40.000.000", "50.000.000", "56.000.000", "64.000.000", "70.000.000", "200.000.000"});
        put(2, new String[]{"50.000", "60.000", "80.000", "100.000", "120.000", "200.000", "300.000", "400.000", "500.000", "600.000", "800.000", "1.000.000", "1.200.000", "500.000", "1.600.000", "2.000.000", "2.500.000", "3.000.000", "4.000.000", "5.000.000", "6.000.000", "7.000.000", "8.000.000", "10.000.000", "12.000.000", "14.000.000", "15.000.000", "16.000.000", "18.000.000", "20.000.000", "25.000.000", "28.000.000", "30.000.000", "32.000.000", "36.000.000", "37.500.000", "40.000.000", "50.000.000", "52.500.000", "56.000.000", "64.000.000", "70.000.000", "75.000.000", "80.000.000", "150.000.000"});
        put(17, new String[]{"50.000","60.000","80.000","100.000","120.000","200.000","250.000","300.000","400.000","500.000","600.000","800.000","1.000.000","1.200.000","1.500.000","1.600.000","2.000.000","3.000.000","4.000.000","5.000.000","6.000.000","7.000.000","7.500.000","8.000.000","10.000.000","12.000.000","13.000.000","14.000.000","15.000.000","16.000.000","18.000.000","20.000.000","24.000.000","25.000.000","28.000.000","30.000.000","32.000.000","35.000.000","36.000.000","40.000.000","50.000.000","56.000.000","60.000.000","64.000.000","70.000.000","75.000.000","140.000.000","150.000.000"});
        put(23, new String[]{"150.000","200.000","250.000","300.000","350.000","400.000","500.000","600.000","700.000","800.000","1.000.000","1.200.000","1.500.000","1.600.000","2.000.000","2.500.000","3.000.000","4.000.000","5.000.000","6.000.000","7.000.000","8.000.000","10.000.000","12.000.000","13.000.000","14.000.000","15.000.000","16.000.000","18.000.000","20.000.000","22.000.000","24.000.000","26.000.000","28.000.000","30.000.000","32.000.000","35.000.000","36.000.000","37.000.000","37.500.000","40.000.000","50.000.000","52.000.000","52.500.000","56.000.000","60.000.000","64.000.000","65.000.000","70.000.000","75.000.000","80.000.000","100.000.000","130.000.000","150.000.000"});
    }};
    private static HashMap<Integer,String> tamgiuPhuongtienDieukhoanID = new HashMap<Integer,String>(){{
        put(6,"6592");
        put(2, "2820");
        put(17, "12766");
        put(23, "16779");
    }};
//    public static String[] danhsachvanban = {"nd46", "qc41", "tt01", "lgt", "lxlvphc", "tt652020"};
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
    public static final String APP_CONFIG_KEY_ADSOPTOUT ="adsOptout";


    public static boolean isRemoteConfigFetched = false;
    public static boolean isAdsOptout = false; //false will allow ads to display
    public static boolean isDevMode = true;
    public static int requiredDBVersion = 16;
    private static int defaultActiveQC41Id = 22; //this would be used for the search of querying the lastest road sign
    private static int defaultActiveNDXPId = 23; //this would be used for the search of querying the latest NDXP

    public static final String MIXPANEL_PROJECT_TOKEN = "df5055fa1fab32aa05305dab957d7674";
    public static final String DEFAULT_MIXPANEL_USER_ID = "USER_ID";
    public static boolean TRACK_AUTOMATIC_EVENTS = false;
    public static boolean MIXPANEL_ENABLED = true;
    public static int DEFAULT_MIXPANEL_EVENT_SEND_TIMEOUT = 3000; //in miliseconds
    public static String MINIMUM_APP_VERSION_REQUIRED = "1.0";
    public static boolean ENABLE_INAPP_NOTIF = false;
    public static boolean ENABLE_BANNER_ADS = false;
    public static boolean ENABLE_INTERSTITIAL_ADS = false;
    public static int MINIMUM_ADS_INTERVAL = 350; //in seconds
    public static int INTERSTITIAL_ADS_OPEN_TIME = 0;
    public static long LAST_APP_OPEN_TIMESTAMP = 0;
    public static long LAST_INTERSTITIAL_ADS_OPEN_TIMESTAMP = 0;

    public static int resultCountLimit = 100;
    public static boolean wasConnectedToInternet = false;
    public static long lastApplicationStateCheckTimestamp = 0;
    public static int defaultHttpRequestConnectionTimeout = 60000; //in miliseconds
    public static int defaultApplicationStateCheckInterval = 60;
    public static boolean isAppClosed = true;

    private static HashMap<Integer, Vanban> vanbanInfo = new HashMap<>();
    private static int maxVanbanId = 0; //check if the maximum value of vanbanId

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

    public static String[] getMucphatRangePerVanban(int id) {
        String[] mucphat = mucphatRangePerVanban.get(id);
        if (mucphat != null){
            return  mucphat;
        }else {
            return new String[]{""};
        }
    }

    public static String getTamgiuPhuongtienDieukhoanID(int id) {
        return tamgiuPhuongtienDieukhoanID.get(id);
    }

    public static void setTamgiuPhuongtienDieukhoanID(HashMap<Integer,String> tamgiuPhuongtienDkID) {
        tamgiuPhuongtienDieukhoanID = tamgiuPhuongtienDkID;
    }

    public static String getVanbanInfo(int id, String info) {
        Vanban vanban = vanbanInfo.get(id);

        if(vanban != null){
            switch (info.toLowerCase()){
                case "valid":
                    return vanban.getHieuluc();
                case "shortname":
                    return vanban.getTenRutgon();
                case "fullname":
                    return vanban.getTen();
                case "replace":
                    return ""+vanban.getVanbanThaytheId();
                default:
                    return "";
            }
        } else {
            return "";
        }
    }

    public static void setVanbanInfo(Vanban[] vanbans) {
        maxVanbanId = 0;
        for(Vanban vb: vanbans) {
            vanbanInfo.put(vb.getId(),vb);
            if (vb.getId() > maxVanbanId) {
                maxVanbanId = vb.getId();
            }
        }
    }

    public static int getMaxVanbanId() {
        return maxVanbanId;
    }

    public static void setMaxVanbanId(int maxVanbanId) {
        GeneralSettings.maxVanbanId = maxVanbanId;
    }

    public static int getDefaultActiveQC41Id() {
        return defaultActiveQC41Id;
    }
    public static void setDefaultActiveQC41Id(int activeQC41Id) {
         defaultActiveQC41Id = activeQC41Id;
    }

    public static int getDefaultActiveNDXPId() {
        return defaultActiveNDXPId;
    }
    public static void setDefaultActiveNDXPId(int activeNDXPId) {
        defaultActiveNDXPId = activeNDXPId;
    }
}
