package com.example.ordersystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ordersystem.entity.Customer;
import com.example.ordersystem.exception.ResourceNotFoundException;
import com.example.ordersystem.form.CustomerForm;
import com.example.ordersystem.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /** 全件取得（UT-B-02） */
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /** 顧客名で検索。name が未指定（null）の場合は全件を返す（UT-D-01, UT-D-02） */
    public List<Customer> findByName(String name) {
        return customerRepository.findByName(name);
    }

    /** IDで1件取得。存在しなければ例外（UT-B-03, UT-B-04） */
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: id=" + id));
    }

    /** 新規登録（UT-B-01） */
    public void save(CustomerForm form) {
        Customer customer = new Customer();
        customer.setCustomerName(form.getCustomerName());
        customer.setPhone(form.getPhone());
        customer.setPostalCode(form.getPostalCode());
        customer.setAddress(form.getAddress());
        customer.setEmail(form.getEmail());
        customerRepository.save(customer);
    }

    /** 更新（UT-B-05） */
    public void update(Long id, CustomerForm form) {
        Customer customer = findById(id);
        customer.setCustomerName(form.getCustomerName());
        customer.setPhone(form.getPhone());
        customer.setPostalCode(form.getPostalCode());
        customer.setAddress(form.getAddress());
        customer.setEmail(form.getEmail());
        customerRepository.save(customer);
    }

    /** 削除（UT-B-06） */
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }
}
