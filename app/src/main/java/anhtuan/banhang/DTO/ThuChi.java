package anhtuan.banhang.DTO;

import java.util.Date;

public class ThuChi {
    Integer Id;
    String MaHD;
    Date Ngay;
    Integer SoTien;
    String GhiChu;
    Boolean Thu1Chi0;
    Double TienTrongNha;

    public void setId(Integer id) {
        Id = id;
    }

    public void setMaHD(String maHD) {
        MaHD = maHD;
    }

    public void setNgay(Date ngay) {
        Ngay = ngay;
    }

    public void setSoTien(Integer soTien) {
        SoTien = soTien;
    }

    public void setGhiChu(String ghiChu) {
        GhiChu = ghiChu;
    }

    public void setThu1Chi0(Boolean thu1Chi0) {
        Thu1Chi0 = thu1Chi0;
    }

    public Integer getId() {
        return Id;
    }

    public String getMaHD() {
        return MaHD;
    }

    public Date getNgay() {
        return Ngay;
    }

    public Integer getSoTien() {
        return SoTien;
    }

    public String getGhiChu() {
        return GhiChu;
    }

    public Boolean getThu1Chi0() {
        return Thu1Chi0;
    }

    public Double getTienTrongNha() {
        return TienTrongNha;
    }

    public void setTienTrongNha(Double tienTrongNha) {
        TienTrongNha = tienTrongNha;
    }

    @Override
    public String toString() {
        return SoTien + " - " +  (Thu1Chi0 == true ? "Thu": "Chi");
    }
}

