package com.vietlh.wethoong.utils;

import android.database.Cursor;

import com.vietlh.wethoong.entities.Coquanbanhanh;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.entities.Loaivanban;
import com.vietlh.wethoong.entities.Vanban;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vietlh on 2/23/18.
 */

public class Queries implements Serializable{

    public static String rawQuery = "select distinct dk.id as dkId, dk.so as dkSo, tieude as dkTieude, dk.noidung as dkNoidung, minhhoa as dkMinhhoa, cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, vanbanid as vbId, vb.ten as vbTen, nam as vbNam, ma as vbMa, vb.noidung as vbNoidung, coquanbanhanh as vbCoquanbanhanhId, cq.ten as cqTen, dk.forSearch as dkSearch from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id where ";
    private DBConnection connection;

    public Queries(DBConnection connection) {
        this.connection = connection;
    }

    public static String getRawQuery() {
        return rawQuery;
    }

    public ArrayList<Dieukhoan> getAllDieukhoan(){
        connection.open();
        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(rawQuery + "vbId = 2");
        generateDieukhoanList(cursor,allDieukhoan);
        connection.close();
        return allDieukhoan;
    }

    private void generateDieukhoanList(Cursor cursor,ArrayList<Dieukhoan> allDieukhoan){
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ArrayList<String> minhhoa = new ArrayList<>();
            for (String mh :
                    cursor.getString(4).split(";")) {
                minhhoa.add(mh);
            }
            Loaivanban loaivanban = new Loaivanban(cursor.getInt(6),cursor.getString(7));
            Coquanbanhanh coquanbanhanh = new Coquanbanhanh(cursor.getInt(14),cursor.getString(15));
            Vanban vanban = new Vanban(cursor.getInt(9),cursor.getString(10),loaivanban,cursor.getString(8),cursor.getString(11),cursor.getString(12),coquanbanhanh,cursor.getString(13));
            Dieukhoan dieukhoan = new Dieukhoan(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),minhhoa,cursor.getInt(5),vanban);
            allDieukhoan.add(dieukhoan);
            cursor.moveToNext();
        }
    }
}
