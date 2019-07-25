package anhtuan.banhang.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import anhtuan.banhang.DTO.KhachHang;
import anhtuan.banhang.Database.ConnectionDB;

public class KhachHangDAO {

    ConnectionDB connectionDB = new ConnectionDB();
    Connection _con = connectionDB.CONN();
    String _ex = "";
    ResultSet _rs;
    ArrayList<KhachHang> arrKhachHang = new ArrayList<KhachHang>();
    KhachHang _khachHang;

    public ArrayList<KhachHang> getArrKhachHang() {
        try {
            String sqlSelect = "SELECT * FROM tblKhachHang";
            PreparedStatement pre = _con.prepareStatement(sqlSelect);
            _rs = pre.executeQuery();
            while (_rs.next()) {
                _khachHang = new KhachHang();
                _khachHang.setMaKH(_rs.getString("MaKH"));
                _khachHang.setTenKH(_rs.getString("TenKH"));
                _khachHang.setDiaChi(_rs.getString("DiaChi"));
                _khachHang.setDienThoai(_rs.getString("SoDT"));
                _khachHang.setNoCu(_rs.getString("NoCu"));
                arrKhachHang.add(_khachHang);
            }
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return arrKhachHang;
    }

    public KhachHang get_khachHang_by_id(String strId) {
        try {
            String sqlSelect = "SELECT * FROM tblKhachHang WHERE MaKH = '" + strId + "'";
            PreparedStatement pre = _con.prepareStatement(sqlSelect);
            _rs = pre.executeQuery();
            while (_rs.next()) {

                _khachHang = new KhachHang();
                _khachHang.setMaKH(_rs.getString("MaKH"));
                _khachHang.setTenKH(_rs.getString("TenKH"));
                _khachHang.setDiaChi(_rs.getString("DiaChi"));
                _khachHang.setDienThoai(_rs.getString("SoDT"));
                _khachHang.setNoCu(_rs.getString("NoCu"));

            }
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return _khachHang;
    }

    public void CapNhapNoCuTheoKhachHang(KhachHang khachHang) {
        try {
            _con = connectionDB.CONN();
            String sqlUpdate = "UPDATE tblKhachHang SET NoCu='" + khachHang.getNoCu() + "' WHERE MaKH='" + khachHang.getMaKH() + "'";
            PreparedStatement statement = _con.prepareStatement(sqlUpdate);
            statement.executeUpdate();
            _con.close();
        } catch (SQLException _ex) {
            _ex.printStackTrace();
        }

    }
}
