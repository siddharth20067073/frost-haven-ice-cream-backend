package com.frosthaven.service;

import com.frosthaven.entity.Product;
import com.frosthaven.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Integer id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
    }

    public Product addProduct(Product product) {

        if (product.getProductName() == null ||
                product.getProductName().isBlank()) {

            throw new RuntimeException(
                    "Product name is required");
        }

        if (product.getPrice() < 0) {

            throw new RuntimeException(
                    "Price cannot be negative");
        }

        if (product.getStock() < 0) {

            throw new RuntimeException(
                    "Stock cannot be negative");
        }

        return productRepository.save(product);
    }

    public Product updateProduct(
            Integer id,
            Product updatedProduct) {

        Product product = getProduct(id);

        product.setProductName(
                updatedProduct.getProductName());

        product.setDescription(
                updatedProduct.getDescription());

        product.setPrice(
                updatedProduct.getPrice());

        product.setImage(
                updatedProduct.getImage());

        product.setCategory(
                updatedProduct.getCategory());

        product.setStock(
                updatedProduct.getStock());

        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {

        if (!productRepository.existsById(id)) {

            throw new RuntimeException(
                    "Product not found");
        }

        productRepository.deleteById(id);
    }
}