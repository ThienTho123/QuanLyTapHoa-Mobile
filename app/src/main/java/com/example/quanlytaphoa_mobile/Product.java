package com.example.quanlytaphoa_mobile;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String productName;
    private String detail;
    private String picture;
    private int  price;

    public Product() {
        // Empty constructor required for Firebase
    }

    public Product(String id, String productName, String detail, String picture, int price) {
        this.id = id;
        this.productName = productName;
        this.detail = detail;
        this.picture = picture;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public double  getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
