package com.iuliieta.entitymanager.controller;

import com.iuliieta.entitymanager.dto.EntityRequest;
import com.iuliieta.entitymanager.dto.EntityResponse;
import com.iuliieta.entitymanager.service.EntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity")
@RequiredArgsConstructor
@Validated
@Tag(name = "Entity CRUD")
public class EntityController {
    private final EntityService entityService;

    @Operation(summary = "создание новой сущности")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityResponse createEntity(@Valid @RequestBody EntityRequest request) {
        return entityService.createEntity(request);
    }

    @Operation(summary = "изменение существующей сущности")
    @PutMapping("/{id}")
    public EntityResponse updateEntity(@PathVariable Long id, @Valid @RequestBody EntityRequest request) {
        return entityService.updateEntity(id, request);
    }

    @Operation(summary = "удаление существующей сущности")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntity(@PathVariable Long id) {
        entityService.deleteEntity(id);
    }

    @Operation(summary = "получение сущности по ее id")
    @GetMapping("/{id}")
    public EntityResponse findById(@PathVariable Long id) {
        return entityService.findById(id);
    }

    @Operation(summary = "получение списка всех сущностей из базы")
    @GetMapping
    public List<EntityResponse> getAllEntities() {
        return entityService.getAllEntities();
    }
}
