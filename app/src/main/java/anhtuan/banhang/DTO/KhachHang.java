package anhtuan.banhang.DTO;

public class KhachHang {

    String MaKH;
    String TenKH;
    String DiaChi;
    String DienThoai;
    String NoCu;
    public String getNoCu() {
        return NoCu;
    }

    public void setNoCu(String noCu) {
        NoCu = noCu;
    }

    public KhachHang(String maKH,String TenKH,String DiaChi,String DienThoai,String noCu) {
        this.MaKH = maKH;
        this.TenKH = TenKH;
        this.DiaChi = DiaChi;
        this.DienThoai = DienThoai;
        this.NoCu = noCu;
    }

    public KhachHang() {
        super();
    }

    public String getTenKH() {
        return TenKH;
    }

    public void setTenKH(String tenKH) {
        TenKH = tenKH;
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



    public String getMaKH() {
        return MaKH;
    }

    public void setMaKH(String maKH) {
        MaKH = maKH;
    }


    @Override
    public String toString() {
        return TenKH;
    }
}
