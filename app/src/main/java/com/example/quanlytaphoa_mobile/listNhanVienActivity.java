package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
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
    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nhan_vien);

        listView = findViewById(R.id.lvNhanVien);
        employeeList = new ArrayList<>();
        adapter = new EmployeeAdapter(this, employeeList);
        listView.setAdapter(adapter);

        Button menuButton = findViewById(R.id.menu);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });

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
                intent.putExtra("image_url", selectedEmployee.getPicture()); // Thêm dòng này để chuyển URL hình ảnh

                startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_nhan_vien_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu) {
            showPopupMenu();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.menu));
        popupMenu.inflate(R.menu.list_nhan_vien_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_add) {
                    Intent intentAddEmployee = new Intent(listNhanVienActivity.this, AddNhanVienActivity.class);
                    startActivity(intentAddEmployee);
                    return true;
                }
                if (item.getItemId() == R.id.action_return) {
                    Intent intentAddProduct = new Intent(listNhanVienActivity.this, AdminActivity.class);
                    startActivity(intentAddProduct);
                    return true;
                }
                else if (item.getItemId() == R.id.action_logout) {
                    Intent intentLogout = new Intent(listNhanVienActivity.this, MainActivity.class);
                    startActivity(intentLogout);
                    finish();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }
}
