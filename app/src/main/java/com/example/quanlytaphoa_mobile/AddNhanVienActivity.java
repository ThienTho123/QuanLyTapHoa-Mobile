package com.example.quanlytaphoa_mobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNhanVienActivity extends AppCompatActivity {

    EditText edtId, edtName, edtHoursWorked, edtSalary;
    Spinner spinnerChucVu;
    Button btnAddEmployee;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nhan_vien);

        // Ánh xạ các view từ layout
        edtId = findViewById(R.id.edtAddID);
        edtName = findViewById(R.id.edtAddName);
        spinnerChucVu = findViewById(R.id.spinnerChucVu);
        edtHoursWorked = findViewById(R.id.edtAddsogiolam);
        edtSalary = findViewById(R.id.edtAddluong);
        btnAddEmployee = findViewById(R.id.btnAdd);
        spinnerChucVu = findViewById(R.id.spinnerChucVu);

// Tạo ArrayAdapter từ resource string-array chứa các chức vụ
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.chuc_vu_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Set adapter cho Spinner
        spinnerChucVu.setAdapter(adapter);
        // Tham chiếu đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("employees");

        // Xử lý sự kiện khi nhấn nút Thêm Nhân viên
        btnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployee();
            }
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addEmployee() {
        // Lấy dữ liệu từ EditText và Spinner
        String id = edtId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String chucvu = spinnerChucVu.getSelectedItem().toString();
        String hoursWorkedStr = edtHoursWorked.getText().toString().trim();
        String salaryStr = edtSalary.getText().toString().trim();

        // Kiểm tra xem các trường có rỗng không
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name) || TextUtils.isEmpty(chucvu) ||
                TextUtils.isEmpty(hoursWorkedStr) || TextUtils.isEmpty(salaryStr)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            // Chuyển đổi dữ liệu số giờ làm và lương từ chuỗi sang số nguyên
            int hoursWorked = Integer.parseInt(hoursWorkedStr);
            int salary = Integer.parseInt(salaryStr);

            // Tạo đối tượng Employee mới
            Employee employee = new Employee(id, name, chucvu, hoursWorked, salary);

            // Thêm nhân viên vào Firebase Database
            databaseReference.child("employee" + id).setValue(employee);

            // Hiển thị thông báo
            Toast.makeText(this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
            finish();

            // Xóa nội dung trong EditText sau khi thêm thành công
            edtId.setText("");
            edtName.setText("");
            edtHoursWorked.setText("");
            edtSalary.setText("");
        }
    }
}
