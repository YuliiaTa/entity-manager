package com.iuliieta.entitymanager.controller;

import com.iuliieta.entitymanager.dto.EntityResponse;
import com.iuliieta.entitymanager.dto.EntityStatusChangeRequest;
import com.iuliieta.entitymanager.service.ChangeEntityStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entity")
@RequiredArgsConstructor
@Validated
@Tag(name = "Entity API")
public class ChangeEntityStatusController {
    private final ChangeEntityStatusService changeEntityStatusService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<EntityResponse> changeEntityStatus(
            @PathVariable Long id,
            @Valid @RequestBody EntityStatusChangeRequest request) {
        EntityResponse response = changeEntityStatusService.changeEntityStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<EntityResponse> startEntity(@PathVariable Long id) {
        EntityResponse response = changeEntityStatusService.startEntity(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<EntityResponse> completeEntity(@PathVariable Long id) {
        EntityResponse response = changeEntityStatusService.completeEntity(id);
        return ResponseEntity.ok(response);
    }
}
