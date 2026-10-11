package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

data class ConvenioDtoRequest(
    val id: Long? = null,
    @field:NotBlank(message = "O nome do convenio nao pode estar vazio")
    @field:Size(min = 2, max = 150, message = "O nome do convenio deve ter entre 2 e 150 caracteres")
    val nome: String = "",
    @field:NotBlank(message = "O CNPJ do convenio nao pode estar vazio")
    val cnpj: String = "",
    @field:NotBlank(message = "O telefone do convenio nao pode estar vazio")
    val telefone: String = "",
    @field:NotNull(message = "O percentual de cobertura e obrigatorio")
    @field:PositiveOrZero(message = "O percentual de cobertura nao pode ser negativo")
    val percentualCobertura: Double? = null,
    val ativo: Boolean = true,
)