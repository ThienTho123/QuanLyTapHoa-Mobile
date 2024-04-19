package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class loginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("account");

        // Initialize views
        usernameEditText = findViewById(R.id.edtUsername);
        passwordEditText = findViewById(R.id.edtsword);
        loginButton = findViewById(R.id.btnLogin);

        // Handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển về activity_main.xml khi nhấn nút "Quay lại"
                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy giá trị username và password từ dataSnapshot
                    Object storedUsernameObject = snapshot.child("username").getValue();
                    Object storedPasswordObject = snapshot.child("password").getValue();
                    Object storedRollObject = snapshot.child("roll").getValue();
                    String storedId = snapshot.child("id").getValue(String.class);

                    if (storedUsernameObject != null && storedPasswordObject != null && storedRollObject != null) {
                        // Chuyển đổi giá trị sang kiểu String nếu là kiểu Long
                        String storedUsername = storedUsernameObject instanceof Long ? String.valueOf(storedUsernameObject) : (String) storedUsernameObject;
                        String storedPassword = storedPasswordObject instanceof Long ? String.valueOf(storedPasswordObject) : (String) storedPasswordObject;
                        String storedRoll = storedRollObject instanceof Long ? String.valueOf(storedRollObject) : (String) storedRollObject;
                        // Kiểm tra đăng nhập
                        if (storedUsername.equals(username) && storedPassword.equals(password)) {
                            userFound = true;
                            // Kiểm tra quyền của tài khoản và chuyển hướng đến hoạt động tương ứng
                            if (storedRoll.equals("admin")) {
                                // Chuyển hướng sang ListNhanVienActivity
                                Intent intent = new Intent(loginActivity.this, AdminActivity.class);
                                startActivity(intent);
                            } else if (storedRoll.equals("nhanvien")) {
                                // Chuyển hướng sang ViewDetailNhanVienActivity và truyền ID của tài khoản
                                Intent intent = new Intent(loginActivity.this, StaffActivity.class);
                                // Truyền ID của tài khoản
                                intent.putExtra("userID", storedId);
                                Log.d("UserID", "userID truyền vào: " + storedId);

                                // snapshot.getKey() sẽ trả về ID của tài khoản trong Firebase
                                startActivity(intent);
                            }
                            finish(); // Đóng LoginActivity nếu bạn không muốn quay lại nó sau khi đăng nhập
                            break;
                        }
                    }
                }
                if (!userFound) {
                    Toast.makeText(loginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(loginActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
