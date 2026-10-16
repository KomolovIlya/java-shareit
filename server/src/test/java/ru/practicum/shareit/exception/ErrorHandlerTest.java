package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundExceptionTest() {
        ErrorResponse response = errorHandler.handleNotFoundException(new NotFoundException("Объект не найден"));
        assertEquals("Объект не найден", response.getError());
    }

    @Test
    void handleConflictExceptionTest() {
        ErrorResponse response = errorHandler.handleConflictException(new ConflictException("Конфликт email"));
        assertEquals("Конфликт email", response.getError());
    }

    @Test
    void handleBadRequestTest() {
        ErrorResponse response = errorHandler.handleBadRequest(new ValidationException("Ошибка валидации"));
        assertEquals("Ошибка валидации", response.getError());
    }

    @Test
    void handleItemOwnerBookingExceptionTest() {
        ErrorResponse response = errorHandler.handleNotFoundException(
                new ItemOwnerBookingException("Владелец бронирует"));
        assertEquals("Владелец бронирует", response.getError());
    }

    @Test
    void handleThrowableTest() {
        ErrorResponse response = errorHandler.handleThrowable(new RuntimeException("Непредвиденная ошибка"));
        assertEquals("Произошла непредвиденная ошибка: Непредвиденная ошибка", response.getError());
    }
}