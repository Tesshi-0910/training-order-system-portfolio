package com.example.ordersystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ordersystem.entity.Product;
import com.example.ordersystem.exception.ResourceNotFoundException;
import com.example.ordersystem.form.ProductForm;
import com.example.ordersystem.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /** 全件取得 */
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /** カテゴリで絞り込む。category が未指定（null）の場合は全件を返す（UT-D-03, UT-D-04） */
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /** IDで1件取得。存在しなければ例外（UT-B-08, UT-B-09） */
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));
    }

    /** 新規登録（UT-B-07） */
    public void save(ProductForm form) {
        Product product = new Product();
        product.setProductName(form.getProductName());
        product.setCategory(form.getCategory());
        product.setUnitPrice(form.getUnitPrice());
        product.setStockQuantity(form.getStockQuantity());
        productRepository.save(product);
    }

    /** 更新 */
    public void update(Long id, ProductForm form) {
        Product product = findById(id);
        product.setProductName(form.getProductName());
        product.setCategory(form.getCategory());
        product.setUnitPrice(form.getUnitPrice());
        product.setStockQuantity(form.getStockQuantity());
        productRepository.save(product);
    }

    /** 削除 */
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
