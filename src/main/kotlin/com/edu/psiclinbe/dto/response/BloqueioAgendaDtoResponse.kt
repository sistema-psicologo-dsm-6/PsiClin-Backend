package com.edu.psiclinbe.dto.response

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class BloqueioAgendaDtoResponse(
    val id: Long? = 0,
    val psicologoId: Long? = 0,
    val dataHoraInicio: LocalDateTime? = LocalDateTime.now(),
    val dataHoraFim: LocalDateTime? = LocalDateTime.now(),
    val motivo: String = "",
    val ativo: Boolean = true,
)