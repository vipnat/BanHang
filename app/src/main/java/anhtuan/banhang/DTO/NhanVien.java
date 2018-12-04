package anhtuan.banhang.DTO;

public class NhanVien {
    String MaNhanVien;
    String TenNhanVien;
    String DiaChi;
    String DienThoai;

    public NhanVien(String maNhanVien, String tenNhanVien, String diaChi, String dienThoai) {
        MaNhanVien = maNhanVien;
        TenNhanVien = tenNhanVien;
        DiaChi = diaChi;
        DienThoai = dienThoai;
    }

    public NhanVien() {
        super();
    }

    public String getMaNhanVien() {
        return MaNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        MaNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return TenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        TenNhanVien = tenNhanVien;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }

    public String getDienThoai() {
        return DienThoai;
    }

    public void setDienThoai(String dienThoai) {
        DienThoai = dienThoai;
    }

    @Override
    public String toString() {
        return TenNhanVien + " (" + DienThoai + ")";
    }
}
