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

import com.example.ordersystem.entity.Customer;
import com.example.ordersystem.form.CustomerForm;
import com.example.ordersystem.service.CustomerService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /** 顧客一覧・検索 (GET /customers) */
    @GetMapping
    public String list(@RequestParam(required = false) String name, Model model) {
        model.addAttribute("customers", customerService.findByName(name));
        model.addAttribute("selectedName", name);
        return "customer/list";
    }

    /** 顧客登録フォーム表示 (GET /customers/new) */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("customerForm", new CustomerForm());
        return "customer/form";
    }

    /** 顧客登録実行 (POST /customers) */
    @PostMapping
    public String create(@Valid @ModelAttribute("customerForm") CustomerForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "customer/form";
        }
        customerService.save(form);
        return "redirect:/customers";
    }

    /** 顧客編集フォーム表示 (GET /customers/{id}/edit) */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.findById(id);

        CustomerForm form = new CustomerForm();
        form.setCustomerId(customer.getId());
        form.setCustomerName(customer.getCustomerName());
        form.setPhone(customer.getPhone());
        form.setPostalCode(customer.getPostalCode());
        form.setAddress(customer.getAddress());
        form.setEmail(customer.getEmail());

        model.addAttribute("customerForm", form);
        return "customer/form";
    }

    /** 顧客更新実行 (POST /customers/{id}) */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("customerForm") CustomerForm form, BindingResult result) {
        if (result.hasErrors()) {
            form.setCustomerId(id);
            return "customer/form";
        }
        customerService.update(id, form);
        return "redirect:/customers";
    }

    /** 顧客削除実行 (POST /customers/{id}/delete) */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        customerService.deleteById(id);
        return "redirect:/customers";
    }
}
