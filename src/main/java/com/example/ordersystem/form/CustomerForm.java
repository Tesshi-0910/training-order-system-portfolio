package com.example.ordersystem.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerForm {

    /** 編集時のみ値が入る（新規登録時は null） */
    private Long customerId;

    @NotBlank(message = "顧客名は必須です")
    @Size(max = 100, message = "顧客名は100文字以内で入力してください")
    private String customerName;

    @Pattern(regexp = "^$|^[0-9-]{10,13}$", message = "電話番号は10〜13桁の数字とハイフンで入力してください")
    private String phone;

    @Pattern(regexp = "^$|^\\d{3}-\\d{4}$", message = "郵便番号は「123-4567」の形式で入力してください")
    private String postalCode;

    @Size(max = 200, message = "住所は200文字以内で入力してください")
    private String address;

    @Email(message = "メールアドレスの形式で入力してください")
    private String email;

    // ===== getter / setter =====

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
