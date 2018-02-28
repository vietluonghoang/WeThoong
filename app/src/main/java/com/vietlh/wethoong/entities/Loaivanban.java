package com.vietlh.wethoong.entities;

/**
 * Created by vietlh on 2/26/18.
 */

public class Loaivanban {
    private int id;
    private String ten;

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

    public Loaivanban(int id, String ten) {

        this.id = id;
        this.ten = ten;
    }
}
