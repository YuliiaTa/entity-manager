package com.iuliieta.entitymanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iuliieta.entitymanager.controller.EntityController;
import com.iuliieta.entitymanager.dto.EntityRequest;
import com.iuliieta.entitymanager.dto.EntityResponse;
import com.iuliieta.entitymanager.model.EntityPriority;
import com.iuliieta.entitymanager.model.EntityStatus;
import com.iuliieta.entitymanager.service.EntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EntityControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EntityService entityService;

    @InjectMocks
    private EntityController entityController;

    private EntityRequest entityRequest;
    private EntityResponse entityResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(entityController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        LocalDateTime now = LocalDateTime.now();

        entityRequest = EntityRequest.builder()
                .title("my entity")
                .description("my entity description")
                .priority(EntityPriority.MEDIUM)
                .deadline(deadline)
                .build();

        entityResponse = EntityResponse.builder()
                .id(1L)
                .title("my entity")
                .description("my entity description")
                .status(EntityStatus.PENDING)
                .priority(EntityPriority.MEDIUM)
                .createdAt(now)
                .updatedAt(now)
                .deadline(deadline)
                .build();
    }

    @Test
    @DisplayName("POST /entity - должен создать сущность и вернуть 201")
    void createEntity_WithValidData_ShouldReturn201AndEntity() throws Exception {
        when(entityService.createEntity(any(EntityRequest.class))).thenReturn(entityResponse);

        mockMvc.perform(post("/entity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entityRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("my entity"))
                .andExpect(jsonPath("$.description").value("my entity description"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    @DisplayName("POST /entity - должен вернуть 400 при невалидных данных")
    void createEntity_WithInvalidData_ShouldReturn400() throws Exception {
        EntityRequest invalidRequest = EntityRequest.builder()
                .title("")
                .description("test")
                .priority(EntityPriority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();

        mockMvc.perform(post("/entity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /entity - должен вернуть 400 при прошедшей дате deadline")
    void createEntity_WithPastDeadline_ShouldReturn400() throws Exception {
        EntityRequest invalidRequest = EntityRequest.builder()
                .title("valid title")
                .description("description")
                .priority(EntityPriority.MEDIUM)
                .deadline(LocalDateTime.now().minusDays(1))
                .build();

        mockMvc.perform(post("/entity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /entity/{id} - должен вернуть сущность и 200")
    void getEntityById_ShouldReturn200AndEntity() throws Exception {
        Long entityId = 1L;
        when(entityService.findById(entityId)).thenReturn(entityResponse);

        mockMvc.perform(get("/entity/{id}", entityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("my entity"))
                .andExpect(jsonPath("$.description").value("my entity description"));
    }

    @Test
    @DisplayName("PUT /entity/{id} - должен обновить сущность и вернуть 200")
    void updateEntity_ShouldReturn200AndUpdatedEntity() throws Exception {
        Long entityId = 1L;
        EntityResponse updatedResponse = EntityResponse.builder()
                .id(entityId)
                .title("updated title")
                .description("updated description")
                .status(EntityStatus.IN_PROGRESS)
                .priority(EntityPriority.HIGH)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusDays(14))
                .build();

        when(entityService.updateEntity(eq(entityId), any(EntityRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/entity/{id}", entityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entityRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("updated title"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("DELETE /entity/{id} - должен вернуть 200 при успешном удалении")
    void deleteEntity_ShouldReturn200() throws Exception {
        Long entityId = 1L;
        mockMvc.perform(delete("/entity/{id}", entityId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /entity - должен вернуть список сущностей и 200")
    void getAllEntities_ShouldReturn200AndList() throws Exception {
        EntityResponse secondResponse = EntityResponse.builder()
                .id(2L)
                .title("my second entity")
                .description("second entity description")
                .status(EntityStatus.IN_PROGRESS)
                .priority(EntityPriority.HIGH)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusDays(30))
                .build();

        when(entityService.getAllEntities()).thenReturn(List.of(entityResponse, secondResponse));

        mockMvc.perform(get("/entity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].title").value("my entity"))
                .andExpect(jsonPath("$[1].title").value("my second entity"));
    }
}