package com.iuliieta.entitymanager;

import com.iuliieta.entitymanager.dto.EntityRequest;
import com.iuliieta.entitymanager.dto.EntityResponse;
import com.iuliieta.entitymanager.model.Entity;
import com.iuliieta.entitymanager.model.EntityPriority;
import com.iuliieta.entitymanager.model.EntityStatus;
import com.iuliieta.entitymanager.repository.EntityRepository;
import com.iuliieta.entitymanager.service.EntityService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityServiceTest {

    @Mock
    private EntityRepository entityRepository;

    @InjectMocks
    private EntityService entityService;

    private Entity testEntity;
    private EntityRequest validEntityRequest;

    @BeforeEach
    void setUp() {
        testEntity = Entity.builder()
                .id(1L)
                .title("my entity")
                .description("my entity description")
                .status(EntityStatus.PENDING)
                .priority(EntityPriority.MEDIUM)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .deadline(LocalDateTime.now().plusDays(7))
                .build();
        validEntityRequest = EntityRequest.builder()
                .title("my entity")
                .description("my entity description")
                .priority(EntityPriority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(7))
                .build();
    }

    @Test
    @DisplayName("Создание сущности - должен сохранить и вернуть сущность с id")
    void createEntity_ShouldSaveAndReturnEntityWithId() {
        when(entityRepository.save(any(Entity.class))).thenReturn(testEntity);

        EntityResponse response = entityService.createEntity(validEntityRequest);
        assertNotNull(response);
        assertEquals(testEntity.getId(), response.getId());
        assertEquals(testEntity.getTitle(), response.getTitle());
        assertEquals(testEntity.getDescription(), response.getDescription());
        assertEquals(EntityStatus.PENDING, response.getStatus());
        assertEquals(EntityPriority.MEDIUM, response.getPriority());

        verify(entityRepository, times(1)).save(any(Entity.class));
    }

    @Test
    @DisplayName("Создание сущности - должен установить дефолтные значения и временные метки")
    void createEntity_ShouldSetDefaultValuesAndTimestamps() {
        EntityRequest requestWithoutDescription = EntityRequest.builder()
                .title("test")
                .priority(EntityPriority.HIGH)
                .deadline(LocalDateTime.now().plusDays(7))
                .build();

        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation -> {
            Entity saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        EntityResponse response = entityService.createEntity(requestWithoutDescription);

        assertNotNull(response.getId());
        assertEquals("test", response.getTitle());
        assertNull(response.getDescription());
        assertEquals(EntityStatus.PENDING, response.getStatus());
        assertEquals(EntityPriority.HIGH, response.getPriority());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    @DisplayName("Поиск сущности по id - должен вернуть сущность при существовании")
    void findById_WhenEntityExists_ShouldReturnEntity() {
        Long entityId = 1L;
        when(entityRepository.findById(entityId)).thenReturn(Optional.of(testEntity));

        EntityResponse response = entityService.findById(entityId);

        assertNotNull(response);
        assertEquals(testEntity.getId(), response.getId());
        assertEquals(testEntity.getTitle(), response.getTitle());
        verify(entityRepository, times(1)).findById(entityId);
    }

    @Test
    @DisplayName("Поиск сущности по id - должен выбросить исключение при отсутствии")
    void findById_WhenEntityNotFound_ShouldThrowEntityNotFoundException() {
        Long nonExistentId = 999L;
        when(entityRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> entityService.findById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(entityRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Обновление сущности - должен обновить поля и вернуть обновленную сущность")
    void updateEntity_ShouldUpdateFieldsAndReturnUpdatedEntity() {
        Long entityId = 1L;
        LocalDateTime expectedDeadline = LocalDateTime.now().plusDays(8);

        EntityRequest updateRequest = EntityRequest.builder()
                .title("updated title")
                .description("updated description")
                .priority(EntityPriority.HIGH)
                .deadline(expectedDeadline)
                .build();

        when(entityRepository.findById(entityId)).thenReturn(Optional.of(testEntity));
        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );
        EntityResponse response = entityService.updateEntity(entityId, updateRequest);

        assertEquals("updated title", response.getTitle());
        assertEquals("updated description", response.getDescription());
        assertEquals(EntityPriority.HIGH, response.getPriority());
        assertEquals(expectedDeadline, response.getDeadline());
        verify(entityRepository, times(1)).findById(entityId);
        verify(entityRepository, times(1)).save(any(Entity.class));
    }

    @Test
    @DisplayName("Обновление сущности - не должен изменять created_at")
    void updateEntity_ShouldNotChangeCreatedAt() {
        Long entityId = 1L;
        LocalDateTime originalCreatedAt = testEntity.getCreatedAt();

        when(entityRepository.findById(entityId)).thenReturn(Optional.of(testEntity));
        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        EntityResponse response = entityService.updateEntity(entityId, validEntityRequest);

        assertEquals(originalCreatedAt, response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertTrue(!response.getUpdatedAt().isBefore(response.getCreatedAt()));
    }

    @Test
    @DisplayName("Удаление сущности - должен удалить при существовании")
    void deleteEntity_ShouldDeleteWhenExists() {
        Long entityId = 1L;
        when(entityRepository.existsById(entityId)).thenReturn(true);
        doNothing().when(entityRepository).deleteById(entityId);

        entityService.deleteEntity(entityId);

        verify(entityRepository, times(1)).existsById(entityId);
        verify(entityRepository, times(1)).deleteById(entityId);
    }

    @Test
    @DisplayName("Удаление сущности - должен выбросить исключение при отсутствии")
    void deleteEntity_WhenNotFound_ShouldThrowException() {
        Long nonExistentId = 999L;
        when(entityRepository.existsById(nonExistentId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> entityService.deleteEntity(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(entityRepository, times(1)).existsById(nonExistentId);
        verify(entityRepository, never()).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("Получение всех сущностей - должен вернуть список сущностей")
    void getAllEntities_ShouldReturnAllEntities() {
        Entity secondEntity = Entity.builder()
                .id(2L)
                .title("my second entity")
                .status(EntityStatus.IN_PROGRESS)
                .priority(EntityPriority.LOW)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(entityRepository.findAll()).thenReturn(List.of(testEntity, secondEntity));

        List<EntityResponse> responses = entityService.getAllEntities();

        assertEquals(2, responses.size());
        assertEquals("my entity", responses.get(0).getTitle());
        assertEquals("my second entity", responses.get(1).getTitle());
        verify(entityRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Получение всех сущностей - должен вернуть пустой список при отсутствии")
    void getAllEntities_WhenNoEntities_ShouldReturnEmptyList() {
        when(entityRepository.findAll()).thenReturn(List.of());

        List<EntityResponse> responses = entityService.getAllEntities();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(entityRepository, times(1)).findAll();
    }
}