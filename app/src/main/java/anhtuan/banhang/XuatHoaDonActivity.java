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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
import anhtuan.banhang.DTO.DanhSachMatHang;
import anhtuan.banhang.DTO.HoaDonXuat;
import anhtuan.banhang.DTO.KhachHang;
import anhtuan.banhang.DTO.MatHang;
import anhtuan.banhang.DTO.NhanVien;

public class XuatHoaDonActivity extends AppCompatActivity {
    RadioGroup _grRadio;
    RadioButton _rdoSanPham;
    RadioButton _rdoDay;
    RadioButton _rdoDauDai;

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


    DanhSachMatHang dsMatHang = new DanhSachMatHang();
    int intSelectSpinMHPosition = 0;
    int intTienMuaHang = 0;

    String pathPDF = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LuuHoaDon";
    String path_a5_clear = pathPDF + "/a5_clear.pdf";
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
        hoaDonXuat.setGhiChu(" ");
        hoaDonXuat.setNgayXuat(new Date());
        hoaDonXuat.setTongTien(0.0);
        hoaDonXuat.setTongTienGoc(0.0);
        hoaDonXuatDAO.ThemHoaDonXuat(hoaDonXuat);

        addEventForm();
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
                Toast.makeText(XuatHoaDonActivity.this, "Đã Xóa " + matHang, Toast.LENGTH_LONG).show();
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
                Integer intSoLuongMua = Integer.parseInt(textSL);
                if (textSL.length() == 0 || textGia.length() == 0 || textGia.equals(".") || intSoLuongMua == 0) {
                    Toast.makeText(XuatHoaDonActivity.this, "Số Lượng, Đơn Giá Phải > 0 ", Toast.LENGTH_LONG).show();
                    return;
                }

                MatHang mhAdd = _matHangDAO.getMatHangByID(_matHang.getMaMatH());
                if (intSoLuongMua > mhAdd.getSoLuong()) {
                    Toast.makeText(XuatHoaDonActivity.this, "Số Lượng Còn " + mhAdd.getSoLuong() + " - Không Đủ", Toast.LENGTH_LONG).show();
                    return;
                }
                Float fGiaban = Float.parseFloat(textGia);
                if (fGiaban <= 0) {
                    Toast.makeText(XuatHoaDonActivity.this, "Giá Bán Phải > 0", Toast.LENGTH_LONG).show();
                    return;
                }
                _matHang.setSoLuong(intSoLuongMua);
                _matHang.setDonGia(fGiaban);
                addMatHangForListView();
                setThongTinKetQua();

                if (arayListView.size() > 0) _spinKH.setEnabled(false);

                TaoFilePDFA5Null();
                Toast.makeText(XuatHoaDonActivity.this, "Đã Mua " + (int) (double) _matHang.getSoLuong() + " dây\n" + _matHang, Toast.LENGTH_LONG).show();
            }
        });

        //
        //Button Print On Click
        //
        btnXuatHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arayListView.size() <= 0) {
                    Toast.makeText(XuatHoaDonActivity.this, "Chưa Có Sản Phẩm !", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    XuatHoaDonPDF();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                // Cập Nhập Lại Tổng Tiền
                hoaDonXuat.setTongTien((double) dsMatHang.getTongTienList());
                hoaDonXuat.setTongTienGoc(hoaDonXuatDAO.LayTongTienGocCuaHD(arayListView));
                hoaDonXuatDAO.UpdateTTHoaDonXuat(hoaDonXuat);
                // Thêm Dữ Liệu Mới Xuống Database
                hoaDonXuatDAO.InsertDuLieuMuaDB(arayListView, hoaDonXuat, _khachHang);

                // Update Lại Nợ Cũ
                intTienMuaHang = dsMatHang.getTongTienList();
                int intTienTra = 0;
                if (!_txtTraTien.getText().toString().equals(""))
                    intTienTra = Integer.parseInt(_txtTraTien.getText().toString());
                if (intTienTra - intTienMuaHang != 0) {
                    double updateNo = Double.parseDouble(_khachHang.getNoCu()) + intTienMuaHang - intTienTra;
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
                btnXuatHD.setEnabled(false);

            }
        });

        //
        // Button View PDF Click
        //
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePathHoaDon = pathPDF + "/" + strMaHoaDon + ".pdf";
                //openPdf();
                File fileHoaDon = new File(filePathHoaDon);
                if (!fileHoaDon.exists()) return;
                try {
                    createA4PdfPrint(filePathHoaDon);

                    // Đóng App Khi In Hóa Đơn Sau 10s
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
    }

    // Đong Form Sau 10s
    private final int interval = 10000; // 10 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            Toast.makeText(XuatHoaDonActivity.this, "Đã Hoàn Thành Hóa Đơn!", Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private void closeForm() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Thông Báo");
        b.setMessage("Đã Hoàn Thành Hóa Đơn ! Đóng App ?");
        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.create().show();
        /*
        Toast.makeText(this, "Hoàn Thành Xuất Hàng !", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
        */
    }

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
        lblTongTien.setText("+$:" + new DecimalFormat("###,###").format(dsMatHang.getTongTienList()).replaceAll(",", "."));
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
        Font font16 = new Font(timesFont, 9);
        Font font22 = new Font(timesFont, 14);


        // Creating iText Table from title data
        PdfPTable pdfTableTitle = new PdfPTable(2);
        pdfTableTitle.setWidthPercentage(95);
        pdfTableTitle.getDefaultCell().setBorder(0);

        // Create Cell Title
        PdfPCell cellTitle;
        cellTitle = new PdfPCell(new Phrase("HÓA ĐƠN BÁN HÀNG \n", font22));
        cellTitle.setColspan(3);
        cellTitle.setHorizontalAlignment(1);
        cellTitle.setBorder(0);
        pdfTableTitle.addCell(cellTitle);

        cellTitle = new PdfPCell(new Paragraph("Người Bán  : " + nhanVien.toString() + "\nKhách Hàng : " + _khachHang.getTenKH() + "\n", font16));
        cellTitle.setBorder(0);
        pdfTableTitle.addCell(cellTitle);
        //
        cellTitle = new PdfPCell(new Paragraph("Ngày :  " + ngayBan + "\nMã HĐ : " + strMaHoaDon + "\n", font16));
        cellTitle.setBorder(0);
        cellTitle.setHorizontalAlignment(2);
        pdfTableTitle.addCell(cellTitle);
        pdfTableTitle.addCell(new Paragraph(""));

        //
        //Creating iTextSharp Table from the DataTable data  Table Bán Hàng
        //
        PdfPTable pdfTable = new PdfPTable(5);
        float[] widths = new float[]{1.5f, 5f, 1.5f, 2f, 2f};
        pdfTable.setWidths(widths);

        pdfTable.setWidthPercentage(95);
        pdfTable.getDefaultCell().setPadding(3);
        pdfTable.getDefaultCell().setBorder(0);
        pdfTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        //
        //Adding Header row
        //
        BaseColor baseHeader = new BaseColor(240, 240, 240);

        PdfPCell cellHeader1 = new PdfPCell(new Phrase("Mã MH", font16));
        cellHeader1.setBackgroundColor(baseHeader);
        cellHeader1.setHorizontalAlignment(1);
        cellHeader1.setFixedHeight(14f);
        pdfTable.addCell(cellHeader1);

        PdfPCell cellHeader2 = new PdfPCell(new Phrase("Tên Mặt Hàng", font16));
        cellHeader2.setBackgroundColor(baseHeader);
        cellHeader2.setHorizontalAlignment(1);
        cellHeader2.setFixedHeight(14f);
        pdfTable.addCell(cellHeader2);

        PdfPCell cellHeader3 = new PdfPCell(new Phrase("Số Lượng", font16));
        cellHeader3.setBackgroundColor(baseHeader);
        cellHeader3.setHorizontalAlignment(1);
        cellHeader3.setFixedHeight(14f);
        pdfTable.addCell(cellHeader3);

        PdfPCell cellHeader4 = new PdfPCell(new Phrase("Đơn Giá", font16));
        cellHeader4.setBackgroundColor(baseHeader);
        cellHeader4.setHorizontalAlignment(1);
        cellHeader4.setFixedHeight(14f);
        pdfTable.addCell(cellHeader4);

        PdfPCell cellHeader5 = new PdfPCell(new Phrase("Thành Tiền", font16));
        cellHeader5.setBackgroundColor(baseHeader);
        cellHeader5.setHorizontalAlignment(1);
        cellHeader5.setFixedHeight(14f);
        pdfTable.addCell(cellHeader5);

        //Adding ListView Row
        for (MatHang mh : arayListView) {
            for (int i = 1; i <= 5; i++) {
                PdfPCell _cellPDF;
                switch (i) {
                    case 1: // Lấy Mã Sản Phẩm
                        String kieuStr = mh.getMaMatH().substring(0, 3);
                        if (kieuStr.trim().equals("DAY")) kieuStr = "D";
                        else if (kieuStr.trim().equals("DAI")) kieuStr = "Đ";
                        else kieuStr = "";
                        _cellPDF = new PdfPCell(new Phrase(kieuStr + mh.getMaMatH().substring(3), font16));
                        _cellPDF.setFixedHeight(14f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 2:  // Lấy Tên Sản Phẩm
                        _cellPDF = new PdfPCell(new Phrase(mh.getTenMatH(), font16));
                        _cellPDF.setFixedHeight(14f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 3: // Lấy Số Lượng
                        _cellPDF = new PdfPCell(new Phrase((new DecimalFormat("##")).format(mh.getSoLuong()), font16));
                        _cellPDF.setFixedHeight(14f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 4:  // Lấy Đơn Giá
                        float dbDonGia = new Float(mh.getDonGia());
                        _cellPDF = new PdfPCell(new Phrase(dbDonGia - (int) dbDonGia > 0 ? dbDonGia + "" : (int) dbDonGia + "", font16));
                        _cellPDF.setFixedHeight(14f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 5: // Thành Tiền
                        double thanhTien = mh.getSoLuong() * mh.getDonGia();
                        _cellPDF = new PdfPCell(new Phrase(thanhTien - (int) thanhTien > 0 ? thanhTien + "" : (int) thanhTien + "", font16));
                        _cellPDF.setFixedHeight(14f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                }
            }
        }
        // Adding Row Tổng Tiền
        pdfTable.addCell(new Phrase(""));
        pdfTable.addCell(new Phrase(""));
        pdfTable.addCell(new Phrase(""));
        pdfTable.addCell(new Phrase(""));
        pdfTable.addCell(new Phrase("Tổng: " + strTongTienBan, font16));

        //
        // Table Tính Tiền
        //
        PdfPTable pdfTableTongTien = new PdfPTable(5);
        float[] widthsTT = new float[]{1.5f, 5f, 1.5f, 2f, 2f};
        pdfTableTongTien.setWidths(widthsTT);
        pdfTableTongTien.setWidthPercentage(95);
        pdfTableTongTien.getDefaultCell().setBorder(0);

        // Add Cell Tính Tiền
        pdfTableTongTien.addCell(new Phrase(lblSoLoai.getText().toString(), font16));
        pdfTableTongTien.addCell(new Phrase(""));
        pdfTableTongTien.addCell(new Phrase(lblSoLuong.getText().toString(), font16));

        if (dbNoCu > 0) {
            PdfPCell _cellPDFTT = new PdfPCell(new Phrase("Tổng Toa : \nNợ Cũ      : \nTổng Tiền: \nTrả Tiền   : \n------------- \nCòn          :", font16));
            _cellPDFTT.setHorizontalAlignment(Element.ALIGN_RIGHT);
            _cellPDFTT.setBorder(0);
            pdfTableTongTien.addCell(_cellPDFTT);
            pdfTableTongTien.addCell(new Phrase("" + strTongTienBan + "\n" + strNoCu + "\n" + strTongNo + "\n" + strTraTien + "\n\n" + strTienConLai, font16));
        } else {
            PdfPCell _cellPDFTT = new PdfPCell(new Phrase("Tổng Toa : \nTrả           : \n------------- \nCòn          :", font16));
            _cellPDFTT.setHorizontalAlignment(Element.ALIGN_RIGHT);
            _cellPDFTT.setBorder(0);
            pdfTableTongTien.addCell(_cellPDFTT);
            pdfTableTongTien.addCell(new Phrase("" + strTongTienBan + "\n" + strTraTien + "\n\n" + strTienConLai, font16));
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

        Toast.makeText(getApplicationContext(), "Xuất Hóa Đơn Thành Công !", Toast.LENGTH_LONG).show();

        viewPdf(pathPDF + "/" + strMaHoaDon + ".pdf");

    }

    public static void copyFile(File source, File destination) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(source);
             FileOutputStream outputStream = new FileOutputStream(destination);) {
            FileChannel sourceChannel = inputStream.getChannel();
            FileChannel destinationChannel = outputStream.getChannel();
            destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
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
    public void TaoFilePDFA5Null() {
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
