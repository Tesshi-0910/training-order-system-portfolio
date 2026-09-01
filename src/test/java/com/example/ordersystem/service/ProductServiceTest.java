package com.example.ordersystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ordersystem.entity.Product;
import com.example.ordersystem.exception.ResourceNotFoundException;
import com.example.ordersystem.form.ProductForm;
import com.example.ordersystem.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    /** UT-B-07 */
    @Test
    void 正常系_商品を登録できること() {
        ProductForm form = new ProductForm();
        form.setProductName("テスト商品");
        form.setUnitPrice(1000);
        form.setStockQuantity(10);

        productService.save(form);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    /** UT-B-08 */
    @Test
    void 正常系_IDで取得できること() {
        Product product = new Product();
        product.setId(1L);
        product.setProductName("テスト商品");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals("テスト商品", result.getProductName());
    }

    /** UT-B-09 */
    @Test
    void 異常系_存在しないIDで例外スロー() {
        when(productRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.findById(99999L));
    }

    /** UT-D-03: カテゴリを指定した場合、ProductRepository.findByCategory(category) が呼ばれること */
    @Test
    void 正常系_カテゴリフィルター_指定あり() {
        when(productRepository.findByCategory("ソフトウェア")).thenReturn(List.of());

        productService.findByCategory("ソフトウェア");

        verify(productRepository, times(1)).findByCategory("ソフトウェア");
    }

    /** UT-D-04: カテゴリを指定しない場合、findByCategory(null) が呼ばれ、@Query の IS NULL 条件で全件返却される */
    @Test
    void 正常系_カテゴリフィルター_未指定() {
        when(productRepository.findByCategory(null)).thenReturn(List.of());

        productService.findByCategory(null);

        verify(productRepository, times(1)).findByCategory(null);
    }
}
