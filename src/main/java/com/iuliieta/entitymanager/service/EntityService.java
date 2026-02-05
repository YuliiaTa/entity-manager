package com.iuliieta.entitymanager.service;

import com.iuliieta.entitymanager.dto.EntityRequest;
import com.iuliieta.entitymanager.dto.EntityResponse;
import com.iuliieta.entitymanager.exception.EntityNotFoundException;
import com.iuliieta.entitymanager.model.Entity;
import com.iuliieta.entitymanager.repository.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EntityService {
    private final EntityRepository entityRepository;

    public EntityResponse createEntity(EntityRequest request) {
        Entity entity = request.toEntity();
        Entity saved = entityRepository.save(entity);
        return EntityResponse.fromEntity(saved);
    }

    public EntityResponse updateEntity(Long id, EntityRequest request) {
        Entity entity = entityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setPriority(request.getPriority());
        entity.setDeadline(request.getDeadline());
        Entity updated = entityRepository.save(entity);

        return EntityResponse.fromEntity(updated);
    }

    public void deleteEntity(Long id) {
        if (!entityRepository.existsById(id)) {
            throw new EntityNotFoundException(id);
        }
        entityRepository.deleteById(id);
    }

    public EntityResponse findById(Long id) {
        Entity entity = entityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        EntityResponse response = new EntityResponse();
        return response.toResponse(entity);
    }

    public List<EntityResponse> getAllEntities() {
        return entityRepository.findAll()
                .stream()
                .map(EntityResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
