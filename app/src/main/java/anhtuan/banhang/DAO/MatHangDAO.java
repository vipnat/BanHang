package anhtuan.banhang.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import anhtuan.banhang.DTO.MatHang;
import anhtuan.banhang.Database.ConnectionDB;


public class MatHangDAO {
    ConnectionDB connectionDB = new ConnectionDB();
    Connection _con = connectionDB.CONN();
    String _ex = "";
    ResultSet _rs;
    ArrayList<MatHang> arrMatHang = new ArrayList<MatHang>();
    MatHang _matHang;

    public ArrayList<MatHang> getArrMatHang(String _maLoai) {

        try {
            String sqlSelect = "SELECT * FROM tblMatHang WHERE SUBSTRING(MaMatH,1,3) = '" + _maLoai + "'";
            if (_maLoai == "DAU")
                sqlSelect = "SELECT * FROM tblMatHang WHERE SUBSTRING(MaMatH,1,3) = 'DAI' OR SUBSTRING(MaMatH,1,3) = 'DAU'";
            PreparedStatement statement = _con.prepareStatement(sqlSelect);
            _rs = statement.executeQuery();
            arrMatHang.clear();
            while (_rs.next()) {
                _matHang = new MatHang();
                _matHang.setMaMatH(_rs.getString("MaMatH"));
                _matHang.setTenMatH(_rs.getString("TenMatH"));
                _matHang.setSoLuong(Float.parseFloat(_rs.getString("SoLuong")));
                _matHang.setDonGia(Float.parseFloat(_rs.getString("DonGia")));

                arrMatHang.add(_matHang);
            }
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return arrMatHang;
    }

    public MatHang getMatHangByID(String mhMH) {
        try {
            String sqlSelect = "SELECT * FROM tblMatHang WHERE MaMatH = '" + mhMH + "'";
            PreparedStatement statement = _con.prepareStatement(sqlSelect);
            _rs = statement.executeQuery();

            while (_rs.next()) {
                _matHang = new MatHang();
                _matHang.setMaMatH(_rs.getString("MaMatH"));
                _matHang.setTenMatH(_rs.getString("TenMatH"));
                _matHang.setSoLuong(Float.parseFloat(_rs.getString("SoLuong")));
                _matHang.setDonGia(Float.parseFloat(_rs.getString("DonGia")));
            }
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return _matHang;
    }

    public String layGiaBanTheoMHvaKH(String maMatHang, String _strMaKhachHang) {
        String strGiaban = "";
        try {
            String selectGiaTheoKH = "SELECT [tblGiaBan].[GiaBan] FROM [tblGiaBan] INNER JOIN [tblMatHang] ON [tblMatHang].MaMatH = [tblGiaBan].MaMatH WHERE [MaKH]='" + _strMaKhachHang + "' AND [tblMatHang].MaMatH = '" + maMatHang + "'";
            PreparedStatement statement = _con.prepareStatement(selectGiaTheoKH);
            _rs = statement.executeQuery();

            while (_rs.next()) {
                strGiaban = _rs.getString("GiaBan");
            }

        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return strGiaban;
    }

}
