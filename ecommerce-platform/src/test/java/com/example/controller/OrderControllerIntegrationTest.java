package com.example.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.exception.InsufficientStockException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.OrderMapper;
import com.example.model.dto.OrderInfoDTO;
import com.example.model.dto.OrderItemDTO;
import com.example.model.dto.OrderRequestDTO;
import com.example.model.entity.Customer;
import com.example.model.entity.Order;
import com.example.model.entity.OrderItem;
import com.example.model.entity.Product;
import com.example.model.entity.User;
import com.example.model.enums.OrderStatus;
import com.example.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderMapper orderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private OrderInfoDTO testOrderInfoDTO;
    private User testUser;
    private Customer testCustomer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Jane Customer");
        testCustomer.setEmail("jane@example.com");

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));

        // Setup test order item
        OrderItem testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setQuantity(2);
        testOrderItem.setPrice(testProduct.getPrice());

        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setCustomer(testCustomer);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setItems(Arrays.asList(testOrderItem));
        testOrder.setTotalAmount(new BigDecimal("199.98"));
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());

        // Setup test order info DTO
        testOrderInfoDTO = new OrderInfoDTO(
                testOrder.getId(),
                testCustomer.getName(),
                testCustomer.getEmail(),
                testUser.getName(),
                testOrder.getTotalAmount(),
                testOrder.getStatus(),
                Arrays.asList(new OrderInfoDTO.ProductInfo(
                        testProduct.getId(),
                        testProduct.getName(),
                        "Test Category",
                        2,
                        testProduct.getPrice().toString())));
    }

    @Nested
    @DisplayName("GET /api/v1/orders")
    class GetAllOrdersTests {
        @Test
        @DisplayName("Should return all orders successfully")
        void shouldReturnAllOrdersSuccessfully() throws Exception {
            List<Order> orders = Arrays.asList(testOrder);
            List<OrderInfoDTO> orderDTOs = Arrays.asList(testOrderInfoDTO);

            when(orderService.getAllOrders()).thenReturn(orders);
            when(orderMapper.toOrderInfoDTOs(orders)).thenReturn(orderDTOs);

            mockMvc.perform(get("/api/v1/orders"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].orderId", is(testOrder.getId().intValue())))
                    .andExpect(jsonPath("$[0].customerName", is(testCustomer.getName())))
                    .andExpect(jsonPath("$[0].status", is("PENDING")));

            verify(orderService).getAllOrders();
            verify(orderMapper).toOrderInfoDTOs(orders);
        }

        @Test
        @DisplayName("Should return empty list when no orders exist")
        void shouldReturnEmptyListWhenNoOrdersExist() throws Exception {
            when(orderService.getAllOrders()).thenReturn(Arrays.asList());
            when(orderMapper.toOrderInfoDTOs(Arrays.asList())).thenReturn(Arrays.asList());

            mockMvc.perform(get("/api/v1/orders"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(orderService).getAllOrders();
            verify(orderMapper).toOrderInfoDTOs(Arrays.asList());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/{id}")
    class GetOrderByIdTests {
        @Test
        @DisplayName("Should return order by ID successfully")
        void shouldReturnOrderByIdSuccessfully() throws Exception {
            when(orderService.getOrderById(1L)).thenReturn(testOrder);
            when(orderMapper.toOrderInfoDTO(testOrder)).thenReturn(testOrderInfoDTO);

            mockMvc.perform(get("/api/v1/orders/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.orderId", is(testOrder.getId().intValue())))
                    .andExpect(jsonPath("$.customerName", is(testCustomer.getName())))
                    .andExpect(jsonPath("$.status", is("PENDING")));

            verify(orderService).getOrderById(1L);
            verify(orderMapper).toOrderInfoDTO(testOrder);
        }

        @Test
        @DisplayName("Should return 404 when order not found")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            when(orderService.getOrderById(999L))
                    .thenThrow(new ResourceNotFoundException("Order not found with id: 999"));

            mockMvc.perform(get("/api/v1/orders/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(orderService).getOrderById(999L);
            verify(orderMapper, never()).toOrderInfoDTO(any());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/status/{status}")
    class GetOrdersByStatusTests {
        @Test
        @DisplayName("Should return orders by status successfully")
        void shouldReturnOrdersByStatusSuccessfully() throws Exception {
            List<Order> orders = Arrays.asList(testOrder);
            List<OrderInfoDTO> orderDTOs = Arrays.asList(testOrderInfoDTO);

            when(orderService.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(orders);
            when(orderMapper.toOrderInfoDTOs(orders)).thenReturn(orderDTOs);

            mockMvc.perform(get("/api/v1/orders/status/{status}", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status", is("PENDING")));

            verify(orderService).getOrdersByStatus(OrderStatus.PENDING);
            verify(orderMapper).toOrderInfoDTOs(orders);
        }

        @Test
        @DisplayName("Should handle invalid order status")
        void shouldHandleInvalidOrderStatus() throws Exception {
            mockMvc.perform(get("/api/v1/orders/status/{status}", "INVALID_STATUS"))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).getOrdersByStatus(any());
            verify(orderMapper, never()).toOrderInfoDTOs(any());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/user/name/{userName}")
    class GetOrdersByUserNameTests {
        @Test
        @DisplayName("Should return orders by user name successfully")
        void shouldReturnOrdersByUserNameSuccessfully() throws Exception {
            List<Order> orders = Arrays.asList(testOrder);
            List<OrderInfoDTO> orderDTOs = Arrays.asList(testOrderInfoDTO);

            when(orderService.getOrdersByUserName("John Doe")).thenReturn(orders);
            when(orderMapper.toOrderInfoDTOs(orders)).thenReturn(orderDTOs);

            mockMvc.perform(get("/api/v1/orders/user/name/{userName}", "John Doe"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].orderedBy", is("John Doe")));

            verify(orderService).getOrdersByUserName("John Doe");
            verify(orderMapper).toOrderInfoDTOs(orders);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/orders")
    class PlaceOrderTests {
        private OrderRequestDTO orderRequest;

        @BeforeEach
        void setUp() {
            OrderItemDTO orderItemDTO = new OrderItemDTO(1L, 2);
            orderRequest = new OrderRequestDTO(1L, 1L, Arrays.asList(orderItemDTO));
        }

        @Test
        @DisplayName("Should place order successfully")
        void shouldPlaceOrderSuccessfully() throws Exception {
            when(orderService.placeOrder(any(OrderRequestDTO.class))).thenReturn(testOrder);
            when(orderMapper.toOrderInfoDTO(testOrder)).thenReturn(testOrderInfoDTO);

            mockMvc.perform(post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.orderId", is(testOrder.getId().intValue())))
                    .andExpect(jsonPath("$.status", is("PENDING")));

            verify(orderService).placeOrder(any(OrderRequestDTO.class));
            verify(orderMapper).toOrderInfoDTO(testOrder);
        }

        @Test
        @DisplayName("Should handle insufficient stock")
        void shouldHandleInsufficientStock() throws Exception {
            when(orderService.placeOrder(any(OrderRequestDTO.class)))
                    .thenThrow(new InsufficientStockException("Test Product", 1, 2));

            mockMvc.perform(post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isBadRequest());

            verify(orderService).placeOrder(any(OrderRequestDTO.class));
            verify(orderMapper, never()).toOrderInfoDTO(any());
        }

        @Test
        @DisplayName("Should handle invalid request data")
        void shouldHandleInvalidRequestData() throws Exception {
            orderRequest.setUserId(null); // Invalid request

            mockMvc.perform(post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).placeOrder(any());
            verify(orderMapper, never()).toOrderInfoDTO(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/orders/{id}/cancel")
    class CancelOrderTests {
        @Test
        @DisplayName("Should cancel order successfully")
        void shouldCancelOrderSuccessfully() throws Exception {
            testOrder.setStatus(OrderStatus.CANCELED);
            OrderInfoDTO canceledOrderDTO = new OrderInfoDTO(
                    testOrder.getId(),
                    testCustomer.getName(),
                    testCustomer.getEmail(),
                    testUser.getName(),
                    testOrder.getTotalAmount(),
                    OrderStatus.CANCELED,
                    Arrays.asList(new OrderInfoDTO.ProductInfo(
                            testProduct.getId(),
                            testProduct.getName(),
                            "Test Category",
                            2,
                            testProduct.getPrice().toString())));
            when(orderService.getOrderById(1L)).thenReturn(testOrder);
            when(orderMapper.toOrderInfoDTO(testOrder)).thenReturn(canceledOrderDTO);

            mockMvc.perform(put("/api/v1/orders/{id}/cancel", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.orderId", is(testOrder.getId().intValue())))
                    .andExpect(jsonPath("$.status", is("CANCELED")));

            verify(orderService).cancelOrder(1L);
            verify(orderService).getOrderById(1L);
            verify(orderMapper).toOrderInfoDTO(testOrder);
        }

        @Test
        @DisplayName("Should handle cancellation of non-existent order")
        void shouldHandleCancellationOfNonExistentOrder() throws Exception {
            doThrow(new ResourceNotFoundException("Order not found with id: 999"))
                    .when(orderService).cancelOrder(999L);

            mockMvc.perform(put("/api/v1/orders/{id}/cancel", 999L))
                    .andExpect(status().isNotFound());

            verify(orderService).cancelOrder(999L);
            verify(orderMapper, never()).toOrderInfoDTO(any());
        }
    }
}
