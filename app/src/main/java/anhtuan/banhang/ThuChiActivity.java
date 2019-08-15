package anhtuan.banhang;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import anhtuan.banhang.DAO.ListViewThuChiAdapter;
import anhtuan.banhang.DAO.ThuChiDAO;
import anhtuan.banhang.DTO.DanhSachThuChi;
import anhtuan.banhang.DTO.ThuChi;

public class ThuChiActivity extends AppCompatActivity {
    EditText _soTien;
    EditText _ghiChu;
    EditText _tuNgay;
    EditText _denNgay;
    TextView _lblTongThuChi;

    CheckBox _cbThuChi;
    ImageView _btnAddThuChi;
    ImageView _btnSearch;

    ListView _listThuChi;

    // Cặp Đối Tượng Cho Listview
    ArrayList<ThuChi> arayListThuChi = new ArrayList<ThuChi>();
    ListViewThuChiAdapter adapterThuChi = null;

    ThuChiDAO thuChiDAO = new ThuChiDAO();
    ThuChi _thuChi;
    Date dateTuNgay, dateDenNgay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thu_chi);
        getControl();
        addEventForm();
    }

    private void getControl() {
        _soTien = (EditText) findViewById(R.id.txtSoTien);
        _ghiChu = (EditText) findViewById(R.id.txtGhiChu);
        _lblTongThuChi = (TextView) findViewById(R.id.lblTongThuChi);
        _cbThuChi = (CheckBox) findViewById(R.id.cbxThuChi);
        _btnAddThuChi = (ImageView) findViewById(R.id.btnAddThuChi);

        _tuNgay = (EditText) findViewById(R.id.txtDate1);
        _denNgay = (EditText) findViewById(R.id.txtDate2);
        _btnSearch = (ImageView) findViewById(R.id.btnSearch);

        _listThuChi = (ListView) findViewById(R.id._listviewThuChi);
        // Cấu Hình Cho ListView
        arayListThuChi = thuChiDAO.getArayThuChi();
        HienThiTongThuChiTheoList(arayListThuChi);
        adapterThuChi = new ListViewThuChiAdapter( //Khởi tạo đối tượng adapter và gán Data source
                this, R.layout.item_thu_chi,
                arayListThuChi/*thiết lập data source*/);
        _listThuChi.setAdapter(adapterThuChi);//gán Adapter vào Lisview
    }

    public void showDialog() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông Báo");
        builder.setMessage("Bạn Có Muốn Xóa Thông Tin? " + (_thuChi.getMaHD().equals("") ? "Id:" + _thuChi.getId() : "Mã HĐ: " + _thuChi.getMaHD()));
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                thuChiDAO.XoaThuChiTrongDatabase(_thuChi);
                //xóa danh sách cũ
                arayListThuChi.clear();
                //Mỗi lần xóa xong thì cập nhập lại ListView
                arayListThuChi = thuChiDAO.getArayThuChi();
                HienThiTongThuChiTheoList(arayListThuChi);
                //cập nhật lại ListView
                adapterThuChi.notifyDataSetChanged();

                Toast.makeText(ThuChiActivity.this, "Đã Xóa " + (_thuChi.getMaHD().equals("") ? "Id:" + _thuChi.getId() : _thuChi.getMaHD()), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void HienThiTongThuChiTheoList(ArrayList<ThuChi> arayListThuChi) {
        Integer intTongThu = 0;
        Integer intTongChi = 0;
        for (ThuChi mh : arayListThuChi) {
            if (mh.getThu1Chi0())
                intTongThu += mh.getSoTien() * 1000;
            else
                intTongChi += mh.getSoTien() * 1000;
        }
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        _lblTongThuChi.setText("Tổng : Thu " +  formatter.format(intTongThu) + "  |   Chi " + formatter.format(intTongChi));
    }


    private void addEventForm() {
        // Sự Kiện Nhấn Giữ Xóa Item Trên ListView
        _listThuChi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    _thuChi = arayListThuChi.get(position);
                    showDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        // Sự Kiện Click Intem Hiểm Thị Thông Tin
        _listThuChi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _thuChi = arayListThuChi.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(ThuChiActivity.this);
                builder.setTitle("Thông Tin");
                String strHDID = "";
                if (_thuChi.getMaHD().equals(""))
                    strHDID = " - Id: " + _thuChi.getId();
                else
                    strHDID = " - Mã HĐ: " + _thuChi.getMaHD();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                String message = (_thuChi.getThu1Chi0() == true ? "THU" : "CHI") + strHDID + "\nNgày: " + sdf.format(_thuChi.getNgay()) + "\n" +
                        "Số Tiền : " + _thuChi.getSoTien() + ".000\nGhi Chú: " +
                        _thuChi.getGhiChu();
                builder.setMessage(message);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.create().show();
            }
        });

        //
        // Button Tim Kiếm Theo Ngay Click
        //
        _btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                try {
                    dateTuNgay = sdf.parse(_tuNgay.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(ThuChiActivity.this, "Ngày Chưa Đúng.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    dateDenNgay = sdf.parse(_denNgay.getText().toString());
                } catch (ParseException e) {
                    dateDenNgay = new Date();
                }

                //xóa danh sách cũ
                arayListThuChi.clear();
                arayListThuChi = thuChiDAO.getArayThuChi(dateTuNgay, dateDenNgay);
                HienThiTongThuChiTheoList(arayListThuChi);
                //cập nhật lại ListView
                adapterThuChi.notifyDataSetChanged();
            }
        });

        //
        // Button Thêm Thu Chi
        //
        _btnAddThuChi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _thuChi = new ThuChi();
                if (_cbThuChi.isChecked()) {
                    _thuChi.setThu1Chi0(true);
                } else _thuChi.setThu1Chi0(false);

                String textSL = _soTien.getText().toString();
                if (textSL.length() == 0 || Integer.parseInt(textSL) == 0) {
                    Toast.makeText(ThuChiActivity.this, "Số Tiền Phải > 0 ", Toast.LENGTH_SHORT).show();
                    return;
                }
                _thuChi.setMaHD("");
                _thuChi.setSoTien(Integer.parseInt(textSL));
                _thuChi.setGhiChu(_ghiChu.getText().toString().trim());
                thuChiDAO.ThemThuChiVaoDatabase(_thuChi);
                Toast.makeText(ThuChiActivity.this, "Đã Thêm.", Toast.LENGTH_SHORT).show();

                //xóa danh sách cũ
                arayListThuChi.clear();
                arayListThuChi = thuChiDAO.getArayThuChi();
                HienThiTongThuChiTheoList(arayListThuChi);
                //cập nhật lại ListView
                adapterThuChi.notifyDataSetChanged();
                _soTien.setText("");
                _ghiChu.setText("");
            }
        });

        _tuNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blDate = true;
                // TODO Auto-generated method stub
                new DatePickerDialog(ThuChiActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        _denNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blDate = false;
                // TODO Auto-generated method stub
                new DatePickerDialog(ThuChiActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    Boolean blDate = true;
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextDate(blDate);
        }
    };

    private void updateTextDate(Boolean _blDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        if (_blDate) {
            if (myCalendar.getTime().after(date)) {
                Toast.makeText(ThuChiActivity.this, "Không Lớn Hơn Ngày Hiện Tại", Toast.LENGTH_LONG).show();
                return;
            }
            dateTuNgay = myCalendar.getTime();
            _tuNgay.setText(sdf.format(dateTuNgay)); // Đưa ngay đã chọn lên text
            try {
                dateDenNgay = sdf.parse(_denNgay.getText().toString());
            } catch (ParseException e) {
                return;
            }
            if (dateDenNgay.before(dateTuNgay))
                _denNgay.setText(sdf.format(dateTuNgay));
        } else {
            Date dateTuNgay = new Date();
            try {
                dateTuNgay = sdf.parse(_tuNgay.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (myCalendar.getTime().before(dateTuNgay)) {
                Toast.makeText(ThuChiActivity.this, "Phải Lớn Hơn Ngày Bắt Đầu Xem", Toast.LENGTH_LONG).show();
                return;
            }
            if (myCalendar.getTime().after(date)) {
                myCalendar.setTime(date);
            }
            _denNgay.setText(sdf.format(myCalendar.getTime()));
        }
    }

}
