package com.iuliieta.entitymanager.dto;

import com.iuliieta.entitymanager.model.Entity;
import com.iuliieta.entitymanager.model.EntityPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntityRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String title;
    @Size(max = 1000)
    private String description;
    @NotNull
    private EntityPriority priority;
    @Future
    private LocalDateTime deadline;

    public Entity toEntity(EntityRequest request) {
        return Entity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .deadline(request.getDeadline())
                .build();
    }
}
