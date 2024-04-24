package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddNhanVienActivity extends AppCompatActivity {

    EditText edtId, edtName, edtHoursWorked, edtSalary;
    Spinner spinnerChucVu;
    Button btnAddEmployee;
    ImageView imageView;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    DatabaseReference employeeCountRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // Thêm biến imageUri ở mức lớp
    private int employeeCount = 0; // Biến để lưu số lượng sản phẩm hiện tại
    private static final String DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/quanlytaphoa-mobile.appspot.com/o/images%2Femployee.png?alt=media&token=0f36221a-5e0c-4620-b935-eda560fa9e76";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nhan_vien);

        // Ánh xạ các view từ layout
        edtId = findViewById(R.id.edtAddID);
        edtName = findViewById(R.id.edtAddName);
        spinnerChucVu = findViewById(R.id.spinnerChucVu);
        edtHoursWorked = findViewById(R.id.edtAddsogiolam);
        edtSalary = findViewById(R.id.edtAddluong);
        btnAddEmployee = findViewById(R.id.btnAdd);
        spinnerChucVu = findViewById(R.id.spinnerChucVu);
        imageView = findViewById(R.id.imgAddNv);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("employees");
        // Tham chiếu đến Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();
        // Tham chiếu đến node chứa số lượng sản phẩm
        employeeCountRef = FirebaseDatabase.getInstance().getReference().child("employee_count");
        employeeCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Nếu node tồn tại, cập nhật giá trị của biến productCount
                    employeeCount = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase Database
                Toast.makeText(AddNhanVienActivity.this, "Lỗi khi đọc dữ liệu từ Firebase Database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Tạo ArrayAdapter từ resource string-array chứa các chức vụ
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.chuc_vu_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Set adapter cho Spinner
        spinnerChucVu.setAdapter(adapter);
        // Tham chiếu đến Firebase Database

        // Xử lý sự kiện khi nhấn nút Thêm Nhân viên
        btnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployee();
            }
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh nhân viên"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Lấy URI của ảnh được chọn từ Intent
            imageUri = data.getData();

            // Hiển thị ảnh được chọn trong ImageView
            imageView.setImageURI(imageUri);
        }
    }
    private void addEmployee() {
        // Lấy dữ liệu từ EditText và Spinner
        String id = edtId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String chucvu = spinnerChucVu.getSelectedItem().toString();
        String hoursWorkedStr = edtHoursWorked.getText().toString().trim();
        String salaryStr = edtSalary.getText().toString().trim();

        // Kiểm tra xem các trường có rỗng không
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name) || TextUtils.isEmpty(chucvu) ||
                TextUtils.isEmpty(hoursWorkedStr) || TextUtils.isEmpty(salaryStr)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Mã nhân viên đã tồn tại
                        Toast.makeText(AddNhanVienActivity.this, "Mã nhân viên đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        // Mã nhân viên chưa tồn tại, tiến hành thêm nhân viên mới
                        // Chuyển đổi dữ liệu số giờ làm và lương từ chuỗi sang số nguyên
                        int hoursWorked = Integer.parseInt(hoursWorkedStr);
                        int salary = Integer.parseInt(salaryStr);

                        StorageReference imageRef;
                        if (imageUri == null) {
                            // Nếu người dùng không chọn ảnh, sử dụng ảnh mặc định
                            imageRef = storageReference.child("images/employee_" + id + name + ".jpg");
                            // Sử dụng URL mặc định
                            Employee employee = new Employee(id, name, chucvu, hoursWorked, salary, DEFAULT_IMAGE_URL);
                            // Thêm sản phẩm vào Firebase Database
                            databaseReference.child(id).setValue(employee);
                            // Cập nhật số lượng sản phẩm
                            employeeCountRef.setValue(employeeCount);
                            createAndSaveUserAccount(id, name, chucvu);
                            // Hiển thị thông báo
                            Toast.makeText(AddNhanVienActivity.this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                            // Chuyển về lại trang listSanPhamActivity
                            Intent intent = new Intent(AddNhanVienActivity.this, listNhanVienActivity.class);
                            startActivity(intent);
                            finish();
                            // Xóa nội dung trong EditText sau khi thêm thành công
                            edtId.setText("");
                            edtName.setText("");
                            edtHoursWorked.setText("");
                            edtSalary.setText("");
                        } else {
                            // Ngược lại, nếu người dùng chọn ảnh, tải ảnh lên Firebase Storage
                            imageRef = storageReference.child("images/" + id + name + ".jpg");
                            imageRef.putFile(imageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Nếu tải lên thành công, lấy URL của ảnh
                                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    // Lấy URL thành công, lưu URL vào Firebase Database
                                                    String imageURL = uri.toString();

                                                    // Tạo đối tượng Product mới với URL ảnh
                                                    Employee employee = new Employee(id, name, chucvu, hoursWorked, salary, imageURL);
                                                    // Thêm sản phẩm vào Firebase Database
                                                    databaseReference.child(id).setValue(employee);
                                                    // Cập nhật số lượng sản phẩm
                                                    employeeCountRef.setValue(employeeCount);

                                                    createAndSaveUserAccount(id, name, chucvu);

                                                    // Hiển thị thông báo
                                                    Toast.makeText(AddNhanVienActivity.this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                                                    // Chuyển về lại trang listSanPhamActivity
                                                    Intent intent = new Intent(AddNhanVienActivity.this, listNhanVienActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                    // Xóa nội dung trong EditText sau khi thêm thành công
                                                    edtId.setText("");
                                                    edtName.setText("");
                                                    edtHoursWorked.setText("");
                                                    edtSalary.setText("");
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Xử lý khi có lỗi xảy ra trong quá trình tải ảnh lên
                                            Toast.makeText(AddNhanVienActivity.this, "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        // Tiến hành thêm nhân viên mới
                        // Code thêm nhân viên vào Firebase tại đây
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase Database
                    Toast.makeText(AddNhanVienActivity.this, "Lỗi khi đọc dữ liệu từ Firebase Database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    private void createAndSaveUserAccount(String id, String name, String chucvu) {
        String roll = chucvu.equalsIgnoreCase("Quản lí") ? "admin" : "nhanvien";
        String username = name.toLowerCase().replaceAll("\\s+", ""); // Tạo username từ tên nhân viên (viết liền không dấu)
        String password = name.toLowerCase().replaceAll("\\s+", "") + id; // Tạo password từ tên nhân viên và mã nhân viên (viết liền không dấu)

        // Tạo đối tượng UserAccount mới
        UserAccount userAccount = new UserAccount(id, username, password, roll);

        // Tham chiếu đến node "user_accounts" trên Firebase
        DatabaseReference userAccountRef = FirebaseDatabase.getInstance().getReference().child("account");

        // Lưu thông tin tài khoản người dùng vào Firebase
        userAccountRef.child(id).setValue(userAccount)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xử lý khi lưu tài khoản người dùng thành công
                        Toast.makeText(AddNhanVienActivity.this, "Tạo tài khoản người dùng thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi có lỗi xảy ra trong quá trình lưu tài khoản người dùng
                        Toast.makeText(AddNhanVienActivity.this, "Lỗi khi tạo tài khoản người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
