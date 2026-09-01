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

import com.example.ordersystem.entity.Customer;
import com.example.ordersystem.exception.ResourceNotFoundException;
import com.example.ordersystem.form.CustomerForm;
import com.example.ordersystem.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    /** UT-B-01 */
    @Test
    void 正常系_登録できること() {
        CustomerForm form = new CustomerForm();
        form.setCustomerName("テスト株式会社");

        customerService.save(form);

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    /** UT-B-02 */
    @Test
    void 正常系_全件取得できること() {
        customerService.findAll();

        verify(customerRepository, times(1)).findAll();
    }

    /** UT-B-03 */
    @Test
    void 正常系_IDで取得できること() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("テスト株式会社");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.findById(1L);

        assertNotNull(result);
        assertEquals("テスト株式会社", result.getCustomerName());
    }

    /** UT-B-04 */
    @Test
    void 異常系_存在しないIDで例外スロー() {
        when(customerRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.findById(99999L));
    }

    /** UT-B-05 */
    @Test
    void 正常系_顧客を更新できること() {
        Customer existing = new Customer();
        existing.setId(1L);
        existing.setCustomerName("更新前");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));

        CustomerForm form = new CustomerForm();
        form.setCustomerName("更新後");

        customerService.update(1L, form);

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    /** UT-B-06 */
    @Test
    void 正常系_顧客を削除できること() {
        customerService.deleteById(1L);

        verify(customerRepository, times(1)).deleteById(1L);
    }

    /** UT-D-01: 名前を指定した場合、CustomerRepository.findByName(name) が呼ばれること */
    @Test
    void 正常系_顧客名検索_名前指定あり() {
        when(customerRepository.findByName("山田")).thenReturn(List.of());

        customerService.findByName("山田");

        verify(customerRepository, times(1)).findByName("山田");
    }

    /** UT-D-02: 名前を指定しない場合、findByName(null) が呼ばれ、@Query の IS NULL 条件で全件返却される */
    @Test
    void 正常系_顧客名検索_名前未指定() {
        when(customerRepository.findByName(null)).thenReturn(List.of());

        customerService.findByName(null);

        verify(customerRepository, times(1)).findByName(null);
    }
}
