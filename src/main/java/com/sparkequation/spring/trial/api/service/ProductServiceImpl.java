package com.sparkequation.spring.trial.api.service;

import com.sparkequation.spring.trial.api.model.Product;
import com.sparkequation.spring.trial.api.repository.ProductRepository;
import com.sparkequation.spring.trial.api.service.exception.NoSuchProductException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final static String PRODUCT_NOT_FOUND = "Product with id = %s not found.";

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                                .orElseThrow(() -> new NoSuchProductException(String.format(PRODUCT_NOT_FOUND, id)));
    }

    @Override
    public void addProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void updateProductById(Integer id, Product product) {
        Product productToUpdate = productRepository.findById(id)
                                                   .orElseThrow(() -> new NoSuchProductException(String.format(
                                                           PRODUCT_NOT_FOUND,
                                                           id
                                                   )));
        productToUpdate.update(product);
        productRepository.save(productToUpdate);
    }

    @Override
    public void deleteProductById(Integer id) {
        productRepository.deleteById(id);
    }
}
