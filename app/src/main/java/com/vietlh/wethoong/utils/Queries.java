package com.vietlh.wethoong.utils;

import android.database.Cursor;

import com.vietlh.wethoong.entities.BosungKhacphuc;
import com.vietlh.wethoong.entities.Coquanbanhanh;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.entities.Loaivanban;
import com.vietlh.wethoong.entities.Vanban;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vietlh on 2/23/18.
 */

public class Queries{

    public static String rawQuery = "select distinct dk.id as dkId, dk.so as dkSo, tieude as dkTieude, dk.noidung as dkNoidung, minhhoa as dkMinhhoa, cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, vanbanid as vbId, vb.ten as vbTen, nam as vbNam, ma as vbMa, vb.noidung as vbNoidung, coquanbanhanh as vbCoquanbanhanhId, cq.ten as cqTen, dk.forSearch as dkSearch from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id where ";
    private DBConnection connection;

    public Queries(DBConnection connection) {
        this.connection = connection;
    }

    public static String getRawQuery() {
        return rawQuery;
    }

    private ArrayList<Dieukhoan> appendDieukhoan(Dieukhoan dieukhoan, ArrayList<Dieukhoan> dkArr) {
        for (Dieukhoan dk : dkArr) {
            if (dieukhoan.getId() == dk.getId()) {
                return dkArr;
            }
        }
        dkArr.add(dieukhoan);
        return dkArr;
    }

    public ArrayList<Dieukhoan> getAllDieukhoan(){
        connection.open();
        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(rawQuery + "vbId = 2");
        generateDieukhoanList(cursor,allDieukhoan);
        connection.close();

        return allDieukhoan;
    }

    public ArrayList<BosungKhacphuc> getAllBienphapkhacphuc(int dieukhoanId) {
        connection.open();
        String sql = "select distinct dk.id as dkId, dk.so as dkSo, dk.tieude as dkTieude, dk.noidung as dkNoidung, dk.minhhoa as dkMinhhoa, dk.cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, dk.vanbanid as vbId, vb.ten as vbTen, vb.nam as vbNam, vb.ma as vbMa, vb.noidung as vbNoidung, vb.coquanbanhanh as vbCoquanbanhanhId, cq.ten as cqTen, dk.forSearch as dkSearch, rdk.id as rdkId, rdk.so as rdkSo, rdk.tieude as rdkTieude, rdk.noidung as rdkNoidung, rdk.minhhoa as rdkMinhhoa, rdk.cha as rdkCha, rvb.loai as rlvbID, rlvb.ten as rlvbTen, rvb.so as rvbSo, rdk.vanbanid as rvbId, rvb.ten as rvbTen, rvb.nam as rvbNam, rvb.ma as rvbMa, rvb.noidung as rvbNoidung, rvb.coquanbanhanh as rvbCoquanbanhanhId, rcq.ten as rcqTen, rdk.forSearch as rdkSearch, kp.noidung as noidung from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id join tblBienphapkhacphuc as kp on dk.id = kp.dieukhoanId join tblChitietvanban as rdk on kp.dieukhoanQuydinhId = rdk.id join tblVanban as rvb on rdk.vanbanid=rvb.id join tblLoaivanban as rlvb on rvb.loai=rlvb.id join tblCoquanbanhanh as rcq on rvb.coquanbanhanh=rcq.id where kp.dieukhoanId = "+dieukhoanId;

        ArrayList<BosungKhacphuc> bosungKhacphucArray = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateBosungKhacphucList(cursor, bosungKhacphucArray);
        connection.close();

        return bosungKhacphucArray;
    }

    public ArrayList<BosungKhacphuc> getAllHinhphatbosung(int dieukhoanId) {
        connection.open();
        String sql = "select distinct dk.id as dkId, dk.so as dkSo, dk.tieude as dkTieude, dk.noidung as dkNoidung, dk.minhhoa as dkMinhhoa, dk.cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, dk.vanbanid as vbId, vb.ten as vbTen, vb.nam as vbNam, vb.ma as vbMa, vb.noidung as vbNoidung, vb.coquanbanhanh as vbCoquanbanhanhId, cq.ten as cqTen, dk.forSearch as dkSearch, rdk.id as rdkId, rdk.so as rdkSo, rdk.tieude as rdkTieude, rdk.noidung as rdkNoidung, rdk.minhhoa as rdkMinhhoa, rdk.cha as rdkCha, rvb.loai as rlvbID, rlvb.ten as rlvbTen, rvb.so as rvbSo, rdk.vanbanid as rvbId, rvb.ten as rvbTen, rvb.nam as rvbNam, rvb.ma as rvbMa, rvb.noidung as rvbNoidung, rvb.coquanbanhanh as rvbCoquanbanhanhId, rcq.ten as rcqTen, rdk.forSearch as rdkSearch, hp.noidung as noidung from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id join tblHinhphatbosung as hp on dk.id = hp.dieukhoanId join tblChitietvanban as rdk on hp.dieukhoanQuydinhId = rdk.id join tblVanban as rvb on rdk.vanbanid=rvb.id join tblLoaivanban as rlvb on rvb.loai=rlvb.id join tblCoquanbanhanh as rcq on rvb.coquanbanhanh=rcq.id where hp.dieukhoanId = "+dieukhoanId;

        ArrayList<BosungKhacphuc> bosungKhacphucArray = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateBosungKhacphucList(cursor, bosungKhacphucArray);
        connection.close();

        return bosungKhacphucArray;
    }

    private String generateWhereClauseForVanbanid(ArrayList<String> vanbanIdList, String vanbanIdColumnName){
        if(vanbanIdList.size() == 0){
            return "";
        }

        String clause = " and (";
        for(String id: vanbanIdList){
            clause = clause + vanbanIdColumnName + " = "+id.trim() + " or ";
        }
        return clause.substring(0,clause.length() - 4) + ")";
    }

    private void generateDieukhoanList(Cursor cursor,ArrayList<Dieukhoan> allDieukhoan){
        if(cursor != null){
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

    //TODO: implement searchDoituongInfo()

    //TODO: implement searchLinhvucInfo()

    //TODO: implement searchPhuongtienInfo()

    //TODO: implement searchMucphatInfo()

    //TODO: implement getAllRelativeRelatedDieukhoan()

    //TODO: implement getAllDirectRelatedDieukhoan()
    //TODO: implement searchChildren()
    //TODO: implement searchDieukhoanBySo()
    //TODO: implement searchDieukhoanByID()
    //TODO: implement searchDieukhoanByQuery()
    //TODO: implement searchDieukhoan()

    private void generateBosungKhacphucList(Cursor cursor,ArrayList<BosungKhacphuc> bosungKhacphucArray) {
        if(cursor != null){
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

                minhhoa = new ArrayList<>();
                for (String mh :
                        cursor.getString(21).split(";")) {
                    minhhoa.add(mh);
                }
                Loaivanban rloaivanban = new Loaivanban(cursor.getInt(23),cursor.getString(24));
                Coquanbanhanh rcoquanbanhanh = new Coquanbanhanh(cursor.getInt(31),cursor.getString(32));
                Vanban rvanban = new Vanban(cursor.getInt(26),cursor.getString(27),loaivanban,cursor.getString(25),cursor.getString(28),cursor.getString(29),coquanbanhanh,cursor.getString(30));
                Dieukhoan rdieukhoan = new Dieukhoan(cursor.getInt(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),minhhoa,cursor.getInt(22),vanban);

                BosungKhacphuc bosungkhacphuc = new BosungKhacphuc(dieukhoan, rdieukhoan, cursor.getString(34));

                bosungKhacphucArray.add(bosungkhacphuc);
            }
        }
    }


    private ArrayList<String> convertKeywordsForDifferentAccentType(String keyword) {
        String convertedKeyword = "";

        String[] vnChars = {"á", "à", "ả", "ã", "ạ", "a", "ấ", "ầ", "ẩ", "ẫ", "ậ", "â", "ắ", "ằ", "ẳ", "ẵ", "ặ", "ă", "é", "è", "ẻ", "ẽ", "ẹ", "e", "ế", "ề", "ể", "ễ", "ệ", "ê", "í", "ì", "ỉ", "ĩ", "ị", "i", "ó", "ò", "ỏ", "õ", "ọ", "o", "ố", "ồ", "ổ", "ỗ", "ộ", "ô", "ớ", "ờ", "ở", "ỡ", "ợ", "ơ", "ú", "ù", "ủ", "ũ", "ụ", "u", "ứ", "ừ", "ử", "ữ", "ự", "ư", "ý", "ỳ", "ỷ", "ỹ", "ỵ", "y"};

        String[] splittedStr = keyword.split(" ");

        for (String sStr: splittedStr) {
            ArrayList<Integer> matchChars = new ArrayList<>();
            ArrayList<Integer> matchAccents = new ArrayList<>();
            for (String chr : sStr.split(".")) {
                int count = 0;
                for(String vnC : vnChars) {
                    if (chr.equals(vnC)) {
                        matchChars.add(count/6);
                        matchAccents.add(count%6);
                    }
                    count += 1;
                }
            }
            int index = 0;
            String newKeyword = sStr;
            for (Integer mChr : matchChars) {
                newKeyword = newKeyword.replace(vnChars[(mChr*6)+matchAccents.get(index)], vnChars[(mChr*6)+(matchAccents.get(matchAccents.size() - (index + 1)))]);
                index += 1;
            }
            convertedKeyword += newKeyword + " ";
        }
        ArrayList<String> newKeys = new ArrayList<>();
        newKeys.add(keyword);
        newKeys.add(convertedKeyword);
        return newKeys;
    }

    private String generateWhereClauseForKeywordsWithDifferentAccentType(String keyword, String targetColumn) {
        String appendString = "";
        ArrayList<String> kw = convertKeywordsForDifferentAccentType(keyword.toLowerCase());
        for(String k : kw) {
            String str = "";
            for(String key : k.split(" ")) {
                str += targetColumn +" like '%"+key+"%' and ";
            }
            str = str.substring(0,str.length() - 5);
            appendString += "("+str+") or ";
        }
        appendString = appendString.substring(0,appendString.length() - 4);
        return appendString;
    }
}
