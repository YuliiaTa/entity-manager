package com.iuliieta.entitymanager.repository;

import com.iuliieta.entitymanager.model.Entity;
import com.iuliieta.entitymanager.model.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntityRepository extends JpaRepository<Entity, Long>, JpaSpecificationExecutor<Entity> {
    List<Entity> findByDeadlineBeforeAndStatusNot(
            LocalDateTime deadline,
            EntityStatus status);
}
