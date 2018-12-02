package anhtuan.banhang.DAO;

import android.content.Intent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import anhtuan.banhang.Database.ConnectionDB;

public class SuLyHoaDonMua {
    ConnectionDB connectionDB = new ConnectionDB();
    Connection _con = connectionDB.CONN();
    String _ex = "";
    ResultSet _rs;

    public String LayMaHoaDonTheoNgay(String DayMonthYear) {
        String MaHD = "";
        int maSo = 1;
        String selectMaHoaDon = "select MaHD FROM tblHoaDonXuat";
        try {
            PreparedStatement statement = _con.prepareStatement(selectMaHoaDon);
            _rs = statement.executeQuery();
            while (_rs.next()) {
                String getMaHD = _rs.getString("MaHD");
                String getNgay = getMaHD.substring(0, 8);
                String getMa = getMaHD.substring(8);
                if (getMaHD.length() == 11) {
                    getNgay = getMaHD.substring(0, 8);
                    getMa = getMaHD.substring(8);
                }
                if (getNgay == DayMonthYear) {
                    if (maSo == Integer.parseInt(getMa))
                        maSo++;
                }
                MaHD = DayMonthYear + (new DecimalFormat("#000")).format(maSo);
            }

        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return MaHD;
    }
}
