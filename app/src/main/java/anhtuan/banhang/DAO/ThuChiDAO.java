package anhtuan.banhang.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import anhtuan.banhang.DTO.ThuChi;
import anhtuan.banhang.Database.ConnectionDB;

public class ThuChiDAO {
    ConnectionDB connectionDB = new ConnectionDB();
    Connection _con = connectionDB.CONN();
    String _ex = "";
    ResultSet _rs;
    PreparedStatement statement;

    public void OpenCONN() {
        _con = connectionDB.CONN();
    }

    public void CloseCONN() {
        try {
            _con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ArrayList<ThuChi> arayThuChi = new ArrayList<ThuChi>();
    ThuChi _thuChi;

    public ArrayList<ThuChi> getArayThuChi() {
        try {
            OpenCONN();
            String sqlSelect = "SELECT * FROM  tblThuChi ORDER BY Ngay DESC,Id DESC";
            PreparedStatement pre = _con.prepareStatement(sqlSelect);
            _rs = pre.executeQuery();
            while (_rs.next()) {
                _thuChi = new ThuChi();
                _thuChi.setId(_rs.getInt("Id"));
                _thuChi.setMaHD(_rs.getString("MaHD"));
                _thuChi.setNgay(_rs.getDate("Ngay"));
                _thuChi.setSoTien(_rs.getInt("SoTien"));
                _thuChi.setGhiChu(_rs.getString("GhiChu"));
                _thuChi.setThu1Chi0(_rs.getInt("Thu1Chi0") == 1 ? true : false);
                _thuChi.setTienTrongNha(_rs.getDouble("TienTrongNha"));
                arayThuChi.add(_thuChi);
            }
            CloseCONN();
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return arayThuChi;
    }

    public ArrayList<ThuChi> getArayThuChi(Date dateStart, Date dateEnd) {
        SimpleDateFormat frmDate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            OpenCONN();
            String sqlSelect = "SELECT * FROM  tblThuChi WHERE Ngay BETWEEN '" + frmDate.format(dateStart) + "' AND '" + frmDate.format(dateEnd) + "' ORDER BY Ngay DESC, Id DESC";
            PreparedStatement pre = _con.prepareStatement(sqlSelect);
            _rs = pre.executeQuery();
            while (_rs.next()) {
                _thuChi = new ThuChi();
                _thuChi.setId(_rs.getInt("Id"));
                _thuChi.setMaHD(_rs.getString("MaHD"));
                _thuChi.setNgay(_rs.getDate("Ngay"));
                _thuChi.setSoTien(_rs.getInt("SoTien"));
                _thuChi.setGhiChu(_rs.getString("GhiChu"));
                _thuChi.setThu1Chi0(_rs.getInt("Thu1Chi0") == 1 ? true : false);
                _thuChi.setTienTrongNha(_rs.getDouble("TienTrongNha"));
                arayThuChi.add(_thuChi);
            }
            CloseCONN();
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return arayThuChi;
    }

    public void ThemThuChiVaoDatabase(ThuChi thuChi) {
        SimpleDateFormat frmDate = new SimpleDateFormat("MM/dd/yyyy");
        OpenCONN();
        String strInsert = "INSERT INTO [dbo].[tblThuChi] ([MaHD] ,[Ngay] ,[SoTien] ,[GhiChu] ,[Thu1Chi0],[TienTrongNha]) " +
                "VALUES (N'" + thuChi.getMaHD() + "',N'" + frmDate.format(thuChi.getNgay()) + "',N'" + thuChi.getSoTien() + "',N'" + (thuChi.getGhiChu() == "" ? frmDate.format(thuChi.getNgay()) : thuChi.getGhiChu()) + "',N'" + (thuChi.getThu1Chi0() == true ? 1 : 0) + "','" + thuChi.getTienTrongNha() + "')";
        try {
            statement = _con.prepareStatement(strInsert);
            statement.executeUpdate();
            CloseCONN();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void XoaThuChiTrongDatabase(ThuChi thuChi) {
        OpenCONN();
        String strDelete = "DELETE FROM [dbo].[tblThuChi] WHERE Id = '" + thuChi.getId() + "'";
        if (!thuChi.getThu1Chi0())
            strDelete = "DELETE FROM [dbo].[tblThuChi] WHERE Ngay = '" + thuChi.getNgay() + "' AND Id = '" + thuChi.getId() + "'";
        try {
            statement = _con.prepareStatement(strDelete);
            statement.executeUpdate();
            CloseCONN();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateThuChiTrongDatabase(ThuChi thuChi) {
        OpenCONN();
        String strUpdate = "UPDATE [dbo].[tblThuChi] SET [TienTrongNha] = '" + thuChi.getTienTrongNha() + "' WHERE Id = '" + thuChi.getId() + "'";
        try {
            statement = _con.prepareStatement(strUpdate);
            statement.executeUpdate();
            CloseCONN();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ThuChi LayThuChiMoiNhat() {
        try {
            OpenCONN();
            String sqlSelect = "SELECT TOP(1)* FROM  tblThuChi ORDER BY Id DESC";
            PreparedStatement pre = _con.prepareStatement(sqlSelect);
            _rs = pre.executeQuery();
            while (_rs.next()) {
                _thuChi = new ThuChi();
                _thuChi.setId(_rs.getInt("Id"));
                _thuChi.setMaHD(_rs.getString("MaHD"));
                _thuChi.setNgay(_rs.getDate("Ngay"));
                _thuChi.setSoTien(_rs.getInt("SoTien"));
                _thuChi.setGhiChu(_rs.getString("GhiChu"));
                _thuChi.setThu1Chi0(_rs.getInt("Thu1Chi0") == 1 ? true : false);
                _thuChi.setTienTrongNha(_rs.getDouble("TienTrongNha"));
            }
            CloseCONN();
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return _thuChi;
    }

    public Double LayTienTrongNhaMoiNhat() {
        Double tienTong = null;
        try {
            OpenCONN();
            String sqlSelect = "SELECT TOP(1)* FROM  tblThuChi ORDER BY Id DESC";
            PreparedStatement pre = _con.prepareStatement(sqlSelect);
            _rs = pre.executeQuery();
            while (_rs.next()) {
                tienTong = _rs.getDouble("TienTrongNha");
            }
            CloseCONN();
        } catch (Exception ex) {
            tienTong = 0.0;
        }
        return tienTong;
    }

    public void UpdateTienTheoThuChi() {
        try {
            OpenCONN();
            String sqlSelect = "SELECT * FROM  tblThuChi ORDER BY Id";
            PreparedStatement pre = _con.prepareStatement(sqlSelect);
            _rs = pre.executeQuery();
            int sotien = 0;
            double tientong = 166125;
            while (_rs.next()) {
                _thuChi = new ThuChi();
                _thuChi.setId(_rs.getInt("Id"));
                _thuChi.setMaHD(_rs.getString("MaHD"));
                _thuChi.setNgay(_rs.getDate("Ngay"));
                _thuChi.setSoTien(_rs.getInt("SoTien"));
                _thuChi.setGhiChu(_rs.getString("GhiChu"));
                _thuChi.setThu1Chi0(_rs.getInt("Thu1Chi0") == 1 ? true : false);
                _thuChi.setTienTrongNha(_rs.getDouble("TienTrongNha"));

                if (_thuChi.getId() > 1116) {
                    sotien = _thuChi.getSoTien();
                    tientong = (_thuChi.getThu1Chi0() ? tientong + sotien : tientong - sotien);
                    if (tientong != _thuChi.getTienTrongNha()) {
                        String strUpdate = "UPDATE [dbo].[tblThuChi] SET [TienTrongNha] = '" + tientong + "' WHERE Id = '" + _thuChi.getId() + "'";
                        try {
                            statement = _con.prepareStatement(strUpdate);
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            CloseCONN();
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
    }

}
