package anhtuan.banhang;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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

import anhtuan.banhang.DAO.HoaDonXuatDAO;
import anhtuan.banhang.DAO.ListViewThuChiAdapter;
import anhtuan.banhang.DAO.ThuChiDAO;
import anhtuan.banhang.DTO.DanhSachThuChi;
import anhtuan.banhang.DTO.ThuChi;

public class ThuChiActivity extends AppCompatActivity {
    EditText _soTien;
    EditText _ghiChu;
    EditText _tuNgay;
    EditText _denNgay;
    EditText _ngayNhap;
    TextView _lblTongThuChi;
    TextView _lblTienTrongNha;

    CheckBox _cbThuChi;
    ImageView _btnAddThuChi;
    ImageView _btnSearch;

    ListView _listThuChi;

    // Cặp Đối Tượng Cho Listview
    ArrayList<ThuChi> arayListThuChi = new ArrayList<ThuChi>();
    ListViewThuChiAdapter adapterThuChi = null;

    HoaDonXuatDAO hoaDonXuatDAO = new HoaDonXuatDAO();
    ThuChiDAO thuChiDAO = new ThuChiDAO();

    ThuChi _thuChi;
    Date dateTuNgay, dateDenNgay;
    DecimalFormat tienformatter = new DecimalFormat("###,###,###");
    SimpleDateFormat frmDateddMMyy = new SimpleDateFormat("dd/MM/yy");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thu_chi);
        getControl();
        addEventForm();
        thuChiDAO.UpdateTienTheoThuChi();
    }

    private void getControl() {
        _soTien = (EditText) findViewById(R.id.txtSoTien);
        _ghiChu = (EditText) findViewById(R.id.txtGhiChu);
        _lblTongThuChi = (TextView) findViewById(R.id.lblTongThuChi);
        _lblTienTrongNha = (TextView) findViewById(R.id.lblTienTrongNha);
        _cbThuChi = (CheckBox) findViewById(R.id.cbxThuChi);
        _btnAddThuChi = (ImageView) findViewById(R.id.btnAddThuChi);

        _tuNgay = (EditText) findViewById(R.id.txtDate1);
        _denNgay = (EditText) findViewById(R.id.txtDate2);
        _ngayNhap = (EditText) findViewById(R.id.txtNgayNhap);
        _btnSearch = (ImageView) findViewById(R.id.btnSearch);

        _listThuChi = (ListView) findViewById(R.id._listviewThuChi);
        // Cấu Hình Cho ListView
        arayListThuChi = thuChiDAO.getArayThuChi();
        HienThiTongThuChiTheoList(arayListThuChi);
        adapterThuChi = new ListViewThuChiAdapter( //Khởi tạo đối tượng adapter và gán Data source
                this, R.layout.item_thu_chi,
                arayListThuChi/*thiết lập data source*/);
        _listThuChi.setAdapter(adapterThuChi);//gán Adapter vào Lisview

        Date date = new Date();
        _ngayNhap.setText(frmDateddMMyy.format(date));

        _lblTienTrongNha.setText(tienformatter.format(thuChiDAO.LayTienTrongNhaMoiNhat()*1000));
    }

    public void showDialog() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông Báo");
        builder.setMessage("Bạn Có Muốn Xóa Thông Tin? " + "Id:" + _thuChi.getId() + "\nGhi Chú:" + _thuChi.getGhiChu());
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                double tienTrongNhaMoiNhat = thuChiDAO.LayTienTrongNhaMoiNhat();
                double soTienXoaDi = _thuChi.getSoTien();
                thuChiDAO.XoaThuChiTrongDatabase(_thuChi);
                ThuChi thu_Chi = thuChiDAO.LayThuChiMoiNhat();
                if (_thuChi.getThu1Chi0())  // Nếu là Thu
                    thu_Chi.setTienTrongNha(tienTrongNhaMoiNhat - soTienXoaDi);
                else  // Nếu là Chi
                    thu_Chi.setTienTrongNha(tienTrongNhaMoiNhat + soTienXoaDi);

                // Cập nhập lại
                thuChiDAO.UpdateThuChiTrongDatabase(thu_Chi);
                _lblTienTrongNha.setText(tienformatter.format(thu_Chi.getTienTrongNha()*1000));

                //xóa danh sách cũ
                arayListThuChi.clear();
                //Mỗi lần xóa xong thì cập nhập lại ListView
                arayListThuChi = thuChiDAO.getArayThuChi();
                HienThiTongThuChiTheoList(arayListThuChi);
                //cập nhật lại ListView
                adapterThuChi.notifyDataSetChanged();

                Toast.makeText(ThuChiActivity.this, "Đã Xóa " + (_thuChi.getMaHD().equals("") ? "" +
                        "Id:" + _thuChi.getId() : _thuChi.getMaHD()), Toast.LENGTH_SHORT).show();
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

        _lblTongThuChi.setText("(+):Thu " + tienformatter.format(intTongThu) + "  |   Chi " + tienformatter.format(intTongChi));
    }

    private void addEventForm() {
        // Sự Kiện Nhấn Giữ Xóa Item Trên ListView
        _listThuChi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    _thuChi = arayListThuChi.get(position);
                    if (!hoaDonXuatDAO.KiemTraTonTaiTrongChiTietHoaDon(_thuChi.getMaHD()))
                        showDialog();
                    else
                        Toast.makeText(ThuChiActivity.this, "Phải Xóa Hóa Đơn Bán.", Toast.LENGTH_LONG).show();
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

                String message = (_thuChi.getThu1Chi0() == true ? "THU" : "CHI") + strHDID + "\nNgày: " + frmDateddMMyy.format(_thuChi.getNgay()) + "\n" +
                        "Số Tiền : " + tienformatter.format(_thuChi.getSoTien()) + "\nGhi Chú: " +
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
                try {
                    dateTuNgay = frmDateddMMyy.parse(_tuNgay.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(ThuChiActivity.this, "Ngày Chưa Đúng.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    dateDenNgay = frmDateddMMyy.parse(_denNgay.getText().toString());
                } catch (ParseException e) {
                    dateDenNgay = new Date();
                }

                //xóa danh sách cũ
                arayListThuChi.clear();
                arayListThuChi = thuChiDAO.getArayThuChi(dateTuNgay, dateDenNgay);
                _lblTienTrongNha.setText(tienformatter.format(arayListThuChi.get(0).getTienTrongNha()*1000));
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
                String textSL = _soTien.getText().toString();
                if (textSL.length() == 0 || Integer.parseInt(textSL) == 0) {
                    Toast.makeText(ThuChiActivity.this, "Số Tiền Phải > 0 ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (_ghiChu.length() == 0 || _ghiChu.getText().toString().trim().equals("")) {
                    Toast.makeText(ThuChiActivity.this, "Nhập Ghi Chú.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int soTien = Integer.parseInt(textSL);
                double tienTrongNha = thuChiDAO.LayTienTrongNhaMoiNhat();
                if (_cbThuChi.isChecked()) {
                    _thuChi.setThu1Chi0(true);
                    tienTrongNha += soTien;
                } else {
                    _thuChi.setThu1Chi0(false);
                    tienTrongNha -= soTien;
                }


                Date dateNgayNhap;
                try {
                    dateNgayNhap = frmDateddMMyy.parse(_ngayNhap.getText().toString());
                } catch (ParseException e) {
                    dateNgayNhap = new Date();
                }
                String strGhiChu = _ghiChu.getText().toString().trim();
                _thuChi.setMaHD("");
                _thuChi.setSoTien(soTien);
                _thuChi.setNgay(dateNgayNhap);
                _thuChi.setGhiChu(VietHoaChuCaiDau(strGhiChu));
                _thuChi.setTienTrongNha(tienTrongNha >= 0 ? tienTrongNha : 0);
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
                _lblTienTrongNha.setText(tienTrongNha >= 0 ? tienformatter.format(tienTrongNha*1000) + "" : 0 + "");
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

        _ngayNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blngayNhap = true;
                // TODO Auto-generated method stub
                new DatePickerDialog(ThuChiActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        _lblTienTrongNha.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ThuChiActivity.this);
                final EditText edittext = new EditText(ThuChiActivity.this);
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setMessage("Nhập Số Tiền.");
                alert.setTitle("Chỉnh Sửa Số Tiền Có");

                alert.setView(edittext);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (edittext.getText().length() == 0)
                            return;
                        _lblTienTrongNha.setText(tienformatter.format(edittext.getText()));
                        ThuChi thu_Chi = thuChiDAO.LayThuChiMoiNhat();
                        thu_Chi.setTienTrongNha(Double.parseDouble(edittext.getText().toString()));
                        thuChiDAO.UpdateThuChiTrongDatabase(thu_Chi);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        return;
                    }
                });
                alert.show();

                return true;
            }
        });

    }

    String VietHoaChuCaiDau(String name) {
        char[] array = name.toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        for (int i = 1; i < array.length; i++) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            }
        }
        return new String(array);
    }

    Boolean blDate = true;
    Boolean blngayNhap = false;
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
        Date date = new Date();
        if (blngayNhap) {
            _ngayNhap.setText(frmDateddMMyy.format(myCalendar.getTime()));
            blngayNhap = false;
        } else if (_blDate) {
            if (myCalendar.getTime().after(date)) {
                Toast.makeText(ThuChiActivity.this, "Không Lớn Hơn Ngày Hiện Tại", Toast.LENGTH_LONG).show();
                return;
            }
            dateTuNgay = myCalendar.getTime();
            _tuNgay.setText(frmDateddMMyy.format(dateTuNgay)); // Đưa ngay đã chọn lên text
            try {
                dateDenNgay = frmDateddMMyy.parse(_denNgay.getText().toString());
            } catch (ParseException e) {
                return;
            }
            if (dateDenNgay.before(dateTuNgay))
                _denNgay.setText(frmDateddMMyy.format(dateTuNgay));
        } else {
            Date dateTuNgay = new Date();
            try {
                dateTuNgay = frmDateddMMyy.parse(_tuNgay.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (myCalendar.getTime().before(dateTuNgay)) {
                Toast.makeText(ThuChiActivity.this, "Phải Lớn Hơn Ngày Bắt Đầu Xem", Toast.LENGTH_LONG).show();
                _denNgay.setText(frmDateddMMyy.format(dateTuNgay));
                return;
            }
            if (myCalendar.getTime().after(date)) {
                myCalendar.setTime(date);
            }
            _denNgay.setText(frmDateddMMyy.format(myCalendar.getTime()));
        }
    }

}
