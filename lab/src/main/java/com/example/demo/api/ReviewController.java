package com.example.demo.api;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.entity.Review;
import com.example.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    // Public endpoint: get all reviews for a product
    @GetMapping
    public List<Review> getReviews(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }

    // Secured endpoint: add review (userId should come from auth in real app)
    @PostMapping
    public Review addReview(@PathVariable Long productId, @RequestBody ReviewDTO dto, @RequestParam Long userId) {
        return reviewService.addReview(dto, userId, productId);
    }
}
