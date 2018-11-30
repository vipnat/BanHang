package anhtuan.banhang.DTO;


public class MatHangDTO {
    String MaMatH;
    String TenMatH;
    Float SoLuong;
    Float DonGia;

    public String getTenMatH() {
        return TenMatH;
    }

    public void setTenMatH(String tenMatH) {
        TenMatH = tenMatH;
    }

    public Float getDonGia() {
        return DonGia;
    }

    public void setDonGia(Float donGia) {
        DonGia = donGia;
    }

    public String getMaMatH() {
        return MaMatH;
    }

    public void setMaMatH(String maMatH) {
        MaMatH = maMatH;
    }

    public Float getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(Float soLuong) {
        SoLuong = soLuong;
    }

    public MatHangDTO(String MaMh, String TenMH,Float SL, Float Dgia) {
        super();
        this.MaMatH = MaMh;
        this.TenMatH = TenMH;
        this.SoLuong = SL;
        this.DonGia = Dgia;
    }
    public MatHangDTO() {
        super();
    }

    public String toString() {
        return this.MaMatH+" - "+this.TenMatH;
    }
}
