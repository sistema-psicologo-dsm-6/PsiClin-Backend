package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.NotBlank

data class AlterarSenhaRequest(
    @field:NotBlank(message = "A senha não pode estar vazia")
    val novaSenha: String,
)