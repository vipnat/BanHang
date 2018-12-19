package anhtuan.banhang;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import anhtuan.banhang.DAO.HoaDonXuatDAO;
import anhtuan.banhang.DAO.NhanVienDAO;
import anhtuan.banhang.DTO.NhanVien;
import anhtuan.banhang.Database.ConnectionDB;

public class DangNhapActivity extends AppCompatActivity {

    private ImageView btnLogin;
    private ImageView imgLogin;
    private ImageView imgBgLogin;

    Spinner _spinNhanVien;

    // Cặp Đối Tượng Cho Spiner Khách Hàng
    ArrayList<NhanVien> arrayNhanVien = new ArrayList<NhanVien>();
    ArrayAdapter<NhanVien> adapterNhanVien = null;

    NhanVienDAO _nhanVienDao = new NhanVienDAO();
    public static NhanVien nhanVien = new NhanVien();
    ConnectionDB connec = new ConnectionDB();

    public static final String KEY_DATA = "key_data";
    private Context context;
    HoaDonXuatDAO hoaDonXuatDAO = new HoaDonXuatDAO();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_dang_nhap);
        if (!connec.checkConnecDatabase()) {
            closeFormDataNull();
            return;
        }
        _spinNhanVien = (Spinner) findViewById(R.id.spnNhanVien);
        // Cấu Hình Cho Spiner Khách Hàng
        arrayNhanVien = _nhanVienDao.getArrNhanVien();
        adapterNhanVien = new ArrayAdapter<NhanVien>(this, android.R.layout.simple_spinner_item, arrayNhanVien);
        adapterNhanVien.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinNhanVien.setAdapter(adapterNhanVien);

        imgBgLogin = (ImageView) findViewById(R.id.imageViewLogin);
        imgBgLogin.setBackgroundResource(R.drawable.image_nen_login);

        imgLogin = (ImageView) findViewById(R.id.imageLogin);
        imgLogin.setBackgroundResource(R.drawable.image_login);

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

        _spinNhanVien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nhanVien = arrayNhanVien.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
}
