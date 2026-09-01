package com.example.ordersystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ordersystem.entity.Product;
import com.example.ordersystem.form.ProductForm;
import com.example.ordersystem.service.ProductService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** 商品一覧・カテゴリフィルター (GET /products) */
    @GetMapping
    public String list(@RequestParam(required = false) String category, Model model) {
        model.addAttribute("products", productService.findByCategory(category));
        model.addAttribute("selectedCategory", category);
        return "product/list";
    }

    /** 商品登録フォーム表示 (GET /products/new) */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        return "product/form";
    }

    /** 商品登録実行 (POST /products) */
    @PostMapping
    public String create(@Valid @ModelAttribute("productForm") ProductForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "product/form";
        }
        productService.save(form);
        return "redirect:/products";
    }

    /** 商品編集フォーム表示 (GET /products/{id}/edit) */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);

        ProductForm form = new ProductForm();
        form.setProductId(product.getId());
        form.setProductName(product.getProductName());
        form.setCategory(product.getCategory());
        form.setUnitPrice(product.getUnitPrice());
        form.setStockQuantity(product.getStockQuantity());

        model.addAttribute("productForm", form);
        return "product/form";
    }

    /** 商品更新実行 (POST /products/{id}) */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("productForm") ProductForm form, BindingResult result) {
        if (result.hasErrors()) {
            form.setProductId(id);
            return "product/form";
        }
        productService.update(id, form);
        return "redirect:/products";
    }

    /** 商品削除実行 (POST /products/{id}/delete) */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}
