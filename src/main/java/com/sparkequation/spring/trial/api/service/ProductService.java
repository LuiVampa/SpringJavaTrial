package com.sparkequation.spring.trial.api.service;

import com.sparkequation.spring.trial.api.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getProducts();

    Product getProductById(Integer id);

    void addProduct(Product product);

    void updateProductById(Integer id, Product product);

    void deleteProductById(Integer id);
}
