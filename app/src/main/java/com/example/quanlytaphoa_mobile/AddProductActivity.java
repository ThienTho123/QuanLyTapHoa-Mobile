package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AddProductActivity extends AppCompatActivity {

    EditText edtTenSP, edtGia, edtMoTa;
    Button btnThemSP, btnThoat;
    ImageView imageView;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    DatabaseReference productCountRef; // Thêm tham chiếu đến node chứa số lượng sản phẩm

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // Thêm biến imageUri ở mức lớp
    private int productCount = 0; // Biến để lưu số lượng sản phẩm hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sanpham);

        // Ánh xạ các view từ layout
        edtTenSP = findViewById(R.id.edtadd_ten);
        edtGia = findViewById(R.id.edtadd_gia);
        edtMoTa = findViewById(R.id.edtadd_mota);
        btnThemSP = findViewById(R.id.btn_themsp);
        btnThoat = findViewById(R.id.btn_thoat);
        imageView = findViewById(R.id.imageView6);

        // Tham chiếu đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("products");
        // Tham chiếu đến Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();
        // Tham chiếu đến node chứa số lượng sản phẩm
        productCountRef = FirebaseDatabase.getInstance().getReference().child("product_count");

        // Lắng nghe sự thay đổi của số lượng sản phẩm
        productCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Nếu node tồn tại, cập nhật giá trị của biến productCount
                    productCount = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase Database
                Toast.makeText(AddProductActivity.this, "Lỗi khi đọc dữ liệu từ Firebase Database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện khi nhấn nút Thêm Sản Phẩm
        btnThemSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themSanPham();
            }
        });

        // Xử lý sự kiện khi nhấn nút Thoát
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện khi nhấn vào ImageView để chọn ảnh từ thư viện
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
    }

    // Phương thức để mở Intent để chọn hình ảnh từ thư viện
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh sản phẩm"), PICK_IMAGE_REQUEST);
    }

    // Xử lý kết quả sau khi chọn ảnh từ thư viện
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

    private void themSanPham() {
        // Lấy dữ liệu từ EditText
        String tenSP = edtTenSP.getText().toString().trim();
        String giaStr = edtGia.getText().toString().trim();
        String moTa = edtMoTa.getText().toString().trim();

        // Kiểm tra xem các trường có rỗng không
        if (TextUtils.isEmpty(tenSP) || TextUtils.isEmpty(giaStr) || TextUtils.isEmpty(moTa)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh sản phẩm", Toast.LENGTH_SHORT).show();
        } else {
            // Chuyển đổi dữ liệu giá từ chuỗi sang số nguyên
            int gia = Integer.parseInt(giaStr);

            // Tạo key mới cho sản phẩm
            String newProductId = String.format("%04d", ++productCount); // Format ID mới

            // Tạo thư mục để lưu trữ ảnh trong Firebase Storage
            StorageReference imageRef = storageReference.child("images/" + newProductId + ".jpg");

            // Tải ảnh lên Firebase Storage
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
                                    Product sanPham = new Product(newProductId, tenSP, moTa, imageURL, gia);
                                    // Thêm sản phẩm vào Firebase Database
                                    databaseReference.child(newProductId).setValue(sanPham);
                                    // Cập nhật số lượng sản phẩm
                                    productCountRef.setValue(productCount);
                                    // Hiển thị thông báo
                                    Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                    // Chuyển về lại trang listSanPhamActivity
                                    Intent intent = new Intent(AddProductActivity.this, listSanPhamActivity.class);
                                    startActivity(intent);
                                    finish();
                                    // Xóa nội dung trong EditText sau khi thêm thành công
                                    edtTenSP.setText("");
                                    edtGia.setText("");
                                    edtMoTa.setText("");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Xử lý khi có lỗi xảy ra trong quá trình tải ảnh lên
                            Toast.makeText(AddProductActivity.this, "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
