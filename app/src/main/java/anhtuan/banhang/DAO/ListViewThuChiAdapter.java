package anhtuan.banhang.DAO;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import anhtuan.banhang.DTO.MatHang;
import anhtuan.banhang.DTO.ThuChi;
import anhtuan.banhang.R;

public class ListViewThuChiAdapter extends ArrayAdapter {
    Activity activity;//activity chứa listview
    ArrayList<ThuChi> myArrayThuChi = null;
    int layoutId;

    public ListViewThuChiAdapter(Activity activity, int layoutId, ArrayList<ThuChi> _arayList) {
        super(activity, layoutId, _arayList);
        this.activity = activity;
        this.layoutId = layoutId;
        this.myArrayThuChi = _arayList;
    }

    //hàm hiện thị từng item lên listview
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        //position là vị tri của mỗi item. nó sẽ chạy hết mảng
        //lấy layout cho từng item
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
        }
        //lấy các textview trong mỗi view
        TextView txtThuChi = (TextView) convertView.findViewById(R.id.lblThuChi);
        TextView txtNgay = (TextView) convertView.findViewById(R.id.lblNgay);
        TextView txtSoTien = (TextView) convertView.findViewById(R.id.lblSoTien);
        TextView txtGhiChu = (TextView) convertView.findViewById(R.id.lblGhiChu);

        //hiển thị dư liệu lên từng item của listview ở vị trí position
        ThuChi thuChi = myArrayThuChi.get(position);
        txtThuChi.setText(thuChi.getThu1Chi0() == true ? "Thu" : "Chi");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        txtNgay.setText(sdf.format(thuChi.getNgay()));
        txtSoTien.setText(thuChi.getSoTien()+"");
        txtGhiChu.setText(thuChi.getGhiChu());
        return convertView;//trả về 1 view khi đã thiết đặt xong
    }
}
