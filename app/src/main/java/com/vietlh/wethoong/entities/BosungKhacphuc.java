package com.vietlh.wethoong.entities;

/**
 * Created by vietlh on 3/1/18.
 */

public class BosungKhacphuc {
    private Dieukhoan dieukhoanLienquan;
    private Dieukhoan dieukhoanQuydinh;
    String noidung;

    public BosungKhacphuc(Dieukhoan dieukhoanLienquan, Dieukhoan dieukhoanQuydinh, String noidung) {
        this.dieukhoanLienquan = dieukhoanLienquan;
        this.dieukhoanQuydinh = dieukhoanQuydinh;
        this.noidung = noidung;
    }

    public Dieukhoan getDieukhoanLienquan() {
        return dieukhoanLienquan;
    }

    public void setDieukhoanLienquan(Dieukhoan dieukhoanLienquan) {
        this.dieukhoanLienquan = dieukhoanLienquan;
    }

    public Dieukhoan getDieukhoanQuydinh() {
        return dieukhoanQuydinh;
    }

    public void setDieukhoanQuydinh(Dieukhoan dieukhoanQuydinh) {
        this.dieukhoanQuydinh = dieukhoanQuydinh;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }
}
