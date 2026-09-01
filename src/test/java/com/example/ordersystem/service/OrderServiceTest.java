package com.example.ordersystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer(Long id) {
        Customer c = new Customer();
        c.setId(id);
        c.setCustomerName("テスト株式会社");
        return c;
    }

    private Product product(Long id, int unitPrice) {
        Product p = new Product();
        p.setId(id);
        p.setProductName("テスト商品" + id);
        p.setUnitPrice(unitPrice);
        return p;
    }

    /** UT-C-01 */
    @Test
    void 正常系_受注登録できること_明細1件() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer(1L)));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, 1000)));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderForm form = new OrderForm();
        form.setCustomerId(1L);
        form.setOrderDate(LocalDate.now());
        OrderDetailForm detail = new OrderDetailForm();
        detail.setProductId(1L);
        detail.setQuantity(2);
        form.setDetails(List.of(detail));

        orderService.create(form);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(2)).save(captor.capture());
        Order saved = captor.getValue();
        assertEquals(2000, saved.getTotalAmount()); // 1000円 × 2個
    }

    /** UT-C-02 */
    @Test
    void 正常系_受注一覧取得できること() {
        orderService.findAll();

        verify(orderRepository, times(1)).findAll();
    }

    /** UT-C-03 */
    @Test
    void 正常系_IDで受注を取得できること() {
        Order order = new Order();
        order.setId(1L);
        List<OrderDetail> details = new ArrayList<>();
        details.add(new OrderDetail());
        order.setDetails(details);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.findById(1L);

        assertNotNull(result);
        assertFalse(result.getDetails().isEmpty());
    }

    /** UT-C-04 */
    @Test
    void 異常系_存在しないIDで例外スロー() {
        when(orderRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.findById(99999L));
    }

    /** UT-C-05 */
    @Test
    void 異常系_存在しない顧客IDで例外スロー() {
        when(customerRepository.findById(99999L)).thenReturn(Optional.empty());

        OrderForm form = new OrderForm();
        form.setCustomerId(99999L);
        form.setOrderDate(LocalDate.now());
        form.setDetails(new ArrayList<>());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.create(form));
    }

    /** UT-C-06 */
    @Test
    void 異常系_存在しない商品IDで例外スロー() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer(1L)));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(99999L)).thenReturn(Optional.empty());

        OrderForm form = new OrderForm();
        form.setCustomerId(1L);
        form.setOrderDate(LocalDate.now());
        OrderDetailForm detail = new OrderDetailForm();
        detail.setProductId(99999L);
        detail.setQuantity(1);
        form.setDetails(List.of(detail));

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.create(form));
    }

    /** UT-C-07 */
    @Test
    void 正常系_明細2件登録時にtotal_amountが合計値になること() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer(1L)));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, 1000)));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product(2L, 500)));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderForm form = new OrderForm();
        form.setCustomerId(1L);
        form.setOrderDate(LocalDate.now());

        OrderDetailForm detail1 = new OrderDetailForm();
        detail1.setProductId(1L);
        detail1.setQuantity(2); // 1000 × 2 = 2000

        OrderDetailForm detail2 = new OrderDetailForm();
        detail2.setProductId(2L);
        detail2.setQuantity(3); // 500 × 3 = 1500

        form.setDetails(List.of(detail1, detail2));

        orderService.create(form);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(2)).save(captor.capture());
        Order saved = captor.getValue();
        assertEquals(3500, saved.getTotalAmount()); // 2000 + 1500
    }

    /** UT-D-05: ステータスを指定した場合、OrderRepository.findByStatus(status) が呼ばれること */
    @Test
    void 正常系_ステータスフィルター_指定あり() {
        when(orderRepository.findByStatus("PENDING")).thenReturn(List.of());

        orderService.findByStatus("PENDING");

        verify(orderRepository, times(1)).findByStatus("PENDING");
    }

    /** UT-D-06: ステータスを指定しない場合、findByStatus(null) が呼ばれ、@Query の IS NULL 条件で全件返却される */
    @Test
    void 正常系_ステータスフィルター_未指定() {
        when(orderRepository.findByStatus(null)).thenReturn(List.of());

        orderService.findByStatus(null);

        verify(orderRepository, times(1)).findByStatus(null);
    }

    /** UT-F-01: 開始日・終了日を両方指定して絞り込む */
    @Test
    void 正常系_受注日範囲検索_両方指定() {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        when(orderRepository.findByCondition(null, from, to)).thenReturn(List.of());

        orderService.findByCondition(null, from, to);

        verify(orderRepository, times(1)).findByCondition(null, from, to);
    }

    /** UT-F-02: 開始日のみ指定して絞り込む */
    @Test
    void 正常系_受注日範囲検索_開始日のみ() {
        LocalDate from = LocalDate.of(2025, 7, 1);
        when(orderRepository.findByCondition(null, from, null)).thenReturn(List.of());

        orderService.findByCondition(null, from, null);

        verify(orderRepository, times(1)).findByCondition(null, from, null);
    }

    /** UT-F-03: 開始日・終了日ともに未指定で全件返却 */
    @Test
    void 正常系_受注日範囲検索_パラメータなし() {
        when(orderRepository.findByCondition(null, null, null)).thenReturn(List.of());

        orderService.findByCondition(null, null, null);

        verify(orderRepository, times(1)).findByCondition(null, null, null);
    }
}
