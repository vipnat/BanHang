package anhtuan.banhang.DAO;

import android.app.LauncherActivity;

import com.itextpdf.text.ListItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import anhtuan.banhang.DTO.HoaDonXuat;
import anhtuan.banhang.DTO.KhachHang;
import anhtuan.banhang.DTO.MatHang;
import anhtuan.banhang.Database.ConnectionDB;

public class HoaDonXuatDAO {
    ConnectionDB connectionDB = new ConnectionDB();
    Connection _con = connectionDB.CONN();
    String _ex = "";
    ResultSet _rs;

    public String LayMaHoaDonTheoNgay(String DayMonthYear) {
        String MaHD = "";
        int maSo = 1;
        String selectMaHoaDon = "SELECT MaHD FROM tblHoaDonXuat";
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
                if (getNgay.trim().equals(DayMonthYear.trim())) {
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

    public void XoaAllHoaDonXuatNull() {
        String sqlQuery = "SELECT MaHD FROM tblHoaDonXuat WHERE NOT EXISTS (SELECT MaHD FROM tblChiTietHDX WHERE " +
                "tblHoaDonXuat.MaHD = tblChiTietHDX.MaHD)";  // Có Trong Hóa Đơn Mà Không Có Chi Tiết. (HĐ Ảo)
        try {
            PreparedStatement statement = _con.prepareStatement(sqlQuery);
            _rs = statement.executeQuery();
            while (_rs.next()) {
                String deleteHDX = "DELETE FROM tblHoaDonXuat WHERE MaHD ='" + _rs.getString("MaHD") + "'";
                statement = _con.prepareStatement(deleteHDX);
                statement.executeUpdate();
            }
        } catch (SQLException _ex) {
            _ex.printStackTrace();
        }
    }

    public double LayTongTienGocCuaHD(ArrayList<MatHang> listMH) {
        double tongTienGoc = 0;
        String sqlSelect = "";
        try {
            for (MatHang mh : listMH) {
                sqlSelect = "SELECT (DonGia*" + mh.getSoLuong() + ")AS TongGoc FROM tblMatHang WHERE MaMatH = N'" + mh.getMaMatH() + "'";
                PreparedStatement statement = _con.prepareStatement(sqlSelect);
                _rs = statement.executeQuery();
                while (_rs.next()) {
                    tongTienGoc = tongTienGoc + _rs.getDouble("TongGoc");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongTienGoc;
    }

    public void ThemHoaDonXuat(HoaDonXuat hdx) {
        SimpleDateFormat frmDate = new SimpleDateFormat("MM/dd/yyyy");
        String ngayBan = frmDate.format(new Date()); // Ngày Hiện Tại
        String insertHDX = "insert into tblHoaDonXuat(MaHD,MaNhanVien,NgayXuat,GhiChu) values(N'" + hdx.getMaHD() + "',N'" + hdx.getMaNhanVien() + "',N'" + ngayBan + "',N'" + hdx.getGhiChu() + "')";
        PreparedStatement statement = null;
        try {
            statement = _con.prepareStatement(insertHDX);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateTTHoaDonXuat(HoaDonXuat hdx) {
        String updateSQL = "UPDATE tblHoaDonXuat SET TongTienGoc ='" + hdx.getTongTienGoc() + "' , TongTien = '" + hdx.getTongTien() + "' WHERE MaHD='" + hdx.getMaHD() + "'";
        PreparedStatement statement = null;
        try {
            statement = _con.prepareStatement(updateSQL);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean InsertDuLieuMuaDB(ArrayList<MatHang> arayListView, HoaDonXuat hdx, KhachHang kh) {
        try {
            String query_SQL = "";
            for (MatHang mh : arayListView) {
                PreparedStatement statement = null;

                //Thêm vào bảng tblChiTietHDX
                query_SQL = "insert into tblChiTietHDX(MaMatH,MaHD,SoLuong,DonGia) values(N'" + mh.getMaMatH() + "',N'" + hdx.getMaHD() + "'," + mh.getSoLuong() + "," + mh.getDonGia() + ")";
                statement = _con.prepareStatement(query_SQL);
                statement.executeUpdate();

                //Cập nhật lại Số Lượng cho bảng tblMatHang (bớt số lượng mặt hàng)
                query_SQL = "update tblMatHang set SoLuong=SoLuong-" + mh.getSoLuong() + " where MaMatH=N'" + mh.getMaMatH() + "'";
                statement = _con.prepareStatement(query_SQL);
                statement.executeUpdate();

                //Cập nhập giá mới cho khách hàng
                query_SQL = "update [tblGiaBan] set [GiaBan]=" + mh.getDonGia() + " where [MaKH]=N'" + kh.getMaKH() + "' and [MaMatH] =N'" + mh.getMaMatH() + "'";
                statement = _con.prepareStatement(query_SQL);
                statement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void DeleteDuLieuMuaDB(String strMaHoaDon) {
        try {
            if (!KiemTraTonTaiTrongChiTietHoaDon(strMaHoaDon))
                return;
            String query_SQL = "SELECT * FROM tblChiTietHDX WHERE MaHD ='" + strMaHoaDon + "'";
            PreparedStatement statement = _con.prepareStatement(query_SQL);
            _rs = statement.executeQuery();
            while (_rs.next()) {
                //Cập nhật lại Số Lượng cho bảng tblMatHang (Thêm số lượng mặt hàng)
                query_SQL = "update tblMatHang set SoLuong=SoLuong+" + _rs.getFloat("SoLuong") + " where MaMatH=N'" + _rs.getString("MaMatH") + "'";
                statement = _con.prepareStatement(query_SQL);
                statement.executeUpdate();
            }

            //Xóa Tất Cả Trong Chi Tiết Hóa Đơn Theo Mã Hóa Đơn
            query_SQL = "DELETE tblChiTietHDX WHERE MaHD='" + strMaHoaDon + "'";
            statement = _con.prepareStatement(query_SQL);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean KiemTraTonTaiTrongChiTietHoaDon(String strMaHD) {
        try {
            String sqlSelect = "SELECT MaHD FROM tblChiTietHDX WHERE MaHD ='" + strMaHD + "'";
            PreparedStatement statement = _con.prepareStatement(sqlSelect);
            _rs = statement.executeQuery();
            if (!_rs.next())
                return false;
            else
                return true;
        } catch (SQLException _ex) {
            _ex.printStackTrace();
            return false;
        }
    }

}
