package com.edu.psiclinbe.dto.response

import com.edu.psiclinbe.model.enums.ProntuarioStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ProntuarioDtoResponse(
    val id: Long? = 0,
    val pacienteId: Long? = 0,
    val dataAbertura: LocalDateTime? = LocalDateTime.now(),
    val queixaPrincipal: String = "",
    val status: ProntuarioStatus? = null,
    val ativo: Boolean = true,
)
