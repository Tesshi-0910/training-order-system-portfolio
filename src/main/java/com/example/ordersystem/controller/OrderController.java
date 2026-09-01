package com.example.ordersystem.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ordersystem.form.OrderDetailForm;
import com.example.ordersystem.form.OrderForm;
import com.example.ordersystem.service.CustomerService;
import com.example.ordersystem.service.OrderService;
import com.example.ordersystem.service.ProductService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrderController(OrderService orderService, CustomerService customerService,
            ProductService productService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.productService = productService;
    }

    /**
     * 受注一覧・ステータス／受注日範囲フィルター (GET /orders)
     * from/to は "yyyy-MM-dd" 形式の文字列で受け取り、空文字・未指定の場合は null として扱う
     * （日付入力が空のまま検索された場合に型変換エラーとならないようにするため、String で受け取ってから変換する）。
     */
    @GetMapping
    public String list(@RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model) {
        LocalDate fromDate = (from != null && !from.isBlank()) ? LocalDate.parse(from) : null;
        LocalDate toDate = (to != null && !to.isBlank()) ? LocalDate.parse(to) : null;

        model.addAttribute("orders", orderService.findByCondition(status, fromDate, toDate));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedFrom", from);
        model.addAttribute("selectedTo", to);
        return "order/list";
    }

    /** 受注登録フォーム表示 (GET /orders/new) */
    @GetMapping("/new")
    public String newForm(Model model) {
        OrderForm form = new OrderForm();
        form.getDetails().add(new OrderDetailForm()); // 明細を最初から1行表示する
        model.addAttribute("orderForm", form);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("products", productService.findAll());
        return "order/form";
    }

    /** 受注登録実行 (POST /orders) */
    @PostMapping
    public String create(@Valid @ModelAttribute("orderForm") OrderForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("products", productService.findAll());
            return "order/form";
        }
        orderService.create(form);
        return "redirect:/orders";
    }

    /** 受注詳細 (GET /orders/{id}) */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.findById(id));
        return "order/detail";
    }
}
