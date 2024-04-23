package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        Button menuButton = findViewById(R.id.menu); // Thay ImageView bằng Button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });

        // Lấy ID của nhân viên từ Intent
        String userID = getIntent().getStringExtra("userID");
        Log.d("EmployeeID", "ID truyền vào: " + userID); // In ra giá trị của ID để kiểm tra

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

                    // Load ảnh từ Firebase Storage vào ImageView
                    String imageUrl = dataSnapshot.child("picture").getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        new LoadImageTask().execute(imageUrl);
                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_sanpham_menu, menu);
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
        popupMenu.inflate(R.menu.nhanvien_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_return) {
                    Intent intentAddProduct = new Intent(ViewDetailNhanVienActivity.this, StaffActivity.class);
                    intentAddProduct.putExtra("userID", getIntent().getStringExtra("userID"));
                    startActivity(intentAddProduct);
                    startActivity(intentAddProduct);
                    return true;
                } else if (item.getItemId() == R.id.action_logout) {
                    Intent intentLogout = new Intent(ViewDetailNhanVienActivity.this, MainActivity.class);
                    intentLogout.putExtra("userID", getIntent().getStringExtra("userID"));
                    startActivity(intentLogout);
                    startActivity(intentLogout);
                    finish();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
