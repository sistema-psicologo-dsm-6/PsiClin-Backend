package com.edu.psiclinbe.dto.response

import com.edu.psiclinbe.model.enums.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class FuncionarioDtoResponse(
    val id: Long? = null,
    val nome: String?,
    val cpf: String?,
    val email: String?,
    val telefone: String?,
    val senha: String?,
    val ativo: Boolean? = true,
    val role: Role? = null,
)