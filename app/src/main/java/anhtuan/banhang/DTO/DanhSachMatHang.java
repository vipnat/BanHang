package anhtuan.banhang.DTO;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Class dùng để lưu trữ thông tin danh mục
 * và danh sách các sản phẩm
 */

public class DanhSachMatHang {
    private ArrayList<MatHang> _arayList = null;

    public DanhSachMatHang(String strMa, MatHang matHang) {
        super();
        this._arayList = new ArrayList<MatHang>();
    }

    public DanhSachMatHang() {
        super();
        this._arayList = new ArrayList<MatHang>();
    }

    /**
     * kiểm tra sản phẩm đã tồn tại trong danh mục hay chưa
     *
     * @param p
     * @return true nếu tồn tại
     */
    public boolean isDuplicate(MatHang p) {
        for (MatHang p1 : _arayList) {
            if (p1.getMaMatH().trim().equalsIgnoreCase(p.getMaMatH().trim())) {
                if (p1.getSoLuong() != p.getSoLuong())
                    p1.setSoLuong(p.getSoLuong());
                if (p1.getDonGia() != p.getDonGia())
                    p1.setDonGia(p.getDonGia());
                return true;
            }
        }
        return false;
    }

    /**
     * thêm 1 sản phẩm vào danh mục
     * thêm thành công =true
     *
     * @param mh
     * @return
     */
    public boolean addMatHangList(MatHang mh) {
        boolean isDup = isDuplicate(mh);
        if (!isDup) {
            return _arayList.add(mh);
        }
        return !isDup;
    }

    /**
     * Lấy Tổng Số Lượng Mặt Hàng Trong Aray List
     */
    public int getSoLuongMatHang() {
        double intTong = 0;
        for (MatHang mh : _arayList) {
            intTong = intTong + mh.getSoLuong();
        }
        return (int) intTong;
    }

    public int getTongTienList() {
        double fTongTien = 0;
        for (MatHang mh : _arayList) {
            double tong = mh.getSoLuong() * mh.getDonGia();
            fTongTien = fTongTien + tong;
        }
        return (int) Math.round(fTongTien);
    }

    /**
     * Xóa Mặt Hàng Trong ArayList
     *
     * @param mh
     * @return
     */
    public boolean deleteMatHangList(MatHang mh) {
        if (isDuplicate(mh))
            return _arayList.remove(mh);
        return true;
    }

    public ArrayList<MatHang> getListMatHang() {
        return this._arayList;
    }

    public int size() {
        return _arayList.size();
    }

    public MatHang get(int i) {
        return _arayList.get(i);
    }

}
