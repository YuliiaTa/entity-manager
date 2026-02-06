package com.iuliieta.entitymanager.controller;

import com.iuliieta.entitymanager.dto.EntityResponse;
import com.iuliieta.entitymanager.model.EntityPriority;
import com.iuliieta.entitymanager.model.EntityStatus;
import com.iuliieta.entitymanager.service.FilterEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity")
@RequiredArgsConstructor
@Tag(name = "Entity Filters")
public class FilterEntityController {
    private final FilterEntityService entityService;

    @Operation(summary = "фильтрация сущностей по нескольким параметрам")
    @GetMapping("/filter")
    public ResponseEntity<List<EntityResponse>> filterEntities(
            @RequestParam(required = false) EntityStatus status,
            @RequestParam(required = false) EntityPriority priority,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean deadline) {
        List<EntityResponse> result = entityService.filterEntities(
                status, priority, search, deadline);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "фильтар по статусу")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EntityResponse>> getByStatus(
            @PathVariable EntityStatus status) {
        List<EntityResponse> result = entityService.getByStatus(status);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "фильтр по приоритету")
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<EntityResponse>> getByPriority(
            @PathVariable EntityPriority priority) {
        List<EntityResponse> result = entityService.getByPriority(priority);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "фильтр по дедлайну")
    @GetMapping("/overdue")
    public ResponseEntity<List<EntityResponse>> getOverDeadline() {
        List<EntityResponse> result = entityService.getOverDeadlineEntities();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "фильтр по имени сущности")
    @GetMapping("/search")
    public ResponseEntity<List<EntityResponse>> search(
            @RequestParam String title) {
        List<EntityResponse> result = entityService.searchByTitle(title);
        return ResponseEntity.ok(result);
    }
}

