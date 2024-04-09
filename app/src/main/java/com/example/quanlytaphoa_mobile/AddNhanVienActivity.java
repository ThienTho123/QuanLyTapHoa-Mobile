package com.example.quanlytaphoa_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNhanVienActivity extends AppCompatActivity {

    EditText edtId, edtName, edtChucVu, edtHoursWorked, edtSalary;
    Button btnAddEmployee;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nhan_vien);

        // Ánh xạ các view từ layout
        edtId = findViewById(R.id.edtAddID);
        edtName = findViewById(R.id.edtAddName);
        edtChucVu = findViewById(R.id.edtAddChucVu);
        edtHoursWorked = findViewById(R.id.edtAddsogiolam);
        edtSalary = findViewById(R.id.edtAddluong);
        btnAddEmployee = findViewById(R.id.btnAdd);

        // Tham chiếu đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("employees");

        // Xử lý sự kiện khi nhấn nút Thêm Nhân viên
        btnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployee();
            }
        });
    }

    private void addEmployee() {
        // Lấy dữ liệu từ EditText
        String id = edtId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String chucvu = edtChucVu.getText().toString().trim();
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

            // Xóa nội dung trong EditText sau khi thêm thành công
            edtId.setText("");
            edtName.setText("");
            edtChucVu.setText("");
            edtHoursWorked.setText("");
            edtSalary.setText("");
        }
    }
}
