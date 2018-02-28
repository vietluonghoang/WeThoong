package com.vietlh.wethoong.entities;

/**
 * Created by vietlh on 2/26/18.
 */

public class Vanban {
    private int id;
    private String ten;
    private Loaivanban loai;
    private String so;
    private String nam;
    private String ma;
    private Coquanbanhanh coquanbanhanh;
    private String noidung;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten.trim();
    }

    public Loaivanban getLoai() {
        return loai;
    }

    public void setLoai(Loaivanban loai) {
        this.loai = loai;
    }

    public String getSo() {
        return so;
    }

    public void setSo(String so) {
        this.so = so.trim();
    }

    public String getNam() {
        return nam;
    }

    public void setNam(String nam) {
        this.nam = nam.trim();
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma.trim();
    }

    public Coquanbanhanh getCoquanbanhanh() {
        return coquanbanhanh;
    }

    public void setCoquanbanhanh(Coquanbanhanh coquanbanhanh) {
        this.coquanbanhanh = coquanbanhanh;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung.trim();
    }

    public Vanban(int id, String ten, Loaivanban loai, String so, String nam, String ma, Coquanbanhanh coquanbanhanh, String noidung) {

        this.id = id;
        this.ten = ten;
        this.loai = loai;
        this.so = so;
        this.nam = nam;
        this.ma = ma;
        this.coquanbanhanh = coquanbanhanh;
        this.noidung = noidung;
    }
}
