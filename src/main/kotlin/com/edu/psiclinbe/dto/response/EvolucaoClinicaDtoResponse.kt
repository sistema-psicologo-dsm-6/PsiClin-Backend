package com.edu.psiclinbe.dto.response

import java.time.LocalDateTime

data class EvolucaoClinicaDtoResponse(
    val id: Long,
    val prontuarioId: Long,
    val psicologoId: Long,
    val agendaId: Long,
    val dataRegistro: LocalDateTime,
    val descricao: String?,
    val ativo: Boolean = true,
)