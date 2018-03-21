package com.vietlh.wethoong.utils;

import android.content.Context;
import android.util.Log;

import com.vietlh.wethoong.entities.Dieukhoan;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by vietlh on 3/19/18.
 */

public class SearchFor {
    private Context context;
    private Queries queries;

    public SearchFor(Context context) {
        this.context = context;
        queries= new Queries(DBConnection.getInstance(context));
    }

    public ArrayList<String> regexSearch(String pattern, String searchIn){
        ArrayList<String> allMatches = new ArrayList<>();
        try {
            String input = searchIn.toLowerCase();
            Pattern ptrn = Pattern.compile(pattern);
            Matcher matcher = ptrn.matcher(input);

            while (matcher.find()) {
                allMatches.add(matcher.group());
            }

        } catch (Exception ex){
            Log.i(TAG,"Error with RegEx");
        }
        return allMatches;
    }

    public boolean isStringExisted(String str, ArrayList<String> strArr) {
        for (String key :
                strArr) {
            if (key.equals(str)){
                return true;
            }
        }
        return false;
    }

    public String getAncestersID(Dieukhoan dieukhoan, ArrayList<String> vanbanId) {
        String ancesters = "";
        if (dieukhoan.getCha() == 0) {
            ancesters = "" + dieukhoan.getId();
        }else{
            ancesters = "" + dieukhoan.getCha();
            ArrayList<Dieukhoan> parents = queries.searchDieukhoanByID("" + dieukhoan.getCha(),vanbanId);
            while (parents.get(0).getCha() != 0) {
                ancesters = "" + parents.get(0).getCha() + "-" + ancesters;
                parents = queries.searchDieukhoanByID("" + parents.get(0).getCha(),vanbanId);
            }

        }
        return ancesters;
    }

    public ArrayList<Dieukhoan> getAncesters(Dieukhoan dieukhoan, ArrayList<String> vanbanId) {
        Dieukhoan dk = dieukhoan;
        ArrayList<Dieukhoan> ancesters = new ArrayList<>();

        while (dk.getCha() != 0) {
            dk = queries.searchDieukhoanByID("" + dk.getCha(),vanbanId).get(0);
            ancesters.add(dk);
        }
        return ancesters;
    }

    public String getAncestersNumber(Dieukhoan dieukhoan, ArrayList<String> vanbanId) {
        String ancesters = "";
        Dieukhoan dk = dieukhoan;

        while (dk.getCha() != 0) {
            Dieukhoan parent = queries.searchDieukhoanByID("" + dk.getCha(),vanbanId).get(0);
            dk = parent;
            ancesters += dk.getSo()+"/";
        }
        return ancesters;
    }

    public Dieukhoan getDieunay(Dieukhoan currentDieukhoan, ArrayList<String> vanbanId) {
        Dieukhoan trackingDieukhoan = currentDieukhoan;
        while (!trackingDieukhoan.getSo().toLowerCase().contains("điều")) {
            if(trackingDieukhoan.getCha() != 0){
                trackingDieukhoan = queries.searchDieukhoanByID("" + trackingDieukhoan.getCha(),vanbanId).get(0);
            }else{
                return trackingDieukhoan;
            }
        }
        return trackingDieukhoan;
    }

    public Dieukhoan getKhoannay(Dieukhoan currentDieukhoan, ArrayList<String> vanbanId)  {
        Dieukhoan trackingDieukhoan = currentDieukhoan;
        Dieukhoan prevTrackingDieukhoan = currentDieukhoan;
        while (!trackingDieukhoan.getSo().toLowerCase().contains("điều")) {
            prevTrackingDieukhoan = trackingDieukhoan;
            if(trackingDieukhoan.getCha() != 0){
                trackingDieukhoan = queries.searchDieukhoanByID("" + trackingDieukhoan.getCha(),vanbanId).get(0);
            }else{
                return prevTrackingDieukhoan;
            }
        }
        return prevTrackingDieukhoan;
    }
}
