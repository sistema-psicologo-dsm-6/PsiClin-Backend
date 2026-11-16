package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.NotBlank

data class AlterarSenhaDtoRequest(
    @field:NotBlank(message = "A senha nao pode estar vazia")
    val novaSenha: String = "",
)
