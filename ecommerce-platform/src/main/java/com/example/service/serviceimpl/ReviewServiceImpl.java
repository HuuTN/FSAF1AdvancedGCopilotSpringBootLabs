package com.example.service.serviceimpl;

import com.example.model.entity.Review;
import com.example.model.entity.Product;
import com.example.model.entity.Customer;
import com.example.repository.ReviewRepository;
import com.example.repository.ProductRepository;
import com.example.repository.CustomerRepository;
import com.example.service.ReviewService;
import com.example.service.ReviewValidationService;
import com.example.service.ProductRatingService;
import com.example.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ReviewValidationService reviewValidationService;
    private final ProductRatingService productRatingService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            ReviewValidationService reviewValidationService,
            ProductRatingService productRatingService) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.reviewValidationService = reviewValidationService;
        this.productRatingService = productRatingService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByCustomerId(Long customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }

    @Override
    @Transactional
    public Review createReview(Long productId, Long customerId, Integer rating, String reviewContent) {
        logger.info("Creating review for product {} by customer {}", productId, customerId);

        // Find product and customer first
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // Validate all inputs and eligibility
        reviewValidationService.validateRating(rating);
        reviewValidationService.validateReviewContent(reviewContent);
        reviewValidationService.validateReviewEligibility(customer, product);

        // Create review
        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setRating(rating);
        review.setReviewContent(reviewContent);

        Review savedReview = reviewRepository.save(review);
        logger.info("Review created with ID: {}", savedReview.getId());

        // Update product average rating asynchronously to avoid blocking
        try {
            productRatingService.updateProductAverageRating(productId);
        } catch (Exception e) {
            logger.error("Failed to update product rating for product {}: {}", productId, e.getMessage());
            // Don't fail the review creation if rating update fails
        }

        return savedReview;
    }

    @Override
    @Transactional
    public Review updateReview(Long id, Integer rating, String reviewContent) {
        logger.info("Updating review with ID: {}", id);

        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        // Validate the new data
        reviewValidationService.validateRating(rating);
        reviewValidationService.validateReviewContent(reviewContent);

        existingReview.setRating(rating);
        existingReview.setReviewContent(reviewContent);

        Review updatedReview = reviewRepository.save(existingReview);

        // Update product average rating
        try {
            productRatingService.updateProductAverageRating(existingReview.getProduct().getId());
        } catch (Exception e) {
            logger.error("Failed to update product rating: {}", e.getMessage());
        }

        logger.info("Review updated successfully");
        return updatedReview;
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        logger.info("Deleting review with ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);

        // Update product rating after deletion
        try {
            productRatingService.updateProductAverageRating(productId);
        } catch (Exception e) {
            logger.error("Failed to update product rating after deletion: {}", e.getMessage());
        }

        logger.info("Review deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByProductId(Long productId) {
        return productRatingService.getProductAverageRating(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReviewCountByProductId(Long productId) {
        return productRatingService.getProductReviewCount(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByProductIdSortedByDate(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByCustomerIdSortedByDate(Long customerId) {
        return reviewRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public boolean hasCustomerReviewedProduct(Long customerId, Long productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasCustomerReviewedProduct'");
    }
}
