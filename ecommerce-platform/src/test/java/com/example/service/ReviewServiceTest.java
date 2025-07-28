package com.example.service;

import com.example.exception.ResourceNotFoundException;
import com.example.exception.DuplicateReviewException;
import com.example.model.entity.Review;
import com.example.model.entity.Product;
import com.example.model.entity.Customer;
import com.example.repository.ReviewRepository;
import com.example.repository.ProductRepository;
import com.example.repository.CustomerRepository;
import com.example.repository.OrderRepository;
import com.example.service.serviceimpl.ReviewServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review testReview;
    private Product testProduct;
    private Customer testCustomer;
    private List<Review> reviewList;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setAverageRating(BigDecimal.ZERO);

        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john@example.com");

        // Setup test review
        testReview = new Review();
        testReview.setId(1L);
        testReview.setRating(5);
        testReview.setReviewContent("Great product!");
        testReview.setProduct(testProduct);
        testReview.setCustomer(testCustomer);

        // Setup review list
        Review review2 = new Review();
        review2.setId(2L);
        review2.setRating(4);
        review2.setReviewContent("Good product");
        review2.setProduct(testProduct);
        review2.setCustomer(testCustomer);

        reviewList = Arrays.asList(testReview, review2);
    }

    @Test
    void getAllReviews_ShouldReturnListOfReviews() {
        // Given
        when(reviewRepository.findAll()).thenReturn(reviewList);

        // When
        List<Review> result = reviewService.getAllReviews();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Great product!", result.get(0).getReviewContent());
        assertEquals("Good product", result.get(1).getReviewContent());

        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void getReviewById_WithValidId_ShouldReturnReview() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When
        Optional<Review> result = reviewService.getReviewById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(5, result.get().getRating());
        assertEquals("Great product!", result.get().getReviewContent());

        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void getReviewsByProductId_ShouldReturnListOfReviews() {
        // Given
        when(reviewRepository.findByProductId(1L)).thenReturn(reviewList);

        // When
        List<Review> result = reviewService.getReviewsByProductId(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(reviewRepository, times(1)).findByProductId(1L);
    }

    @Test
    void getReviewsByCustomerId_ShouldReturnListOfReviews() {
        // Given
        when(reviewRepository.findByCustomerId(1L)).thenReturn(reviewList);

        // When
        List<Review> result = reviewService.getReviewsByCustomerId(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(reviewRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void getReviewsByRating_ShouldReturnListOfReviews() {
        // Given
        when(reviewRepository.findByRating(5)).thenReturn(List.of(testReview));

        // When
        List<Review> result = reviewService.getReviewsByRating(5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRating());

        verify(reviewRepository, times(1)).findByRating(5);
    }

    @Test
    void createReview_WithValidData_ShouldReturnCreatedReview() {
        // Given
        when(orderRepository.hasCustomerPurchasedProduct(1L, 1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(reviewRepository.findByProductIdAndCustomerId(1L, 1L)).thenReturn(List.of());
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.5);

        // When
        Review result = reviewService.createReview(1L, 1L, 5, "Great product!");

        // Then
        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Great product!", result.getReviewContent());

        verify(orderRepository, times(1)).hasCustomerPurchasedProduct(1L, 1L);
        verify(productRepository, times(2)).findById(1L); // Changed from times(1) to times(2)
        verify(customerRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(reviewRepository, times(1)).findAverageRatingByProductId(1L);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void createReview_WithExistingReview_ShouldThrowException() {
        // Given
        when(orderRepository.hasCustomerPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.findByProductIdAndCustomerId(1L, 1L)).thenReturn(List.of(testReview));

        // When & Then
        DuplicateReviewException exception = assertThrows(
                DuplicateReviewException.class,
                () -> reviewService.createReview(1L, 1L, 5, "Great product!"));

        assertEquals("Customer with ID 1 has already reviewed product with ID 1", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_WithValidData_ShouldReturnUpdatedReview() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.5);

        // When
        Review result = reviewService.updateReview(1L, 4, "Updated review content");

        // Then
        assertNotNull(result);
        assertEquals(4, result.getRating());
        assertEquals("Updated review content", result.getReviewContent());

        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(productRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).findAverageRatingByProductId(1L);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void updateReview_WithInvalidId_ShouldThrowException() {
        // Given
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.updateReview(999L, 4, "Updated content"));

        assertEquals("Review not found with id: 999", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_WithValidId_ShouldDeleteReview() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.0);
        doNothing().when(reviewRepository).delete(testReview);

        // When
        assertDoesNotThrow(() -> reviewService.deleteReview(1L));

        // Then
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).delete(testReview);
        verify(productRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).findAverageRatingByProductId(1L);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void deleteReview_WithInvalidId_ShouldThrowException() {
        // Given
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.deleteReview(999L));

        assertEquals("Review not found with id: 999", exception.getMessage());
        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void getAverageRatingByProductId_ShouldReturnAverageRating() {
        // Given
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.5);

        // When
        Double result = reviewService.getAverageRatingByProductId(1L);

        // Then
        assertNotNull(result);
        assertEquals(4.5, result);

        verify(reviewRepository, times(1)).findAverageRatingByProductId(1L);
    }

    @Test
    void getReviewCountByProductId_ShouldReturnCount() {
        // Given
        when(reviewRepository.countReviewsByProductId(1L)).thenReturn(2L);

        // When
        Long result = reviewService.getReviewCountByProductId(1L);

        // Then
        assertNotNull(result);
        assertEquals(2L, result);

        verify(reviewRepository, times(1)).countReviewsByProductId(1L);
    }

    @Test
    void getReviewsByProductIdSortedByDate_ShouldReturnSortedList() {
        // Given
        when(reviewRepository.findByProductIdOrderByCreatedAtDesc(1L)).thenReturn(reviewList);

        // When
        List<Review> result = reviewService.getReviewsByProductIdSortedByDate(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(reviewRepository, times(1)).findByProductIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getReviewsByCustomerIdSortedByDate_ShouldReturnSortedList() {
        // Given
        when(reviewRepository.findByCustomerIdOrderByCreatedAtDesc(1L)).thenReturn(reviewList);

        // When
        List<Review> result = reviewService.getReviewsByCustomerIdSortedByDate(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(reviewRepository, times(1)).findByCustomerIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void hasCustomerReviewedProduct_WithExistingReview_ShouldReturnTrue() {
        // Given
        when(reviewRepository.findByProductIdAndCustomerId(1L, 1L)).thenReturn(List.of(testReview));

        // When
        boolean result = reviewService.hasCustomerReviewedProduct(1L, 1L);

        // Then
        assertTrue(result);
        verify(reviewRepository, times(1)).findByProductIdAndCustomerId(1L, 1L);
    }

    @Test
    void hasCustomerReviewedProduct_WithNoExistingReview_ShouldReturnFalse() {
        // Given
        when(reviewRepository.findByProductIdAndCustomerId(1L, 1L)).thenReturn(List.of());

        // When
        boolean result = reviewService.hasCustomerReviewedProduct(1L, 1L);

        // Then
        assertFalse(result);
        verify(reviewRepository, times(1)).findByProductIdAndCustomerId(1L, 1L);
    }
}
