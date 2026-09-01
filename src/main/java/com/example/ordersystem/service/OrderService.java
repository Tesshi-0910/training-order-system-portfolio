package com.example.ordersystem.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ordersystem.entity.Customer;
import com.example.ordersystem.entity.Order;
import com.example.ordersystem.entity.OrderDetail;
import com.example.ordersystem.entity.Product;
import com.example.ordersystem.exception.ResourceNotFoundException;
import com.example.ordersystem.form.OrderDetailForm;
import com.example.ordersystem.form.OrderForm;
import com.example.ordersystem.repository.CustomerRepository;
import com.example.ordersystem.repository.OrderDetailRepository;
import com.example.ordersystem.repository.OrderRepository;
import com.example.ordersystem.repository.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    /** 全件取得（UT-C-02） */
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /** ステータスで絞り込む。status が未指定（null）の場合は全件を返す（UT-D-05, UT-D-06） */
    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * ステータス・受注日範囲（開始日〜終了日）で絞り込む（フェーズF: UT-F-01〜UT-F-03）。
     * status/from/to はそれぞれ未指定（null）の場合、その条件は絞り込みに影響しない
     * （3つとも未指定なら全件を返す＝UT-F-03）。
     * from のみ指定時は開始日以降、to のみ指定時は終了日以前のみで絞り込まれる。
     */
    public List<Order> findByCondition(String status, LocalDate from, LocalDate to) {
        return orderRepository.findByCondition(status, from, to);
    }

    /** IDで1件取得。存在しなければ例外（UT-C-03, UT-C-04） */
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: id=" + id));
    }

    /**
     * 受注登録（ヘッダ＋明細）
     * ヘッダ・明細の保存をすべて1トランザクションにまとめる。
     * 途中で例外が発生した場合、ここまでのINSERTはすべてロールバックされる。
     */
    @Transactional
    public void create(OrderForm form) {

        // ① 顧客を取得する（見つからなければ例外→ロールバック）
        Customer customer = customerRepository.findById(form.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: id=" + form.getCustomerId()));

        // ② 受注ヘッダを作成して保存する（この時点では total_amount = 0）
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(form.getOrderDate());
        order.setStatus(Order.STATUS_PENDING);
        order = orderRepository.save(order); // ← ここで order.id が確定する（明細に必要）

        // ③ 明細をループで保存しながら合計金額を積算する
        int total = 0;
        for (OrderDetailForm df : form.getDetails()) {
            Product product = productRepository.findById(df.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: id=" + df.getProductId()));

            int subtotal = product.getUnitPrice() * df.getQuantity();

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(df.getQuantity());
            detail.setUnitPrice(product.getUnitPrice()); // 受注時点の単価をコピー
            detail.setSubtotal(subtotal);
            orderDetailRepository.save(detail);

            total += subtotal;
        }

        // ④ ヘッダの total_amount を確定値で更新する
        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}
