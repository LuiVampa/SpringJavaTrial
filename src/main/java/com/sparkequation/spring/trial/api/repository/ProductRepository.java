package com.sparkequation.spring.trial.api.repository;


import com.sparkequation.spring.trial.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface ProductRepository
        extends JpaRepository<Product, Integer>  {
}
