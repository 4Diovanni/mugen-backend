package com.mugen.backend.exception;

/**
 * Exceção para recursos não encontrados
 * Usado quando um recurso solicitado não existe no banco de dados
 * Exemplos:
 * - Arma com ID inexistente
 * - Personagem não encontrado
 * - Skill não cadastrada
 * - Transformação não existe
 * HTTP Status: 404 Not Found
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }
}
