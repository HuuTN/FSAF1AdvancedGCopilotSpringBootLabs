package com.example.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.example.model.entity.Review;

public class ReviewTest {

    @Test
    public void testReviewCreation() {
        // Create a review
        Review review = new Review();
        review.setRating(5);
        review.setReviewContent("Great product!");

        // Verify the review properties
        assertEquals(5, review.getRating().intValue());
        assertEquals("Great product!", review.getReviewContent());
    }

    @Test
    public void testReviewEquality() {
        Review review1 = new Review();
        review1.setId(1L);

        Review review2 = new Review();
        review2.setId(1L);

        Review review3 = new Review();
        review3.setId(2L);

        // Test equality
        assertEquals(review1, review2);
        assertNotEquals(review1, review3);
        assertEquals(review1.hashCode(), review2.hashCode());
    }
}
