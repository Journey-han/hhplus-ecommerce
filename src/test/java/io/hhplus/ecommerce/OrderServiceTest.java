package io.hhplus.ecommerce;

import io.hhplus.ecommerce.app.infrastructure.persistence.ProductStockRepository;
import io.hhplus.ecommerce.app.application.service.OrderService;
import io.hhplus.ecommerce.app.domain.common.OrderStatus;
import io.hhplus.ecommerce.app.domain.model.Order;
import io.hhplus.ecommerce.app.application.request.OrderItemRequest;
import io.hhplus.ecommerce.app.application.request.OrderRequest;
import io.hhplus.ecommerce.app.application.response.OrderResponse;
import io.hhplus.ecommerce.app.infrastructure.persistence.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OrderServiceTest {

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ProductStockRepository productStockRepository;

    @Autowired
    private OrderService orderService;

    @Test
    public void testCreateOrder() {
        Long userId = 1L;
        List<OrderItemRequest> items = List.of(
                new OrderItemRequest(1L, 2),
                new OrderItemRequest(2L, 1)
        );

        OrderRequest orderRequest = new OrderRequest(items);
        Order order = new Order(userId, OrderStatus.PENDING.getMessage());

        Mockito.when(productStockRepository.getCurrentStock(1L)).thenReturn(10);
        Mockito.when(productStockRepository.getCurrentStock(2L)).thenReturn(5);
        //Mockito.when(orderRepository.saveOrder(order)).thenReturn(order);

        OrderResponse response = orderService.createOrder(1001L, orderRequest);

        assertEquals(OrderStatus.COMPLETED.getMessage(), response.getStatus());
        Mockito.verify(orderRepository).saveOrder(order);
    }
}
