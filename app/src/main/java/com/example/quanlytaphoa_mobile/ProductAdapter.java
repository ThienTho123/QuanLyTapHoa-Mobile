package com.example.quanlytaphoa_mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    private Context mContext;
    private List<Product> mProductList;

    public ProductAdapter(Context context, List<Product> productList) {
        super(context, 0, productList);
        mContext = context;
        mProductList = productList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.list_sanpham, parent, false);
        }

        // Lấy đối tượng Product ở vị trí position trong danh sách
        Product currentProduct = mProductList.get(position);

        // Ánh xạ các view từ layout
        ImageView imgProduct = listItemView.findViewById(R.id.txtpicture);
        TextView txtProductName = listItemView.findViewById(R.id.txtName_sp);
        TextView txtPrice = listItemView.findViewById(R.id.txtPrice);

        // Hiển thị dữ liệu
        // Trong trường hợp này, bạn có thể hiển thị một văn bản tùy chỉnh cho hình ảnh
        new DownloadImageTask(imgProduct).execute(currentProduct.getPicture());

        txtProductName.setText(currentProduct.getProductName());
        txtPrice.setText(String.valueOf(currentProduct.getPrice()));

        return listItemView;
    }

    // Lớp AsyncTask để tải ảnh từ URL và đặt ảnh vào ImageView
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
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
