package com.iuliieta.entitymanager.repository;

import com.iuliieta.entitymanager.model.Entity;
import com.iuliieta.entitymanager.model.EntityPriority;
import com.iuliieta.entitymanager.model.EntityStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class EntitySpecification {
    // Фильтр по статусу
    public static Specification<Entity> hasStatus(EntityStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    // Фильтр по приоритету
    public static Specification<Entity> hasPriority(EntityPriority priority) {
        return (root, query, criteriaBuilder) ->
                priority == null ? null : criteriaBuilder.equal(root.get("priority"), priority);
    }

    // Поиск по названию
    public static Specification<Entity> titleContains(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isBlank()) return null;
            String likePattern = "%" + title.toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    likePattern
            );
        };
    }

    // Фильтр по просроченности
    public static Specification<Entity> isOverdue() {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime now = LocalDateTime.now();
            return criteriaBuilder.and(
                    criteriaBuilder.lessThan(root.get("deadline"), now),
                    criteriaBuilder.notEqual(root.get("status"), EntityStatus.COMPLETED)
            );
        };
    }
}
