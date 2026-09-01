package com.example.ordersystem.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderForm {

    @NotNull(message = "顧客を選択してください")
    private Long customerId;

    @NotNull(message = "受注日は必須です")
    private LocalDate orderDate;

    @Valid
    @NotEmpty(message = "受注明細を1件以上入力してください")
    private List<OrderDetailForm> details = new ArrayList<>();

    // ===== getter / setter =====

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderDetailForm> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailForm> details) {
        this.details = details;
    }
}
