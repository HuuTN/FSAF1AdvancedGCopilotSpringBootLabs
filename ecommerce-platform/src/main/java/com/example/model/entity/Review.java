package com.example.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.example.model.entity.base.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.Objects;

import org.hibernate.annotations.Check;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"product", "customer"})
@Entity
@Table(name = "reviews")
@Check(constraints = "rating >= 1 AND rating <= 5")
public class Review extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_seq")
    @SequenceGenerator(name = "review_seq", sequenceName = "review_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    @NotNull(message = "Product is required")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    @NotNull(message = "Customer is required")
    private Customer customer;

    @Column(nullable = false)
    @NotNull(message = "Rating is required")
    private Integer rating;

    @Column(name = "review_cont", length = 1000)
    @Size(max = 1000, message = "Review content must not exceed 1000 characters")
    private String reviewContent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review review)) return false;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
