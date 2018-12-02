package anhtuan.banhang;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity; // Loại Bỏ Header Mặc Định
import android.view.Window; // Loại Bỏ Header Mặc Định

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import net.sourceforge.jtds.jdbc.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import java.util.Date;
import java.util.List;

import anhtuan.banhang.DAO.KhachHangDAO;
import anhtuan.banhang.DAO.ListViewMatHangAdapter;
import anhtuan.banhang.DAO.MatHangDAO;
import anhtuan.banhang.DAO.SuLyHoaDonMua;
import anhtuan.banhang.DTO.DanhSachMatHang;
import anhtuan.banhang.DTO.KhachHang;
import anhtuan.banhang.DTO.MatHang;

import static anhtuan.banhang.R.font.times;

public class MainActivity extends Activity {
    RadioGroup _grRadio;
    RadioButton _rdoSanPham;
    RadioButton _rdoDay;
    RadioButton _rdoDauDai;

    TextView lblNoCu;
    TextView lblSoLoai;
    TextView lblSoLuong;
    TextView lblTongTien;

    Button btnLoad;
    Button btnPrint;
    Button btnView;

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
    SuLyHoaDonMua _sulyHoaDon = new SuLyHoaDonMua();

    DanhSachMatHang dsMatHang = new DanhSachMatHang();
    int intSelectSpinMHPosition = 0;

    String pathPDF = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LuuHoaDon";
    String strMaHoaDon = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        // Cấp Quyền Đọc Ghi Cho App
        checkAndRequestPermissions();
        if (!isExternalStorageReadable())
            return;

        strMaHoaDon = taoMoiHoaDon();

        getControl();
        addEventForm();

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
        return _sulyHoaDon.LayMaHoaDonTheoNgay(getDay + getMonth + year + "");
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

        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnPrint = (Button) findViewById(R.id.bntPrint);
        btnView = (Button) findViewById(R.id.bntView);

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
                Toast.makeText(MainActivity.this, "Đã Xóa " + matHang, Toast.LENGTH_LONG).show();
                setThongTinKetQua();
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

                // Set Gia Ban
                String giaBan = _matHangDAO.layGiaBanTheoMHvaKH(_matHang.getMaMatH(), _khachHang.getMaKH());
                if (giaBan == "" && _matHang.getDonGia() != null)
                    giaBan = (_matHang.getDonGia() + 4) + "";
                _txtdonGia.setText(giaBan);
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
                // Set Gia Ban
                String giaBan = _matHangDAO.layGiaBanTheoMHvaKH(_matHang.getMaMatH(), _khachHang.getMaKH());
                if (giaBan == "" && _matHang.getDonGia() != null)
                    giaBan = (_matHang.getDonGia() + 4) + "";
                _txtdonGia.setText(giaBan);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //
        // Click Button Add
        //
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _matHang = arrayMH.get(intSelectSpinMHPosition);
                String textSL = _txtsoLuong.getText().toString();
                String textGia = _txtdonGia.getText().toString();
                if (textSL.length() == 0 || textGia.length() == 0) {
                    Toast.makeText(MainActivity.this, "Số Lượng, Đơn Giá Phải > 0 ", Toast.LENGTH_LONG).show();
                    return;
                }
                Integer intSoLuong = Integer.parseInt(textSL);
                if (intSoLuong > _matHangDAO.getMatHangByID(_matHang.getMaMatH()).getSoLuong()) {
                    Toast.makeText(MainActivity.this, "Số Lượng Không Đủ", Toast.LENGTH_LONG).show();
                    return;
                }
                Float fGiaban = Float.parseFloat(textGia);
                if (fGiaban <= 0) {
                    Toast.makeText(MainActivity.this, "Giá Bán Phải > 0", Toast.LENGTH_LONG).show();
                    return;
                }

                _matHang.setSoLuong(intSoLuong);
                _matHang.setDonGia(fGiaban);
                addMatHangForListView();
                setThongTinKetQua();
            }
        });

        //
        //Button Print On Click
        //
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XuatHoaDonPDF();
            }
        });

        //
        // Button View PDF Click
        //
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdf();
            }
        });
    }

    /**
     * Hàm Set Thông Tin Kết Quả
     */
    private void setThongTinKetQua() {
        lblSoLoai.setText(arayListView.size() + " Loại");
        lblSoLuong.setText("SL:" + dsMatHang.getSoLuongMatHang());
        lblTongTien.setText("+$:" + dsMatHang.getTongTienList());
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
    private void XuatHoaDonPDF() {
        // Update Nợ Cũ
        double dbNoCu = Double.parseDouble(_khachHang.getNoCu());
        double dbTraTien = 0;
        if (_txtTraTien.getText().length() > 0)
            dbTraTien = Double.parseDouble(_txtTraTien.getText() + "");
        String strTraTien = dbTraTien - (int) dbTraTien > 0 ? dbTraTien + "" : (int) dbTraTien + "";

        double dbTongTienBan = dsMatHang.getTongTienList();
        String strTongTienBan = dbTongTienBan - (int) dbTongTienBan > 0 ? dbTongTienBan + "" : (int) dbTongTienBan + "";

        double dbTienConLai = dbTongTienBan + dbNoCu - dbTraTien;
        String strTienConLai = dbTienConLai - (int) dbTienConLai > 0 ? dbTienConLai + "" : (int) dbTienConLai + "";


        //Get Date Now
        Date date = new Date();
        SimpleDateFormat frmDate = new SimpleDateFormat("dd/MM/yyyy");
        String ngayBan = frmDate.format(date);

        String strFONT = "/res/font/times.ttf";
        Font font16 = new Font(Font.FontFamily.TIMES_ROMAN, 16);
        Font font22 = new Font(Font.FontFamily.TIMES_ROMAN, 22);
        try {
            BaseFont timesFont = BaseFont.createFont(strFONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            font16 = new Font(timesFont, 16);
            font22 = new Font(timesFont, 22);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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

        cellTitle = new PdfPCell(new Paragraph("Người Bán  : Anh Tuấn (0915458345)" + "\nKhách Hàng : " + _khachHang.getTenKH() + "\n", font16));
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
        try {
            pdfTable.setWidths(widths);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        pdfTable.setWidthPercentage(95);
        pdfTable.getDefaultCell().setPadding(3);
        pdfTable.getDefaultCell().setBorder(0);
        pdfTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        //
        //Adding Header row
        //
        PdfPCell cellHeader1 = new PdfPCell(new Phrase("Mã MH", font16));
        cellHeader1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cellHeader1.setHorizontalAlignment(1);
        cellHeader1.setFixedHeight(20f);
        pdfTable.addCell(cellHeader1);

        PdfPCell cellHeader2 = new PdfPCell(new Phrase("Tên Mặt Hàng", font16));
        cellHeader2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cellHeader2.setHorizontalAlignment(1);
        cellHeader2.setFixedHeight(20f);
        pdfTable.addCell(cellHeader2);

        PdfPCell cellHeader3 = new PdfPCell(new Phrase("Số Lượng", font16));
        cellHeader3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cellHeader3.setHorizontalAlignment(1);
        cellHeader3.setFixedHeight(20f);
        pdfTable.addCell(cellHeader3);

        PdfPCell cellHeader4 = new PdfPCell(new Phrase("Đơn Giá", font16));
        cellHeader4.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cellHeader4.setHorizontalAlignment(1);
        cellHeader4.setFixedHeight(20f);
        pdfTable.addCell(cellHeader4);

        PdfPCell cellHeader5 = new PdfPCell(new Phrase("Tổng Tiền", font16));
        cellHeader5.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cellHeader5.setHorizontalAlignment(1);
        cellHeader5.setFixedHeight(20f);
        pdfTable.addCell(cellHeader5);


        int maSP = 0;
        //Adding ListView Row
        for (MatHang mh : arayListView) {
            for (int i = 1; i <= 5; i++) {
                PdfPCell _cellPDF;
                switch (i) {
                    case 1: // Lấy Mã Sản Phẩm
                        _cellPDF = new PdfPCell(new Phrase(mh.getMaMatH().substring(3), font16));
                        _cellPDF.setFixedHeight(20f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 2:  // Lấy Tên Sản Phẩm
                        _cellPDF = new PdfPCell(new Phrase(mh.getTenMatH(), font16));
                        _cellPDF.setFixedHeight(20f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 3: // Lấy Số Lượng
                        _cellPDF = new PdfPCell(new Phrase((new DecimalFormat("##")).format(mh.getSoLuong()), font16));
                        _cellPDF.setFixedHeight(20f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 4:  // Lấy Đơn Giá
                        double dbDonGia = mh.getDonGia();
                        _cellPDF = new PdfPCell(new Phrase(dbDonGia - (int) dbDonGia > 0 ? dbDonGia + "" : (int) dbDonGia + "", font16));
                        _cellPDF.setFixedHeight(20f);
                        _cellPDF.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(_cellPDF);
                        break;
                    case 5: // Thành Tiền
                        double thanhTien = mh.getSoLuong() * mh.getDonGia();
                        _cellPDF = new PdfPCell(new Phrase(thanhTien - (int) thanhTien > 0 ? thanhTien + "" : (int) thanhTien + "", font16));
                        _cellPDF.setFixedHeight(20f);
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
        try {
            pdfTableTongTien.setWidths(widthsTT);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        pdfTableTongTien.setWidthPercentage(95);
        pdfTableTongTien.getDefaultCell().setBorder(0);

        // Add Cell Tính Tiền
        pdfTableTongTien.addCell(new Phrase(lblSoLoai.getText().toString(), font16));
        pdfTableTongTien.addCell(new Phrase(""));
        pdfTableTongTien.addCell(new Phrase(lblSoLuong.getText().toString(), font16));

        if (dbNoCu > 0) {
            PdfPCell _cellPDFTT = new PdfPCell(new Phrase("\nTổng Toa : \nNợ Cũ      : \nTổng Tiền: \nTrả Tiền   : \n------------- \nCòn          :", font16));
            _cellPDFTT.setHorizontalAlignment(Element.ALIGN_RIGHT);
            _cellPDFTT.setBorder(0);
            pdfTableTongTien.addCell(_cellPDFTT);
            pdfTableTongTien.addCell(new Phrase("\n" + strTongTienBan + "\n" + dbNoCu + "\n" + (dbTongTienBan + dbNoCu) + "\n" + strTraTien + "\n\n" + strTienConLai, font16));
        } else {
            PdfPCell _cellPDFTT = new PdfPCell(new Phrase("\nTổng Toa : \nTrả           : \n------------- \nCòn          :", font16));
            _cellPDFTT.setHorizontalAlignment(Element.ALIGN_RIGHT);
            _cellPDFTT.setBorder(0);
            pdfTableTongTien.addCell(_cellPDFTT);
            pdfTableTongTien.addCell(new Phrase("\n" + strTongTienBan + "\n" + strTraTien + "\n\n" + strTienConLai, font16));
        }

        //
        //Exporting to PDF
        //
        String pathLuu = pathPDF;

        File folderPath = new File(pathLuu);
        if (!folderPath.exists())
            folderPath.mkdirs();

        File pdfHoaDon = new File(folderPath, strMaHoaDon + ".pdf");
        // Kiểm Tra Tồn Tại
        if (pdfHoaDon.exists()) {
            try {
                pdfHoaDon.delete(); // Xóa
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, "Xóa Hóa Đơn Tạo Lại.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Document pdfDoc = new Document(PageSize.A5, 10f, 10f, 15f, 30f);
        pdfDoc.setPageSize(PageSize.LETTER);  // A5 Dọc


        try {
            PdfWriter.getInstance(pdfDoc, new FileOutputStream(pdfHoaDon));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pdfDoc.open();

        pdfDoc.addAuthor("Anh Tuan");
        try {
            pdfDoc.add(pdfTableTitle);
            pdfDoc.add(pdfTable);
            pdfDoc.add(pdfTableTongTien);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        pdfDoc.close();

        Toast.makeText(getApplicationContext(), "Created...Ok", Toast.LENGTH_LONG).show();
        viewPdf(strMaHoaDon + ".pdf");

    }


    // Method for opening a pdf file
    private void viewPdf(String file) {
        File pdfFile = new File(pathPDF + "/" + file);
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

    // Click Xem Lại PDF Hóa Đơn
    private void openPdf() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(pathPDF, dsMatHang.getTongTienList() + ".pdf");
        if (!file.exists())
            Toast.makeText(this, "Không Tồn Tại File \n" + file, Toast.LENGTH_SHORT).show();
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        startActivity(intent);
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

}
