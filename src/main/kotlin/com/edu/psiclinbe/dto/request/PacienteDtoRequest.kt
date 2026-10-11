package com.edu.psiclinbe.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class PacienteDtoRequest(
    val id: Long? = null,
    @field:NotBlank(message = "O nome nao pode estar vazio")
    @field:Size(max = 150, message = "O nome deve ter no maximo 150 caracteres")
    val nome: String = "",
    @field:NotBlank(message = "O CPF nao pode estar vazio")
    @field:Size(max = 14, message = "O CPF deve ter no maximo 14 caracteres")
    val cpf: String = "",
    @field:NotNull(message = "A data de nascimento e obrigatoria")
    @field:Past(message = "A data de nascimento deve estar no passado")
    val dataNascimento: LocalDate,
    @field:NotBlank(message = "O e-mail nao pode estar vazio")
    @field:Email(message = "O e-mail deve ser valido")
    @field:Size(max = 254, message = "O e-mail deve ter no maximo 254 caracteres")
    val email: String = "",
    @field:NotBlank(message = "O telefone nao pode estar vazio")
    val telefone: String = "",
    @field:NotBlank(message = "O endereco nao pode estar vazio")
    val endereco: String = "",
    val observacoes: String,
    val ativo: Boolean = true,
)