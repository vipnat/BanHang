package anhtuan.banhang.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import anhtuan.banhang.DTO.MatHang;
import anhtuan.banhang.DTO.NhanVien;
import anhtuan.banhang.Database.ConnectionDB;

public class NhanVienDAO {
    ConnectionDB connectionDB = new ConnectionDB();
    Connection _con = connectionDB.CONN();
    String _ex = "";
    ResultSet _rs;

    ArrayList<NhanVien> arrNhanVien = new ArrayList<NhanVien>();
    NhanVien nhanVien;

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

    public ArrayList<NhanVien> getArrNhanVien() {
        OpenCONN();
        try {
            String sqlSelect = "SELECT * FROM tblNhanVien ORDER BY MaNhanVien DESC";
            PreparedStatement statement = _con.prepareStatement(sqlSelect);
            _rs = statement.executeQuery();
            arrNhanVien.clear();
            while (_rs.next()) {
                nhanVien = new NhanVien();
                nhanVien.setMaNhanVien(_rs.getString("MaNhanVien"));
                nhanVien.setTenNhanVien(_rs.getString("TenNhanVien"));
                nhanVien.setDiaChi(_rs.getString("DiaChi"));
                nhanVien.setDienThoai(_rs.getString("DienThoai"));
                arrNhanVien.add(nhanVien);
            }
            CloseCONN();
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return arrNhanVien;
    }

    public NhanVien LayThongTinNhanVienTheoMa(String maNV) {
        String sqlSelect = "SELECT * FROM tblNhanVien Where MaNhanVien ='" + maNV + "'";
        OpenCONN();
        try {
            PreparedStatement statement = _con.prepareStatement(sqlSelect);
            _rs = statement.executeQuery();
            while (_rs.next()) {
                nhanVien = new NhanVien();
                nhanVien.setMaNhanVien(_rs.getString("MaNhanVien"));
                nhanVien.setTenNhanVien(_rs.getString("TenNhanVien"));
                nhanVien.setDiaChi(_rs.getString("DiaChi"));
                nhanVien.setDienThoai(_rs.getString("DienThoai"));
            }
            CloseCONN();
        } catch (SQLException _ex) {
            _ex.printStackTrace();
        }
        return nhanVien;
    }

}
