package com.edu.psiclinbe.dto.response

import com.edu.psiclinbe.model.enums.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class RecepcionistaDtoResponse(
    val id: Long? = 0,
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val telefone: String = "",
    val senha: String = "",
    val setor: String = "",
    val dataCadastro: LocalDateTime? = LocalDateTime.now(),
    val ativo: Boolean = true,
    val role: Role? = null
)