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

import anhtuan.banhang.DAO.MatHangDAO;
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
    ArrayList<MatHangDTO> arlMH = new  ArrayList<MatHangDTO>();
    ArrayAdapter<MatHangDTO> adtMH =null;

    // Cặp Đối Tượng Cho Listview
    ArrayList<MatHangDTO> arayListView = new  ArrayList<MatHangDTO>();
    ArrayAdapter<MatHangDTO> adapterListView =null;

    MatHangDTO _matHangDTO = new MatHangDTO();
    MatHangDAO _matHangDAO = new MatHangDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getControl();
        addEventForm();
    }

    private void getControl()
    {
        _grRadio = (RadioGroup) findViewById(R.id._groupRdo) ;
        _rdoSanPham = (RadioButton) findViewById(R.id.radioSanPham);
        _rdoDay = (RadioButton) findViewById(R.id.radioDay);
        _rdoDauDai = (RadioButton) findViewById(R.id.radioDauDai);
        _txtsoLuong = (EditText) findViewById(R.id.txtSoLuong);
        _txtdonGia = (EditText) findViewById(R.id.txtDonGia);
        _listV = (ListView) findViewById(R.id._listviewMatHang);

        _spin = (Spinner) findViewById(R.id.spinnerMatHang);
        lblName = (TextView) findViewById(R.id.lblTenMH);
        btnLoad = (Button) findViewById(R.id.btnLoad);

        arlMH = _matHangDAO.getArrMatHang(_strMaLoai);
        // Tao Adapter Gan Data Source Arr vao Adapter
        adtMH = new ArrayAdapter<MatHangDTO>
                (this, android.R.layout.simple_spinner_item, arlMH);

        adtMH.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        _spin.setAdapter(adtMH);


    }

    private void addEventForm(){

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
                Toast.makeText(MainActivity.this, "Số Lượng : " + _txtsoLuong.getText() + " _ Đơn Giá : " + _txtdonGia.getText(), Toast.LENGTH_LONG).show();
            }
        });
        // Select Spinner
        _spin.setOnItemSelectedListener(new selectSpinnerEvent());
    }


    // Khi radio group có thay đổi.
    private void groupRadioChangedEvent(RadioGroup group, int checkedId) {
        int checkedRadioId = group.getCheckedRadioButtonId();

        switch(checkedRadioId){
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
        _spin.setAdapter(adtMH);
    }

    private class selectSpinnerEvent implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            lblName.setText(arlMH.get(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            lblName.setText("-----");
        }
    }
}
