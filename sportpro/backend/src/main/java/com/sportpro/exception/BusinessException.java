package com.sportpro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BusinessException — lançada para regras de negócio violadas.
 * Ex: email já cadastrado, senha incorreta.
 * Resulta em HTTP 400.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
