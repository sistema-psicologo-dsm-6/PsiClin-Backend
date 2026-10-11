package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class EvolucaoClinicaDtoRequest(
    val id: Long? = null,
    @field:NotNull(message = "O prontuario e obrigatorio")
    @field:Positive(message = "O id do prontuario deve ser maior que zero")
    val prontuarioId: Long,
    @field:NotNull(message = "O psicologo e obrigatorio")
    @field:Positive(message = "O id do psicologo deve ser maior que zero")
    val psicologoId: Long,
    @field:NotNull(message = "A agenda e obrigatoria")
    @field:Positive(message = "O id da agenda deve ser maior que zero")
    val agendaId: Long,
    @field:NotNull(message = "A data de registro e obrigatoria")
    val dataRegistro: LocalDateTime = LocalDateTime.now(),
    @field:NotBlank(message = "A descricao nao pode estar vazia")
    @field:Size(max = 5000, message = "A descricao deve ter no maximo 5000 caracteres")
    val descricao: String?,
)