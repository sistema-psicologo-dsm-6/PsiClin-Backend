package com.edu.psiclinbe.dto.response

import com.edu.psiclinbe.model.enums.PagamentoStatus
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime

data class PagamentoDtoResponse(
    val id: Long? = 0,
    val agendaId: Long? = 0,
    val pacienteId: Long? = 0,
    val convenioId: Long? = 0,
    val valorTotal: Double? = 0.0,
    val valorConvenio: Double? = 0.0,
    val valorPaciente: Double? = 0.0,
    val formaPagamento: String = "",
    val dataPagamento: LocalDateTime? = LocalDateTime.now(),
    @Enumerated(EnumType.STRING)
    val status: PagamentoStatus? = null,
)
