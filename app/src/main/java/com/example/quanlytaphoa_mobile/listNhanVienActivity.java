package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listNhanVienActivity extends AppCompatActivity {

    private ListView listView;
    private List<Employee> employeeList;
    private EmployeeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nhan_vien);

        listView = findViewById(R.id.lvNhanVien);
        employeeList = new ArrayList<>();
        adapter = new EmployeeAdapter(this, employeeList);
        listView.setAdapter(adapter);

        // Tham chiếu đến node "employees" trên Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("employees");

        // Thêm listener để lắng nghe sự thay đổi trong dữ liệu
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa dữ liệu cũ
                employeeList.clear();

                // Lặp qua từng child node trong "employees"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy dữ liệu của mỗi nhân viên và thêm vào danh sách
                    Employee employee = snapshot.getValue(Employee.class);
                    if (employee != null) {
                        employeeList.add(employee);
                    }
                }

                // Cập nhật giao diện
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Toast.makeText(listNhanVienActivity.this, "Không thể đọc dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện nhấn vào một nhân viên trong ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy thông tin nhân viên đã chọn
                Employee selectedEmployee = employeeList.get(position);

                // Chuyển đến hoạt động Detail_NV và chuyển thông tin của nhân viên đã chọn
                Intent intent = new Intent(listNhanVienActivity.this, Detail_NV.class);
                intent.putExtra("selected_employee", selectedEmployee);
                startActivity(intent);
            }
        });
    }
}
