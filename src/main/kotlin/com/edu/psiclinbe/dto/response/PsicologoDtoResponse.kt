package com.edu.psiclinbe.dto.response

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class PsicologoDtoResponse(
    val id: Long? = 0,
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val telefone: String = "",
    val crp: String = "",
    val especialidade: String = "",
    val dataCadastro: LocalDateTime? = LocalDateTime.now(),
    val ativo: Boolean = true,
)