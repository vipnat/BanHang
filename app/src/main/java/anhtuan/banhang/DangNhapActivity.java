package anhtuan.banhang;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anhtuan.banhang.DAO.HoaDonXuatDAO;
import anhtuan.banhang.DAO.KhachHangDAO;
import anhtuan.banhang.DAO.NhanVienDAO;
import anhtuan.banhang.DTO.HoaDonXuat;
import anhtuan.banhang.DTO.NhanVien;
import anhtuan.banhang.Database.ConnectionDB;

public class DangNhapActivity extends AppCompatActivity {

    String pathPDF = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LuuHoaDon";
    String path_a5_clear = pathPDF + "/a5_clear.pdf";
    String path_a4_print = pathPDF + "/a4_print.pdf";
    private ImageView btnLogin;
    private ImageView imgLogin;
    private ImageButton btnPrint;
    private ImageView imgBgLogin;

    Spinner _spinNhanVien;
    Spinner _spinHDX;
    TextView _txtTenKh;

    // Cặp Đối Tượng Cho Spiner Khách Hàng
    ArrayList<NhanVien> arrayNhanVien = new ArrayList<NhanVien>();
    ArrayAdapter<NhanVien> adapterNhanVien = null;

    // Cặp Đối Tượng Cho Spiner Hóa Đơn Xuất
    ArrayList<HoaDonXuat> arrayHoaDonXuat = new ArrayList<HoaDonXuat>();
    ArrayAdapter<HoaDonXuat> adapterHoaDonXuat = null;
    HoaDonXuatDAO _hoaDonXuatDao = new HoaDonXuatDAO();
    public static HoaDonXuat hoadonxuat = new HoaDonXuat();

    NhanVienDAO _nhanVienDao = new NhanVienDAO();
    public static NhanVien nhanVien = new NhanVien();


    ConnectionDB connec = new ConnectionDB();
    public static final String KEY_DATA = "key_data";
    private Context context;
    HoaDonXuatDAO hoaDonXuatDAO = new HoaDonXuatDAO();
    KhachHangDAO _khachHangDAO = new KhachHangDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_dang_nhap);
        if (!connec.checkConnecDatabase()) {
            closeFormDataNull();
            return;
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();  // Đọc Ghi PDF
        StrictMode.setVmPolicy(builder.build());  // Đọc Ghi PDF

        // ? Cấp Quyền Đọc Ghi Cho App
        checkAndRequestPermissions();
        if (!isExternalStorageReadable())
            return;


        _spinNhanVien = (Spinner) findViewById(R.id.spnNhanVien);
        // Cấu Hình Cho Spiner Khách Hàng
        arrayNhanVien = _nhanVienDao.getArrNhanVien();
        adapterNhanVien = new ArrayAdapter<NhanVien>(this, android.R.layout.simple_spinner_item, arrayNhanVien);
        adapterNhanVien.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinNhanVien.setAdapter(adapterNhanVien);

        _spinHDX = (Spinner) findViewById(R.id.spnHoaDonXuat);
        // Cấu Hình Cho Spiner Khách Hàng
        arrayHoaDonXuat = _hoaDonXuatDao.arrHoaDonXuat();
        adapterHoaDonXuat = new ArrayAdapter<HoaDonXuat>(this, android.R.layout.simple_spinner_item, arrayHoaDonXuat);
        adapterHoaDonXuat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinHDX.setAdapter(adapterHoaDonXuat);

        _txtTenKh = (TextView) findViewById(R.id.txtTenKH);

        imgBgLogin = (ImageView) findViewById(R.id.imageViewLogin);
        imgBgLogin.setBackgroundResource(R.drawable.image_nen_login);

        imgLogin = (ImageView) findViewById(R.id.imageLogin);
        imgLogin.setBackgroundResource(R.drawable.image_login);

        btnPrint = (ImageButton) findViewById(R.id.btn_print);
        //
        // Button View PDF Click
        //
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hoaDonXuatDAO.XoaAllHoaDonXuatNull();
                String filePathHoaDon = pathPDF + "/" + hoadonxuat.getMaHD() + ".pdf";
                // Tạo Thư Mục Chứa Nếu Chưa Có
                File folderPath = new File(pathPDF);
                if (!folderPath.exists())
                    folderPath.mkdirs();
                // Xuất File PDF Tư Database
                hoaDonXuatDAO.XemLaiHoaDonTheoMaHD(filePathHoaDon);
                try {
                    createA4PdfPrint(filePathHoaDon);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        btnLogin = (ImageView) findViewById(R.id.btnLogin);
        btnLogin.setBackgroundResource(R.drawable.image_button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, XuatHoaDonActivity.class);
                intent.putExtra(KEY_DATA, nhanVien.getMaNhanVien().toString());
                startActivity(intent);
            }
        });

        if (!isConnected()) {
            Toast.makeText(this, "Không Kết Nối INTERNET !", Toast.LENGTH_SHORT).show();
            closeFormDataNull();
        }

        // Select Nhân Viên
        _spinNhanVien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nhanVien = arrayNhanVien.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Select Hóa Đơn Xuất
        _spinHDX.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hoadonxuat = arrayHoaDonXuat.get(position);
                _txtTenKh.setText(_khachHangDAO.LayKhachHangTheoMaKH(hoadonxuat.getMaKH()).getTenKH());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void createA4PdfPrint(String strA5)
            throws IOException, DocumentException {
        File fileA4 = new File(path_a4_print);
        if (fileA4.exists())
            fileA4.delete();
        // Creating a reader A5
        PdfReader reader = new PdfReader(strA5);

        XuatHoaDonActivity.TaoFilePDFA5Null();
        // Creating a reader A5 Null
        PdfReader readerNull = new PdfReader(path_a5_clear);

        // step 1
        Document document = new Document(PageSize.A4.rotate());
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path_a4_print));
        DangNhapActivity.Rotate rotation = new DangNhapActivity.Rotate(); // Xoay Ngang
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

    private void closeFormDataNull() {
        if (!connec.checkConnecDatabase())
            Toast.makeText(this, "Không Kết Nối Dữ Liệu !", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
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
