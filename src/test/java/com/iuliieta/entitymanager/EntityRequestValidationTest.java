package com.iuliieta.entitymanager;

import com.iuliieta.entitymanager.dto.EntityRequest;
import com.iuliieta.entitymanager.model.EntityPriority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EntityRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Валидация - должен пройти при корректных данных")
    void shouldPassValidationWithValidData() {
        EntityRequest request = EntityRequest.builder()
                .title("Valid Title")
                .description("Valid Description")
                .priority(EntityPriority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        Set<ConstraintViolation<EntityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация - должен не пройти при пустом title")
    void shouldFailValidationWithEmptyTitle() {
        EntityRequest request = EntityRequest.builder()
                .title("") // Пустой
                .description("Description")
                .priority(EntityPriority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        Set<ConstraintViolation<EntityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    @DisplayName("Валидация - должен не пройти при null priority")
    void shouldFailValidationWithNullPriority() {
        EntityRequest request = EntityRequest.builder()
                .title("Valid Title")
                .description("Description")
                .priority(null) // null
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        Set<ConstraintViolation<EntityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("priority")));
    }

    @Test
    @DisplayName("Валидация - должен не пройти при прошедшей дате deadline")
    void shouldFailValidationWithPastDeadline() {
        EntityRequest request = EntityRequest.builder()
                .title("Valid Title")
                .description("Description")
                .priority(EntityPriority.MEDIUM)
                .deadline(LocalDateTime.now().minusDays(1))
                .build();
        Set<ConstraintViolation<EntityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("deadline")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "a"})
    @DisplayName("Валидация title - должен не пройти при слишком коротком title")
    void shouldFailValidationWithShortTitle(String shortTitle) {
        EntityRequest request = EntityRequest.builder()
                .title(shortTitle)
                .description("Description")
                .priority(EntityPriority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        Set<ConstraintViolation<EntityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Валидация - должен пройти при null description (опциональное поле)")
    void shouldPassValidationWithNullDescription() {
        EntityRequest request = EntityRequest.builder()
                .title("Valid Title")
                .description(null)
                .priority(EntityPriority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        Set<ConstraintViolation<EntityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}