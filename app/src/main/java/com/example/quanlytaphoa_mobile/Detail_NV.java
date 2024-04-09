package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Detail_NV extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nv);

        // Nhận dữ liệu nhân viên đã chọn từ Intent
        Employee selectedEmployee = (Employee) getIntent().getSerializableExtra("selected_employee");

        // Hiển thị thông tin của nhân viên đã chọn trên giao diện
        TextView txtId = findViewById(R.id.edtMaNV);
        txtId.setText(selectedEmployee.getId());

        TextView txtName = findViewById(R.id.edtEditName);
        txtName.setText(selectedEmployee.getName());

        TextView txtChucVu = findViewById(R.id.edtChucVu);
        txtChucVu.setText(selectedEmployee.getChucvu());

        TextView txtSogio = findViewById(R.id.edtsogio);
        txtSogio.setText(String.valueOf(selectedEmployee.getHoursWorked()));

        TextView txtLuong = findViewById(R.id.edtluong);
        txtLuong.setText(String.valueOf(selectedEmployee.getSalary()));

        // Tính tổng lương và hiển thị lên giao diện
        TextView txtTongLuong = findViewById(R.id.edttongluong);
        int tongLuong = selectedEmployee.getHoursWorked() * selectedEmployee.getSalary();
        txtTongLuong.setText(String.valueOf(tongLuong));

        // Lắng nghe sự kiện click của nút "Thoát"
        Button btnExitEdit = findViewById(R.id.btnExitEdit);
        btnExitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại activity_list_nhan_vien.xml
                finish();
            }
        });
    }
}
