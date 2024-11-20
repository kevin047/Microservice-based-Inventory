package com.kevin.microservices.productService.repository;

import com.kevin.microservices.productService.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
