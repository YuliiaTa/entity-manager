package com.iuliieta.entitymanager.service;

import com.iuliieta.entitymanager.dto.EntityResponse;
import com.iuliieta.entitymanager.dto.EntityStatusChangeRequest;
import com.iuliieta.entitymanager.exception.ChangeStatusFailedException;
import com.iuliieta.entitymanager.exception.EntityNotFoundException;
import com.iuliieta.entitymanager.model.Entity;
import com.iuliieta.entitymanager.model.EntityStatus;
import com.iuliieta.entitymanager.repository.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChangeEntityStatusService {
    private final EntityRepository entityRepository;

    public EntityResponse changeEntityStatus(Long id, EntityStatusChangeRequest request) {
        Entity entity = entityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        try {
            entity.changeStatus(request.getNewStatus());
            Entity updatedEntity = entityRepository.save(entity);
            return EntityResponse.fromEntity(updatedEntity);
        } catch (IllegalStateException e) {
            throw new ChangeStatusFailedException(e.getMessage());
        }
    }

    public EntityResponse startEntity(Long id) {
        return changeEntityStatus(id,
                EntityStatusChangeRequest.builder()
                        .newStatus(EntityStatus.IN_PROGRESS)
                        .build());
    }

    public EntityResponse completeEntity(Long id) {
        return changeEntityStatus(id,
                EntityStatusChangeRequest.builder()
                        .newStatus(EntityStatus.COMPLETED)
                        .build());
    }

    @Scheduled(cron = "0 0 9 * * ?") // Каждый день в 9 утра
    @Transactional
    public void checkAndUpdateFailedEntities() {
        List<Entity> overdueEntities = entityRepository.findByDeadlineBeforeAndStatusNot(
                LocalDateTime.now(),
                EntityStatus.COMPLETED
        );
        for (Entity entity : overdueEntities) {
            entity.makeFailedAfterDeadline();
        }
    }
}
