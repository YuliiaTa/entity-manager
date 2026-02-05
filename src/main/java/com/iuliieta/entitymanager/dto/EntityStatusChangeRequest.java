package com.iuliieta.entitymanager.dto;

import com.iuliieta.entitymanager.model.EntityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntityStatusChangeRequest {
    @NotNull(message = "New status is required")
    private EntityStatus newStatus;
}
