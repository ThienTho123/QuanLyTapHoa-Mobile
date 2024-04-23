package com.example.quanlytaphoa_mobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Detail_Product_Guest extends AppCompatActivity {

    ImageView imageView;

    private List<Product> productList;
    private Product selectedProduct;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // Thêm biến imageUri ở mức lớp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sp_guest);

        selectedProduct = (Product) getIntent().getSerializableExtra("selected_product");
        productList = ((ProductListWrapper) getIntent().getSerializableExtra("productList")).getProductList();

        EditText txtName = findViewById(R.id.edtEditName);
        txtName.setText(selectedProduct.getProductName());

        EditText txtDetail = findViewById(R.id.edt_mota);
        txtDetail.setText(selectedProduct.getDetail());

        EditText txtPrice = findViewById(R.id.edt_gia);
        txtPrice.setText(String.valueOf(selectedProduct.getPrice()));
        txtPrice.setText(String.format("%d", (int) selectedProduct.getPrice()));


        String imageUrlFromIntent = getIntent().getStringExtra("image_url");
        imageView = findViewById(R.id.imageView3);
        if (imageUrlFromIntent != null && !imageUrlFromIntent.isEmpty()) {
            new LoadImageTask(imageView).execute(imageUrlFromIntent);
        }

        Button btnExitEdit = findViewById(R.id.btnExitEdit);
        btnExitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
