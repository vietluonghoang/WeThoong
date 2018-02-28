package com.vietlh.wethoong.utils;

/**
 * Created by vietlh on 2/23/18.
 */

public class GeneralSettings {
    public static String[] mucphatRange = {"50.000","60.000","80.000","100.000","120.000","200.000","300.000","400.000","500.000","600.000","800.000","1.000.000","1.200.000","500.000","1.600.000","2.000.000","2.500.000","3.000.000","4.000.000","5.000.000","6.000.000","7.000.000","8.000.000","10.000.000","12.000.000","14.000.000","15.000.000","16.000.000","18.000.000","20.000.000","25.000.000","28.000.000","30.000.000","32.000.000","36.000.000","37.500.000","40.000.000","50.000.000","52.500.000","56.000.000","64.000.000","70.000.000","75.000.000","80.000.000","150.000.000"};
    public static String nd46Id = "2";
    public static String qc41Id = "1";
    public static String tt01Id = "3";
    public static String lgtId = "4";
    public static String lxlvphcId = "5";
    public static String tamgiuPhuongtienDieukhoanID = "2820";

    public static int getRecordCapByRam(){
        float ram = getFreePhysicalMemorySize();

        if(ram <= 0.5){
            return 100;
        }
        if(ram <= 1){
            return 150;
        }
        if(ram <= 1.5){
            return 200;
        }
        if(ram <= 2){
            return 300;
        }
        if(ram <= 2.5){
            return 500;
        }
        if(ram <= 3){
            return 700;
        }
        if(ram <= 3.5){
            return 900;
        }
        if(ram <= 4){
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
        return (float)(freeSize / 1048576L);
    }
}
