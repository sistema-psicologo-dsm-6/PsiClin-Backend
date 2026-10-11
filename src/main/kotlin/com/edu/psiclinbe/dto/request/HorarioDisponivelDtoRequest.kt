package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalTime

data class HorarioDisponivelDtoRequest(
    val id: Long? = null,
    @field:NotNull(message = "O psicologo e obrigatorio")
    @field:Positive(message = "O id do psicologo deve ser maior que zero")
    val psicologoId: Long? = null,
    @field:NotBlank(message = "O dia da semana nao pode estar vazio")
    @field:Pattern(
        regexp = "DOMINGO|SEGUNDA|TERCA|QUARTA|QUINTA|SEXTA|SABADO",
        message = "O dia da semana informado e invalido"
    )
    val diaSemana: String = "",
    @field:NotNull(message = "A hora de inicio e obrigatoria")
    val horaInicio: LocalTime,
    @field:NotNull(message = "A hora de fim e obrigatoria")
    val horaFim: LocalTime,
    @field:Size(max = 500, message = "A descricao deve ter no maximo 500 caracteres")
    val descricao: String? = null,
)