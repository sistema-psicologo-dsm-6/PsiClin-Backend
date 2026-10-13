package com.edu.psiclinbe.dto.request

import com.edu.psiclinbe.model.enums.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class FuncionarioDtoRequest(
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
    val senha: String = "",
    val ativo: Boolean = true,
    val role: Role = Role.ADMIN,
)