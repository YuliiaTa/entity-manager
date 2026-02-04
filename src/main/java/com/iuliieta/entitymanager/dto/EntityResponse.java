package com.iuliieta.entitymanager.dto;

import com.iuliieta.entitymanager.model.Entity;
import com.iuliieta.entitymanager.model.EntityPriority;
import com.iuliieta.entitymanager.model.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntityResponse {
    private Long id;
    private String title;
    private String description;
    private EntityStatus status;
    private EntityPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deadline;

    public EntityResponse toResponse(Entity entity) {
        return EntityResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deadline(entity.getDeadline())
                .build();
    }
    public static EntityResponse fromEntity(Entity entity) {
        return EntityResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deadline(entity.getDeadline())
                .build();
    }
}
