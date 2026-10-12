package com.edu.psiclinbe.dto.response

import com.edu.psiclinbe.model.Prontuario
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

data class PacienteDtoResponse(
    val id: Long? = 0,
    val nome: String = "",
    val cpf: String = "",
    val dataNascimento: LocalDate? = LocalDate.now(),
    val email: String = "",
    val telefone: String = "",
    val endereco: String = "",
    val observacoes: String? = "",
    val ativo: Boolean = true,
)