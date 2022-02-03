package anhtuan.banhang.DTO;


import java.text.DecimalFormat;
import java.util.Comparator;

public class MatHang {
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

    public void setSoLuong(float soLuong) {
        this.SoLuong = soLuong;
    }

    public MatHang(String MaMh, String TenMH, Float SL, Float Dgia) {
        super();
        this.MaMatH = MaMh;
        this.TenMatH = TenMH;
        this.SoLuong = SL;
        this.DonGia = Dgia;
    }
    public MatHang() {
        super();
    }

    public String toString() {
        return this.MaMatH+"-"+ (new DecimalFormat("##")).format(this.SoLuong) + "_" +this.TenMatH;
    }


}
