package anhtuan.banhang.DTO;

import java.util.Date;

public class HoaDonXuat {
    String MaHD;
    String MaNhanVien;
    Date NgayXuat;
    Double TongTien;
    String GhiChu;
    Double TongTienGoc;

    public HoaDonXuat(String maHD, String maNhanVien, Date ngayXuat, Double tongTien, String ghiChu, Double tongTienGoc) {
        this.MaHD = maHD;
        this.MaNhanVien = maNhanVien;
        this.NgayXuat = ngayXuat;
        this.TongTien = tongTien;
        this.GhiChu = ghiChu;
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

    public String getGhiChu() {
        return GhiChu;
    }

    public void setGhiChu(String ghiChu) {
        GhiChu = ghiChu;
    }

    public Double getTongTienGoc() {
        return TongTienGoc;
    }

    public void setTongTienGoc(Double tongTienGoc) {
        TongTienGoc = tongTienGoc;
    }



}
