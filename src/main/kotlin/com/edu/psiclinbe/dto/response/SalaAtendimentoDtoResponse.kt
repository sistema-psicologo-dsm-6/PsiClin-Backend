package com.edu.psiclinbe.dto.response

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class SalaAtendimentoDtoResponse(
    val id: Long? = null,
    val nome: String,
    val descricao: String?,
    val capacidade: Int? = 0,
    val ativo: Boolean = true,
    val dataCadastro: LocalDateTime,
)