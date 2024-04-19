package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);

        ImageView imageViewAdmin = findViewById(R.id.imageView5);

        // Hiển thị PopupMenu khi hình ảnh admin được nhấn
        imageViewAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // Xử lý sự kiện khi nhấn vào nút "Nhân viên"
        Button btnQLNV = findViewById(R.id.btn_qlnv);
        btnQLNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang activity_list_nhan_vien.xml khi nhấn nút "Nhân viên"
                Intent intent = new Intent(AdminActivity.this, listNhanVienActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện khi nhấn vào nút "Sản phẩm"
        Button btnQLHH = findViewById(R.id.btn_qlhh);
        btnQLHH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang activity_list_sanpham.xml khi nhấn nút "Sản phẩm"
                Intent intent = new Intent(AdminActivity.this, listSanPhamActivity.class);
                startActivity(intent);
            }
        });
    }

    // Hiển thị PopupMenu
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.logout_menu); // Sử dụng menu đăng xuất

        // Xử lý sự kiện khi người dùng chọn mục trong PopupMenu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_logout) {
                    // Xử lý đăng xuất ở đây
                    // Chuyển về activity_main.xml khi đăng xuất
                    Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Kết thúc activity hiện tại
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }
}
