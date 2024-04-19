package com.example.quanlytaphoa_mobile;


import java.io.Serializable;
import java.util.List;

public class ProductListWrapper implements Serializable {
    private List<Product> productList;

    public ProductListWrapper(List<Product> productList) {
        this.productList = productList;
    }

    public List<Product> getProductList() {
        return productList;
    }
}
