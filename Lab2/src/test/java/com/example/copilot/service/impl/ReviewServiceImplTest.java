package com.example.copilot.service.impl;

import com.example.copilot.core.dto.ReviewDTO;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.entity.Review;
import com.example.copilot.core.entity.User;
import com.example.copilot.core.enums.OrderStatus;
import com.example.copilot.core.enums.UserRole;
import com.example.copilot.core.repository.OrderRepository;
import com.example.copilot.core.repository.ProductRepository;
import com.example.copilot.core.repository.UserRepository;
import com.example.copilot.exception.DuplicateReviewException;
import com.example.copilot.exception.ResourceNotFoundException;
import com.example.copilot.exception.UserNotPurchasedProductException;
import com.example.copilot.repository.ReviewRepository;
import com.example.copilot.service.OrderVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderVerificationService orderVerificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User testUser;
    private Product testProduct;
    private ReviewDTO testReviewDTO;
    private Review testReview;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.USER);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        testReviewDTO = new ReviewDTO();
        testReviewDTO.setUserId(1L);
        testReviewDTO.setProductId(1L);
        testReviewDTO.setRating(5);
        testReviewDTO.setComment("Great product!");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setUser(testUser);
        testReview.setProduct(testProduct);
        testReview.setRating(5);
        testReview.setComment("Great product!");
        testReview.setCreatedAt(LocalDateTime.now());
        testReview.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void addReview_WhenUserHasNotPurchasedProduct_ShouldThrowUserNotPurchasedProductException() {
        // Given
        when(orderVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(false);

        // When & Then
        UserNotPurchasedProductException exception = assertThrows(
            UserNotPurchasedProductException.class,
            () -> reviewService.addReview(testReviewDTO)
        );

        assertEquals("User has not purchased this product or order is not delivered yet.", 
                    exception.getMessage());
        
        verify(orderVerificationService).hasUserPurchasedProduct(1L, 1L);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void addReview_WhenUserHasAlreadyReviewedProduct_ShouldThrowDuplicateReviewException() {
        // Given
        when(orderVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(true);

        // When & Then
        DuplicateReviewException exception = assertThrows(
            DuplicateReviewException.class,
            () -> reviewService.addReview(testReviewDTO)
        );

        assertEquals("User has already reviewed this product.", exception.getMessage());
        
        verify(orderVerificationService).hasUserPurchasedProduct(1L, 1L);
        verify(reviewRepository).existsByUserIdAndProductId(1L, 1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void addReview_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        // Given
        when(orderVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reviewService.addReview(testReviewDTO)
        );

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void addReview_WhenProductNotFound_ShouldThrowResourceNotFoundException() {
        // Given
        when(orderVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reviewService.addReview(testReviewDTO)
        );

        assertEquals("Product not found with id: 1", exception.getMessage());
    }

    @Test
    void addReview_WhenAllValidationsPass_ShouldSaveReviewSuccessfully() {
        // Given
        when(orderVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(Optional.of(5.0));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ReviewDTO result = reviewService.addReview(testReviewDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getProductId());
        assertEquals(5, result.getRating());
        assertEquals("Great product!", result.getComment());
        assertEquals("Test User", result.getUserFullName());

        verify(orderVerificationService).hasUserPurchasedProduct(1L, 1L);
        verify(reviewRepository).existsByUserIdAndProductId(1L, 1L);
        verify(userRepository).findById(1L);
        verify(productRepository, times(2)).findById(1L); // Called twice: once in addReview, once in updateProductRating
        verify(reviewRepository).save(any(Review.class));
        verify(reviewRepository).getAverageRatingByProductId(1L);
        verify(productRepository).save(testProduct);
    }

    // ============ TDD ADDITIONAL TESTS FOR EDGE CASES ============

    @Test
    void addReview_WhenOrderStatusIsPending_ShouldThrowUserNotPurchasedProductException() {
        // Given - User has order but status is PENDING, not DELIVERED
        when(orderVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(false);

        // When & Then
        UserNotPurchasedProductException exception = assertThrows(
            UserNotPurchasedProductException.class,
            () -> reviewService.addReview(testReviewDTO)
        );

        assertEquals("User has not purchased this product or order is not delivered yet.", 
                    exception.getMessage());
        
        // Verify only the first check was called
        verify(orderVerificationService).hasUserPurchasedProduct(1L, 1L);
        verifyNoInteractions(reviewRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(productRepository);
    }

    @Test 
    void addReview_VerifyExactOrderOfOperations_TDD() {
        // This test verifies the exact order of operations as specified in optimized prompt
        when(orderVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(Optional.of(5.0));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        reviewService.addReview(testReviewDTO);

        // Then - Verify exact order using InOrder
        var inOrder = inOrder(orderVerificationService, reviewRepository, userRepository, productRepository);
        
        // 1. First call: verify purchase
        inOrder.verify(orderVerificationService).hasUserPurchasedProduct(1L, 1L);
        
        // 2. Second call: check duplicate review
        inOrder.verify(reviewRepository).existsByUserIdAndProductId(1L, 1L);
        
        // 3. Third call: fetch user
        inOrder.verify(userRepository).findById(1L);
        
        // 4. Fourth call: fetch product  
        inOrder.verify(productRepository).findById(1L);
        
        // 5. Fifth call: save review
        inOrder.verify(reviewRepository).save(any(Review.class));
        
        // 6. Sixth call: calculate average rating (in updateProductRating - first call)
        inOrder.verify(reviewRepository).getAverageRatingByProductId(1L);
        
        // 7. Seventh call: fetch product again (in updateProductRating - second call)
        inOrder.verify(productRepository).findById(1L);
        
        // 8. Eighth call: save updated product
        inOrder.verify(productRepository).save(testProduct);
    }

    @Test
    void getProductReviews_ShouldReturnListOfReviews() {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByProductIdOrderByCreatedAtDesc(1L)).thenReturn(reviews);

        // When
        List<ReviewDTO> result = reviewService.getProductReviews(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test User", result.get(0).getUserFullName());
        
        verify(reviewRepository).findByProductIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void deleteReview_WhenReviewNotFound_ShouldThrowResourceNotFoundException() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> reviewService.deleteReview(1L, 1L)
        );

        assertEquals("Review not found with id: 1", exception.getMessage());
    }

    @Test
    void deleteReview_WhenUserNotAuthorized_ShouldThrowIllegalArgumentException() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.deleteReview(1L, 2L) // Different user ID
        );

        assertEquals("User can only delete their own reviews", exception.getMessage());
    }

    @Test
    void deleteReview_WhenAuthorized_ShouldDeleteSuccessfully() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(Optional.of(4.5));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        reviewService.deleteReview(1L, 1L);

        // Then
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).delete(testReview);
        verify(reviewRepository).getAverageRatingByProductId(1L);
        verify(productRepository).save(testProduct);
    }
}
