package anhtuan.banhang;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import java.util.Date;
import java.util.List;

import anhtuan.banhang.DAO.HoaDonXuatDAO;
import anhtuan.banhang.DAO.KhachHangDAO;
import anhtuan.banhang.DAO.ListViewMatHangAdapter;
import anhtuan.banhang.DAO.MatHangDAO;
import anhtuan.banhang.DAO.NhanVienDAO;
import anhtuan.banhang.DAO.ThuChiDAO;
import anhtuan.banhang.DTO.DanhSachMatHang;
import anhtuan.banhang.DTO.HoaDonXuat;
import anhtuan.banhang.DTO.KhachHang;
import anhtuan.banhang.DTO.MatHang;
import anhtuan.banhang.DTO.NhanVien;
import anhtuan.banhang.DTO.ThuChi;

public class XuatHoaDonActivity extends AppCompatActivity {
    RadioGroup _grRadio;
    RadioButton _rdoSanPham;
    RadioButton _rdoDay;
    RadioButton _rdoDauDai;
    CheckBox _cbxAdd;

    TextView lblNoCu;
    TextView lblSoLoai;
    TextView lblSoLuong;
    TextView lblTongTien;
    TextView lblMaHoaDon;

    ImageButton btnThem;
    ImageButton btnXuatHD;
    ImageButton btnPrint;
    ImageView imgView;

    Spinner _spinMatHang;
    Spinner _spinKH;

    EditText _txtsoLuong;
    EditText _txtdonGia;
    EditText _txtTraTien;

    ListView _listV;

    String _strMaLoai = "MSP";
    String _NoCuKhachHang = "0";
    // Cặp Đối Tượng Cho Spiner Mặt Hàng
    ArrayList<MatHang> arrayMH = new ArrayList<MatHang>();
    ArrayAdapter<MatHang> adapterMH = null;

    // Cặp Đối Tượng Cho Spiner Khách Hàng
    ArrayList<KhachHang> arrayKhachHang = new ArrayList<KhachHang>();
    ArrayAdapter<KhachHang> adapterKhachHang = null;

    // Cặp Đối Tượng Cho Listview
    ArrayList<MatHang> arayListView = new ArrayList<MatHang>();
    ListViewMatHangAdapter adapterListView = null;

    MatHang _matHang = new MatHang();
    MatHangDAO _matHangDAO = new MatHangDAO();
    KhachHang _khachHang = new KhachHang();
    KhachHangDAO _khachHangDAO = new KhachHangDAO();
    NhanVien nhanVien = new NhanVien();
    NhanVienDAO _nhanVienDAO = new NhanVienDAO();
    HoaDonXuatDAO hoaDonXuatDAO = new HoaDonXuatDAO();
    HoaDonXuat hoaDonXuat = new HoaDonXuat();
    ThuChiDAO thuChiDAO = new ThuChiDAO();

    DanhSachMatHang dsMatHang = new DanhSachMatHang();
    int intSelectSpinMHPosition = 0;
    int intTienMuaHang = 0;
    String tongTienBan = "0";

    static String pathPDF = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LuuHoaDon";
    static String path_a5_clear = pathPDF + "/a5_clear.pdf";
    String path_a4_print = pathPDF + "/a4_print.pdf";
    String strMaHoaDon = "";
    File pdfHoaDon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_xuat_hoa_don);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);// Không Tự Động Chọn EditText
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);  // Không Tittle
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();  // Đọc Ghi PDF
        StrictMode.setVmPolicy(builder.build());  // Đọc Ghi PDF

        // ? Cấp Quyền Đọc Ghi Cho App
        checkAndRequestPermissions();
        if (!isExternalStorageReadable())
            return;

        getDataFromLogin();
        getControl();

        hoaDonXuatDAO.XoaAllHoaDonXuatNull();
        strMaHoaDon = taoMoiHoaDon();
        lblMaHoaDon.setText(nhanVien.getTenNhanVien() + " - HĐ: " + strMaHoaDon);
        pdfHoaDon = new File(pathPDF + "/" + strMaHoaDon + ".pdf");
        hoaDonXuat.setMaHD(strMaHoaDon);
        hoaDonXuat.setMaNhanVien(nhanVien.getMaNhanVien());
        hoaDonXuat.setMaKH("");
        hoaDonXuat.setNgayXuat(new Date());
        hoaDonXuat.setTongTien(0.0);
        hoaDonXuat.setTongTienGoc(0.0);
        hoaDonXuatDAO.ThemHoaDonXuat(hoaDonXuat);

        addEventForm();
        hoaDonXuatDAO.CloseCONN();
    }

    private void getDataFromLogin() {
        String getDataLogin = getIntent().getStringExtra(DangNhapActivity.KEY_DATA);
        nhanVien = _nhanVienDAO.LayThongTinNhanVienTheoMa(getDataLogin);
    }

    private String taoMoiHoaDon() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayNow = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        String getDay = (new DecimalFormat("#00")).format(dayNow);
        String getMonth = (new DecimalFormat("#00")).format(month);
        return hoaDonXuatDAO.LayMaHoaDonTheoNgay(getDay + getMonth + year + "");
    }

    private void getControl() {
        _grRadio = (RadioGroup) findViewById(R.id._groupRdo);
        _rdoSanPham = (RadioButton) findViewById(R.id.radioSanPham);
        _rdoDay = (RadioButton) findViewById(R.id.radioDay);
        _rdoDauDai = (RadioButton) findViewById(R.id.radioDauDai);
        _cbxAdd = (CheckBox) findViewById(R.id.cbxAdd);

        _txtsoLuong = (EditText) findViewById(R.id.txtSoLuong);
        _txtdonGia = (EditText) findViewById(R.id.txtDonGia);
        _txtTraTien = (EditText) findViewById(R.id.txtTraTien);

        _listV = (ListView) findViewById(R.id._listviewMatHang);
        _spinKH = (Spinner) findViewById(R.id.spinnerKhachHang);
        _spinMatHang = (Spinner) findViewById(R.id.spinnerMatHang);

        lblNoCu = (TextView) findViewById(R.id.lblNoCu);
        lblSoLoai = (TextView) findViewById(R.id._lblSoLoai);
        lblSoLuong = (TextView) findViewById(R.id._lblSoLuong);
        lblTongTien = (TextView) findViewById(R.id._lblTongTien);
        lblMaHoaDon = (TextView) findViewById(R.id.lblMaHoaDon);

        btnThem = (ImageButton) findViewById(R.id.btnThem);
        btnThem.setImageResource(R.drawable.image_add);
        btnXuatHD = (ImageButton) findViewById(R.id.btn_xuatHD);
        btnXuatHD.setImageResource(R.drawable.image_save_pdf);
        btnPrint = (ImageButton) findViewById(R.id.btn_print);
        btnPrint.setImageResource(R.drawable.image_print);

        imgView = (ImageView) findViewById(R.id.imageViewXH);
        imgView.setImageResource(R.drawable.image_xuat_hang);

        // Cấu Hình Cho Spiner Khách Hàng
        arrayKhachHang = _khachHangDAO.getArrKhachHang();
        adapterKhachHang = new ArrayAdapter<KhachHang>(this, android.R.layout.simple_spinner_item, arrayKhachHang);
        adapterKhachHang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinKH.setAdapter(adapterKhachHang);

        // Cấu Hình Cho Spiner Mặt Hàng
        arrayMH = _matHangDAO.getArrMatHang(_strMaLoai);
        adapterMH = new ArrayAdapter<MatHang>// Tao Adapter Gan Data Source Arr vao Adapter
                (this, android.R.layout.simple_spinner_item, arrayMH);
        adapterMH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinMatHang.setAdapter(adapterMH);

        // Cấu Hình Cho ListView
        arayListView = new ArrayList<MatHang>();
        adapterListView = new ListViewMatHangAdapter( //Khởi tạo đối tượng adapter và gán Data source
                this, R.layout.my_intem_layout,
                arayListView/*thiết lập data source*/);
        _listV.setAdapter(adapterListView);//gán Adapter vào Lisview

        // Sự Kiện Nhấn Giữ Xóa Item Trên ListView
        _listV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MatHang matHang = arayListView.get(position);
                dsMatHang.deleteMatHangList(matHang);
                //Mỗi lần thêm xong thì cập nhập lại ListView
                loadListMatHangByDanhSach(dsMatHang);
                Toast.makeText(XuatHoaDonActivity.this, "Đã Xóa " + matHang, Toast.LENGTH_SHORT).show();
                setThongTinKetQua();
                if (arayListView.size() <= 0) {
                    _spinKH.setEnabled(true);
                }
                return false;
            }
        });
    }

    private void addEventForm() {
        // Group Radio Checked
        _grRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                groupRadioChangedEvent(group, checkedId);
            }
        });

        _cbxAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String strHienThi = "";
                if (_cbxAdd.isChecked()) {
                    strHienThi = "Thêm Sản Phẩm.";
                    btnPrint.setEnabled(false);
                    btnXuatHD.setEnabled(false);
                } else {
                    strHienThi = "Mua Sản Phẩm.";
                    btnPrint.setEnabled(true);
                    btnXuatHD.setEnabled(true);
                }
                Toast.makeText(XuatHoaDonActivity.this, strHienThi, Toast.LENGTH_SHORT).show();
            }
        });

        // Select Spinner Mặt Hàng
        _spinMatHang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _matHang = arrayMH.get(position);
                intSelectSpinMHPosition = position;
                LayGiaBanLenEditText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Select Spriner Khách Hàng
        _spinKH.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int position, long id) {
                //mỗi lần chọn danh mục trong Spinner
                _khachHang = arrayKhachHang.get(position);
                Float fNoCu = Float.parseFloat(_khachHang.getNoCu());
                if (fNoCu > 0)
                    lblNoCu.setText("Nợ: " + _khachHang.getNoCu());
                else
                    lblNoCu.setText("");
                if (intSelectSpinMHPosition > 0) // Đã Chọn Mặt Hàng
                    LayGiaBanLenEditText();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //
        // Click Button Add
        //
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _matHang = arrayMH.get(intSelectSpinMHPosition);
                String textSL = _txtsoLuong.getText().toString();
                String textGia = _txtdonGia.getText().toString();
                if (textSL.length() == 0) {
                    Toast.makeText(XuatHoaDonActivity.this, "Số Lượng Phải > 0 ", Toast.LENGTH_SHORT).show();
                    return;
                }
                MatHang mhAdd = _matHangDAO.getMatHangByID(_matHang.getMaMatH());
                Integer intSoLuongMua = Integer.parseInt(textSL);

                // Nếu cbx Thêm Sp Checked .
                if (_cbxAdd.isChecked()) {
                    hoaDonXuatDAO.UpdateSoLuongMatHang(intSoLuongMua, mhAdd.getMaMatH());
                    Toast.makeText(XuatHoaDonActivity.this, "Đã Update, SL: " + intSoLuongMua, Toast.LENGTH_SHORT).show();
                    mhAdd.setSoLuong(intSoLuongMua);
                    return;
                }
                if (textSL.length() == 0 || textGia.length() == 0 || textGia.equals(".") || intSoLuongMua == 0) {
                    Toast.makeText(XuatHoaDonActivity.this, "Số Lượng, Đơn Giá Phải > 0 ", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (intSoLuongMua > mhAdd.getSoLuong()) {
                    Toast.makeText(XuatHoaDonActivity.this, "Số Lượng Còn " + mhAdd.getSoLuong() + " - Không Đủ", Toast.LENGTH_SHORT).show();
                    return;
                }
                Float fGiaban = Float.parseFloat(textGia);
                if (fGiaban <= 0) {
                    Toast.makeText(XuatHoaDonActivity.this, "Giá Bán Phải > 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                _matHang.setSoLuong(intSoLuongMua);
                _matHang.setDonGia(fGiaban);
                hoaDonXuatDAO.UpdateGiaBanTheoKhachHang(_khachHang, _matHang);
                addMatHangForListView();
                setThongTinKetQua();
                if (arayListView.size() > 0) _spinKH.setEnabled(false);
                // Upadate Giá Bán


                TaoFilePDFA5Null();
                Toast.makeText(XuatHoaDonActivity.this, "Đã Mua " + (int) (double) _matHang.getSoLuong() + " dây\n" + _matHang, Toast.LENGTH_LONG).show();
            }
        });

        //
        // Button View Print On Click
        // Xem Trước Hóa Đơn
        //
        btnXuatHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arayListView.size() <= 0) {
                    Toast.makeText(XuatHoaDonActivity.this, "Chưa Có Sản Phẩm !", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!_txtTraTien.isEnabled()) {
                    // Mở Trả Tiền Khi Sửa Hóa Đơn.
                    _txtTraTien.setEnabled(true);
                    btnThem.setEnabled(true);
                    _listV.setEnabled(true);
                    lblTongTien.setEnabled(true);
                    // Chuyển Tha`nh Nút Edit
                    btnXuatHD.setImageResource(R.drawable.image_save_pdf);
                    // Xóa Dữ Liệu
                    hoaDonXuatDAO.DeleteDuLieuMuaDB(strMaHoaDon);
                    // Cập Nhật Lại Nợ Cũ
                    _khachHang.setNoCu(_NoCuKhachHang);
                    _khachHangDAO.CapNhapNoCuTheoKhachHang(_khachHang);
                    Float fNoCu = Float.parseFloat(_khachHang.getNoCu());
                    if (fNoCu > 0)
                        lblNoCu.setText("Nợ: " + _khachHang.getNoCu());
                    else
                        lblNoCu.setText("");
                    return;
                }
                try {
                    XuatHoaDonPDF();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                // Mở Kết Nối
                hoaDonXuatDAO.OpenCONN();
                // Cập Nhập Lại Tổng Tiền
                hoaDonXuat.setTongTien((double) dsMatHang.getTongTienList());
                hoaDonXuat.setTongTienGoc(hoaDonXuatDAO.LayTongTienGocCuaHD(arayListView));
                hoaDonXuat.setMaKH(_khachHang.getMaKH());
                hoaDonXuatDAO.UpdateTTHoaDonXuat(hoaDonXuat);
                // Thêm Dữ Liệu Mới Xuống Database
                hoaDonXuatDAO.InsertDuLieuMuaDB(arayListView, hoaDonXuat, _khachHang);

                // Update Lại Nợ Cũ
                intTienMuaHang = dsMatHang.getTongTienList();
                int intTienTra = 0;
                _NoCuKhachHang = _khachHang.getNoCu(); // Lưu Lại Nợ Cũ
                if (!_txtTraTien.getText().toString().equals(""))
                    intTienTra = Integer.parseInt(_txtTraTien.getText().toString());
                if (intTienTra - intTienMuaHang != 0) {
                    double updateNo = Double.parseDouble(_NoCuKhachHang) + intTienMuaHang - intTienTra;
                    _khachHang.setNoCu(updateNo - (int) updateNo > 0 ? updateNo + "" : (int) updateNo + "");
                    _khachHangDAO.CapNhapNoCuTheoKhachHang(_khachHang);
                    if (updateNo > 0) {
                        lblNoCu.setText("Nợ: " + _khachHang.getNoCu());
                    } else {
                        lblNoCu.setText("");
                    }
                }
                // Khóa Trả Tiền Và Xuất Hóa Đơn Khi Tạo Thành Công.
                _txtTraTien.setEnabled(false);
                //btnXuatHD.setEnabled(false);
                btnThem.setEnabled(false);
                _listV.setEnabled(false);
                lblTongTien.setEnabled(false);
                // Chuyển Tha`nh Nút Edit
                btnXuatHD.setImageResource(R.drawable.image_edit);
                // Đóng Kết Nối
                hoaDonXuatDAO.CloseCONN();
            }
        });

        //
        // Button Print PDF Click
        // Xoay Ngang Để In
        //
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lblTongTien.isEnabled()) {
                    Toast.makeText(XuatHoaDonActivity.this, "Chưa Tạo Được Hóa Đơn !", Toast.LENGTH_LONG).show();
                    return;
                }

                String filePathHoaDon = pathPDF + "/" + strMaHoaDon + ".pdf";
                //openPdf();
                File fileHoaDon = new File(filePathHoaDon);
                if (!fileHoaDon.exists()) return;
                hoaDonXuatDAO.UploadFilePDFToDatabase(filePathHoaDon);

                if (!_txtTraTien.getText().toString().equals(""))
                    ThemThuChiVaoDatabase();

                try {
                    createA4PdfPrint(filePathHoaDon);
                    // Đóng App Khi In Hóa Đơn Sau 3s
                    handler.postAtTime(runnable, System.currentTimeMillis() + interval);
                    handler.postDelayed(runnable, interval);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        lblTongTien.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!_txtTraTien.isEnabled()) {  // Nếu Mục Trả Tiền Bị Đóng.
                    // Có Nợ Cũ
                    int intTienTra = 0;
                    if (!_txtTraTien.getText().toString().equals(""))
                        intTienTra = Integer.parseInt(_txtTraTien.getText().toString());
                    double updateNo = Double.parseDouble(_khachHang.getNoCu()) + intTienTra - intTienMuaHang;
                    _khachHang.setNoCu(updateNo - (int) updateNo > 0 ? updateNo + "" : (int) updateNo + "");
                    _khachHangDAO.CapNhapNoCuTheoKhachHang(_khachHang);
                    if (updateNo > 0) {
                        lblNoCu.setText("Nợ: " + _khachHang.getNoCu());
                    } else {
                        lblNoCu.setText("");
                    }

                    // Xóa Dữ Liệu Hóa Đơn Đã Tạo.
                    File filePrint = new File(pathPDF + "/" + strMaHoaDon + ".pdf");
                    if (filePrint.exists()) filePrint.delete();  // Xóa File In Tạo Lại
                    hoaDonXuatDAO.DeleteDuLieuMuaDB(hoaDonXuat.getMaHD()); // Xóa Dữ Liệu DB
                    _txtTraTien.setText("");
                    _txtTraTien.setEnabled(true);
                    btnXuatHD.setEnabled(true);
                }
            }
        });

        //
        //  Tổng Tiên Onclick
        //
        lblTongTien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Double.parseDouble(_khachHang.getNoCu()) > 0) {
                    if (tongTienBan.equals(new DecimalFormat("###,###").format(dsMatHang.getTongTienList()).replaceAll(",", "."))) {
                        tongTienBan = new DecimalFormat("###,###").format(Double.parseDouble(tongTienBan) + Double.parseDouble(_khachHang.getNoCu()));
                    } else {
                        tongTienBan = new DecimalFormat("###,###").format(dsMatHang.getTongTienList()).replaceAll(",", ".");
                    }
                    lblTongTien.setText("+$:" + tongTienBan);
                }
            }
        });

        //
        // Tên Hóa Đơn Onclick Đổi Sang In Theo STT
        //
        lblMaHoaDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lblMaHoaDon.getText().equals("In_STT"))
                    lblMaHoaDon.setText(nhanVien.getTenNhanVien() + " - HĐ: " + strMaHoaDon);
                else
                    lblMaHoaDon.setText("In_STT");
            }
        });

    }

    private void ThemThuChiVaoDatabase() {
        int tienTra = Integer.parseInt(_txtTraTien.getText().toString());
        double tienTrongNhaMoiNhat = thuChiDAO.LayTienTrongNhaMoiNhat();
        // Tạo Thu Chi
        ThuChi thuChi = new ThuChi();
        thuChi.setNgay(hoaDonXuat.getNgayXuat());
        thuChi.setMaHD(strMaHoaDon);
        thuChi.setGhiChu(_khachHang.getTenKH());
        thuChi.setSoTien(tienTra);
        thuChi.setThu1Chi0(true);
        thuChi.setTienTrongNha(tienTrongNhaMoiNhat + tienTra);
        thuChiDAO.ThemThuChiVaoDatabase(thuChi);
    }

    // Đong Form Sau 10s
    private final int interval = 3000; // 3 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            Toast.makeText(XuatHoaDonActivity.this, "Đã Hoàn Thành Hóa Đơn!", Toast.LENGTH_LONG).show();

            setResult(RESULT_OK, null);
            finish();
        }
    };


    private void LayGiaBanLenEditText() {
        // Set Gia Ban
        String giaBan = _matHangDAO.layGiaBanTheoMHvaKH(_matHang.getMaMatH(), _khachHang.getMaKH());
        if (giaBan.equals("") && _matHang.getDonGia() != null)
            giaBan = (_matHang.getDonGia() + 4) + "";
        float f_gia = new Float(giaBan.trim());
        _txtdonGia.setText(f_gia - (int) f_gia > 0 ? f_gia + "" : (int) f_gia + "");
    }

    /**
     * Hàm Set Thông Tin Kết Quả
     */
    private void setThongTinKetQua() {
        lblSoLoai.setText(arayListView.size() + " Loại");
        lblSoLuong.setText("SL:" + dsMatHang.getSoLuongMatHang());
        tongTienBan = new DecimalFormat("###,###").format(dsMatHang.getTongTienList()).replaceAll(",", ".");
        lblTongTien.setText("+$:" + tongTienBan);

    }

    /**
     * Hàm thêm một sản phẩm vào cho danh mục được chọn trong Spinner
     */
    private void addMatHangForListView() {
        dsMatHang.addMatHangList(_matHang);
        //Mỗi lần thêm xong thì cập nhập lại ListView
        loadListMatHangByDanhSach(dsMatHang);
    }

    /**
     * Lọc danh sách sản phẩm theo danh mục và update lại ListView
     *
     * @param ds
     */
    private void loadListMatHangByDanhSach(DanhSachMatHang ds) {
        //xóa danh sách cũ
        arayListView.clear();
        //lấy danh sách mới từ Mặt Hàng chọn trong Spinner
        arayListView.addAll(ds.getListMatHang());

        // Sắp Xếp Theo Mã MH
        Collections.sort(arayListView, new Comparator<MatHang>() {
            @Override
            public int compare(MatHang o1, MatHang o2) {
                return (o1.getMaMatH().compareTo(o2.getMaMatH()));
            }
        });

        //cập nhật lại ListView
        adapterListView.notifyDataSetChanged();
    }

    // Khi radio group có thay đổi.
    private void groupRadioChangedEvent(RadioGroup group, int checkedId) {
        int checkedRadioId = group.getCheckedRadioButtonId();
        switch (checkedRadioId) {
            case R.id.radioSanPham:
                _strMaLoai = "MSP";
                break;
            case R.id.radioDay:
                _strMaLoai = "DAY";
                break;
            case R.id.radioDauDai:
                _strMaLoai = "DAU";
                break;
        }
        arrayMH = _matHangDAO.getArrMatHang(_strMaLoai);
        // Tao Adapter Gan Data Source Arr vao Adapter
        adapterMH.notifyDataSetChanged();
    }

    /**
     * Xuat File PDF
     */
    private void XuatHoaDonPDF() throws IOException, DocumentException, FileNotFoundException {
        // Update Nợ Cũ
        double dbNoCu = Double.parseDouble(_khachHang.getNoCu());
        String strNoCu = new DecimalFormat("###,###").format(dbNoCu).replaceAll(",", ".");

        int intTraTien = 0;
        if (_txtTraTien.getText().length() > 0)
            intTraTien = Integer.parseInt(_txtTraTien.getText().toString());
        String strTraTien = intTraTien + "";

        int dbTongTienBan = dsMatHang.getTongTienList();
        String strTongTienBan = new DecimalFormat("###,###").format(dbTongTienBan).replaceAll(",", ".");
        //String strTongTienBan = dbTongTienBan - (int) dbTongTienBan > 0 ? dbTongTienBan + "" : (int) dbTongTienBan + "";

        double dbTongNo = dbTongTienBan + dbNoCu;
        String strTongNo = new DecimalFormat("###,###").format(dbTongNo).replaceAll(",", ".");
        //String strTongNo = dbTongNo - (int) dbTongNo > 0 ? dbTongNo + "" : (int) dbTongNo + "";

        double dbTienConLai = dbTongNo - intTraTien;
        String strTienConLai = new DecimalFormat("###,###").format(dbTienConLai).replaceAll(",", ".");
        //String strTienConLai = dbTienConLai - (int) dbTienConLai > 0 ? dbTienConLai + "" : (int) dbTienConLai + "";

        //Get Date Now
        Date date = new Date();
        SimpleDateFormat frmDate = new SimpleDateFormat("dd/MM/yyyy");
        String ngayBan = frmDate.format(date);

        String strFONT = "/res/font/times.ttf";
        BaseFont timesFont = BaseFont.createFont(strFONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font09 = new Font(timesFont, 9);
        Font font14 = new Font(timesFont, 14);


        // Creating iText Table from title data
        PdfPTable pdfTableTitle = new PdfPTable(2);
        pdfTableTitle.setWidthPercentage(95);
        pdfTableTitle.getDefaultCell().setBorder(0);

        // Create Cell Title
        PdfPCell cellTitle;
        cellTitle = new PdfPCell(new Phrase("HÓA ĐƠN BÁN HÀNG \n", font14));
        cellTitle.setColspan(3);
        cellTitle.setHorizontalAlignment(1);
        cellTitle.setBorder(0);
        pdfTableTitle.addCell(cellTitle);

        cellTitle = new PdfPCell(new Paragraph("Người Bán  : " + nhanVien.toString() + "\nKhách Hàng : " + _khachHang.getTenKH() + " (" + hoaDonXuatDAO.LayTongSoHoaDonTheoKH(_khachHang.getMaKH()) + ")\n", font09));
        cellTitle.setBorder(0);
        pdfTableTitle.addCell(cellTitle);
        //
        cellTitle = new PdfPCell(new Paragraph("Ngày :  " + ngayBan + "\nMã HĐ : " + strMaHoaDon + "\n", font09));
        cellTitle.setBorder(0);
        cellTitle.setHorizontalAlignment(2);
        pdfTableTitle.addCell(cellTitle);
        pdfTableTitle.addCell(new Paragraph(""));

        //
        //Creating iTextSharp Table from the DataTable data  Table Bán Hàng
        //
        PdfPTable pdfTable = new PdfPTable(5);
        float[] widths = new float[]{2f, 5f, 1.5f, 1.5f, 2f};
        pdfTable.setWidths(widths);

        pdfTable.setWidthPercentage(95);
        pdfTable.getDefaultCell().setPadding(3);
        pdfTable.getDefaultCell().setBorder(0);
        pdfTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        //
        //Adding Header row
        //
        BaseColor baseHeader = new BaseColor(240, 240, 240);

        PdfPCell cellHeader1 = new PdfPCell(new Phrase("Mã MH", font09));
        cellHeader1.setBackgroundColor(baseHeader);
        cellHeader1.setHorizontalAlignment(1);
        cellHeader1.setFixedHeight(14f);
        pdfTable.addCell(cellHeader1);

        PdfPCell cellHeader2 = new PdfPCell(new Phrase("Tên Mặt Hàng", font09));
        cellHeader2.setBackgroundColor(baseHeader);
        cellHeader2.setHorizontalAlignment(1);
        cellHeader2.setFixedHeight(14f);
        pdfTable.addCell(cellHeader2);

        PdfPCell cellHeader3 = new PdfPCell(new Phrase("Số Lượng", font09));
        cellHeader3.setBackgroundColor(baseHeader);
        cellHeader3.setHorizontalAlignment(1);
        cellHeader3.setFixedHeight(14f);
        pdfTable.addCell(cellHeader3);

        PdfPCell cellHeader4 = new PdfPCell(new Phrase("Đơn Giá", font09));
        cellHeader4.setBackgroundColor(baseHeader);
        cellHeader4.setHorizontalAlignment(1);
        cellHeader4.setFixedHeight(14f);
        pdfTable.addCell(cellHeader4);

        PdfPCell cellHeader5 = new PdfPCell(new Phrase("Thành Tiền", font09));
        cellHeader5.setBackgroundColor(baseHeader);
        cellHeader5.setHorizontalAlignment(1);
        cellHeader5.setFixedHeight(14f);
        pdfTable.addCell(cellHeader5);

        //Adding ListView Row
        int soTT = 0;
        String strSoLuong = "";
        float fDay = 0;
        float fDau = 0;
        float fSP = 0;
        float fBop = 0;
        for (MatHang mh : arayListView) {
            for (int i = 1; i <= 5; i++) {
                PdfPCell _cellPDF;
                switch (i) {
                    case 1: // Lấy Mã Sản Phẩm
                        soTT = soTT + 1;
                        String kieuStr = mh.getMaMatH().substring(0, 3);
                        if (kieuStr.trim().equals("DAY")) {
                            kieuStr = "D";
                            fDay = mh.getSoLuong() + fDay;
                        } else if (kieuStr.trim().equals("DAI")) kieuStr = "Đ";
                        else if (kieuStr.trim().equals("DAU")) {
                            kieuStr = "Đ";
                            fDau = mh.getSoLuong() + fDau;
                        } else if (kieuStr.trim().equals("MSP")) {
                            kieuStr = "";
                            if (mh.getMaMatH().substring(3, 5).equals("50"))
                                fBop = mh.getSoLuong() + fBop;
                            else
                                fSP = mh.getSoLuong() + fSP;
                        }
                        if (lblMaHoaDon.getText().equals("In_STT"))
                            kieuStr = "(" + soTT + ")" + kieuStr;

                        _cellPDF = new PdfPCell(new Phrase(kieuStr + mh.getMaMatH().substring(3), font14));
                        _cellPDF.setFixedHeight(18f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 2:  // Lấy Tên Sản Phẩm
                        _cellPDF = new PdfPCell(new Phrase(mh.getTenMatH(), font14));
                        _cellPDF.setFixedHeight(18f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 3: // Lấy Số Lượng
                        _cellPDF = new PdfPCell(new Phrase((new DecimalFormat("##")).format(mh.getSoLuong()), font14));
                        _cellPDF.setFixedHeight(18f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 4:  // Lấy Đơn Giá
                        float dbDonGia = new Float(mh.getDonGia());
                        _cellPDF = new PdfPCell(new Phrase(dbDonGia - (int) dbDonGia > 0 ? dbDonGia + "" : (int) dbDonGia + "", font14));
                        _cellPDF.setFixedHeight(18f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 5: // Thành Tiền
                        double thanhTien = mh.getSoLuong() * mh.getDonGia();
                        _cellPDF = new PdfPCell(new Phrase(thanhTien - (int) thanhTien > 0 ? thanhTien + "" : (int) thanhTien + "", font14));
                        _cellPDF.setFixedHeight(18f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                }
            }
        }

        if (fSP > 0)
            strSoLuong = (int) fSP + " Dây SP";
        if (fDau > 0)
            strSoLuong = strSoLuong + "\n" + (int) fDau + " Đầu Ko";
        if (fDay > 0)
            strSoLuong = strSoLuong + "\n" + (int) fDay + " Dây Ko";
        if (fBop > 0)
            strSoLuong = strSoLuong + "\n" + (int) fBop + " Bóp.";

        // Adding Row Tổng Tiền
        pdfTable.addCell(new Phrase(lblSoLoai.getText().toString().replaceAll(" ", ""), font14));
        PdfPCell _cellSoLuong = new PdfPCell(new Phrase(strSoLuong, font14));
        _cellSoLuong.setColspan(2);
        _cellSoLuong.setBorder(0);
        _cellSoLuong.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pdfTable.addCell(_cellSoLuong);

        PdfPCell _cellTong = new PdfPCell(new Phrase("Tổng: ", font14));
        _cellTong.setHorizontalAlignment(Element.ALIGN_RIGHT);
        _cellTong.setBorder(0);
        pdfTable.addCell(_cellTong);
        PdfPCell _cellTongTien = new PdfPCell(new Phrase(strTongTienBan, font14));
        _cellTongTien.setHorizontalAlignment(Element.ALIGN_LEFT);
        _cellTongTien.setBorder(0);
        pdfTable.addCell(_cellTongTien);

        //
        // Table Tính Tiền
        //
        PdfPTable pdfTableTongTien = new PdfPTable(5);
        float[] widthsTT = new float[]{1.5f, 5f, 1.5f, 2f, 2f};
        pdfTableTongTien.setWidths(widthsTT);
        pdfTableTongTien.setWidthPercentage(95);
        pdfTableTongTien.getDefaultCell().setBorder(0);

        // Add Cell Tính Tiền
        pdfTableTongTien.addCell(new Phrase(""));
        pdfTableTongTien.addCell(new Phrase(""));

        if (dbNoCu > 0) {
            PdfPCell _cellPDFTT = new PdfPCell(new Phrase("Nợ Cũ      : \nTổng Tiền: \nTrả Tiền   : \n------------- \nCòn          :", font14));
            _cellPDFTT.setHorizontalAlignment(Element.ALIGN_RIGHT);
            _cellPDFTT.setColspan(2);
            _cellPDFTT.setBorder(0);
            pdfTableTongTien.addCell(_cellPDFTT);
            pdfTableTongTien.addCell(new Phrase("" + strNoCu + "\n" + strTongNo + "\n" + strTraTien + "\n\n" + strTienConLai, font14));
        } else {
            PdfPCell _cellPDFTT = new PdfPCell(new Phrase("Trả           : \n------------- \nCòn          :", font14));
            _cellPDFTT.setHorizontalAlignment(Element.ALIGN_RIGHT);
            _cellPDFTT.setColspan(2);
            _cellPDFTT.setBorder(0);
            pdfTableTongTien.addCell(_cellPDFTT);
            pdfTableTongTien.addCell(new Phrase("" + strTraTien + "\n\n" + strTienConLai, font14));
        }

        //
        //Exporting to PDF
        //
        File folderPath = new File(pathPDF);
        if (!folderPath.exists())
            folderPath.mkdirs();

        // Kiểm Tra Tồn Tại
        if (pdfHoaDon.exists()) {
            pdfHoaDon.delete(); // Xóa
        }

        Document pdfDoc = new Document(PageSize.A5, 10f, 10f, 10f, 10f);
        //pdfDoc.setPageSize(PageSize.LETTER);  // A5 Dọc
        PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(pdfHoaDon));

        Rotate rotation = new Rotate(); // Xoay Ngang
        writer.setPageEvent(rotation); // Xoay Ngang

        pdfDoc.open();
        //rotation.setRotation(PdfPage.SEASCAPE);  // Xoay Ngang Đầu Hướng Sang Trái
        pdfDoc.addAuthor("Anh Tuan");
        pdfDoc.add(pdfTableTitle);
        pdfDoc.add(pdfTable);
        pdfDoc.add(pdfTableTongTien);
        pdfDoc.close();

        //Toast.makeText(getApplicationContext(), "Xuất Hóa Đơn Thành Công !", Toast.LENGTH_LONG).show();

        viewPdf(pathPDF + "/" + strMaHoaDon + ".pdf");

    }

    // Method for opening a pdf file
    private void viewPdf(String filePath) {
        File pdfFile = new File(filePath);
        if (!pdfFile.exists()) {
            Toast.makeText(this, "Chưa Tạo Được File PDF !", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri path = Uri.fromFile(pdfFile);
        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(pdfIntent, "Open File");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void createA4PdfPrint(String strA5)
            throws IOException, DocumentException {
        File fileA4 = new File(path_a4_print);
        if (fileA4.exists())
            fileA4.delete();
        // Creating a reader A5
        PdfReader reader = new PdfReader(strA5);

        // Creating a reader A5 Null
        PdfReader readerNull = new PdfReader(path_a5_clear);

        // step 1
        Document document = new Document(PageSize.A4.rotate());
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path_a4_print));
        Rotate rotation = new Rotate(); // Xoay Ngang
        writer.setPageEvent(rotation); // Xoay Ngang

        // step 3
        document.open();
        rotation.setRotation(PdfPage.PORTRAIT);  // Xoay Thẳng Khi Hiển Thị
        // step 4
        PdfContentByte canvas = writer.getDirectContent();
        float a5_width = PageSize.A5.getWidth();
        int n = reader.getNumberOfPages();
        int p = 0;
        PdfImportedPage page = writer.getImportedPage(readerNull, 1);
        while (p++ < n) {
            // Thêm Trang Trắng
            page = writer.getImportedPage(readerNull, 1);
            canvas.addTemplate(page, 0, 0);
            // Lấy Và Thêm Trang Hóa Đơn
            page = writer.getImportedPage(reader, p);
            canvas.addTemplate(page, a5_width, 0);
            document.newPage();
        }
        // step 5
        document.close();
        reader.close();
        readerNull.close();

        viewPdf(path_a4_print);
    }

    //
    // Tạo File PDF A5 Trắng Để Chèn Vào A4
    //
    public static void TaoFilePDFA5Null() {
        // Get File
        File a5_null = new File(path_a5_clear);
        if (a5_null.exists()) a5_null.delete();
        // step 1
        Document document = new Document();
        // step 2
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path_a5_clear));
            // step 3
            document.open();
            // step 4
            document.add(new Paragraph(" "));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        document.close();
    }

    //
    //  Class Xoay File PDF
    //
    public class Rotate extends PdfPageEventHelper {
        protected PdfNumber rotation = PdfPage.PORTRAIT;

        public void setRotation(PdfNumber rotation) {
            this.rotation = rotation;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            writer.addPageDictEntry(PdfName.ROTATE, rotation);
        }
    }
}
