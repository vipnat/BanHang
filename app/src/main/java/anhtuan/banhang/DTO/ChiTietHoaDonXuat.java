package anhtuan.banhang.DTO;

public class ChiTietHoaDonXuat {
    String MaMatH;
    String MaHD;
    String SoLuong;
    String DonGia;

    public ChiTietHoaDonXuat(String maMatH, String maHD, String soLuong, String donGia) {
        MaMatH = maMatH;
        MaHD = maHD;
        SoLuong = soLuong;
        DonGia = donGia;
    }

    public ChiTietHoaDonXuat() {
        super();
    }

    public String getMaMatH() {
        return MaMatH;
    }

    public void setMaMatH(String maMatH) {
        MaMatH = maMatH;
    }

    public String getMaHD() {
        return MaHD;
    }

    public void setMaHD(String maHD) {
        MaHD = maHD;
    }

    public String getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(String soLuong) {
        SoLuong = soLuong;
    }

    public String getDonGia() {
        return DonGia;
    }

    public void setDonGia(String donGia) {
        DonGia = donGia;
    }
}
