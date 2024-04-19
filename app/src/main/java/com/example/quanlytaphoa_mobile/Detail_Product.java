package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Detail_Product extends AppCompatActivity {
    private List<Product> productList;
    private int position;
    private ProductAdapter adapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Product selectedProduct;

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

        ImageView imageView = findViewById(R.id.imageView3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        new DownloadImageTask(imageView).execute(selectedProduct.getPicture());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                ImageView imageView = findViewById(R.id.imageView3);
                imageView.setImageBitmap(bitmap);

                // Lưu URL hình ảnh mới vào selectedProduct
                selectedProduct.setPicture(imageUri.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
