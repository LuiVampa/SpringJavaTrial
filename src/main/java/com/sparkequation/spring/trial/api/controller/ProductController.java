package com.sparkequation.spring.trial.api.controller;

import com.sparkequation.spring.trial.api.model.Product;
import com.sparkequation.spring.trial.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("api/product")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping(path = "/all")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable @Positive(message = "Id must be positive.") Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void addProduct(@RequestBody @Valid Product product) {
        productService.addProduct(product);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateProductById(
            @PathVariable @Positive(message = "Id must be positive.") Integer id,
            @RequestBody @Valid Product product
    ) {
        productService.updateProductById(id, product);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable @Positive(message = "Id must be positive.") Integer id) {
        productService.deleteProductById(id);
    }
}
