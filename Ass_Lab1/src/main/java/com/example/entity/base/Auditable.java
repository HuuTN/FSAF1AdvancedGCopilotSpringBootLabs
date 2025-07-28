// don't need get methods and set methods for createdAt and updatedAt as they are managed by JPA Auditing
// Create an abstract auditable base class with createdAt and updatedAt fields managed by JPA Auditing.
package com.example.entity.base;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;   
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import jakarta.persistence.EntityListeners;
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}