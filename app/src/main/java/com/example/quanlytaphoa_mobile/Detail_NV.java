package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nv);

        Employee selectedEmployee = (Employee) getIntent().getSerializableExtra("selected_employee");
        employeeList = ((EmployeeListWrapper) getIntent().getSerializableExtra("employeeList")).getEmployeeList();
        position = getIntent().getIntExtra("position", 0);

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

        TextView txtTongLuong = findViewById(R.id.edttongluong);
        int tongLuong = selectedEmployee.getHoursWorked() * selectedEmployee.getSalary();
        txtTongLuong.setText(String.valueOf(tongLuong));

        Button btnExitEdit = findViewById(R.id.btnExitEdit);
        btnExitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnDel = findViewById(R.id.btnDel);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Employee selectedEmployee = employeeList.get(position);

                DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference().child("employees").child(selectedEmployee.getId());
                employeeRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        employeeList.remove(position);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updated_employee_list", new EmployeeListWrapper(employeeList));
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Detail_NV.this, "Xóa nhân viên không thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
