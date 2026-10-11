package com.edu.psiclinbe.dto.response

import com.edu.psiclinbe.model.enums.AgendaStatus
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class AgendaDtoResponse(
    val id: Long? = null,
    val pacienteId: Long,
    val psicologoId: Long,
    val salaId: Long?,
    val tipoAtendimentoId: Long,
    val convenioId: Long?,
    val dataHoraInicio: LocalDateTime,
    val dataHoraFim: LocalDateTime,
    val status: AgendaStatus,
    val observacoes: String?,
    val ativo: Boolean = true,
)