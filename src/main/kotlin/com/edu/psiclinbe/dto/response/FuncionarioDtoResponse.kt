package com.edu.psiclinbe.dto.response

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class FuncionarioDtoResponse(
    val id: Long? = null,
    val nome: String?,
    val cpf: String?,
    val email: String?,
    val telefone: String?,
    val senhaHash: String?,
    val ativo: Boolean? = true,
)