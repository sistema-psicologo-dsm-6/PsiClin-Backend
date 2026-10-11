package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class SalaAtendimentoDtoRequest(
    val id: Long? = null,
    @field:NotBlank(message = "O nome da sala nao pode estar vazio")
    val nome: String = "",
    @field:NotBlank(message = "A descricao da sala nao pode estar vazia")
    val descricao: String = "",
    @field:NotNull(message = "A capacidade e obrigatoria")
    @field:Positive(message = "A capacidade deve ser maior que zero")
    val capacidade: Int? = null,
    val ativo: Boolean = true,
)