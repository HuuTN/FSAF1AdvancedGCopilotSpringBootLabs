package com.example.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.model.entity.Review;
import com.example.service.ReviewService;

@WebMvcTest(ReviewController.class)
@DisplayName("Review Controller Integration Tests")
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("Get all reviews - success")
    void getAllReviews_success() throws Exception {
        Review review1 = new Review();
        review1.setId(1L);
        Review review2 = new Review();
        review2.setId(2L);
        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewService.getAllReviews()).thenReturn(reviews);

        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("Get review by ID - success")
    void getReviewById_success() throws Exception {
        Review review = new Review();
        review.setId(1L);
        when(reviewService.getReviewById(1L)).thenReturn(Optional.of(review));

        mockMvc.perform(get("/api/v1/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Create review - success")
    void createReview_success() throws Exception {
        Review review = new Review();
        review.setId(1L);
        when(reviewService.createReview(anyLong(), anyLong(), anyInt(), anyString())).thenReturn(review);

        mockMvc.perform(post("/api/v1/reviews")
                .param("productId", "1")
                .param("customerId", "1")
                .param("rating", "5")
                .param("reviewContent", "Great product!"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Delete review - success")
    void deleteReview_success() throws Exception {
        doNothing().when(reviewService).deleteReview(1L);
        mockMvc.perform(delete("/api/v1/reviews/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Get reviews by product ID - success")
    void getReviewsByProductId_success() throws Exception {
        Review review1 = new Review();
        review1.setId(1L);
        Review review2 = new Review();
        review2.setId(2L);
        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewService.getReviewsByProductId(1L)).thenReturn(reviews);

        mockMvc.perform(get("/api/v1/reviews/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("Get reviews by customer ID - success")
    void getReviewsByCustomerId_success() throws Exception {
        Review review1 = new Review();
        review1.setId(1L);
        Review review2 = new Review();
        review2.setId(2L);
        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewService.getReviewsByCustomerId(1L)).thenReturn(reviews);

        mockMvc.perform(get("/api/v1/reviews/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("Get reviews by rating - success")
    void getReviewsByRating_success() throws Exception {
        Review review1 = new Review();
        review1.setId(1L);
        Review review2 = new Review();
        review2.setId(2L);
        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewService.getReviewsByRating(5)).thenReturn(reviews);

        mockMvc.perform(get("/api/v1/reviews/rating/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("Update review - success")
    void updateReview_success() throws Exception {
        Review review = new Review();
        review.setId(1L);
        when(reviewService.updateReview(anyLong(), anyInt(), anyString())).thenReturn(review);

        mockMvc.perform(put("/api/v1/reviews/1")
                .param("rating", "5")
                .param("reviewContent", "Updated review content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Get average rating by product ID - success")
    void getAverageRatingByProductId_success() throws Exception {
        when(reviewService.getAverageRatingByProductId(1L)).thenReturn(4.5);

        mockMvc.perform(get("/api/v1/reviews/product/1/average-rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(4.5));
    }

    @Test
    @DisplayName("Get review count by product ID - success")
    void getReviewCountByProductId_success() throws Exception {
        when(reviewService.getReviewCountByProductId(1L)).thenReturn(10L);

        mockMvc.perform(get("/api/v1/reviews/product/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10));
    }

    // Add hasCustomerReviewedProduct method test
    @Test
    @DisplayName("Check if customer has reviewed product - success")
    void hasCustomerReviewedProduct_success() throws Exception {
        when(reviewService.hasCustomerReviewedProduct(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/reviews/customer/1/product/1/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    // Add getReviewsByProductIdSortedByDate method test
    @Test
    @DisplayName("Get reviews by product ID sorted by date - success")
    void getReviewsByProductIdSortedByDate_success() throws Exception {
        Review review1 = new Review();
        review1.setId(1L);
        Review review2 = new Review();
        review2.setId(2L);
        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewService.getReviewsByProductIdSortedByDate(1L)).thenReturn(reviews);

        mockMvc.perform(get("/api/v1/reviews/product/1/sorted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}
