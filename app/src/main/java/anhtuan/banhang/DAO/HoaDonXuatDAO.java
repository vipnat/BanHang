package anhtuan.banhang.DAO;

import android.os.Environment;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import anhtuan.banhang.DTO.HoaDonXuat;
import anhtuan.banhang.DTO.KhachHang;
import anhtuan.banhang.DTO.MatHang;
import anhtuan.banhang.Database.ConnectionDB;

public class HoaDonXuatDAO {
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

    ArrayList<HoaDonXuat> arrHoaDonXuat = new ArrayList<HoaDonXuat>();
    HoaDonXuat _hoaDonXuat;

    public ArrayList<HoaDonXuat> arrHoaDonXuat() {
        try {
            OpenCONN();
            String sqlSelect = "SELECT TOP 100 * FROM  tblHoaDonXuat ORDER BY NgayXuat DESC, MaHD DESC";
            PreparedStatement pre = _con.prepareStatement(sqlSelect);
            _rs = pre.executeQuery();
            while (_rs.next()) {
                _hoaDonXuat = new HoaDonXuat();
                _hoaDonXuat.setMaHD(_rs.getString("MaHD"));
                _hoaDonXuat.setMaNhanVien(_rs.getString("MaNhanVien"));
                _hoaDonXuat.setNgayXuat(_rs.getDate("NgayXuat"));
                _hoaDonXuat.setTongTien(_rs.getDouble("TongTien"));
                _hoaDonXuat.setMaKH(_rs.getString("MaKH"));
                _hoaDonXuat.setTongTienGoc(_rs.getDouble("TongTienGoc"));
                arrHoaDonXuat.add(_hoaDonXuat);
                CloseCONN();
            }
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return arrHoaDonXuat;
    }

    public String LayMaHoaDonTheoNgay(String DayMonthYear) {
        String MaHD = "";
        int maSo = 1;
        String selectMaHoaDon = "SELECT MaHD FROM tblHoaDonXuat";
        OpenCONN();
        try {
            statement = _con.prepareStatement(selectMaHoaDon);
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
            CloseCONN();
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
        return MaHD;
    }

    public void XoaAllHoaDonXuatNull() {
        String sqlQuery = "SELECT MaHD FROM tblHoaDonXuat WHERE NOT EXISTS (SELECT MaHD FROM tblChiTietHDX WHERE " +
                "tblHoaDonXuat.MaHD = tblChiTietHDX.MaHD)";  // Có Trong Hóa Đơn Mà Không Có Chi Tiết. (HĐ Ảo)
        OpenCONN();
        try {
            statement = _con.prepareStatement(sqlQuery);
            _rs = statement.executeQuery();
            while (_rs.next()) {
                String strMaHD = _rs.getString("MaHD");
                String deleteHDX = "DELETE FROM tblHoaDonXuat WHERE MaHD ='" + strMaHD + "'";
                statement = _con.prepareStatement(deleteHDX);
                statement.executeUpdate();
                XoaFilePDFTheoMaHD(strMaHD);
            }
            CloseCONN();
        } catch (SQLException _ex) {
            _ex.printStackTrace();
        }
    }

    public double LayTongTienGocCuaHD(ArrayList<MatHang> listMH) {
        double tongTienGoc = 0;
        String sqlSelect = "";
        OpenCONN();
        try {
            for (MatHang mh : listMH) {
                sqlSelect = "SELECT (DonGia*" + mh.getSoLuong() + ")AS TongGoc FROM tblMatHang WHERE MaMatH = N'" + mh.getMaMatH() + "'";
                statement = _con.prepareStatement(sqlSelect);
                _rs = statement.executeQuery();
                while (_rs.next()) {
                    tongTienGoc = tongTienGoc + _rs.getDouble("TongGoc");
                }
            }
            CloseCONN();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongTienGoc;
    }

    public void ThemHoaDonXuat(HoaDonXuat hdx) {
        SimpleDateFormat frmDate = new SimpleDateFormat("MM/dd/yyyy");
        String ngayBan = frmDate.format(new Date()); // Ngày Hiện Tại
        String insertHDX = "insert into tblHoaDonXuat(MaHD,MaNhanVien,NgayXuat,MaKH) values(N'" + hdx.getMaHD() + "',N'" + hdx.getMaNhanVien() + "',N'" + ngayBan + "',N'" + hdx.getMaKH() + "')";
        OpenCONN();
        try {
            statement = _con.prepareStatement(insertHDX);
            statement.executeUpdate();
            CloseCONN();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateTTHoaDonXuat(HoaDonXuat hdx) {
        String updateSQL = "UPDATE tblHoaDonXuat SET TongTienGoc ='" + hdx.getTongTienGoc() + "' , TongTien = '" + hdx.getTongTien() + "' , MaKH = '" + hdx.getMaKH() + "' WHERE MaHD='" + hdx.getMaHD() + "'";
        OpenCONN();
        try {
            statement = _con.prepareStatement(updateSQL);
            statement.executeUpdate();
            CloseCONN();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean InsertDuLieuMuaDB(ArrayList<MatHang> arayListView, HoaDonXuat hdx, KhachHang kh) {
        OpenCONN();
        try {
            String query_SQL = "";
            for (MatHang mh : arayListView) {
                //Thêm vào bảng tblChiTietHDX
                query_SQL = "insert into tblChiTietHDX(MaMatH,MaHD,SoLuong,DonGia) values(N'" + mh.getMaMatH() + "',N'" + hdx.getMaHD() + "'," + mh.getSoLuong() + "," + mh.getDonGia() + ")";
                statement = _con.prepareStatement(query_SQL);
                statement.executeUpdate();

                //Cập nhật lại Số Lượng cho bảng tblMatHang (bớt số lượng mặt hàng)
                query_SQL = "update tblMatHang set SoLuong=SoLuong-" + mh.getSoLuong() + " where MaMatH=N'" + mh.getMaMatH() + "'";
                statement = _con.prepareStatement(query_SQL);
                statement.executeUpdate();
            }
            CloseCONN();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void UpdateGiaBanTheoKhachHang(KhachHang kh, MatHang mh) {
        String query_SQL = "";
        // Kiểm Tra Tồn Tại Giá Bán Của Khách Hàng
        try {
            _con = connectionDB.CONN();
            if (!KiemTraTonTaiGiaBan(kh.getMaKH(), mh.getMaMatH())) {
                query_SQL = "INSERT INTO tblGiaBan([MaMatH],[MaKH],[GiaBan]) VALUES (N'" + mh.getMaMatH() + "',N'" + kh.getMaKH() + "'," + mh.getDonGia() + ")";
                statement = _con.prepareStatement(query_SQL);
                statement.executeUpdate();
            } else {
                //Cập nhập giá mới cho khách hàng
                query_SQL = "update [tblGiaBan] set [GiaBan]=" + mh.getDonGia() + " where [MaKH]=N'" + kh.getMaKH() + "' and [MaMatH] =N'" + mh.getMaMatH() + "'";
                statement = _con.prepareStatement(query_SQL);
                statement.executeUpdate();
            }
            _con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateSoLuongMatHang(Integer soluong, String maMH) {
        try {
            _con = connectionDB.CONN();
            String sqlUpdate = "UPDATE tblMatHang SET SoLuong = " + soluong + " WHERE MaMatH = '" + maMH + "'";
            PreparedStatement statement = _con.prepareStatement(sqlUpdate);
            statement.executeUpdate();
            _con.close();
        } catch (Exception ex) {
            _ex = "Exceptions";
        }
    }

    public void DeleteDuLieuMuaDB(String strMaHoaDon) {
        try {
            _con = connectionDB.CONN();
            if (!KiemTraTonTaiTrongChiTietHoaDon(strMaHoaDon))
                return;
            String query_SQL = "SELECT * FROM tblChiTietHDX WHERE MaHD ='" + strMaHoaDon + "'";
            statement = _con.prepareStatement(query_SQL);
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
            _con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean KiemTraTonTaiTrongChiTietHoaDon(String strMaHD) {
        OpenCONN();
        try {
            String sqlSelect = "SELECT MaHD FROM tblChiTietHDX WHERE MaHD ='" + strMaHD + "'";
            statement = _con.prepareStatement(sqlSelect);
            _rs = statement.executeQuery();
            if (!_rs.next()) {
                CloseCONN();
                return false;
            }else {
                CloseCONN();
                return true;

            }
        } catch (SQLException _ex) {
            _ex.printStackTrace();
            return false;
        }
    }

    public boolean KiemTraTonTaiGiaBan(String strMaKH, String strMaMH) {
        OpenCONN();
        try {
            String sqlSelect = "SELECT MaMatH FROM tblGiaBan WHERE MaMatH ='" + strMaMH + "' AND MaKH = '" + strMaKH + "'";
            statement = _con.prepareStatement(sqlSelect);
            _rs = statement.executeQuery();
            if (!_rs.isBeforeFirst()) {
                CloseCONN();
                return false;
            } else{
                CloseCONN();
                return true;
            }
        } catch (SQLException _ex) {
            _ex.printStackTrace();
            return false;
        }
    }

    public String LayMaHoaDonMoiNhat() {
        String query_SQL = "SELECT TOP 1 MaHD FROM tblHoaDonXuat ORDER BY NgayXuat DESC,MaHD DESC";
        String strMaHD = "";
        OpenCONN();
        try {
            statement = _con.prepareStatement(query_SQL);
            _rs = statement.executeQuery();
            while (_rs.next()) {
                strMaHD = _rs.getString("MaHD");
            }
            CloseCONN();
        } catch (SQLException _ex) {
            _ex.printStackTrace();
        }
        return strMaHD;
    }

    public int LayTongSoHoaDonTheoKH(String maKH) {
        String query_SQL = "SELECT COUNT(MaKH) FROM tblHoaDonXuat WHERE MaKH='" + maKH + "' AND NgayXuat > '2019-02-05'";
        int intTong = 0;
        try {
            _con = connectionDB.CONN();
            //XoaAllHoaDonXuatNull();
            statement = _con.prepareStatement(query_SQL);
            _rs = statement.executeQuery();
            while (_rs.next()) {
                intTong = _rs.getInt(1);
            }
            _con.close();
        } catch (SQLException _ex) {
            _ex.printStackTrace();
        }
        return intTong + 1;
    }

    public void UploadFilePDFToDatabase(String filePath) {
        try {
            _con = connectionDB.CONN();
            File pdfFile = new File(filePath);
            byte[] pdfData = new byte[(int) pdfFile.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(pdfFile));
            dis.readFully(pdfData);  // read from file into byte[] array
            dis.close();
            String strMaHD = layTenFileKhongMoRong(pdfFile.getName());
            PreparedStatement ps = _con.prepareStatement("INSERT INTO SavePDFTable (MaHD,PDFFile) VALUES (?,?)");
            ps.setString(1, strMaHD);
            ps.setBytes(2, pdfData);  // byte[] array
            ps.executeUpdate();
            _con.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String layTenFileKhongMoRong(String fileName) {
        if (fileName != null && fileName.length() > 0) {
            while (fileName.contains(".")) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            }
        }
        return fileName;
    }

    public void XoaFilePDFTheoMaHD(String strMaHD){
        String deletePDF = "DELETE FROM SavePDFTable WHERE MaHD ='" + strMaHD + "'";
        OpenCONN();
        try {
            statement = _con.prepareStatement(deletePDF);
            statement.executeQuery();
            CloseCONN();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void XemLaiHoaDonTheoMaHD(String filePathHoaDon) {
        OpenCONN();
        try {
            File pdfFile = new File(filePathHoaDon);
            String strMaHD = layTenFileKhongMoRong(pdfFile.getName());
            if (pdfFile.exists()) { // Kiểm tra tôn tại
                pdfFile.delete(); // Xóa
            }
            String sqlGetFile = "Select PDFFile from SavePDFTable where [MaHD]='" + strMaHD + "' ";
            statement = _con.prepareStatement(sqlGetFile);
            _rs = statement.executeQuery();
            while (_rs.next()) {
                FileOutputStream fos = new FileOutputStream(pdfFile);
                fos.write(_rs.getBytes("PDFFile"));
                fos.close();
            }
            CloseCONN();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
