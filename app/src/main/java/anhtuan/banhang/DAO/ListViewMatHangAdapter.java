package anhtuan.banhang.DAO;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import anhtuan.banhang.DTO.MatHang;
import anhtuan.banhang.R;

public class ListViewMatHangAdapter extends ArrayAdapter {
    Activity activity;//activity chứa listview
    ArrayList<MatHang> myArray = null;
    int layoutId;

    public ListViewMatHangAdapter(Activity activity, int layoutId, ArrayList<MatHang> _arayList) {
        super(activity, 0, _arayList);
        this.activity = activity;
        this.layoutId = layoutId;
        this.myArray = _arayList;
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
        TextView tvTen = (TextView) convertView.findViewById(R.id._lblMaMH);
        TextView tvSL = (TextView) convertView.findViewById(R.id._lblSL);
        TextView tvGia = (TextView) convertView.findViewById(R.id._lblGia);
        TextView tvTTien = (TextView) convertView.findViewById(R.id._lblTong);

        //hiển thị dư liệu lên từng item của listview ở vị trí position
        MatHang matHang = myArray.get(position);
        tvTen.setText(matHang.getMaMatH());
        String strSL = (new DecimalFormat("##")).format(matHang.getSoLuong());
        tvSL.setText(strSL);
        double dbDonGia = matHang.getDonGia() * 1;
        tvGia.setText(dbDonGia - (int) dbDonGia > 0 ? dbDonGia + "" : (int) dbDonGia + "");
        double tongTien = matHang.getSoLuong() * matHang.getDonGia();
        tvTTien.setText(tongTien != (int) tongTien ? tongTien + "" : (int) tongTien + "");

        return convertView;//trả về 1 view khi đã thiết đặt xong
    }
}
