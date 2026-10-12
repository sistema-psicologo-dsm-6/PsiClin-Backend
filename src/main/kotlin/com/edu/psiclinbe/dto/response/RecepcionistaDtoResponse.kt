package com.edu.psiclinbe.dto.response

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RecepcionistaDtoResponse(
    val id: Long? = 0,
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val telefone: String = "",
    val senha: String = "",
    val setor: String = "",
    val ativo: Boolean = true,
)