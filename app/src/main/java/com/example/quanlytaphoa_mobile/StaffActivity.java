package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

public class StaffActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff);
        String userID = getIntent().getStringExtra("userID");
        Log.d("EmployeeID", "ID nhân viên: " + userID);

        ImageView imageViewStaff = findViewById(R.id.imageView5);

        // Hiển thị PopupMenu khi hình ảnh nhân viên được nhấn
        imageViewStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // Xử lý sự kiện khi nhấn vào nút "Thông tin"
        Button btnTTNV = findViewById(R.id.btn_qlnv);
        btnTTNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang view_detail_nv.xml khi nhấn nút "Thông tin"
                Intent intent = new Intent(StaffActivity.this, ViewDetailNhanVienActivity.class);
                intent.putExtra("userID", userID);

                startActivity(intent);
            }
        });

        // Xử lý sự kiện khi nhấn vào nút "Sản phẩm"
        Button btnQLHHGuest = findViewById(R.id.btn_qlhh);
        btnQLHHGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang activity_list_sanpham_guest.xml khi nhấn nút "Sản phẩm"
                Intent intent = new Intent(StaffActivity.this, listSanPhamActivity_Staff.class);
                intent.putExtra("userID", userID);

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
                    Intent intent = new Intent(StaffActivity.this, MainActivity.class);
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
