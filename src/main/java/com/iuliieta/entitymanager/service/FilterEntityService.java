package com.iuliieta.entitymanager.service;

import com.iuliieta.entitymanager.dto.EntityResponse;
import com.iuliieta.entitymanager.model.Entity;
import com.iuliieta.entitymanager.model.EntityPriority;
import com.iuliieta.entitymanager.model.EntityStatus;
import com.iuliieta.entitymanager.repository.EntityRepository;
import com.iuliieta.entitymanager.repository.EntitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FilterEntityService {
    private final EntityRepository entityRepository;

    public List<EntityResponse> filterEntities(
            EntityStatus status,
            EntityPriority priority,
            String searchTitle,
            Boolean overdue) {
        Specification<Entity> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and(EntitySpecification.hasStatus(status));
        }
        if (priority != null) {
            spec = spec.and(EntitySpecification.hasPriority(priority));
        }
        if (searchTitle != null && !searchTitle.isBlank()) {
            spec = spec.and(EntitySpecification.titleContains(searchTitle));
        }
        if (Boolean.TRUE.equals(overdue)) {
            spec = spec.and(EntitySpecification.isOverdue());
        }
        List<Entity> entities = entityRepository.findAll(spec);
        return entities.stream()
                .map(EntityResponse::fromEntity)
                .toList();
    }

    public List<EntityResponse> getByStatus(EntityStatus status) {
        return filterEntities(status, null, null, null);
    }

    public List<EntityResponse> getByPriority(EntityPriority priority) {
        return filterEntities(null, priority, null, null);
    }

    public List<EntityResponse> getOverDeadlineEntities() {
        return filterEntities(null, null, null, true);
    }

    public List<EntityResponse> searchByTitle(String title) {
        return filterEntities(null, null, title, null);
    }
}
