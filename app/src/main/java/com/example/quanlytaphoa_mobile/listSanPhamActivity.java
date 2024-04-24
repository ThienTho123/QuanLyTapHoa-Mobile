package com.example.quanlytaphoa_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listSanPhamActivity extends AppCompatActivity {

    private ListView listView;
    private List<Product> productList;
    private ProductAdapter adapter;
    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 1;

    private EditText searchEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sanpham);

        listView = findViewById(R.id.list_sanpham);
        searchEditText = findViewById(R.id.searchEditText);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

        Button menuButton = findViewById(R.id.menu); // Thay ImageView bằng Button

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("products");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(listSanPhamActivity.this, "Không thể đọc dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product selectedProduct = productList.get(position);

                Intent intent = new Intent(listSanPhamActivity.this, Detail_Product.class);
                intent.putExtra("selected_product", selectedProduct);
                intent.putExtra("productList", new ProductListWrapper(productList));
                intent.putExtra("position", position);
                intent.putExtra("image_url", selectedProduct.getPicture()); // Thêm dòng này để chuyển URL hình ảnh

                startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No need to implement anything here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Called when text is changed
                String searchText = s.toString().toLowerCase().trim(); // Chuyển đổi text thành chữ thường và loại bỏ khoảng trắng ở đầu và cuối
                searchProducts(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No need to implement anything here
            }
        });
    }
    private void searchProducts(String searchText) {
        List<Product> filteredList = new ArrayList<>();

        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(searchText)) {
                filteredList.add(product);
            }
        }

        adapter = new ProductAdapter(this, filteredList);
        listView.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAIL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            ProductListWrapper wrapper = (ProductListWrapper) data.getSerializableExtra("updated_product_list");
            List<Product> updatedProductList = wrapper.getProductList();

            productList.clear();
            productList.addAll(updatedProductList);
            adapter.notifyDataSetChanged();
        }
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
        popupMenu.inflate(R.menu.list_sanpham_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_add) {
                    Intent intentAddProduct = new Intent(listSanPhamActivity.this, AddProductActivity.class);
                    startActivity(intentAddProduct);
                    return true;
                }
                if (item.getItemId() == R.id.action_return) {
                    Intent intentAddProduct = new Intent(listSanPhamActivity.this, AdminActivity.class);
                    startActivity(intentAddProduct);
                    return true;
                }
                else if (item.getItemId() == R.id.action_logout) {
                    Intent intentLogout = new Intent(listSanPhamActivity.this, MainActivity.class);
                    startActivity(intentLogout);
                    finish();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }
}
