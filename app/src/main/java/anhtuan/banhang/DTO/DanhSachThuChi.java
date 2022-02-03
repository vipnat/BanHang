package anhtuan.banhang.DTO;

import java.util.ArrayList;

/**
 * Class dùng để lưu trữ thông tin danh mục
 * và danh sách các sản phẩm
 */

public class DanhSachThuChi {
    private ArrayList<ThuChi> _arayList = null;

    public DanhSachThuChi(String strMa, ThuChi matHang) {
        super();
        this._arayList = new ArrayList<ThuChi>();
    }

    public DanhSachThuChi() {
        super();
        this._arayList = new ArrayList<ThuChi>();
    }


    /**
     * thêm 1 sản phẩm vào danh mục
     * thêm thành công =true
     *
     * @param mh
     * @return
     */
    public boolean addMatHangList(ThuChi mh) {
        return _arayList.add(mh);
    }

    public int getTongTienList() {
        int fTongTien = 0;
        for (ThuChi mh : _arayList) {
            fTongTien += mh.getSoTien();
        }
        return fTongTien;
    }

    /**
     * Xóa Mặt Hàng Trong ArayList
     *
     * @param mh
     * @return
     */
    public boolean deleteThuChiOnList(ThuChi mh) {
        return _arayList.remove(mh);
    }

    public ArrayList<ThuChi> getListMatHang() {
        return this._arayList;
    }

    public int size() {
        return _arayList.size();
    }

    public ThuChi get(int i) {
        return _arayList.get(i);
    }

}
