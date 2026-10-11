package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RecepcionistaDtoRequest(
    val id: Long? = null,
    @field:NotBlank(message = "O nome nao pode estar vazio")
    val nome: String = "",
    @field:NotBlank(message = "O CPF nao pode estar vazio")
    val cpf: String = "",
    @field:NotBlank(message = "O e-mail nao pode estar vazio")
    @field:Email(message = "O e-mail deve ser valido")
    val email: String = "",
    @field:NotBlank(message = "O telefone nao pode estar vazio")
    val telefone: String = "",
    @field:NotBlank(message = "A senha nao pode estar vazia")
    val senhaHash: String = "",
    @field:NotBlank(message = "O setor nao pode estar vazio")
    val setor: String = "",
    val ativo: Boolean = true,
)