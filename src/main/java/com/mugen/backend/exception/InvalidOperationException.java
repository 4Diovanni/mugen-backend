package com.mugen.backend.exception;

/**
 * Exceção para operações inválidas
 * Usado quando uma operação não pode ser executada devido a estado inválido
 * Exemplos:
 * - Tentar equipar item que o personagem não tem requisitos
 * - Tentar comprar item sem espaço no inventário
 * - Tentar usar habilidade sem ter aprendido
 * HTTP Status: 400 Bad Request
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOperationException(Throwable cause) {
        super(cause);
    }
}
