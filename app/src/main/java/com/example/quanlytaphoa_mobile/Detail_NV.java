package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Detail_NV extends AppCompatActivity {

    ImageView imageView;
    StorageReference storageReference;
    private List<Employee> employeeList;
    private int position;
    private EmployeeAdapter adapter; // Khai báo biến adapter
    private Spinner spinnerChucVu; // Thêm Spinner
    private String selectedChucVu; // Thêm biến lưu trữ chức vụ được chọn
    private static final int PICK_IMAGE_REQUEST = 1;
    private Employee selectedEmployee; // Chuyển selectedEmployee thành biến toàn cục

    private Uri imageUri; // Thêm biến imageUri ở mức lớp



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nv);

        selectedEmployee = (Employee) getIntent().getSerializableExtra("selected_employee");
        employeeList = ((EmployeeListWrapper) getIntent().getSerializableExtra("employeeList")).getEmployeeList();
        position = getIntent().getIntExtra("position", 0);

        TextView txtId = findViewById(R.id.edtMaNV);
        txtId.setText(selectedEmployee.getId());

        EditText txtName = findViewById(R.id.edtEditName);
        txtName.setText(selectedEmployee.getName());

        spinnerChucVu = findViewById(R.id.spinnerChucVu);
        setupSpinner();

        EditText txtSogio = findViewById(R.id.edtsogio);
        txtSogio.setText(String.valueOf(selectedEmployee.getHoursWorked()));

        EditText txtLuong = findViewById(R.id.edtluong);
        txtLuong.setText(String.valueOf(selectedEmployee.getSalary()));

        TextView txtTongLuong = findViewById(R.id.edttongluong);
        int tongLuong = selectedEmployee.getHoursWorked() * selectedEmployee.getSalary();
        txtTongLuong.setText(String.valueOf(tongLuong));

        String imageUrl = getIntent().getStringExtra("image_url");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Sử dụng Glide, Picasso hoặc thậm chí BitmapFactory để tải hình ảnh từ URL và đặt cho imageView
            // Ở đây tôi sẽ sử dụng BitmapFactory để minh họa
            new LoadImageTask(imageView).execute(imageUrl);
        }

        imageView  = findViewById(R.id.imageViewNV);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
        String imageUrlFromIntent = getIntent().getStringExtra("image_url");
        if (imageUrlFromIntent != null && !imageUrlFromIntent.isEmpty()) {
            // Sử dụng Glide, Picasso hoặc thậm chí BitmapFactory để tải hình ảnh từ URL và đặt cho imageView
            // Ở đây tôi sẽ sử dụng BitmapFactory để minh họa
            new LoadImageTask(imageView).execute(imageUrlFromIntent);
        }
        storageReference = FirebaseStorage.getInstance().getReference();

        // Khởi tạo adapter
        adapter = new EmployeeAdapter(this, employeeList);

        Button btnExitEdit = findViewById(R.id.btnExitEdit);
        btnExitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy nhân viên được chọn từ employeeList
                Employee selectedEmployee = employeeList.get(position);

                // Lấy thông tin mới từ các EditText
                String newId = txtId.getText().toString(); // Lấy mã nhân viên mới
                String newName = txtName.getText().toString();
                int newSogio = Integer.parseInt(txtSogio.getText().toString());
                int newLuong = Integer.parseInt(txtLuong.getText().toString());
                String employeeKey = selectedEmployee.getId();

                // Kiểm tra xem mã nhân viên mới đã tồn tại chưa
                DatabaseReference checkIdRef = FirebaseDatabase.getInstance().getReference().child("employees").child(newId);
                checkIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && !newId.equals(selectedEmployee.getId())) {
                            // Mã nhân viên mới đã tồn tại và không trùng với mã nhân viên cũ
                            Toast.makeText(Detail_NV.this, "Mã nhân viên đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            // Mã nhân viên mới chưa tồn tại hoặc trùng với mã nhân viên cũ, tiến hành cập nhật thông tin
                            DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference().child("employees").child(employeeKey);

                            // Nếu có ảnh mới được chọn
                            if (imageUri != null) {
                                // Tiến hành tải ảnh lên Firebase Storage
                                StorageReference imageRef = storageReference.child("images/" + selectedEmployee.getId() + ".jpg");

                                imageRef.putFile(imageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        // Xóa account nhân viên cũ
                                                        DatabaseReference oldUserAccountRef = FirebaseDatabase.getInstance().getReference().child("account").child(selectedEmployee.getId());
                                                        oldUserAccountRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Tạo và lưu account nhân viên mới
                                                                createAndSaveUserAccount(newId, newName, selectedChucVu);

                                                                // Tiến hành cập nhật thông tin nhân viên
                                                                String imageURL = uri.toString();
                                                                Employee updatedEmployee = new Employee(newId, newName, selectedChucVu, newSogio, newLuong, imageURL);
                                                                DatabaseReference updatedEmployeeRef = FirebaseDatabase.getInstance().getReference().child("employees").child(newId);
                                                                updatedEmployeeRef.setValue(updatedEmployee).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(Detail_NV.this, "Sửa thông tin nhân viên thành công", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(Detail_NV.this, "Sửa thông tin nhân viên không thành công", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(Detail_NV.this, "Lỗi khi xóa account nhân viên cũ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Detail_NV.this, "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Nếu không có ảnh mới được chọn, chỉ cập nhật thông tin nhân viên không thay đổi về ảnh
                                Employee updatedEmployee = new Employee(newId, newName, selectedChucVu, newSogio, newLuong, imageUrl);
                                // Xóa account nhân viên cũ
                                DatabaseReference oldUserAccountRef = FirebaseDatabase.getInstance().getReference().child("account").child(selectedEmployee.getId());
                                oldUserAccountRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Tạo và lưu account nhân viên mới
                                        createAndSaveUserAccount(newId, newName, selectedChucVu);

                                        // Tiến hành cập nhật thông tin nhân viên
                                        employeeRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DatabaseReference updatedEmployeeRef = FirebaseDatabase.getInstance().getReference().child("employees").child(newId);
                                                updatedEmployeeRef.setValue(updatedEmployee).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Detail_NV.this, "Sửa thông tin nhân viên thành công", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Detail_NV.this, "Sửa thông tin nhân viên không thành công", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Detail_NV.this, "Lỗi khi xóa nhân viên cũ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Detail_NV.this, "Lỗi khi xóa account nhân viên cũ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase Database
                        Toast.makeText(Detail_NV.this, "Lỗi khi đọc dữ liệu từ Firebase Database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        Button btnDel = findViewById(R.id.btnDel);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy nhân viên được chọn từ employeeList
                Employee selectedEmployee = employeeList.get(position);

                // Tham chiếu đến node "employees" và "account" trên Firebase
                DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference().child("employees").child(selectedEmployee.getId());
                DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference().child("account").child(selectedEmployee.getId());

                // Xóa nhân viên và tài khoản tương ứng từ Firebase Database
                employeeRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xóa tài khoản người dùng
                        accountRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Nếu xóa thành công từ Firebase, cập nhật ListView và danh sách nhân viên
                                employeeList.remove(position);
                                adapter.notifyDataSetChanged(); // Cập nhật ListView

                                // Tạo một Intent mới để truyền danh sách nhân viên đã cập nhật
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("updated_employee_list", new EmployeeListWrapper(employeeList));
                                setResult(RESULT_OK, resultIntent);

                                // Kết thúc activity và quay lại danh sách nhân viên
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý trường hợp xóa tài khoản thất bại từ Firebase (nếu cần)
                                Toast.makeText(Detail_NV.this, "Xóa tài khoản người dùng không thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý trường hợp xóa nhân viên thất bại từ Firebase (nếu cần)
                        Toast.makeText(Detail_NV.this, "Xóa nhân viên không thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
    private void setupSpinner() {
        // Lấy danh sách chức vụ từ mảng string chuc_vu_array trong file string.xml
        String[] chucVuArray = getResources().getStringArray(R.array.chuc_vu_array);

        // Tạo Adapter cho Spinner sử dụng mảng string chuc_vu_array
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chucVuArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Thiết lập Adapter cho Spinner
        spinnerChucVu.setAdapter(spinnerAdapter);

        // Lấy chức vụ của nhân viên được chọn
        selectedChucVu = selectedEmployee.getChucvu();

        // Tìm vị trí của chức vụ trong mảng chucVuArray
        int position = Arrays.asList(chucVuArray).indexOf(selectedChucVu);
        if (position >= 0) {
            // Thiết lập chức vụ của nhân viên là mục được chọn trong Spinner
            spinnerChucVu.setSelection(position);
        }

        // Xử lý sự kiện khi một mục được chọn trên Spinner
        spinnerChucVu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy chức vụ được chọn từ mảng string chuc_vu_array
                selectedChucVu = chucVuArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có mục nào được chọn
            }
        });
    }


    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh sản phẩm"), PICK_IMAGE_REQUEST);
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
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public LoadImageTask(ImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && imageViewReference != null && imageViewReference.get() != null) {
                imageViewReference.get().setImageBitmap(bitmap);
            }
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
                        Toast.makeText(Detail_NV.this, "Tạo tài khoản người dùng thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi có lỗi xảy ra trong quá trình lưu tài khoản người dùng
                        Toast.makeText(Detail_NV.this, "Lỗi khi tạo tài khoản người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
