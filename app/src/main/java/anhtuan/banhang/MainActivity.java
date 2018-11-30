package anhtuan.banhang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

import anhtuan.banhang.DAO.ListViewMatHangAdapter;
import anhtuan.banhang.DAO.MatHangDAO;
import anhtuan.banhang.DTO.DanhSachMatHang;
import anhtuan.banhang.DTO.MatHangDTO;

public class MainActivity extends AppCompatActivity {
    RadioGroup _grRadio;
    RadioButton _rdoSanPham;
    RadioButton _rdoDay;
    RadioButton _rdoDauDai;
    TextView lblName;
    Button btnLoad;
    Spinner _spin;

    EditText _txtsoLuong;
    EditText _txtdonGia;

    ListView _listV;

    String _strMaLoai = "MSP";
    // Cặp Đối Tượng Cho Spiner
    ArrayList<MatHangDTO> arlMH = new ArrayList<MatHangDTO>();
    ArrayAdapter<MatHangDTO> adtMH = null;

    // Cặp Đối Tượng Cho Listview
    ArrayList<MatHangDTO> arayListView = new ArrayList<MatHangDTO>();
    ListViewMatHangAdapter adapterListView = null;

    MatHangDTO _matHangDTO = new MatHangDTO();
    MatHangDAO _matHangDAO = new MatHangDAO();
    DanhSachMatHang dsMatHang = new DanhSachMatHang();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getControl();
        addEventForm();
    }

    private void getControl() {
        _grRadio = (RadioGroup) findViewById(R.id._groupRdo);
        _rdoSanPham = (RadioButton) findViewById(R.id.radioSanPham);
        _rdoDay = (RadioButton) findViewById(R.id.radioDay);
        _rdoDauDai = (RadioButton) findViewById(R.id.radioDauDai);
        _txtsoLuong = (EditText) findViewById(R.id.txtSoLuong);
        _txtdonGia = (EditText) findViewById(R.id.txtDonGia);
        _listV = (ListView) findViewById(R.id._listviewMatHang);

        _spin = (Spinner) findViewById(R.id.spinnerMatHang);
        lblName = (TextView) findViewById(R.id.lblTenMH);
        btnLoad = (Button) findViewById(R.id.btnLoad);

        // Cấu Hình Cho Spiner
        arlMH = _matHangDAO.getArrMatHang(_strMaLoai);
        // Tao Adapter Gan Data Source Arr vao Adapter
        adtMH = new ArrayAdapter<MatHangDTO>
                (this, android.R.layout.simple_spinner_item, arlMH);
        adtMH.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        _spin.setAdapter(adtMH);

        // Cấu Hình Cho ListView
        arayListView = new ArrayList<MatHangDTO>();

        //Khởi tạo đối tượng adapter và gán Data source
        adapterListView = new ListViewMatHangAdapter(
                this, R.layout.my_intem_layout,
                arayListView/*thiết lập data source*/);
        _listV.setAdapter(adapterListView);//gán Adapter vào Lisview

    }

    private void addEventForm() {

        // Group Radio Checked
        _grRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                groupRadioChangedEvent(group, checkedId);
            }
        });

        // Click Button
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textSL = _txtsoLuong.getText().toString();
                if (textSL.length() == 0){
                    Toast.makeText(MainActivity.this, "Số Lượng Phải > 0 ", Toast.LENGTH_LONG).show();
                    return;
                }
                Float intSoLuong = Float.parseFloat(textSL);
                _matHangDTO.setSoLuong((float)intSoLuong);
                addMatHangForListView();
            }
        });
        // Select Spinner
        _spin.setOnItemSelectedListener(new selectSpinnerEvent());
    }

    /**
     * Hàm thêm một sản phẩm vào cho danh mục được chọn trong Spinner
     */
    private void addMatHangForListView() {
        dsMatHang.addMatHangList(_matHangDTO);
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
        arlMH = _matHangDAO.getArrMatHang(_strMaLoai);
        // Tao Adapter Gan Data Source Arr vao Adapter
        adtMH.notifyDataSetChanged();
        //_spin.setAdapter(adtMH);
    }

    private class selectSpinnerEvent implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            lblName.setText(arlMH.get(position).toString());
            _matHangDTO = arlMH.get(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            lblName.setText("-----");
        }
    }
}
