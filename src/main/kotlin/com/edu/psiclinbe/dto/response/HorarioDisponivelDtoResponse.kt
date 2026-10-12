package com.edu.psiclinbe.dto.response

import java.time.LocalTime

data class HorarioDisponivelDtoResponse(
    val id: Long? = 0,
    val psicologoId: Long? = 0,
    val diaSemana: String ?= "",
    val horaInicio: LocalTime? = LocalTime.now(),
    val horaFim: LocalTime? = LocalTime.now(),
    val descricao: String? = null,
    val ativo: Boolean = true,
)