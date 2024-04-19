package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnDangNhap = findViewById(R.id.btndangnhap);
        Button btnXemHangHoa = findViewById(R.id.btnhanghoa);

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang activity_login.xml khi nhấn nút Đăng nhập
                Intent intent = new Intent(MainActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

        btnXemHangHoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang activity_listsanpham_guest.xml khi nhấn nút Xem hàng hóa
                Intent intent = new Intent(MainActivity.this, listSanPhamActivity_Staff.class);
                startActivity(intent);
            }
        });
    }
}
