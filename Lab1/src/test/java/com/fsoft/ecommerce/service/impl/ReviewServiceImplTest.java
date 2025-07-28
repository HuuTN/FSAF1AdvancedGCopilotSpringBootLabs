package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.ReviewDTO;
import com.fsoft.ecommerce.entity.Review;
import com.fsoft.ecommerce.entity.User;
import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.exception.DuplicateReviewException;
import com.fsoft.ecommerce.exception.UserNotPurchasedProductException;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.repository.ReviewRepository;
import com.fsoft.ecommerce.repository.UserRepository;
import com.fsoft.ecommerce.service.ProductRatingService;
import com.fsoft.ecommerce.service.PurchaseVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurchaseVerificationService purchaseVerificationService;

    @Mock
    private ProductRatingService productRatingService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User testUser;
    private Product testProduct;
    private ReviewDTO testReviewDTO;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password", "Test", "User");
        testUser.setId(1L);

        testProduct = new Product("Test Product", "Description", BigDecimal.valueOf(100), 10, "Electronics");
        testProduct.setId(1L);

        testReviewDTO = new ReviewDTO(5, "Great product!");
    }

    @Test
    void addReview_ShouldThrowException_WhenUserHasNotPurchasedProduct() {
        // Given
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L))
                .thenReturn(false);

        // When & Then
        assertThrows(UserNotPurchasedProductException.class, () -> {
            reviewService.addReview(testReviewDTO, 1L, 1L);
        });
    }

    @Test
    void addReview_ShouldThrowException_WhenUserAlreadyReviewedProduct() {
        // Given
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L))
                .thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L))
                .thenReturn(true);

        // When & Then
        assertThrows(DuplicateReviewException.class, () -> {
            reviewService.addReview(testReviewDTO, 1L, 1L);
        });
    }

    @Test
    void addReview_ShouldSucceed_WhenValidConditions() {
        // Given
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L))
                .thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L))
                .thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review(testUser, testProduct, 5, "Great product!"));
        
        // When
        Review result = reviewService.addReview(testReviewDTO, 1L, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Great product!", result.getComment());
        verify(reviewRepository).save(any(Review.class));
        verify(productRatingService).updateProductAverageRating(1L); // Verify rating service is called
    }
}
