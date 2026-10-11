package com.edu.psiclinbe.dto.request

import com.edu.psiclinbe.model.enums.AgendaStatus
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class AgendaDtoRequest(
    val id: Long? = null,
    @field:NotNull(message = "O paciente e obrigatorio")
    @field:Positive(message = "O id do paciente deve ser maior que zero")
    val pacienteId: Long,
    @field:NotNull(message = "O psicologo e obrigatorio")
    @field:Positive(message = "O id do psicologo deve ser maior que zero")
    val psicologoId: Long,
    @field:Positive(message = "O id da sala deve ser maior que zero")
    val salaId: Long,
    @field:NotNull(message = "O tipo de atendimento e obrigatorio")
    @field:Positive(message = "O id do tipo de atendimento deve ser maior que zero")
    val tipoAtendimentoId: Long,
    @field:Positive(message = "O id do convenio deve ser maior que zero")
    val convenioId: Long,
    @field:NotNull(message = "A data e hora de inicio sao obrigatorias")
    val dataHoraInicio: LocalDateTime = LocalDateTime.now(),
    @field:NotNull(message = "A data e hora de fim sao obrigatorias")
    val dataHoraFim: LocalDateTime = LocalDateTime.now(),
    @field:NotNull(message = "O status da agenda e obrigatorio")
    val status: AgendaStatus,
    @field:Size(max = 2000, message = "As observacoes devem ter no maximo 2000 caracteres")
    val observacoes: String = "",
)