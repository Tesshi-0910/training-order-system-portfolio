package com.example.ordersystem.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderDetailForm {

    @NotNull(message = "商品を選択してください")
    private Long productId;

    @NotNull(message = "数量は必須です")
    @Min(value = 1, message = "数量は1以上で入力してください")
    private Integer quantity;

    // ===== getter / setter =====

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
