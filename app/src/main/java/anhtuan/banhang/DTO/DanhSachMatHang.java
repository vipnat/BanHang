package anhtuan.banhang.DTO;

import java.util.ArrayList;

/**
 * Class dùng để lưu trữ thông tin danh mục
 * và danh sách các sản phẩm
 *
 */

public class DanhSachMatHang {
    private ArrayList<MatHangDTO> _arayList = null;

    public DanhSachMatHang(String strMa,MatHangDTO matHang){
        super();
        this._arayList = new ArrayList<MatHangDTO>();
    }

    public DanhSachMatHang(){
        super();
        this._arayList = new ArrayList<MatHangDTO>();
    }

    /**
     * kiểm tra sản phẩm đã tồn tại trong danh mục hay chưa
     * @param p
     * @return true nếu tồn tại
     */
    public boolean isDuplicate(MatHangDTO p)
    {
        for(MatHangDTO p1: _arayList)
        {
            if(p1.getMaMatH().trim().equalsIgnoreCase(p.getMaMatH().trim())){

                return true;
            }
        }
        return false;
    }

    /**
     * thêm 1 sản phẩm vào danh mục
     * thêm thành công =true
     * @param mh
     * @return
     */
    public boolean addMatHangList(MatHangDTO mh)
    {
        boolean isDup=isDuplicate(mh);
        if(!isDup)
        {
            return _arayList.add(mh);
        }
        return !isDup;
    }

    public ArrayList<MatHangDTO>getListMatHang()
    {
        return this._arayList;
    }
    public int size()
    {
        return _arayList.size();
    }
    public MatHangDTO get(int i)
    {
        return _arayList.get(i);
    }

}
