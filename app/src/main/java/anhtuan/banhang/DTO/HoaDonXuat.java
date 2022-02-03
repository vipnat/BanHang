package anhtuan.banhang.DTO;

import java.util.Date;

import anhtuan.banhang.DAO.KhachHangDAO;

public class HoaDonXuat {
    String MaHD;
    String MaNhanVien;
    Date NgayXuat;
    Double TongTien;
    String MaKH;
    Double TongTienGoc;
    KhachHangDAO _khachHangDAO = new KhachHangDAO();

    public HoaDonXuat(String maHD, String maNhanVien, Date ngayXuat, Double tongTien, String maKH, Double tongTienGoc) {
        this.MaHD = maHD;
        this.MaNhanVien = maNhanVien;
        this.NgayXuat = ngayXuat;
        this.TongTien = tongTien;
        this.MaKH = maKH;
        this.TongTienGoc = tongTienGoc;
    }

    public HoaDonXuat() {
        super();
    }

    public String getMaHD() {
        return MaHD;
    }

    public void setMaHD(String maHD) {
        MaHD = maHD;
    }

    public String getMaNhanVien() {
        return MaNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        MaNhanVien = maNhanVien;
    }

    public Date getNgayXuat() {
        return NgayXuat;
    }

    public void setNgayXuat(Date ngayXuat) {
        NgayXuat = ngayXuat;
    }

    public Double getTongTien() {
        return TongTien;
    }

    public void setTongTien(Double tongTien) {
        TongTien = tongTien;
    }

    public String getMaKH() {
        return MaKH;
    }

    public void setMaKH(String maKH) {
        MaKH = maKH;
    }

    public Double getTongTienGoc() {
        return TongTienGoc;
    }

    public void setTongTienGoc(Double tongTienGoc) {
        TongTienGoc = tongTienGoc;
    }

    public String toString() {
        /*String TenKH = "";
        try {
            TenKH = _khachHangDAO.get_khachHang_by_id(this.MaKH).getTenKH();
        }
        catch (Exception ex)
        {
            TenKH = "...";
        }
        */
        return this.MaHD;
    }

}
