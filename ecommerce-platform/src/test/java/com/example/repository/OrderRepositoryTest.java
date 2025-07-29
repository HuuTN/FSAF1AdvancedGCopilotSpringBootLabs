package com.example.repository;

import com.example.model.entity.*;
import com.example.model.enums.OrderStatus;
import com.example.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Order Repository Tests")
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private User testUser1;
    private User testUser2;
    private Customer testCustomer1;
    private Customer testCustomer2;
    private Order testOrder1;
    private Order testOrder2;
    private Order testOrder3;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User();
        testUser1.setName("John Doe");
        testUser1.setEmail("john@example.com");
        testUser1.setPassword("password123");
        testUser1.setRole(UserRole.ADMIN);
        entityManager.persistAndFlush(testUser1);

        testUser2 = new User();
        testUser2.setName("Jane Smith");
        testUser2.setEmail("jane@example.com");
        testUser2.setPassword("password456");
        testUser2.setRole(UserRole.EMPLOYEE);
        entityManager.persistAndFlush(testUser2);

        // Create test customers
        testCustomer1 = new Customer();
        testCustomer1.setName("Customer One");
        testCustomer1.setEmail("customer1@example.com");
        testCustomer1.setPhone("123-456-7890");
        testCustomer1.setAddress("123 Main St");
        testCustomer1.setCreatedAt(LocalDateTime.now());
        testCustomer1.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(testCustomer1);

        testCustomer2 = new Customer();
        testCustomer2.setName("Customer Two");
        testCustomer2.setEmail("customer2@example.com");
        testCustomer2.setPhone("987-654-3210");
        testCustomer2.setAddress("456 Oak Ave");
        testCustomer2.setCreatedAt(LocalDateTime.now());
        testCustomer2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(testCustomer2);

        // Create test orders
        testOrder1 = new Order();
        testOrder1.setUser(testUser1);
        testOrder1.setCustomer(testCustomer1);
        testOrder1.setStatus(OrderStatus.PENDING);
        testOrder1.setTotalAmount(new BigDecimal("100.00"));
        testOrder1.setCreatedAt(LocalDateTime.now());
        testOrder1.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(testOrder1);

        testOrder2 = new Order();
        testOrder2.setUser(testUser1);
        testOrder2.setCustomer(testCustomer2);
        testOrder2.setStatus(OrderStatus.COMPLETED);
        testOrder2.setTotalAmount(new BigDecimal("200.00"));
        testOrder2.setCreatedAt(LocalDateTime.now());
        testOrder2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(testOrder2);

        testOrder3 = new Order();
        testOrder3.setUser(testUser2);
        testOrder3.setCustomer(testCustomer1);
        testOrder3.setStatus(OrderStatus.PENDING);
        testOrder3.setTotalAmount(new BigDecimal("150.00"));
        testOrder3.setCreatedAt(LocalDateTime.now());
        testOrder3.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(testOrder3);
    }

    // Helper method to create order with proper timestamps
    private Order createOrderWithTimestamps(User user, Customer customer, OrderStatus status, BigDecimal totalAmount) {
        Order order = new Order();
        order.setUser(user);
        order.setCustomer(customer);
        order.setStatus(status);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save order successfully")
        void shouldSaveOrderSuccessfully() {
            // Given
            Order newOrder = createOrderWithTimestamps(testUser2, testCustomer2, OrderStatus.CONFIRMED,
                    new BigDecimal("300.00"));

            // When
            Order savedOrder = orderRepository.save(newOrder);

            // Then
            assertThat(savedOrder).isNotNull();
            assertThat(savedOrder.getId()).isNotNull();
            assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(savedOrder.getTotalAmount()).isEqualTo(new BigDecimal("300.00"));
        }

        @Test
        @DisplayName("Should find order by ID")
        void shouldFindOrderById() {
            // When
            Optional<Order> foundOrder = orderRepository.findById(testOrder1.getId());

            // Then
            assertThat(foundOrder).isPresent();
            assertThat(foundOrder.get().getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(foundOrder.get().getTotalAmount()).isEqualTo(new BigDecimal("100.00"));
        }

        @Test
        @DisplayName("Should return empty when order not found by ID")
        void shouldReturnEmptyWhenOrderNotFoundById() {
            // When
            Optional<Order> foundOrder = orderRepository.findById(999L);

            // Then
            assertThat(foundOrder).isEmpty();
        }

        @Test
        @DisplayName("Should find all orders")
        void shouldFindAllOrders() {
            // When
            List<Order> orders = orderRepository.findAll();

            // Then
            assertThat(orders).hasSize(3);
            assertThat(orders).extracting(Order::getStatus)
                    .containsExactlyInAnyOrder(OrderStatus.PENDING, OrderStatus.COMPLETED, OrderStatus.PENDING);
        }

        @Test
        @DisplayName("Should delete order by ID")
        void shouldDeleteOrderById() {
            // Given
            Long orderId = testOrder1.getId();

            // When
            orderRepository.deleteById(orderId);
            entityManager.flush();

            // Then
            Optional<Order> deletedOrder = orderRepository.findById(orderId);
            assertThat(deletedOrder).isEmpty();

            List<Order> remainingOrders = orderRepository.findAll();
            assertThat(remainingOrders).hasSize(2);
        }

        @Test
        @DisplayName("Should update order successfully")
        void shouldUpdateOrderSuccessfully() {
            // Given
            Order orderToUpdate = testOrder1;
            orderToUpdate.setStatus(OrderStatus.CONFIRMED);
            orderToUpdate.setTotalAmount(new BigDecimal("120.00"));

            // When
            Order updatedOrder = orderRepository.save(orderToUpdate);
            entityManager.flush();

            // Then
            assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(updatedOrder.getTotalAmount()).isEqualTo(new BigDecimal("120.00"));

            // Verify in database
            Optional<Order> foundOrder = orderRepository.findById(testOrder1.getId());
            assertThat(foundOrder).isPresent();
            assertThat(foundOrder.get().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        }
    }

    @Nested
    @DisplayName("Find by User Operations")
    class FindByUserOperations {

        @Test
        @DisplayName("Should find orders by user name")
        void shouldFindOrdersByUserName() {
            // When
            List<Order> orders = orderRepository.findByUser_Name("John Doe");

            // Then
            assertThat(orders).isNotNull();
            assertThat(orders).hasSize(2);
            assertThat(orders).extracting(order -> order.getUser().getName())
                    .containsOnly("John Doe");
            assertThat(orders).extracting(Order::getStatus)
                    .containsExactlyInAnyOrder(OrderStatus.PENDING, OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("Should find orders by user ID")
        void shouldFindOrdersByUserId() {
            // When
            List<Order> orders = orderRepository.findByUser_Id(testUser1.getId());

            // Then
            assertThat(orders).isNotNull();
            assertThat(orders).hasSize(2);
            assertThat(orders).extracting(order -> order.getUser().getId())
                    .containsOnly(testUser1.getId());
        }

        @Test
        @DisplayName("Should return empty list for non-existent user name")
        void shouldReturnEmptyListForNonExistentUserName() {
            // When
            List<Order> orders = orderRepository.findByUser_Name("NonExistent User");

            // Then
            assertThat(orders).isNotNull();
            assertThat(orders).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list for non-existent user ID")
        void shouldReturnEmptyListForNonExistentUserId() {
            // When
            List<Order> orders = orderRepository.findByUser_Id(999L);

            // Then
            assertThat(orders).isNotNull();
            assertThat(orders).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find by Customer Operations")
    class FindByCustomerOperations {

        @Test
        @DisplayName("Should find orders by customer ID")
        void shouldFindOrdersByCustomerId() {
            // When
            List<Order> orders = orderRepository.findByCustomer_Id(testCustomer1.getId());

            // Then
            assertThat(orders).isNotNull();
            assertThat(orders).hasSize(2);
            assertThat(orders).extracting(order -> order.getCustomer().getId())
                    .containsOnly(testCustomer1.getId());
        }

        @Test
        @DisplayName("Should return empty list for non-existent customer ID")
        void shouldReturnEmptyListForNonExistentCustomerId() {
            // When
            List<Order> orders = orderRepository.findByCustomer_Id(999L);

            // Then
            assertThat(orders).isNotNull();
            assertThat(orders).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find by Status Operations")
    class FindByStatusOperations {

        @Test
        @DisplayName("Should find orders by status PENDING")
        void shouldFindOrdersByStatusPending() {
            // When
            List<Order> orders = orderRepository.findByStatus(OrderStatus.PENDING);

            // Then
            assertThat(orders).isNotNull();
            assertThat(orders).hasSize(2);
            assertThat(orders).extracting(Order::getStatus)
                    .containsOnly(OrderStatus.PENDING);
        }

        @Test
        @DisplayName("Should find orders by status COMPLETED")
        void shouldFindOrdersByStatusCompleted() {
            // When
            List<Order> orders = orderRepository.findByStatus(OrderStatus.COMPLETED);

            // Then
            assertThat(orders).isNotNull();
            assertThat(orders).hasSize(1);
            assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(orders.get(0).getUser().getName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should return empty list for non-existent status")
        void shouldReturnEmptyListForNonExistentStatus() {
            // When
            List<Order> orders = orderRepository.findByStatus(OrderStatus.CANCELED);

            // Then
            assertThat(orders).isNotNull();
            assertThat(orders).isEmpty();
        }
    }

    @Nested
    @DisplayName("Entity Relationships")
    class EntityRelationships {

        @Test
        @DisplayName("Should maintain user relationship")
        void shouldMaintainUserRelationship() {
            // When
            Optional<Order> order = orderRepository.findById(testOrder1.getId());

            // Then
            assertThat(order).isPresent();
            assertThat(order.get().getUser()).isNotNull();
            assertThat(order.get().getUser().getName()).isEqualTo("John Doe");
            assertThat(order.get().getUser().getEmail()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("Should maintain customer relationship")
        void shouldMaintainCustomerRelationship() {
            // When
            Optional<Order> order = orderRepository.findById(testOrder1.getId());

            // Then
            assertThat(order).isPresent();
            assertThat(order.get().getCustomer()).isNotNull();
            assertThat(order.get().getCustomer().getName()).isEqualTo("Customer One");
            assertThat(order.get().getCustomer().getEmail()).isEqualTo("customer1@example.com");
        }

        @Test
        @DisplayName("Should load relationships lazily when needed")
        void shouldLoadRelationshipsLazilyWhenNeeded() {
            // When
            Order order = orderRepository.findById(testOrder1.getId()).orElseThrow();

            // Clear the persistence context to test lazy loading
            entityManager.clear();

            // Access user and customer should still work due to the relationship
            User user = order.getUser();
            Customer customer = order.getCustomer();

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getName()).isEqualTo("John Doe");
            assertThat(customer).isNotNull();
            assertThat(customer.getName()).isEqualTo("Customer One");
        }
    }

    @Nested
    @DisplayName("Complex Query Tests")
    class ComplexQueryTests {

        @Test
        @DisplayName("Should find orders by multiple criteria")
        void shouldFindOrdersByMultipleCriteria() {
            // Create specific test data
            Order specificOrder = createOrderWithTimestamps(testUser1, testCustomer1, OrderStatus.SHIPPED,
                    new BigDecimal("250.00"));
            entityManager.persistAndFlush(specificOrder);

            // When
            List<Order> pendingOrdersForUser1 = orderRepository.findByUser_Id(testUser1.getId());
            List<Order> shippedOrders = orderRepository.findByStatus(OrderStatus.SHIPPED);

            // Then
            assertThat(pendingOrdersForUser1).hasSize(3); // 2 original + 1 new
            assertThat(shippedOrders).hasSize(1);
            assertThat(shippedOrders.get(0).getTotalAmount()).isEqualTo(new BigDecimal("250.00"));
        }

        @Test
        @DisplayName("Should handle orders with same user and customer")
        void shouldHandleOrdersWithSameUserAndCustomer() {
            // When - Find orders where user1 ordered for customer1
            List<Order> user1Orders = orderRepository.findByUser_Id(testUser1.getId());
            List<Order> customer1Orders = orderRepository.findByCustomer_Id(testCustomer1.getId());

            // Then
            assertThat(user1Orders).hasSize(2);
            assertThat(customer1Orders).hasSize(2);

            // Find intersection - orders by user1 for customer1
            List<Order> user1Customer1Orders = user1Orders.stream()
                    .filter(order -> order.getCustomer().getId().equals(testCustomer1.getId()))
                    .toList();

            assertThat(user1Customer1Orders).hasSize(1);
            assertThat(user1Customer1Orders.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Data Integrity")
    class EdgeCasesAndDataIntegrity {

        @Test
        @DisplayName("Should handle orders with minimum required data")
        void shouldHandleOrdersWithMinimumRequiredData() {
            // Given
            Order minimalOrder = createOrderWithTimestamps(testUser1, testCustomer1, OrderStatus.PENDING,
                    BigDecimal.ZERO);

            // When
            Order savedOrder = orderRepository.save(minimalOrder);

            // Then
            assertThat(savedOrder).isNotNull();
            assertThat(savedOrder.getId()).isNotNull();
            assertThat(savedOrder.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should handle orders with large total amounts")
        void shouldHandleOrdersWithLargeTotalAmounts() {
            // Given
            Order largeAmountOrder = createOrderWithTimestamps(testUser1, testCustomer1, OrderStatus.PENDING,
                    new BigDecimal("999999.99"));

            // When
            Order savedOrder = orderRepository.save(largeAmountOrder);

            // Then
            assertThat(savedOrder).isNotNull();
            assertThat(savedOrder.getTotalAmount()).isEqualTo(new BigDecimal("999999.99"));
        }

        @Test
        @DisplayName("Should maintain data consistency across operations")
        void shouldMaintainDataConsistencyAcrossOperations() {
            // Given
            int initialOrderCount = orderRepository.findAll().size();

            // When - Create a new order
            Order newOrder = createOrderWithTimestamps(testUser2, testCustomer2, OrderStatus.PROCESSING,
                    new BigDecimal("75.00"));
            orderRepository.save(newOrder);

            // Then - Verify counts are consistent
            List<Order> allOrders = orderRepository.findAll();
            List<Order> user2Orders = orderRepository.findByUser_Id(testUser2.getId());
            List<Order> processingOrders = orderRepository.findByStatus(OrderStatus.PROCESSING);

            assertThat(allOrders).hasSize(initialOrderCount + 1);
            assertThat(user2Orders).hasSize(2); // testOrder3 + newOrder
            assertThat(processingOrders).hasSize(1);
        }
    }

    @Test
    @DisplayName("Should verify repository injection")
    void shouldVerifyRepositoryInjection() {
        assertThat(orderRepository).isNotNull();
        assertThat(entityManager).isNotNull();
    }
}
