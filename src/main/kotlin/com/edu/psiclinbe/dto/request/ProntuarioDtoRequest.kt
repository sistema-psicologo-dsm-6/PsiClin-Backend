package com.edu.psiclinbe.dto.request

import com.edu.psiclinbe.model.enums.ProntuarioStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ProntuarioDtoRequest(
    val id: Long? = null,
    @field:NotNull(message = "O paciente e obrigatorio")
    val pacienteId: Long? = null,
    @field:NotNull(message = "A data de abertura e obrigatoria")
    val dataAbertura: LocalDateTime? = LocalDateTime.now(),
    @field:NotBlank(message = "A queixa principal nao pode estar vazia")
    val queixaPrincipal: String = "",
    @field:NotNull(message = "O status do prontuario e obrigatorio")
    val status: ProntuarioStatus? = null,
)
