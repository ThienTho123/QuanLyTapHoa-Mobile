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

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    private Context mContext;
    private List<Employee> mEmployeeList;

    public EmployeeAdapter(Context context, List<Employee> employeeList) {
        super(context, 0, employeeList);
        mContext = context;
        mEmployeeList = employeeList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.list_nhanvien, parent, false);
        }

        // Lấy đối tượng Employee ở vị trí position trong danh sách
        Employee currentEmployee = mEmployeeList.get(position);

        // Ánh xạ các view từ layout
        ImageView imgNV = listItemView.findViewById(R.id.imgNV);
        TextView txtId = listItemView.findViewById(R.id.txtId);
        TextView txtName = listItemView.findViewById(R.id.txtName);
        TextView txtchucvu = listItemView.findViewById(R.id.txtchucvu);

        // Đặt dữ liệu cho các view

        imgNV.setImageResource(R.drawable.employee);

        txtId.setText(currentEmployee.getId());
        txtName.setText(currentEmployee.getName());
        txtchucvu.setText(currentEmployee.getChucvu());

        new DownloadImageTask(imgNV).execute(currentEmployee.getPicture());

        return listItemView;
    }
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
