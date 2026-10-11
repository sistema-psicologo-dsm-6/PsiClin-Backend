package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class BloqueioAgendaDtoRequest(
    val id: Long,
    @field:NotNull(message = "O psicologo e obrigatorio")
    @field:Positive(message = "O id do psicologo deve ser maior que zero")
    val psicologoId: Long,
    @field:NotNull(message = "A data e hora de inicio sao obrigatorias")
    val dataHoraInicio: LocalDateTime,
    @field:NotNull(message = "A data e hora de fim sao obrigatorias")
    val dataHoraFim: LocalDateTime,
    @field:NotBlank(message = "O motivo do bloqueio nao pode estar vazio")
    @field:Size(max = 500, message = "O motivo do bloqueio deve ter no maximo 500 caracteres")
    val motivo: String = "",
)