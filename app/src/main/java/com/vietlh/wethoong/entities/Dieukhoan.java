package com.vietlh.wethoong.entities;

import java.util.ArrayList;

/**
 * Created by vietlh on 2/26/18.
 */

public class Dieukhoan {
    private int id;
    private String so;
    private String tieude;
    private String noidung;
    private ArrayList<String> minhhoa = new ArrayList<>();
    private int cha;
    private Vanban vanban;
    private String hinhphatbosung = "";
    private String bienphapkhacphuc = "";
    private int sortPoint = 0;
    private int matchCount = 0;
    private int firstAppearanceDistance = 0;
    private int defaultMinhhoa = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSo() {
        return so;
    }

    public void setSo(String so) {
        this.so = so.trim();
    }

    public String getTieude() {
        return tieude;
    }

    public void setTieude(String tieude) {
        this.tieude = tieude.trim();
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung.trim();
    }

    public ArrayList<String> getMinhhoa() {
        return minhhoa;
    }

    public void setMinhhoa(ArrayList<String> minhhoa) {
        this.minhhoa = minhhoa;
    }

    public int getCha() {
        return cha;
    }

    public void setCha(int cha) {
        this.cha = cha;
    }

    public Vanban getVanban() {
        return vanban;
    }

    public void setVanban(Vanban vanban) {
        this.vanban = vanban;
    }

    public String getHinhphatbosung() {
        return hinhphatbosung;
    }

    public void setHinhphatbosung(String hinhphatbosung) {
        this.hinhphatbosung = hinhphatbosung.trim();
    }

    public String getBienphapkhacphuc() {
        return bienphapkhacphuc;
    }

    public void setBienphapkhacphuc(String bienphapkhacphuc) {
        this.bienphapkhacphuc = bienphapkhacphuc.trim();
    }

    public int getSortPoint() {
        return sortPoint;
    }

    public void setSortPoint(int sortPoint) {
        this.sortPoint = sortPoint;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public int getFirstAppearanceDistance() {
        return firstAppearanceDistance;
    }

    public int getDefaultMinhhoa() {
        return defaultMinhhoa;
    }

    public void setDefaultMinhhoa(int order) {
        if (order >= 0 && order < minhhoa.size()) {
            this.defaultMinhhoa = order;
        }
    }

    public void setDefaultMinhhoa(String name) {
        int order = 0;
        for (String mh : minhhoa) {
            if (mh.trim().toLowerCase().contains(name.trim().toLowerCase())) {
                this.defaultMinhhoa = order;
                break;
            }
            order += 1;
        }
    }

    public void setFirstAppearanceDistance(int firstAppearanceDistance) {
        this.firstAppearanceDistance = firstAppearanceDistance;
    }

    public Dieukhoan(int id, int cha, Vanban vanban) {
        this.id = id;
        this.cha = cha;
        this.vanban = vanban;
        this.so = "";
        this.tieude = "";
        this.noidung = "";
        this.sortPoint = 0;
    }

    public Dieukhoan(int id, String so, String tieude, String noidung, ArrayList<String> minhhoa, int cha, Vanban vanban) {

        this.id = id;
        this.so = so;
        this.tieude = tieude;
        this.noidung = noidung;
        this.minhhoa = minhhoa;
        this.cha = cha;
        this.vanban = vanban;
    }

    public Dieukhoan(int id, String so, String tieude, String noidung, int cha, Vanban vanban) {

        this.id = id;
        this.so = so;
        this.tieude = tieude;
        this.noidung = noidung;
        this.cha = cha;
        this.vanban = vanban;
    }

    public void addMinhhoa(String minhhoa) {
        if (minhhoa.length() > 0) {
            this.minhhoa.add(minhhoa);
        }
    }

    public void cloneDieukhoan(Dieukhoan dk) {
        this.id = dk.getId();
        this.so = dk.getSo();
        this.tieude = dk.getTieude();
        this.noidung = dk.getNoidung();
        this.minhhoa = dk.getMinhhoa();
        this.cha = dk.getCha();
        this.vanban = dk.getVanban();
        this.hinhphatbosung = dk.getHinhphatbosung();
        this.bienphapkhacphuc = dk.getBienphapkhacphuc();
        this.sortPoint = dk.getSortPoint();
        this.defaultMinhhoa = dk.getDefaultMinhhoa();
    }
}
