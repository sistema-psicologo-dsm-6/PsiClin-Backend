package com.edu.psiclinbe.dto.response

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

data class ConvenioDtoResponse(
    val id: Long?,
    val nome: String,
    val cnpj: String,
    val telefone: String,
    val percentualCobertura: Double?,
    val ativo: Boolean = true,
    val pagamentos: Set<Long>,
)