package com.mugen.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ✅ GLOBAL EXCEPTION HANDLER - REORGANIZADO
 * Centralizando o tratamento de TODAS as exceptions
 * Usa o ErrorResponse existente no seu projeto
 * Organizado por categoria: Character, Inventory, TP, Resource, Validation, Generic
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(403).body(Map.of(
                "status", 403,
                "message", "You do not have permission to access this resource"
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(401).body(Map.of(
                "status", 401,
                "message", "Authentication required"
        ));
    }

    // ==================== 1️⃣ CHARACTER EXCEPTIONS ====================

    /**
     * 404 - Personagem não encontrado
     */
    @ExceptionHandler(CharacterNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCharacterNotFound(
            CharacterNotFoundException ex, WebRequest request) {
        log.warn("❌ Character not found: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "CHARACTER_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }

    // ==================== 2️⃣ INVENTORY EXCEPTIONS ====================

    /**
     * 404 - Item não encontrado no inventário
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFound(
            ItemNotFoundException ex, WebRequest request) {
        log.warn("❌ Item not found: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "ITEM_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }

    /**
     * 400 - Estoque insuficiente
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException ex, WebRequest request) {
        log.warn("❌ Insufficient stock: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_STOCK",
                ex.getMessage(),
                request
        );
    }

    /**
     * 422 - Ouro insuficiente
     */
    @ExceptionHandler(InsufficientGoldException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientGold(
            InsufficientGoldException ex, WebRequest request) {
        log.warn("❌ Insufficient gold: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INSUFFICIENT_GOLD",
                ex.getMessage(),
                request
        );
    }

    /**
     * 400 - Requisitos do item não atendidos
     */
    @ExceptionHandler(ItemRequirementsNotMetException.class)
    public ResponseEntity<ErrorResponse> handleItemRequirementsNotMet(
            ItemRequirementsNotMetException ex, WebRequest request) {
        log.warn("❌ Item requirements not met: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "ITEM_REQUIREMENTS_NOT_MET",
                ex.getMessage(),
                request
        );
    }

    // ==================== 3️⃣ EQUIPMENT EXCEPTIONS ====================

    /**
     * 400 - Erro ao equipar/desequipar
     */
    @ExceptionHandler(EquipmentException.class)
    public ResponseEntity<ErrorResponse> handleEquipmentException(
            EquipmentException ex, WebRequest request) {
        log.warn("❌ Equipment error: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "EQUIPMENT_ERROR",
                ex.getMessage(),
                request
        );
    }

    // ==================== 4️⃣ TP SYSTEM EXCEPTIONS ====================

    /**
     * 400 - TP insuficiente
     */
    @ExceptionHandler(InsufficientTPException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientTP(
            InsufficientTPException ex, WebRequest request) {
        log.warn("❌ Insufficient TP: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_TP",
                ex.getMessage(),
                request
        );
    }

    /**
     * 400 - Atributo inválido
     */
    @ExceptionHandler(InvalidAttributeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAttribute(
            InvalidAttributeException ex, WebRequest request) {
        log.warn("❌ Invalid attribute: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_ATTRIBUTE",
                ex.getMessage(),
                request
        );
    }

    /**
     * 400 - Atributo máximo excedido
     */
    @ExceptionHandler(MaxAttributeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxAttributeExceeded(
            MaxAttributeExceededException ex, WebRequest request) {
        log.warn("❌ Max attribute exceeded: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "MAX_ATTRIBUTE_EXCEEDED",
                ex.getMessage(),
                request
        );
    }

    // ==================== 5️⃣ RESOURCE EXCEPTIONS ====================

    /**
     * 404 - Recurso não encontrado (genérico)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("❌ Resource not found: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }

    /**
     * 400 - Operação inválida
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperation(
            InvalidOperationException ex, WebRequest request) {
        log.warn("❌ Invalid operation: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_OPERATION",
                ex.getMessage(),
                request
        );
    }

    /**
     * 404 - Skill não encontrada
     */
    @ExceptionHandler(SkillNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSkillNotFound(
            SkillNotFoundException ex, WebRequest request) {
        log.warn("❌ Skill not found: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "SKILL_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }

    // ==================== 6️⃣ VALIDATION EXCEPTIONS ====================

    /**
     * 400 - Validação de entrada falhou
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("❌ Validation error: {}", errors);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Validation failed")
                .validationErrors(errors)
                .path(getPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 400 - Argumento ilegal
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        log.warn("❌ Illegal argument: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                request
        );
    }

    // ==================== 7️⃣ GENERIC EXCEPTIONS ====================

    /**
     * 500 - Erro inesperado (catch-all)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("❌ UNEXPECTED ERROR:", ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Internal server error. Contact administrator.",
                request
        );
    }

    // ==================== HELPER METHODS ====================

    /**
     *  Auxiliar para construir resposta de erro padronizada
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            WebRequest request) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(getPath(request))
                .build();

        return ResponseEntity.status(status).body(response);
    }

    /**
     * ✅ Extrai o path da requisição
     */
    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
