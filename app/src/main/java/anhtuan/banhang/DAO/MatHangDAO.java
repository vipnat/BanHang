package anhtuan.banhang.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import anhtuan.banhang.DTO.MatHangDTO;
import anhtuan.banhang.Database.ConnectionDB;


public class MatHangDAO {
    ConnectionDB connectionDB = new ConnectionDB();
    Connection _con = connectionDB.CONN();
    String _ex = "";
    ResultSet _rs;
    ArrayList<MatHangDTO> arrMatHang = new ArrayList<MatHangDTO>();
    MatHangDTO _matHang;

    public ArrayList<MatHangDTO> getArrMatHang(String _maLoai) {
        try {
            String sqlSelect = "SELECT * FROM tblMatHang WHERE SUBSTRING(MaMatH,1,3) = '"+ _maLoai +"'";
            if(_maLoai == "DAU")
                sqlSelect = "SELECT * FROM tblMatHang WHERE SUBSTRING(MaMatH,1,3) = 'DAI' OR SUBSTRING(MaMatH,1,3) = 'DAU'";
            PreparedStatement statement = _con.prepareStatement(sqlSelect);
            _rs = statement.executeQuery();

            arrMatHang.clear();
            while (_rs.next()) {
                _matHang = new MatHangDTO();
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

    public MatHangDTO getMatHangByID() {
        try {
            PreparedStatement statement = _con.prepareStatement("EXEC getMatHangByID");
            _rs = statement.executeQuery();


            while (_rs.next()) {
                _matHang.setMaMatH(_rs.getString("MaMatH"));
                _matHang.setTenMatH(_rs.getString("TenMatH"));
                _matHang.setSoLuong(Float.parseFloat(_rs.getString("SoLuong")));
                _matHang.setDonGia(Float.parseFloat(_rs.getString("DonGia")));

                arrMatHang.add(_matHang);
            }
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return  _matHang;
    }


}
