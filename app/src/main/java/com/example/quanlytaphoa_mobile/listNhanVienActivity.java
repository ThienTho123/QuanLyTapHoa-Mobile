package com.example.quanlytaphoa_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nhan_vien);

        listView = findViewById(R.id.lvNhanVien);
        employeeList = new ArrayList<>();
        adapter = new EmployeeAdapter(this, employeeList);
        listView.setAdapter(adapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("employees");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employeeList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Employee employee = snapshot.getValue(Employee.class);
                    if (employee != null) {
                        employeeList.add(employee);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(listNhanVienActivity.this, "Không thể đọc dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Employee selectedEmployee = employeeList.get(position);

                Intent intent = new Intent(listNhanVienActivity.this, Detail_NV.class);
                intent.putExtra("selected_employee", selectedEmployee);
                intent.putExtra("employeeList", new EmployeeListWrapper(employeeList));
                intent.putExtra("position", position);
                startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);
            }
        });

        Button btnAddEmployee = findViewById(R.id.btnAddEmployee);
        btnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(listNhanVienActivity.this, AddNhanVienActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAIL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            EmployeeListWrapper wrapper = (EmployeeListWrapper) data.getSerializableExtra("updated_employee_list");
            List<Employee> updatedEmployeeList = wrapper.getEmployeeList();

            employeeList.clear();
            employeeList.addAll(updatedEmployeeList);
            adapter.notifyDataSetChanged();
        }
    }
}
