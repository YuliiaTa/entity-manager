package com.iuliieta.entitymanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityStatus status = EntityStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityPriority priority = EntityPriority.MEDIUM;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;

    protected boolean canChangeStatusTo(EntityStatus newStatus) {
        if (this.status == EntityStatus.COMPLETED || this.status == EntityStatus.FAILED) {
            return false;
        }
        if (newStatus == EntityStatus.PENDING &&
                this.status != EntityStatus.PENDING) {
            return false;
        }
        if (newStatus == EntityStatus.COMPLETED &&
                this.status == EntityStatus.PENDING) {
            return false;
        }
        return true;
    }

    public void changeStatus(EntityStatus newStatus) {
        if (!canChangeStatusTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot change status from %s to %s",
                            this.status, newStatus)
            );
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void makeFailedAfterDeadline() {
        if (this.deadline != null && LocalDateTime.now().isAfter(this.deadline) &&
                this.status != EntityStatus.COMPLETED) {
            this.status = EntityStatus.FAILED;
        }
    }
}
