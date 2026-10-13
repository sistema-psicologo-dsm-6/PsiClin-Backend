package com.edu.psiclinbe.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApplicationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException) : ResponseEntity<Map<String, String>> {
        val errors = HashMap<String, String>()
        ex.bindingResult.allErrors.forEach({ error ->
            if (error is FieldError) {
                val fieldName = error.field
                val errorMessage = error.defaultMessage ?: "Erro no campo $fieldName"
                errors[fieldName] = errorMessage
            }
        })
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(
        ex: NoSuchElementException,
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            mapOf(
                "erro" to (ex.message ?: "Recurso não encontrado.")
            )
        )
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(
        ex: AuthenticationException
    ): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("erro" to "Credenciais inválidas"))

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(
        ex: AccessDeniedException
    ): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(mapOf("erro" to "Acesso negado para este perfil"))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(
        ex: IllegalArgumentException
    ): ResponseEntity<Map<String, String>> {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                mapOf(
                    "erro" to (ex.message ?: "Requisição inválida")
                )
            )
    }

}