package com.example.service;

import com.example.exception.ResourceNotFoundException;
import com.example.exception.InsufficientStockException;
import com.example.model.dto.OrderRequestDTO;
import com.example.model.dto.OrderItemDTO;
import com.example.model.entity.*;
import com.example.model.enums.OrderStatus;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import com.example.repository.CustomerRepository;
import com.example.repository.ProductRepository;
import com.example.service.serviceimpl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private User testUser;
    private Customer testCustomer;
    private Product testProduct;
    private OrderRequestDTO orderRequestDTO;
    private OrderItemDTO orderItemDTO;

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
        testCustomer.setName("Jane Smith");
        testCustomer.setEmail("jane@example.com");

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setStock(10);

        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setCustomer(testCustomer);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("999.99"));

        // Setup order item DTO
        orderItemDTO = new OrderItemDTO();
        orderItemDTO.setProductId(1L);
        orderItemDTO.setQuantity(2);

        // Setup order request DTO
        orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setUserId(1L);
        orderRequestDTO.setCustomerId(1L);
        orderRequestDTO.setOrderItems(Arrays.asList(orderItemDTO));
    }

    @Nested
    @DisplayName("Order Retrieval Operations")
    class OrderRetrievalOperations {

        @Test
        @DisplayName("Should get all orders")
        void shouldGetAllOrders() {
            // Given
            List<Order> orders = Arrays.asList(testOrder);
            when(orderRepository.findAll()).thenReturn(orders);

            // When
            List<Order> result = orderService.getAllOrders();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            verify(orderRepository).findAll();
        }

        @Test
        @DisplayName("Should get order by ID")
        void shouldGetOrderById() {
            // Given
            when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

            // When
            Order result = orderService.getOrderById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
            verify(orderRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when order not found by ID")
        void shouldThrowExceptionWhenOrderNotFoundById() {
            // Given
            when(orderRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.getOrderById(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order not found with id: 1");

            verify(orderRepository).findById(1L);
        }

        @Test
        @DisplayName("Should get orders by user name")
        void shouldGetOrdersByUserName() {
            // Given
            List<Order> orders = Arrays.asList(testOrder);
            when(orderRepository.findByUser_Name("John Doe")).thenReturn(orders);

            // When
            List<Order> result = orderService.getOrdersByUserName("John Doe");

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUser().getName()).isEqualTo("John Doe");
            verify(orderRepository).findByUser_Name("John Doe");
        }

        @Test
        @DisplayName("Should get orders by status")
        void shouldGetOrdersByStatus() {
            // Given
            List<Order> orders = Arrays.asList(testOrder);
            when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(orders);

            // When
            List<Order> result = orderService.getOrdersByStatus(OrderStatus.PENDING);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
            verify(orderRepository).findByStatus(OrderStatus.PENDING);
        }

        @Test
        @DisplayName("Should return empty list when no orders found by user name")
        void shouldReturnEmptyListWhenNoOrdersFoundByUserName() {
            // Given
            when(orderRepository.findByUser_Name("NonExistent")).thenReturn(Arrays.asList());

            // When
            List<Order> result = orderService.getOrdersByUserName("NonExistent");

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(orderRepository).findByUser_Name("NonExistent");
        }
    }

    @Nested
    @DisplayName("Order Placement Operations")
    class OrderPlacementOperations {

        @Test
        @DisplayName("Should place order successfully")
        void shouldPlaceOrderSuccessfully() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
            when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));
            when(productService.hasStock(1L, 2)).thenReturn(true);
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            // When
            Order result = orderService.placeOrder(orderRequestDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUser()).isEqualTo(testUser);
            assertThat(result.getCustomer()).isEqualTo(testCustomer);
            verify(userRepository).findById(1L);
            verify(customerRepository).findById(1L);
            verify(productService).getProductById(1L);
            verify(productService).hasStock(1L, 2);
            verify(productService).reduceStock(1L, 2);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found during order placement")
        void shouldThrowExceptionWhenUserNotFoundDuringOrderPlacement() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: 1");

            verify(userRepository).findById(1L);
            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when customer not found during order placement")
        void shouldThrowExceptionWhenCustomerNotFoundDuringOrderPlacement() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(customerRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Customer not found with id: 1");

            verify(userRepository).findById(1L);
            verify(customerRepository).findById(1L);
            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when product not found during order placement")
        void shouldThrowExceptionWhenProductNotFoundDuringOrderPlacement() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
            when(productService.getProductById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: 1");

            verify(userRepository).findById(1L);
            verify(customerRepository).findById(1L);
            verify(productService).getProductById(1L);
            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when insufficient stock during order placement")
        void shouldThrowExceptionWhenInsufficientStockDuringOrderPlacement() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
            when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));
            when(productService.hasStock(1L, 2)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequestDTO))
                    .isInstanceOf(InsufficientStockException.class)
                    .hasMessageContaining("Insufficient stock");

            verify(userRepository).findById(1L);
            verify(customerRepository).findById(1L);
            verify(productService).getProductById(1L);
            verify(productService).hasStock(1L, 2);
            verify(productService, never()).reduceStock(any(), anyInt());
            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Order Cancellation Operations")
    class OrderCancellationOperations {

        @Test
        @DisplayName("Should cancel order successfully")
        void shouldCancelOrderSuccessfully() {
            // Given
            testOrder.setStatus(OrderStatus.PENDING);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(testProduct);
            orderItem.setQuantity(2);
            testOrder.setItems(Arrays.asList(orderItem));

            when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            // When
            orderService.cancelOrder(1L);

            // Then
            verify(orderRepository).findById(1L);
            verify(productService).restoreStock(1L, 2);
            verify(orderRepository).save(testOrder);
            assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
        }

        @Test
        @DisplayName("Should handle cancelling non-existent order silently")
        void shouldHandleCancellingNonExistentOrderSilently() {
            // Given
            when(orderRepository.findById(1L)).thenReturn(Optional.empty());

            // When
            orderService.cancelOrder(1L);

            // Then
            verify(orderRepository).findById(1L);
            verify(orderRepository, never()).save(any());
            verify(productService, never()).restoreStock(anyLong(), anyInt());
        }

        @Test
        @DisplayName("Should handle cancelling already cancelled order silently")
        void shouldHandleCancellingAlreadyCancelledOrderSilently() {
            // Given
            testOrder.setStatus(OrderStatus.CANCELED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

            // When
            orderService.cancelOrder(1L);

            // Then
            verify(orderRepository).findById(1L);
            verify(orderRepository, never()).save(any());
            verify(productService, never()).restoreStock(anyLong(), anyInt());
            assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
        }

        @Test
        @DisplayName("Should handle cancelling completed order silently")
        void shouldHandleCancellingCompletedOrderSilently() {
            // Given
            testOrder.setStatus(OrderStatus.COMPLETED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

            // When
            orderService.cancelOrder(1L);

            // Then
            verify(orderRepository).findById(1L);
            verify(orderRepository, never()).save(any());
            verify(productService, never()).restoreStock(anyLong(), anyInt());
            assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Validation")
    class EdgeCasesAndValidation {

        @Test
        @DisplayName("Should handle null order request")
        void shouldHandleNullOrderRequest() {
            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order request cannot be null");
        }

        @Test
        @DisplayName("Should handle invalid quantity in order request")
        void shouldHandleInvalidQuantityInOrderRequest() {
            // Given
            orderItemDTO.setQuantity(0);

            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantity must be greater than 0");
        }

        @Test
        @DisplayName("Should handle negative quantity in order request")
        void shouldHandleNegativeQuantityInOrderRequest() {
            // Given
            orderItemDTO.setQuantity(-1);

            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantity must be greater than 0");
        }

        @Test
        @DisplayName("Should handle null user ID in order request")
        void shouldHandleNullUserIdInOrderRequest() {
            // Given
            orderRequestDTO.setUserId(null);

            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User ID cannot be null");
        }

        @Test
        @DisplayName("Should handle null customer ID in order request")
        void shouldHandleNullCustomerIdInOrderRequest() {
            // Given
            orderRequestDTO.setCustomerId(null);

            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Customer ID cannot be null");
        }

        @Test
        @DisplayName("Should handle null product ID in order request")
        void shouldHandleNullProductIdInOrderRequest() {
            // Given
            orderItemDTO.setProductId(null);

            // When & Then
            assertThatThrownBy(() -> orderService.placeOrder(orderRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Product ID cannot be null");
        }

        @Test
        @DisplayName("Should handle order with no items during cancellation")
        void shouldHandleOrderWithNoItemsDuringCancellation() {
            // Given
            testOrder.setStatus(OrderStatus.PENDING);
            testOrder.setItems(Arrays.asList()); // Empty list
            when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            // When
            orderService.cancelOrder(1L);

            // Then
            verify(orderRepository).findById(1L);
            verify(productService, never()).restoreStock(any(), anyInt());
            verify(orderRepository).save(testOrder);
            assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
        }
    }

    @Nested
    @DisplayName("Additional Order Service Tests")
    class AdditionalOrderServiceTests {

        @Test
        @DisplayName("Should successfully place order with direct repository mocking")
        void testPlaceOrder_Success() {
            // Given
            Long userId = 1L;
            Long customerId = 1L;
            List<OrderItemDTO> orderItems = List.of(
                    new OrderItemDTO(1L, 2),
                    new OrderItemDTO(2L, 1));
            OrderRequestDTO orderRequest = new OrderRequestDTO(userId, customerId, orderItems);

            Product product1 = new Product();
            product1.setId(1L);
            product1.setName("Gaming Laptop");
            product1.setPrice(new BigDecimal("999.99"));
            product1.setStock(10);

            Product product2 = new Product();
            product2.setId(2L);
            product2.setName("Gaming Mouse");
            product2.setPrice(new BigDecimal("29.99"));
            product2.setStock(5);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));
            when(productService.getProductById(1L)).thenReturn(Optional.of(product1));
            when(productService.getProductById(2L)).thenReturn(Optional.of(product2));
            when(productService.hasStock(1L, 2)).thenReturn(true);
            when(productService.hasStock(2L, 1)).thenReturn(true);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(1L);
                return order;
            });

            // When
            Order result = orderService.placeOrder(orderRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getCustomer().getName()).isEqualTo(testCustomer.getName());
            assertThat(result.getCustomer().getEmail()).isEqualTo(testCustomer.getEmail());
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(result.getUser()).isEqualTo(testUser);
            assertThat(result.getCustomer()).isEqualTo(testCustomer);
            assertThat(result.getItems()).hasSize(2);

            // Verify total amount calculation: (999.99 * 2) + (29.99 * 1) = 2029.97
            BigDecimal expectedTotal = new BigDecimal("999.99").multiply(new BigDecimal("2"))
                    .add(new BigDecimal("29.99").multiply(new BigDecimal("1")));
            assertThat(result.getTotalAmount()).isEqualByComparingTo(expectedTotal);

            // Verify repository interactions
            verify(userRepository).findById(userId);
            verify(customerRepository).findById(customerId);
            verify(productService).getProductById(1L);
            verify(productService).getProductById(2L);
            verify(productService).hasStock(1L, 2);
            verify(productService).hasStock(2L, 1);
            verify(productService).reduceStock(1L, 2);
            verify(productService).reduceStock(2L, 1);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Should cancel PENDING order and restore stock")
        void testCancelOrder_WithStockRestore() {
            // Given
            Order order = new Order();
            order.setId(1L);
            order.setStatus(OrderStatus.PENDING);

            Product product = new Product();
            product.setId(1L);
            product.setStock(10);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(2);
            order.setItems(List.of(orderItem));

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // When
            orderService.cancelOrder(1L);

            // Then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);

            verify(orderRepository).findById(1L);
            verify(productService).restoreStock(1L, 2);
            verify(orderRepository).save(order);
        }
    }
}
