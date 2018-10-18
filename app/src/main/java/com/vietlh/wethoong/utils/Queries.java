package com.vietlh.wethoong.utils;

import android.database.Cursor;

import com.vietlh.wethoong.entities.BosungKhacphuc;
import com.vietlh.wethoong.entities.Coquanbanhanh;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.entities.Loaivanban;
import com.vietlh.wethoong.entities.Vanban;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vietlh on 2/23/18.
 */

public class Queries{

    public static String rawQuery = "select distinct dk.id as dkId, dk.so as dkSo, tieude as dkTieude, dk.noidung as dkNoidung, minhhoa as dkMinhhoa, cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, vanbanid as vbId, vb.ten as vbTen, nam as vbNam, ma as vbMa, vb.noidung as vbNoidung, coquanbanhanh as vbCoquanbanhanhId, cq.ten as cqTen, dk.forSearch as dkSearch from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id where ";
    private DBConnection connection;
    private UtilsHelper utils = new UtilsHelper();

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
        String sql = "select distinct dk.id as dkId, dk.so as dkSo, dk.tieude as dkTieude, dk.noidung as dkNoidung, dk.minhhoa as dkMinhhoa, dk.cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, dk.vanbanid as vbId, vb.ten as vbTen, vb.nam as vbNam, vb.ma as vbMa, vb.noidung as vbNoidung, vb.coquanbanhanh as vbCoquanbanhanhId, cq.ten as cqTen, dk.forSearch as dkSearch, rdk.id as rdkId, rdk.so as rdkSo, rdk.tieude as rdkTieude, rdk.noidung as rdkNoidung, rdk.minhhoa as rdkMinhhoa, rdk.cha as rdkCha, rvb.loai as rlvbID, rlvb.ten as rlvbTen, rvb.so as rvbSo, rdk.vanbanid as rvbId, rvb.ten as rvbTen, rvb.nam as rvbNam, rvb.ma as rvbMa, rvb.noidung as rvbNoidung, rvb.coquanbanhanh as rvbCoquanbanhanhId, rcq.ten as rcqTen, rdk.forSearch as rdkSearch, kp.noidung as noidung from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id join tblBienphapkhacphuc as kp on dk.id = kp.dieukhoanId join tblChitietvanban as rdk on kp.dieukhoanQuydinhId = rdk.id join tblVanban as rvb on rdk.vanbanid=rvb.id join tblLoaivanban as rlvb on rvb.loai=rlvb.id join tblCoquanbanhanh as rcq on rvb.coquanbanhanh=rcq.id where kp.dieukhoanId = "+ dieukhoanId;

        ArrayList<BosungKhacphuc> bosungKhacphucArray = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateBosungKhacphucList(cursor, bosungKhacphucArray);
        connection.close();

        return bosungKhacphucArray;
    }

    public ArrayList<BosungKhacphuc> getAllHinhphatbosung(int dieukhoanId) {
        connection.open();
        String sql = "select distinct dk.id as dkId, dk.so as dkSo, dk.tieude as dkTieude, dk.noidung as dkNoidung, dk.minhhoa as dkMinhhoa, dk.cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, dk.vanbanid as vbId, vb.ten as vbTen, vb.nam as vbNam, vb.ma as vbMa, vb.noidung as vbNoidung, vb.coquanbanhanh as vbCoquanbanhanhId, cq.ten as cqTen, dk.forSearch as dkSearch, rdk.id as rdkId, rdk.so as rdkSo, rdk.tieude as rdkTieude, rdk.noidung as rdkNoidung, rdk.minhhoa as rdkMinhhoa, rdk.cha as rdkCha, rvb.loai as rlvbID, rlvb.ten as rlvbTen, rvb.so as rvbSo, rdk.vanbanid as rvbId, rvb.ten as rvbTen, rvb.nam as rvbNam, rvb.ma as rvbMa, rvb.noidung as rvbNoidung, rvb.coquanbanhanh as rvbCoquanbanhanhId, rcq.ten as rcqTen, rdk.forSearch as rdkSearch, hp.noidung as noidung from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id join tblHinhphatbosung as hp on dk.id = hp.dieukhoanId join tblChitietvanban as rdk on hp.dieukhoanQuydinhId = rdk.id join tblVanban as rvb on rdk.vanbanid=rvb.id join tblLoaivanban as rlvb on rvb.loai=rlvb.id join tblCoquanbanhanh as rcq on rvb.coquanbanhanh=rcq.id where hp.dieukhoanId = "+ dieukhoanId;

        ArrayList<BosungKhacphuc> bosungKhacphucArray = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateBosungKhacphucList(cursor, bosungKhacphucArray);
        connection.close();

        return bosungKhacphucArray;
    }

    public String searchDoituongInfo(String id) {

        connection.open();

        String sql = "select distinct canhan, tochuc, doanhnghiep, trungtam, daotao, nguoidieukhien, nguoingoitrenxe, nguoiduoctro" +
                ", giaovien, ga, chuphuongtien, nhanvien, dangkiemvien, laitau, truongdon, truongtau, dieukhienmaydon, trucban" +
                ", duaxe, kinhdoanh, vanchuyen, vantai, hanhkhach, hanghoa, ketcau, hatang, luukho, laprap, xepdo, quanly, thuphi" +
                ", dangkiem, sathach, dichvu, hotro, ghepnoi, gacchan, khamxe, thuham, phucvu, baoquan, sanxuat, hoancai, phuchoi" +
                ", khaithac, baotri from tblKeywords where dieukhoanId = " + id;

        Cursor cursor = connection.executeQuery(sql);
        String result = "";

        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String canhan = cursor.getString(0);
                String tochuc = cursor.getString(1);
                String doanhnghiep = cursor.getString(2);
                String trungtam = cursor.getString(3);
                String daotao = cursor.getString(4);
                String nguoidieukhien = cursor.getString(5);
                String nguoingoitrenxe = cursor.getString(6);
                String nguoiduoctro = cursor.getString(7);
                String giaovien = cursor.getString(8);
                String ga = cursor.getString(9);
                String chuphuongtien = cursor.getString(10);
                String nhanvien = cursor.getString(11);
                String dangkiemvien = cursor.getString(12);
                String laitau = cursor.getString(13);
                String truongdon = cursor.getString(14);
                String truongtau = cursor.getString(15);
                String dieukhienmaydon = cursor.getString(16);
                String trucban = cursor.getString(17);
                String duaxe = cursor.getString(18);
                String kinhdoanh = cursor.getString(19);
                String vanchuyen = cursor.getString(20);
                String vantai = cursor.getString(21);
                String hanhkhach = cursor.getString(22);
                String hanghoa = cursor.getString(23);
                String ketcau = cursor.getString(24);
                String hatang = cursor.getString(25);
                String luukho = cursor.getString(26);
                String laprap = cursor.getString(27);
                String xepdo = cursor.getString(28);
                String quanly = cursor.getString(29);
                String thuphi = cursor.getString(30);
                String dangkiem = cursor.getString(31);
                String sathach = cursor.getString(32);
                String dichvu = cursor.getString(33);
                String hotro = cursor.getString(34);
                String ghepnoi = cursor.getString(35);
                String gacchan = cursor.getString(36);
                String khamxe = cursor.getString(37);
                String thuham = cursor.getString(38);
                String phucvu = cursor.getString(39);
                String baoquan = cursor.getString(40);
                String sanxuat = cursor.getString(41);
                String hoancai = cursor.getString(42);
                String phuchoi = cursor.getString(43);
                String khaithac = cursor.getString(44);
                String baotri = cursor.getString(45);

                if (!"0".equals(ga)) {
                    result += "Ga, ";
                }
                if (!"0".equals(daotao)) {
                    result += "Cơ sở đào tạo Lái xe, ";
                }
                if (!"0".equals(nhanvien) && !"0".equals(ghepnoi)) {
                    result += "Nhân viên ghép nối đầu máy, toa xe, ";
                }
                if (!"0".equals(nhanvien) && !"0".equals(gacchan)) {
                    result += "Nhân viên gác chắn, ";
                }
                if (!"0".equals(nhanvien) && !"0".equals(khamxe)) {
                    result += "NNhân viên khám xe, ";
                }
                if (!"0".equals(nhanvien) && !"0".equals(khamxe) && !"0".equals(thuham)) {
                    result += "Nhân viên khám xe Phụ trách thử hãm, ";
                }
                if (!"0".equals(nhanvien) && !"0".equals(phucvu)) {
                    result += "Nhân viên Phục vụ, ";
                }
                //                if nhanvien != "0" && dieudo != "0" {
                //                    result += "Nhân viên điều độ chạy tàu, "
                //                }
                //                if nhanvien != "0" && ghepnoi != "0" {
                //                    result += "Nhân viên đường sắt, "
                //                }

                //                if dibo != "0" {
                //                    result += "Người đi bộ, "
                //                }
                if (!"0".equals(dangkiemvien)) {
                    result += "Đăng kiểm viên, ";
                }
                if (!"0".equals(nhanvien) && !"0".equals(trungtam) && !"0".equals(dangkiem)) {
                    result += "Nhân viên nghiệp vụ của Trung tâm Đăng kiểm, ";
                }
                if (!"0".equals(laitau)){
                    result += "Lái tàu (Phụ Lái tàu), ";
                }
                if (!"0".equals(truongdon)) {
                    result += "Trưởng dồn, ";
                }
                if (!"0".equals(truongtau)) {
                    result += "Trưởng tàu, ";
                }
                if (!"0".equals(dieukhienmaydon)) {
                    result += "Điều khiển máy dồn, ";
                }
                if (!"0".equals(trucban)) {
                    result += "Trực ban chạy tàu ga, ";
                }
                if (!"0".equals(canhan)) {
                    result += "Cá nhân, ";
                }
                if (!"0".equals(chuphuongtien)) {
                    result += "Chủ phương tiện, ";
                }
                if (!"0".equals(canhan) && (!"0".equals(kinhdoanh) || !"0".equals(hotro)) && !"0".equals(vantai)) {
                    result += "Cá nhân kinh doanh vận tải, dịch vụ hỗ trợ vận tải, ";
                }
                if (!"0".equals(giaovien)) {
                    result += "Giáo viên dạy Lái xe, ";
                }
                if (!"0".equals(duaxe)) {
                    result += "Người đua xe, ";
                }
                if (!"0".equals(nguoidieukhien)) {
                    result += "Người Điều khiển phương tiện, ";
                }
                if (!"0".equals(nguoiduoctro) || !"0".equals(nguoingoitrenxe)) {
                    result += "Người được chở (người ngồi trên xe), ";
                }
                if (!"0".equals(doanhnghiep) && !"0".equals(kinhdoanh) && !"0".equals(ketcau) && !"0".equals(hatang)){
                    result += "Doanh nghiệp kinh doanh kết cấu hạ tầng đường sắt (đường bộ), ";
                }
                if (!"0".equals(doanhnghiep) && (!"0".equals(luukho) || !"0".equals(baoquan)) && !"0".equals(kinhdoanh)
                        && !"0".equals(hanghoa)) {
                    result += "Doanh nghiệp kinh doanh lưu kho, bảo quản hàng hóa, ";
                }
                if (!"0".equals(doanhnghiep) && !"0".equals(kinhdoanh) && (!"0".equals(sanxuat) || !"0".equals(laprap)
                        || !"0".equals(hoancai) || !"0".equals(phuchoi))) {
                    result += "Doanh nghiệp kinh doanh sản xuất, lắp ráp, hoán cải, Phục hồi phương tiện giao thông, ";
                }
                if (!"0".equals(doanhnghiep) && !"0".equals(kinhdoanh) && !"0".equals(vantai)) {
                    result += "Doanh nghiệp kinh doanh vận tải đường sắt, ";
                }
                if (!"0".equals(doanhnghiep) && !"0".equals(kinhdoanh) && !"0".equals(xepdo) && !"0".equals(hanghoa)) {
                    result += "Doanh nghiệp kinh doanh xếp, dỡ hàng hóa, ";
                }
                if (!"0".equals(trungtam) && !"0".equals(sathach)) {
                    result += "Trung tâm sát hạch Lái xe, ";
                }
                if (!"0".equals(trungtam) && !"0".equals(dangkiem)) {
                    result += "Trung tâm Đăng kiểm, ";
                }
                if (!"0".equals(tochuc) && (!"0".equals(kinhdoanh) || !"0".equals(hotro)) && !"0".equals(vantai)) {
                    result += "Tổ chức kinh doanh vận tải, dịch vụ hỗ trợ vận tải, ";
                }
                if (!"0".equals(tochuc) && !"0".equals(quanly) && !"0".equals(kinhdoanh)) {
                    result += "Tổ chức quản lý, kinh doanh đường sắt, ";
                }
                if (!"0".equals(tochuc) && !"0".equals(thuphi)) {
                    result += "Tổ chức thu phí đường bộ, ";
                }
                if (!"0".equals(tochuc) && !"0".equals(quanly) && !"0".equals(khaithac)) {
                    result += "Tổ chức Trực tiếp quản lý, khai thác phương tiện giao thông đường sắt, ";
                }
                if (!"0".equals(tochuc) && !"0".equals(quanly) && !"0".equals(khaithac)
                        && !"0".equals(baotri) && !"0".equals(ketcau) && !"0".equals(hatang)) {
                    result += "Tổ chức được giao quản lý, khai thác, bảo trì kết cấu hạ tầng giao thông đường bộ (đường sắt), ";
                }
                cursor.moveToNext();
            }
        }

        if (result.length() >= 2) {
            result = result.substring(0,result.length() - 2);
        }
        connection.close();

        return result;
    }

    public String searchLinhvucInfo(String id) {
        connection.open();

        String sql = "select distinct duongbo, duongsat from tblLinhvuc where dieukhoanId = " + id;

        Cursor cursor = connection.executeQuery(sql);
        String result = "";

        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String duongbo = cursor.getString(0);
                String duongsat = cursor.getString(1);
                if (!"0".equals(duongbo)  && !"0".equals(duongsat)){
                    result = "Đường bộ, Đường sắt";
                }else{
                    if (!"0".equals(duongsat)) {
                        result = "Đường sắt";
                    }
                    if (!"0".equals(duongbo)) {
                        result = "Đường bộ";
                    }
                }
                cursor.moveToNext();
            }
        }
        connection.close();

        return result;
    }

    public String searchPhuongtienInfo(String id) {
        connection.open();

        String sql = "select distinct oto, otoTai, maykeo, xechuyendung, tau, moto, xeganmay, xemaydien, xedapmay, xedap, xedapdien" +
                ", xethoso, sucvat, xichlo, dibo from tblPhuongtien where dieukhoanId = " + id;

        Cursor cursor = connection.executeQuery(sql);
        String result = "";

        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String oto = cursor.getString(0);
                String otoTai = cursor.getString(1);
                String maykeo = cursor.getString(2);
                String xechuyendung = cursor.getString(3);
                String tau = cursor.getString(4);
                String moto = cursor.getString(5);
                String xeganmay = cursor.getString(6);
                String xemaydien = cursor.getString(7);
                String xedapmay = cursor.getString(8);
                String xedap = cursor.getString(9);
                String xedapdien = cursor.getString(10);
                String xethoso = cursor.getString(11);
                String sucvat = cursor.getString(12);
                String xichlo = cursor.getString(13);
                String dibo = cursor.getString(14);


                if (!"0".equals(oto) || !"0".equals(otoTai)) {
                    result += "Ô tô (ô tô tải, rơ moóc), ";
                }
                if (!"0".equals(maykeo) || !"0".equals(xechuyendung)) {
                    result += "Máy kéo (xe chuyên dùng), ";
                }
                if (!"0".equals(moto) || !"0".equals(xeganmay) || !"0".equals(xemaydien)) {
                    result += "Xe máy, ";
                }
                if (!"0".equals(xedapmay) || !"0".equals(xedap) || !"0".equals(xedapdien)
                        || !"0".equals(xethoso) || !"0".equals(sucvat) || !"0".equals(xichlo)) {
                    result += "Xe thô sơ (xe súc vật kéo), ";
                }
                if (!"0".equals(tau)) {
                    result += "Tàu hoả, ";
                }
                if (!"0".equals(dibo)) {
                    result += "Người đi bộ, ";
                }
                cursor.moveToNext();
            }
        }

        if (result.length() >= 2) {
            result = result.substring(0,result.length() - 2);
        }
        connection.close();

        return result;
    }

    public String searchMucphatInfo(String id) {
        connection.open();

        String sql = "select distinct canhanTu, canhanDen, tochucTu, tochucDen from tblMucphat where dieukhoanId = " + id;

        Cursor cursor = connection.executeQuery(sql);
        String result = "";
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String cnTu = cursor.getString(0);
                String cnDen = cursor.getString(1);
                String tcTu = cursor.getString(2);
                String tcDen = cursor.getString(3);

                if ((!"".equals(tcTu)) && (!"".equals(tcDen))) {
                    result = "cá nhân: " + cnTu + " - " + cnDen + "tổ chức: " + tcTu + " - " + tcDen;
                } else {
                    if (!"".equals(cnDen)) {
                        if (!"".equals(cnTu)) {
                            result = cnTu + " - " + cnDen;
                        } else {
                            result = "đến " + cnDen;
                        }
                    }
                }
                cursor.moveToNext();
            }
        }

        connection.close();

        return result;

    }

    public ArrayList<Dieukhoan> getAllRelativeRelatedDieukhoan(int dieukhoanId) {
        connection.open();
        String sql = getRawQuery() + " dkId in (select dieukhoanId from tblRelatedDieukhoan where relatedDieukhoanId = " + dieukhoanId + ")";

        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateDieukhoanList(cursor,allDieukhoan);

        connection.close();

        return allDieukhoan;
    }

    public ArrayList<Dieukhoan> getAllDirectRelatedDieukhoan(int dieukhoanId) {
        connection.open();
        String sql = getRawQuery() + " dkId in (select relatedDieukhoanId from tblRelatedDieukhoan where dieukhoanId = "+ dieukhoanId + ")";

        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateDieukhoanList(cursor,allDieukhoan);

        connection.close();

        return allDieukhoan;
    }

    public ArrayList<Dieukhoan> searchChildren(String keyword,ArrayList<String> vanbanid) {
        connection.open();
        String specificVanban = generateWhereClauseForVanbanid(vanbanid, "vbId");
        String searchArgurment = "";
        String searchKeyword = keyword.trim();

        if(searchKeyword.length() == 0){
            searchArgurment = "is null";
        }else{
            searchArgurment = "= " + searchKeyword;
        }

        String sql = getRawQuery() + "dkCha " + searchArgurment + specificVanban;

        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateDieukhoanList(cursor,allDieukhoan);

        connection.close();

        return allDieukhoan;
    }

    public ArrayList<Dieukhoan> searchDieukhoanBySo(String keyword, ArrayList<String> vanbanid) {
        connection.open();
        String specificVanban = generateWhereClauseForVanbanid(vanbanid, "vbId");
        String sql = getRawQuery() + "(dkSo = '" + keyword + "' or dk.forsearch like '" + keyword + "' or dk.forsearch like '" + keyword + "')" + specificVanban;

        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateDieukhoanList(cursor,allDieukhoan);

        connection.close();

        return allDieukhoan;
    }

    public ArrayList<Dieukhoan> searchDieukhoanByID(String keyword, ArrayList<String> vanbanid) {
        connection.open();
        String specificVanban = generateWhereClauseForVanbanid(vanbanid, "vbId");
        String sql = getRawQuery() + " dkId = " + keyword + " " + specificVanban;

        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateDieukhoanList(cursor,allDieukhoan);

        connection.close();

        return allDieukhoan;
    }

    public ArrayList<Dieukhoan> searchDieukhoanByIDs(ArrayList<String> keyword, ArrayList<String> vanbanid) {
        connection.open();
        String specificVanban = generateWhereClauseForVanbanid(vanbanid, "vbId");
        String idGroup = "";
        for(String id: keyword) {
            idGroup += "dkId = " + id + " or ";
        }

        String sql = getRawQuery();

        if (idGroup.length() > 3) {
            sql += "(" + utils.removeLastCharacters(idGroup, 4) + ")" + specificVanban;
        } else {
            sql += utils.removeFirstCharacters(specificVanban, 4);
        }

        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateDieukhoanList(cursor,allDieukhoan);

        connection.close();

        return allDieukhoan;
    }

    public ArrayList<Dieukhoan> searchDieukhoanByQuery(String query, ArrayList<String> vanbanid) {
        connection.open();

        String specificVanban = generateWhereClauseForVanbanid(vanbanid, "vbId");
        String sql = query.toLowerCase() + specificVanban;

        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateDieukhoanList(cursor,allDieukhoan);

        connection.close();

        return allDieukhoan;
    }

    public ArrayList<Dieukhoan> searchDieukhoan(String keyword, ArrayList<String> vanbanid) {
        connection.open();

        String specificVanban = generateWhereClauseForVanbanid(vanbanid, "vbId");
        String appendString = generateWhereClauseForKeywordsWithDifferentAccentType(keyword, "dkSearch");

        String sql = getRawQuery()+ " (" + appendString + ") " + specificVanban;

        ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
        Cursor cursor = connection.executeQuery(sql);
        generateDieukhoanList(cursor,allDieukhoan);

        connection.close();

        return allDieukhoan;
    }

    public ArrayList<String> getPlateGroups(){
        connection.open();

        String sql = "select ten from tblShapeGroups";

        Cursor cursor = connection.executeQuery(sql);
        ArrayList<String> result = new ArrayList<>();
        if(cursor != null){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);
                if(name.length() > 0){
                    result.add(name);
                }
                cursor.moveToNext();
            }
        }

        connection.close();

        return result;
    }

    public ArrayList<String> getPlateShapeByGroup(ArrayList<String> groups) {
        connection.open();
        String whereClause = "";
        for(String group: groups) {
            whereClause += "ten = '" + group + "' or ";
        }

        //workaround for no group selected
        if(groups.size() < 1) {
            whereClause = "ten = '' or ";
        }
        String sql = "select ten from tblPlateShapes where type in (select id from tblShapeGroups where (" + utils.removeLastCharacters(whereClause, 4) + "))";

        Cursor cursor = connection.executeQuery(sql);
        ArrayList<String> result = new ArrayList<>();
        if(cursor != null){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);
                if(name.length() > 0){
                    result.add(name);
                }
                cursor.moveToNext();
            }
        }

        connection.close();

        return result;
    }

    public ArrayList<Dieukhoan> getPlateByParams(ArrayList<String> params, ArrayList<String> groups) {
        connection.open();
        String sql = "";
        int index = 0;
        String inGroup = "";
        for (String group : groups) {
            inGroup += "'" + group + "', ";
        }

        // if no params or groups set, get all plates
        if (params.size() == 0 && groups.size() < 1) {
            sql = "select plateId as pid, name from tblPlateReferences";
        } else if (params.size() == 0) { // if no params but groups set, select plates that has type of 'tblPlateShapes' and refID is one of the selected shapes
            sql = "select plateId as pid, name from tblPlateReferences where type = 'tblPlateShapes' and refId in (select id from tblPlateShapes where type in (select id from tblShapeGroups where ten in (" + utils.removeLastCharacters(inGroup, 2) + ")))";
        } else { //if at list 1 param set, select plate that matched that param
            for (String type : params) {
                String[] details = type.split(":");
                if (index == 0) {
                    sql = "select a0.plateId as pid, a0.name as name from (select * from tblPlateReferences where type = '" + details[0] + "'";
                    if (details.length > 1) {
                        sql +=  " and refId = (select id from '" + details[0] + "' where ten = '" + details[1] + "')";
                    }
                    sql += " and (plateId in (select plateID from tblPlateReferences where type = 'tblPlateShapes' and refid in (select id from tblPlateShapes where type in (select id from tblShapeGroups where ten in (" + utils.removeLastCharacters(inGroup, 2) + ")))))) as a0";
                } else {
                    sql += " JOIN (select * from tblPlateReferences where type = '" + details[0] + "'";
                    if (details.length > 1) {
                        sql +=  " and refId = (select id from '" + details[0] + "' where ten = '" + details[1] + "')";
                    }

                    sql += ") as a" + index +" on a0.name = a" + index + ".name";
                }
                index += 1;
            }
        }

        Cursor cursor = connection.executeQuery(sql);
        HashMap<String,String> result = new HashMap<>();
        if(cursor != null){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String pid = String.valueOf(cursor.getInt(0));
                String pname = cursor.getString(1);
                if(pid != null){
                    result.put(pname,pid);
                }
                cursor.moveToNext();
            }
        }

        connection.close();

        ArrayList<Dieukhoan> finalResult = new ArrayList<>();

        if (result.size() < 1) {
            return finalResult;
        }

        HashMap<String,Dieukhoan> dkList = new HashMap<>();
        ArrayList<String> vbid = new ArrayList<String>();
        vbid.add(GeneralSettings.getVanbanInfo("qc41","id"));
        for (Dieukhoan dk : searchDieukhoanByIDs(new ArrayList<String>(result.values()), vbid)) {
            dkList.put(String.valueOf(dk.getId()),dk);
        }

        for (String rs : result.keySet()) {
            Dieukhoan dk = dkList.get(result.get(rs));
            Dieukhoan fdk = new Dieukhoan(0,0,null);
            fdk.cloneDieukhoan(dk);
            fdk.setDefaultMinhhoa(rs);
            finalResult.add(fdk);
        }

        return finalResult;
    }

    private void generateBosungKhacphucList(Cursor cursor,ArrayList<BosungKhacphuc> bosungKhacphucArray) {
        if(cursor != null){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ArrayList<String> minhhoa = new ArrayList<>();
                for (String mh : cursor.getString(4).split(";")) {
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
                cursor.moveToNext();
            }
        }
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
                    if (mh.length() > 0) {
                        minhhoa.add(mh);
                    }
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

    public ArrayList<String> convertKeywordsForDifferentAccentType(String keyword) {
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
