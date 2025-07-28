package com.example.usermanagement.entity;

import jakarta.persistence.*;
import com.example.usermanagement.constant.OrderStatus;
import lombok.*;
import java.util.List;
import com.example.usermanagement.entity.OrderItem;
import com.example.usermanagement.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;
} 