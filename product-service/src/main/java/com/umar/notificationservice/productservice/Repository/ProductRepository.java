package com.umar.notificationservice.productservice.Repository;

import com.umar.notificationservice.productservice.Model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product,String> {
}
