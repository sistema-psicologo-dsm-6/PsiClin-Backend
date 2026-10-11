package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class TipoAtendimentoDtoRequest(
    val id: Long? = null,
    @field:NotBlank(message = "O nome do tipo de atendimento nao pode estar vazio")
    @field:Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    val nome: String = "",
    @field:NotBlank(message = "A descricao nao pode estar vazia")
    val descricao: String = "",
    @field:NotBlank(message = "A duracao nao pode estar vazia")
    val duracao: String = "",
    val ativo: Boolean = true,
)