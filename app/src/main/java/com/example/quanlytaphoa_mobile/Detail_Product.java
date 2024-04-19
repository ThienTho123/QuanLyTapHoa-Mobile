package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Detail_Product extends AppCompatActivity {

    ImageView imageView;
    StorageReference storageReference;


    private List<Product> productList;
    private int position;
    private ProductAdapter adapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Product selectedProduct;

    private Uri imageUri; // Thêm biến imageUri ở mức lớp


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sp);

        selectedProduct = (Product) getIntent().getSerializableExtra("selected_product");
        productList = ((ProductListWrapper) getIntent().getSerializableExtra("productList")).getProductList();
        position = getIntent().getIntExtra("position", 0);

        EditText txtName = findViewById(R.id.edtEditName);
        txtName.setText(selectedProduct.getProductName());

        EditText txtDetail = findViewById(R.id.edt_mota);
        txtDetail.setText(selectedProduct.getDetail());

        EditText txtPrice = findViewById(R.id.edt_gia);
        txtPrice.setText(String.valueOf(selectedProduct.getPrice()));
        String imageUrl = getIntent().getStringExtra("image_url");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Sử dụng Glide, Picasso hoặc thậm chí BitmapFactory để tải hình ảnh từ URL và đặt cho imageView
            // Ở đây tôi sẽ sử dụng BitmapFactory để minh họa
            new LoadImageTask(imageView).execute(imageUrl);
        }

        imageView  = findViewById(R.id.imageView3);
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



        adapter = new ProductAdapter(this, productList);

        Button btnExitEdit = findViewById(R.id.btnExitEdit);
        btnExitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnEdit = findViewById(R.id.btn_suasp);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtName = findViewById(R.id.edtEditName);
                EditText txtDetail = findViewById(R.id.edt_mota);
                EditText txtPrice = findViewById(R.id.edt_gia);

                String newName = txtName.getText().toString();
                String newDetail = txtDetail.getText().toString();
                int newPrice = Integer.parseInt(txtPrice.getText().toString().replaceAll("\\.0*$", ""));

                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("products").child(selectedProduct.getId());

                String newImageUrl = selectedProduct.getPicture();

                if (selectedProduct.getPicture() != null && !selectedProduct.getPicture().isEmpty()) {
                    newImageUrl = selectedProduct.getPicture();
                }

                if (imageUri != null) {
                    // Nếu đã chọn ảnh mới, tải ảnh lên Firebase Storage và lưu URL vào sản phẩm
                    StorageReference imageRef = storageReference.child("images/" + selectedProduct.getId() + ".jpg");

                    imageRef.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Nếu tải ảnh lên thành công, lấy URL của ảnh
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Lấy URL thành công, lưu URL mới vào sản phẩm
                                            String imageURL = uri.toString();
                                            // Tiếp tục cập nhật thông tin sản phẩm với URL mới
                                            Product updatedProduct = new Product(selectedProduct.getId(), newName, newDetail,  imageURL, newPrice);
                                            productRef.setValue(updatedProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(Detail_Product.this, "Sửa thông tin sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Detail_Product.this, "Sửa thông tin sản phẩm không thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Xử lý khi có lỗi xảy ra trong quá trình tải ảnh lên
                                    Toast.makeText(Detail_Product.this, "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Nếu không có ảnh mới được chọn, chỉ cập nhật thông tin sản phẩm không thay đổi về ảnh
                    Product updatedProduct = new Product(selectedProduct.getId(), newName, newDetail, newImageUrl, newPrice);
                    productRef.setValue(updatedProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Detail_Product.this, "Sửa thông tin sản phẩm thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Detail_Product.this, "Sửa thông tin sản phẩm không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        Button btnDel = findViewById(R.id.btn_xoasp);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("products").child(selectedProduct.getId());
                productRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        productList.remove(position);
                        adapter.notifyDataSetChanged();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updated_product_list", new ProductListWrapper(productList));
                        setResult(RESULT_OK, resultIntent);

                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Detail_Product.this, "Xóa sản phẩm không thành công", Toast.LENGTH_SHORT).show();
                    }
                });
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
}
