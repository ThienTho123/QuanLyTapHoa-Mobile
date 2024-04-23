package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Detail_NV extends AppCompatActivity {
    private List<Employee> employeeList;
    private int position;
    private EmployeeAdapter adapter; // Khai báo biến adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nv);

        Employee selectedEmployee = (Employee) getIntent().getSerializableExtra("selected_employee");
        employeeList = ((EmployeeListWrapper) getIntent().getSerializableExtra("employeeList")).getEmployeeList();
        position = getIntent().getIntExtra("position", 0);

        TextView txtId = findViewById(R.id.edtMaNV);
        txtId.setText(selectedEmployee.getId());

        EditText txtName = findViewById(R.id.edtEditName);
        txtName.setText(selectedEmployee.getName());

        EditText txtChucVu = findViewById(R.id.edtChucVu);
        txtChucVu.setText(selectedEmployee.getChucvu());

        EditText txtSogio = findViewById(R.id.edtsogio);
        txtSogio.setText(String.valueOf(selectedEmployee.getHoursWorked()));

        EditText txtLuong = findViewById(R.id.edtluong);
        txtLuong.setText(String.valueOf(selectedEmployee.getSalary()));

        TextView txtTongLuong = findViewById(R.id.edttongluong);
        int tongLuong = selectedEmployee.getHoursWorked() * selectedEmployee.getSalary();
        txtTongLuong.setText(String.valueOf(tongLuong));

        // Khởi tạo adapter
        adapter = new EmployeeAdapter(this, employeeList);

        Button btnExitEdit = findViewById(R.id.btnExitEdit);
        btnExitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy nhân viên được chọn từ employeeList
                Employee selectedEmployee = employeeList.get(position);

                // Lấy thông tin mới từ các EditText
                String newId = txtId.getText().toString(); // Lấy mã nhân viên mới
                String newName = txtName.getText().toString();
                String newChucVu = txtChucVu.getText().toString();
                int newSogio = Integer.parseInt(txtSogio.getText().toString());
                int newLuong = Integer.parseInt(txtLuong.getText().toString());
                String employeeKey = "employee" + selectedEmployee.getId();


                // Cập nhật thông tin nhân viên trong Firebase
                DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference().child("employees").child(employeeKey);
                Employee updatedEmployee = new Employee(newId, newName, newChucVu, newSogio, newLuong);
                employeeRef.setValue(updatedEmployee).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Hiển thị thông báo sửa thành công (nếu cần)
                        Toast.makeText(Detail_NV.this, "Sửa thông tin nhân viên thành công", Toast.LENGTH_SHORT).show();

                        // Kết thúc activity và quay lại danh sách nhân viên
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý trường hợp sửa thông tin thất bại từ Firebase (nếu cần)
                        Toast.makeText(Detail_NV.this, "Sửa thông tin nhân viên không thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button btnDel = findViewById(R.id.btnDel);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy nhân viên được chọn từ employeeList
                Employee selectedEmployee = employeeList.get(position);

                // Xóa nhân viên khỏi Firebase Database
                DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference().child("employees").child(selectedEmployee.getId());
                employeeRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Nếu xóa thành công từ Firebase, cập nhật ListView và danh sách nhân viên
                        employeeList.remove(position);
                        adapter.notifyDataSetChanged(); // Cập nhật ListView

                        // Tạo một Intent mới để truyền danh sách nhân viên đã cập nhật
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updated_employee_list", new EmployeeListWrapper(employeeList));
                        setResult(RESULT_OK, resultIntent);

                        // Kết thúc activity và quay lại danh sách nhân viên
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý trường hợp xóa thất bại từ Firebase (nếu cần)
                        Toast.makeText(Detail_NV.this, "Xóa nhân viên không thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
