package com.example.quanlytaphoa_mobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewDetailNhanVienActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView txtIdView, txtNameView, txtChucVuView, txtSoGioLamView, txtLuongView, txtTongLuong;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_detail_nv);

        // Khởi tạo database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("employees");

        // Khởi tạo views
        imageView = findViewById(R.id.imgnvview);
        txtIdView = findViewById(R.id.txtIdview);
        txtNameView = findViewById(R.id.txtNameView);
        txtChucVuView = findViewById(R.id.txtchucvuView);
        txtSoGioLamView = findViewById(R.id.txtsogiolamView);
        txtLuongView = findViewById(R.id.txtluongView);
        txtTongLuong = findViewById(R.id.txtTongluong);

        // Lấy ID của tài khoản từ Intent
        String userID = getIntent().getStringExtra("userID");
        Log.d("UserID", "ID truyền vào: " + userID); // In ra giá trị của ID để kiểm tra


        // Truy vấn dữ liệu từ Firebase
        databaseReference.child("employee" + userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra xem dữ liệu có tồn tại không
                if (dataSnapshot.exists()) {
                    // Lấy thông tin từ dataSnapshot và hiển thị lên các TextView tương ứng
                    String id = dataSnapshot.child("id").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String chucVu = dataSnapshot.child("chucvu").getValue(String.class);
                    int soGioLam = dataSnapshot.child("hoursWorked").getValue(Integer.class);
                    int luong = dataSnapshot.child("salary").getValue(Integer.class);

                    // Tính toán tổng lương
                    int tongLuong = soGioLam * luong;

                    // Hiển thị thông tin lên giao diện
                    txtIdView.setText("Mã nhân viên: " + id);
                    txtNameView.setText("Tên nhân viên: " + name);
                    txtChucVuView.setText("Chức vụ: " + chucVu);
                    txtSoGioLamView.setText("Số giờ làm việc: " + soGioLam);
                    txtLuongView.setText("Lương: " + luong);
                    txtTongLuong.setText("Tổng lương: " + tongLuong);
                } else {
                    Toast.makeText(ViewDetailNhanVienActivity.this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewDetailNhanVienActivity.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
