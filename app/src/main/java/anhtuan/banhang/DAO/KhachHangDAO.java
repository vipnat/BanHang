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

    public void OpenCONN() {
        try {
            if (_con.isClosed())
                _con = connectionDB.CONN();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CloseCONN() {
        try {
            _con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<KhachHang> getArrKhachHang() {
        OpenCONN();
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
            CloseCONN();
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return arrKhachHang;
    }

    public KhachHang LayKhachHangTheoMaKH(String strId) {
        OpenCONN();
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
            CloseCONN();
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return _khachHang;
    }

    public void CapNhapNoCuTheoKhachHang(KhachHang khachHang) {
        try {
            OpenCONN();
            String sqlUpdate = "UPDATE tblKhachHang SET NoCu='" + khachHang.getNoCu() + "' WHERE MaKH='" + khachHang.getMaKH() + "'";
            PreparedStatement statement = _con.prepareStatement(sqlUpdate);
            statement.executeUpdate();
            CloseCONN();
        } catch (SQLException _ex) {
            _ex.printStackTrace();
        }

    }
}
