package com.example.usermanagement.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
} 